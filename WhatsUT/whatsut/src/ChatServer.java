import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private final Map<String, String> users = new HashMap<>(); // username -> password
    private final Map<String, IChatClient> onlineUsers = new HashMap<>(); // username -> client instance

    public ChatServer() throws RemoteException {
        super();
        // Usuários iniciais
        users.put("user1", "password1");
        users.put("user2", "password2");
    }

    @Override
    public boolean login(String username, String password, IChatClient client) throws RemoteException {
        if (users.containsKey(username) && users.get(username).equals(password)) {
            onlineUsers.put(username, client);
            System.out.println(username + " logged in.");
            return true;
        }
        return false;
    }

    @Override
    public void logout(String username) throws RemoteException {
        onlineUsers.remove(username);
        System.out.println(username + " logged out.");
    }

    @Override
    public List<String> getUserList() throws RemoteException {
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
