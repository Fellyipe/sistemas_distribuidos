import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMICliente {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Uso: java -cp bin RMIClient <operacao> <argumento>");
            System.out.println("Operacoes: P, I, +, -, V, C, A, Z, W, F");
            return;
        }

        String operacao = args[0];
        String parametro = args[1];

        try {
            Registry registro = LocateRegistry.getRegistry("localhost", 1099);

            IRMIMessage mensagem = (IRMIMessage) registro.lookup("MessageService");

            switch (operacao) {
                case "P":
                    boolean palindromo = mensagem.verificaPalindromo(parametro);
                    System.out.println("É palíndromo? " + palindromo);
                    break;
                    case "I":
                    String invertido = mensagem.inverteString(parametro);
                    System.out.println("String invertida: " + invertido);
                    break;
                case "+":
                    String maiusculas = mensagem.transformaMaiusculas(parametro);
                    System.out.println("Maiúsculas: " + maiusculas);
                    break;
                case "-":
                    String minusculas = mensagem.transformaMinusculas(parametro);
                    System.out.println("Minúsculas: " + minusculas);
                    break;
                case "V":
                    int vogais = mensagem.contaVogais(parametro);
                    System.out.println("Quantidade de vogais: " + vogais);
                    break;
                case "C":
                    int consoantes = mensagem.contaConsoantes(parametro);
                    System.out.println("Quantidade de consoantes: " + consoantes);
                    break;
                    case "A":
                    String apenasVogais = mensagem.apenasVogais(parametro);
                    System.out.println("Apenas as vogais: " + apenasVogais);
                    break;
                case "Z":
                    String apenasConsoantes = mensagem.apenasConsoantes(parametro);
                    System.out.println("Apenas as consoantes: " + apenasConsoantes);
                    break;
                case "W":
                    if (args.length < 3) {
                        System.out.println("Para a operação W, é necessário um terceiro parâmetro (caractere a ser buscado).");
                        return;
                    }
                    char caractere = args[2].charAt(0);
                    int posicao = mensagem.posicaoCaracter(parametro, caractere);
                    System.out.println("Posição do caractere '" + caractere + "': " + posicao);
                    break;
                case "F":
                    if (args.length < 3) {
                        System.out.println("Para a operação F, é necessário um terceiro parâmetro (substring a ser buscada).");
                        return;
                    }
                    String substring = args[2];
                    int posSubstring = mensagem.posicaoSubstring(parametro, substring);
                    System.out.println("Posição da substring \"" + substring + "\": " + posSubstring);
                    break;
                default:
                    System.out.println("Operação desconhecida.");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
