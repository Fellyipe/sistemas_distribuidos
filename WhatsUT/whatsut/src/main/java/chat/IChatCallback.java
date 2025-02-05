package chat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatCallback extends Remote {
    void onNewMessage(String chatKey) throws RemoteException;
}
