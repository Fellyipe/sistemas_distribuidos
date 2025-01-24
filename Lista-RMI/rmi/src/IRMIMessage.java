import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRMIMessage extends Remote {
    boolean verificaPalindromo(String s) throws RemoteException;
    String inverteString(String s) throws RemoteException;
    String transformaMaiusculas(String s) throws RemoteException;
    String transformaMinusculas(String s) throws RemoteException;
    int contaVogais(String s) throws RemoteException;
    int contaConsoantes(String s) throws RemoteException;
    String apenasVogais(String s) throws RemoteException;
    String apenasConsoantes(String s) throws RemoteException;
    int posicaoCaracter(String s, char c) throws RemoteException;
    int posicaoSubstring(String s, String sub) throws RemoteException;
}
