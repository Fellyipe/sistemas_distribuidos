package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import chat.info.*;
import chat.utils.*;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private final Map<String, UserInfo> users = new HashMap<>(); // username -> UserInfo
    private final Map<String, IChatClient> onlineUsers = new HashMap<>(); // username -> client instance
    private final Map<String, Map<String, List<MessageInfo>>> messageHistory = new HashMap<>();
    private final Map<String, GroupInfo> groups = new HashMap<>(); // Mapeia o nome do grupo para o objeto Group


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

    @Override
    public void sendMessage(String sender, String receiver, String message) throws RemoteException {
        
        // Armazena a mensagem na estrutura de histórico
        messageHistory
            .computeIfAbsent(sender, k -> new HashMap<>())
            .computeIfAbsent(receiver, k -> new ArrayList<>())
            .add(new MessageInfo(sender, message));

        messageHistory
            .computeIfAbsent(receiver, k -> new HashMap<>())
            .computeIfAbsent(sender, k -> new ArrayList<>())
            .add(new MessageInfo(sender, message));
        
        IChatClient client = onlineUsers.get(receiver);
        if (client != null) {
            client.receiveMessage(sender, message);
        } else {
            System.out.println("User " + receiver + " not found or not online.");
        }
    }

    // Recupera o histórico de mensagens entre dois usuários
    @Override
    public List<MessageInfo> getMessageHistory(String user1, String user2) {
        if (messageHistory.containsKey(user1) && messageHistory.get(user1).containsKey(user2)) {
            return messageHistory.get(user1).get(user2);
        }
        return new ArrayList<>(); // Retorna lista vazia se não houver histórico
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
    public List<GroupInfo> listGroups() throws RemoteException {
        return new ArrayList<>(groups.values());
    }

}
