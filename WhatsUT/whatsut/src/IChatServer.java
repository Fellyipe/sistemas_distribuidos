import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatServer extends Remote {
    boolean login(String username, String password, IChatClient client) throws RemoteException;
    void logout(String username) throws RemoteException;
    List<String> getUserList() throws RemoteException;
    void sendMessage(String sender, String receiver, String message) throws RemoteException;
}
