import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIMessage extends UnicastRemoteObject implements IRMIMessage {

	public RMIMessage() throws RemoteException {
		super();
	}

	@Override
    public boolean verificaPalindromo(String s) throws RemoteException {
        String invertida = new StringBuilder(s).reverse().toString();
        return s.equalsIgnoreCase(invertida);
    }

    @Override
    public String inverteString(String s) throws RemoteException {
        return new StringBuilder(s).reverse().toString();
    }

    @Override
    public String transformaMaiusculas(String s) throws RemoteException {
        return s.toUpperCase();
    }

    @Override
    public String transformaMinusculas(String s) throws RemoteException {
        return s.toLowerCase();
    }

    @Override
    public int contaVogais(String s) throws RemoteException {
        int count = 0;
        for (char c : s.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int contaConsoantes(String s) throws RemoteException {
        int count = 0;
        for (char c : s.toLowerCase().toCharArray()) {
            if (Character.isLetter(c) && "aeiou".indexOf(c) == -1) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String apenasVogais(String s) throws RemoteException {
        StringBuilder result = new StringBuilder();
        for (char c : s.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(c) != -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @Override
    public String apenasConsoantes(String s) throws RemoteException {
        StringBuilder result = new StringBuilder();
        for (char c : s.toLowerCase().toCharArray()) {
            if (Character.isLetter(c) && "aeiou".indexOf(c) == -1) {
                result.append(c);
            }
        }
        return result.toString();
    }

    @Override
    public int posicaoCaracter(String s, char c) throws RemoteException {
        return s.indexOf(c) + 1;
    }

    @Override
    public int posicaoSubstring(String s, String sub) throws RemoteException {
        return s.indexOf(sub) + 1;
    }
    
}