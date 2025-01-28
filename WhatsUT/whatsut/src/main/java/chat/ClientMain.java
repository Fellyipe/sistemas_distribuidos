// import java.rmi.registry.LocateRegistry;
// import java.rmi.registry.Registry;

// import java.util.Scanner;

// public class ClientMain {
//     public static void main(String[] args) {
//         try {
//             Registry registry = LocateRegistry.getRegistry("localhost", 1099);
//             IChatServer server = (IChatServer) registry.lookup("ChatService");

//             Scanner scanner = new Scanner(System.in);

//             System.out.print("Enter your username: ");
//             String username = scanner.nextLine();

//             System.out.print("Enter your password: ");
//             String password = scanner.nextLine();

//             ChatClient client = new ChatClient(username);

//             if (server.login(username, password, client)) {
//                 System.out.println("Logged in as " + username);

//                 while (true) {
//                     System.out.println("1. View users");
//                     System.out.println("2. Send message");
//                     System.out.println("3. Logout");
//                     System.out.print("Choose an option: ");
//                     int choice = scanner.nextInt();
//                     scanner.nextLine(); // Consume newline

//                     if (choice == 1) {
//                         System.out.println("Online users: " + server.getUserList());
//                     } else if (choice == 2) {
//                         System.out.print("Enter recipient: ");
//                         String recipient = scanner.nextLine();

//                         System.out.print("Enter message: ");
//                         String message = scanner.nextLine();

//                         server.sendMessage(username, recipient, message);
//                     } else if (choice == 3) {
//                         server.logout(username);
//                         break;
//                     }
//                 }
//             } else {
//                 System.out.println("Invalid login.");
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }
