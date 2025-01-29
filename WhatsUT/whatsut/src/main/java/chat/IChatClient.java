package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatClient extends Remote {
    public String getUsername() throws RemoteException;
    public List<String> getUnreadMessages(String sender) throws RemoteException;
    public void clearUnreadMessages(String sender) throws RemoteException;
    public void receiveMessage(String sender, String message) throws RemoteException;
    public boolean login(String password) throws RemoteException;
    public void logout() throws RemoteException;
    public void sendMessage(String recipient, String message) throws RemoteException;
    public java.util.List<String> getUserList() throws RemoteException;
}

