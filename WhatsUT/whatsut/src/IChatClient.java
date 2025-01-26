import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatClient extends Remote {
    void receiveMessage(String sender, String message) throws RemoteException;
}
