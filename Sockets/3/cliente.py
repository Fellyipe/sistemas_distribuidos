import socket

def play_hangman(server_ip, server_port):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((server_ip, server_port))
            print(client_socket.recv(1024).decode('utf-8'))

            while True:
                response = client_socket.recv(1024).decode('utf-8')
                if "Tente uma letra" in response:
                    print(response)
                    guess = input("Sua tentativa: ").strip()
                    client_socket.sendall(guess.encode('utf-8'))
                else:
                    print(response)
                    if "Parabéns" in response or "Você perdeu" in response:
                        break
    except Exception as e:
        print(f"Erro ao conectar ao servidor: {e}")

if __name__ == "__main__":
    server_ip = input("Digite o IP do servidor (padrão: 127.0.0.1): ") or "127.0.0.1"
    server_port = input("Digite a porta do servidor (padrão: 12345): ") or 12345
    play_hangman(server_ip, int(server_port))
