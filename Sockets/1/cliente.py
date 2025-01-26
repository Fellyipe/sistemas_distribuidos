import socket

def send_command(server_ip, server_port):
    print("Bem-vindo ao cliente de Fortunes!")
    print("Comandos disponíveis:")
    print("  GET-FORTUNE             - Retorna uma frase aleatória.")
    print("  ADD-FORTUNE <frase>     - Adiciona uma nova frase.")
    print("  UPD-FORTUNE <pos> <frase> - Atualiza uma frase pela posição.")
    print("  LST-FORTUNE             - Lista todas as frases.")
    print("  EXIT                    - Sai do cliente.\n")

    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((server_ip, server_port))
            print(f"Conectado ao servidor em {server_ip}:{server_port}\n")
            
            while True:
                command = input("Digite um comando: ").strip()
                if command.upper() == "EXIT":
                    print("Encerrando conexão...")
                    break

                client_socket.sendall(command.encode('utf-8'))
                response = client_socket.recv(1024).decode('utf-8')
                print(f"Resposta do servidor:\n{response}\n")
    except Exception as e:
        print(f"Erro ao conectar ao servidor: {e}")

if __name__ == "__main__":
    server_ip = input("Digite o IP do servidor (padrão: 127.0.0.1): ") or "127.0.0.1"
    server_port = input("Digite a porta do servidor (padrão: 12345): ") or 12345
    send_command(server_ip, int(server_port))
