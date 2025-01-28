package chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements IChatClient {
    private final String username;
    private final IChatServer server;

    public ChatClient(String username, IChatServer server) throws RemoteException {
        super();
        this.username = username;
        this.server = server;
    }

    @Override
    public void receiveMessage(String sender, String message) throws RemoteException {
        System.out.println(sender + " says: " + message); // Pode ser substituído por uma atualização de UI
    }

    @Override
    public boolean login(String password) throws RemoteException {
        return server.login(username, password, this);
    }

    @Override
    public void logout() throws RemoteException {
        server.logout(username);
    }

    @Override
    public void sendMessage(String recipient, String message) throws RemoteException {
        server.sendMessage(username, recipient, message);
    }

    @Override
    public java.util.List<String> getUserList() throws RemoteException {
        return server.getUserList();
    }

    @Override
    public String getUsername() {
        return username;
    }
}
