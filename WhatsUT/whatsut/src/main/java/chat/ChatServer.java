package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import chat.info.*;
import chat.utils.*;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private final Map<String, UserInfo> users = new HashMap<>(); // username -> UserInfo
    private final Map<String, IChatClient> onlineUsers = new HashMap<>(); // username -> client instance
    //private final Map<String, Map<String, List<MessageInfo>>> messageHistory = new HashMap<>();
    private final Map<String, GroupInfo> groups = new HashMap<>(); // Mapeia o nome do grupo para o objeto Group
    private Map<String, FileInfo> storedFiles = new ConcurrentHashMap<>();
    private Map<String, List<MessageInfo>> messageHistory = new ConcurrentHashMap<>();

    public ChatServer() throws RemoteException {
        super();
        // Usuários iniciais com nome de usuário, senha e e-mail
        users.put("user1", new UserInfo("user1", HashUtil.generateHash("password1"), "user1@mail.com"));
        users.put("user2", new UserInfo("user2", HashUtil.generateHash("password2"), "user2@mail.com"));
    }

    @Override
    public boolean registerUser(String username, String password, String email) throws RemoteException {
        if (users.containsKey(username)) {
            return false; // Usuário já existe
        }
        
        users.put(username, new UserInfo(username, HashUtil.generateHash(password), email));
        return true; // Usuário registrado com sucesso
    }

    @Override
    public boolean login(String username, String password, IChatClient client) throws RemoteException {
        
        if (users.containsKey(username)) {
            String storedHash = users.get(username).getPassword();
            if (storedHash.equals(HashUtil.generateHash(password))) {
                onlineUsers.put(username, client);
                System.out.println(username + " logged in.");
                return true;
            }
            
        }
        return false;
    }

    @Override
    public void logout(String username) throws RemoteException {
        onlineUsers.remove(username);
        System.out.println(username + " logged out.");
    }

    @Override
    public List<String> listUsers() throws RemoteException {
        return new ArrayList<>(users.keySet());
    }

    @Override
    public List<String> listOnlineUsers() throws RemoteException {
        return new ArrayList<>(onlineUsers.keySet());
    }

    // @Override
    // public void sendMessage(String sender, String receiver, String message) throws RemoteException {
        
    //     // Armazena a mensagem na estrutura de histórico
    //     messageHistory
    //         .computeIfAbsent(sender, k -> new HashMap<>())
    //         .computeIfAbsent(receiver, k -> new ArrayList<>())
    //         .add(new MessageInfo(sender, message));

    //     messageHistory
    //         .computeIfAbsent(receiver, k -> new HashMap<>())
    //         .computeIfAbsent(sender, k -> new ArrayList<>())
    //         .add(new MessageInfo(sender, message));
        
    //     IChatClient client = onlineUsers.get(receiver);
    //     if (client != null) {
    //         client.receiveMessage(sender, message);
    //     } else {
    //         System.out.println("User " + receiver + " not found or not online.");
    //     }
    // }

    // // Recupera o histórico de mensagens entre dois usuários
    // @Override
    // public List<MessageInfo> getMessageHistory(String user1, String user2) {
    //     if (messageHistory.containsKey(user1) && messageHistory.get(user1).containsKey(user2)) {
    //         return messageHistory.get(user1).get(user2);
    //     }
    //     return new ArrayList<>(); // Retorna lista vazia se não houver histórico
    // }

    @Override
    public void sendMessage(String sender, String recipient, String message) throws RemoteException {
        MessageInfo msg = new MessageInfo(sender, recipient, message);
        storeMessage(sender, recipient, msg);
    }

    @Override
    public void sendFile(String sender, String recipient, FileInfo file) throws RemoteException {
        MessageInfo msg = new MessageInfo(sender, recipient, file);
        storeMessage(sender, recipient, msg);
    }

    private void storeMessage(String sender, String recipient, MessageInfo msg) {
        String key = getChatKey(sender, recipient);
        messageHistory.putIfAbsent(key, new ArrayList<>());
        messageHistory.get(key).add(msg);
    }

    @Override
    public List<MessageInfo> getMessageHistory(String user1, String user2) throws RemoteException {
        return messageHistory.getOrDefault(getChatKey(user1, user2), new ArrayList<>());
    }

    private String getChatKey(String user1, String user2) {
        List<String> users = Arrays.asList(user1, user2);
        Collections.sort(users); // Garante sempre a mesma ordem
        return users.get(0) + "_" + users.get(1);
    }

    @Override
    // Criar um grupo
    public boolean createGroup(String groupName, String description, String owner) throws RemoteException {
        if (groups.containsKey(groupName)) {
            return false; // Grupo com o mesmo nome já existe
        }
        groups.put(groupName, new GroupInfo(groupName, description, owner));
        return true;
    }

    @Override
    // Solicitar entrada em um grupo
    public boolean requestJoinGroup(String groupName, String username) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        if (group == null) {
            return false; // Grupo não encontrado
        }
        if (group.getOwner().equals(username)) {
            return false; // O dono do grupo não pode solicitar entrada
        }
        if (group.getMembers().contains(username)) {
            return false; // Usuário já é membro do grupo
        }
        group.addPendingRequest(username);
        return true;
    }

    @Override
    // Aprovar ou rejeitar uma solicitação
    public boolean approveJoinRequest(String groupName, String owner, String username, boolean approve) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        if (group == null || !group.getOwner().equals(owner)) {
            return false; // Grupo não encontrado ou usuário não é o dono
        }

        if (!group.getPendingRequests().contains(username)) {
            return false; // Solicitação não encontrada
        }

        group.removePendingRequest(username);
        if (approve) {
            group.addMember(username);
        }
        return true;
    }

    @Override
    // Listar grupos
    public List<String> listGroups() throws RemoteException {
        return groups.values().stream() // Cria um stream a partir dos valores do mapa
                 .map(GroupInfo::getName) // Mapeia cada GroupInfo para seu nome
                 .collect(Collectors.toList()); // Coleta os resultados em uma List<String>
    }

    @Override
    public void sendGroupMessage(String groupName, String sender, String message) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        if (group != null) {
            group.addMessage(sender + ": " + message);
        }
    }

    @Override
    public List<String> getGroupMessages(String groupName) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        return (group != null) ? group.getMessages() : new ArrayList<>();
    }

    @Override
    public boolean leaveGroup(String groupName, String username) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        if (group != null) {
            return group.removeMember(username);
        }
        return false;
    }

    @Override
    public GroupInfo getGroupInfo(String groupName) throws RemoteException {
        return groups.get(groupName);
    }

    @Override
    public boolean removeUserFromGroup(String groupName, String userToRemove) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        if (group != null) { // Verifica se quem chama é o dono
            boolean removed = group.removeMember(userToRemove);
            if (removed) {
                System.out.println(userToRemove + " foi removido do grupo " + groupName);
            }
            return removed;
        }
        return false;
    }

    @Override
    public boolean deleteGroup(String groupName) throws RemoteException {
        GroupInfo group = groups.get(groupName);
        if (group != null) {
            groups.remove(groupName);
            return true;
        }
        return false;
    }

    @Override
    public boolean changeGroupOwner(String groupName, String newOwner) throws RemoteException {
        GroupInfo group = groups.get(groupName);

        if (group != null && group.isMember(newOwner)) {
            group.setOwner(newOwner);
            return true;
        }
        return false;
    }

    // @Override
    // public void sendFile(String sender, String recipient, FileInfo file) throws RemoteException {
    //     String fileKey = sender + "_" + recipient + "_" + file.getFileName(); // Nome único
    //     storedFiles.put(fileKey, file);
    //     System.out.println("📁 Arquivo recebido: " + file.getFileName() + " de " + sender + " para " + recipient);
    // }

    @Override
    public FileInfo receiveFile(String sender, String recipient, String fileName) throws RemoteException {
        String key = getChatKey(sender, recipient);
        System.out.println(key);

        List<MessageInfo> messages = messageHistory.getOrDefault(key, new ArrayList<>());

        for (Map.Entry<String, List<MessageInfo>> entry : messageHistory.entrySet()) {
            System.out.println("Key: " + entry.getKey());
        }

        System.out.println(fileName);
        System.out.println(messages.size());
        for (MessageInfo msg : messages) {
            System.out.println(msg.isFile() + "Dentro");
            System.out.println(msg.getFile().getFileName());
            if (msg.isFile() && msg.getFile().getFileName().equals(fileName)) {
                return msg.getFile(); // Retorna o arquivo encontrado
            }
        }
        return null; // Retorna null se o arquivo não existir no histórico
    }

}
