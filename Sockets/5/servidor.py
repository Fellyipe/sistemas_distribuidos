import socket
import threading

store_data = {}
lock = threading.Lock()

def handle_branch(client_socket, branch_id):
    while True:
        try:
            data = client_socket.recv(1024).decode("utf-8")
            if not data:
                break
            with lock:
                if branch_id not in store_data:
                    store_data[branch_id] = []
                store_data[branch_id].append(data)
            print(f"[FILIAL {branch_id}] {data}")
        except Exception as e:
            print(f"[ERRO FILIAL {branch_id}] {e}")
            break

    client_socket.close()
    print(f"[DESCONECTADO] Filial {branch_id} desconectada.")

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", 12345))
    server.listen(5)
    print("[SERVIDOR INICIADO] Aguardando filiais...")

    branch_id = 1
    while True:
        client_socket, client_address = server.accept()
        print(f"[CONEXÃO] Filial {branch_id} conectada: {client_address}")
        threading.Thread(target=handle_branch, args=(client_socket, branch_id)).start()
        branch_id += 1

if __name__ == "__main__":
    start_server()
