import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServidor {
    public RMIServidor() {
        try {
            IRMIMessage mensagem = new RMIMessage();

            Registry registro = LocateRegistry.createRegistry(1099);

            registro.rebind("MessageService", mensagem);

            System.out.println("Servidor está executando (Esta é a parte do Servidor).");

        } catch (Exception e) {
            System.out.println("Erro:" + e);
        }
    }

    public static void main(String args[]) {
        RMIServidor servidor_message = new RMIServidor();
    }

}
