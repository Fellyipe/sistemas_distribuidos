import socket
import threading

def handle_client(conn, addr):
    print(f"Conexão de {addr} estabelecida.")
    try:
        while True:
            data = conn.recv(1024)
            if not data:
                break
            print(f"Dados recebidos de {addr}: {data.decode()}")
    finally:
        print(f"Conexão com {addr} encerrada.")
        conn.close()

def start_server():
    host = 'localhost'
    port = 5000
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen()
    print(f"Servidor ouvindo em {host}:{port}")

    try:
        while True:
            conn, addr = server_socket.accept()
            thread = threading.Thread(target=handle_client, args=(conn, addr))
            thread.start()
            print(f"Ativo conexões de {threading.activeCount() - 1}")
    finally:
        server_socket.close()

if __name__ == "__main__":
    start_server()
