package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatServer extends Remote {
    public boolean registerUser(String username, String password, String email) throws RemoteException;
    boolean login(String username, String password, IChatClient client) throws RemoteException;
    void logout(String username) throws RemoteException;
    List<String> listUsers() throws RemoteException; // Retorna todos os usuários
    List<String> listOnlineUsers() throws RemoteException; // Retorna apenas os usuários online
    void sendMessage(String sender, String receiver, String message) throws RemoteException;
}
