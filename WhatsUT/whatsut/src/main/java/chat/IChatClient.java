package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatClient extends Remote {
    void receiveMessage(String sender, String message) throws RemoteException;
    public boolean login(String password) throws RemoteException;
    public void logout() throws RemoteException;
    public void sendMessage(String recipient, String message) throws RemoteException;
    public java.util.List<String> getUserList() throws RemoteException;
    public String getUsername() throws RemoteException;
}

