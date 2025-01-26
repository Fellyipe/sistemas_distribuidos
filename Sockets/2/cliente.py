import socket

def send_data(server_ip, server_port):
    print("Cliente para envio de sequência de inteiros.\n")
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((server_ip, server_port))
            print(f"Conectado ao servidor em {server_ip}:{server_port}")

            data = input("Digite a sequência de inteiros e o operador (ex: 1 2 3 +): ").strip()
            client_socket.sendall(data.encode('utf-8'))

            response = client_socket.recv(1024).decode('utf-8')
            print(f"Resposta do servidor: {response}")
    except Exception as e:
        print(f"Erro ao conectar ao servidor: {e}")

if __name__ == "__main__":
    server_ip = input("Digite o IP do servidor (padrão: 127.0.0.1): ") or "127.0.0.1"
    server_port = input("Digite a porta do servidor (padrão: 12345): ") or 12345
    send_data(server_ip, int(server_port))
