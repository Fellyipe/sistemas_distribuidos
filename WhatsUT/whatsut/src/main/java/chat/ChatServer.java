package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import chat.info.UserInfo;
import chat.utils.*;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private final Map<String, UserInfo> users = new HashMap<>(); // username -> UserInfo
    private final Map<String, IChatClient> onlineUsers = new HashMap<>(); // username -> client instance

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
        IChatClient client = onlineUsers.get(receiver);
        if (client != null) {
            client.receiveMessage(sender, message);
        } else {
            System.out.println("User " + receiver + " not found.");
        }
    }

}
