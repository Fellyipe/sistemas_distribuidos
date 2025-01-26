import socket
import threading
import random

fortunes = [
    "Grandes Bençãos",
    "Pequenas Bençãos",
    "Meias Bençãos",
    "Nenhuma Benção"
]

def handle_client(client_socket, client_address):
    print(f"[NOVA CONEXÃO] Cliente conectado: {client_address}")
    while True:
        try:
            message = client_socket.recv(1024).decode('utf-8').strip()
            if not message:
                break
            
            if message == "GET-FORTUNE":
                response = random.choice(fortunes)
            elif message.startswith("ADD-FORTUNE"):
                _, new_fortune = message.split(" ", 1)
                fortunes.append(new_fortune)
                response = "Frase adicionada com sucesso."
            elif message.startswith("UPD-FORTUNE"):
                parts = message.split(" ", 2)
                if len(parts) < 3:
                    response = "Formato inválido. Use: UPD-FORTUNE <pos> <nova frase>"
                else:
                    pos, new_fortune = int(parts[1]), parts[2]
                    if 0 <= pos < len(fortunes):
                        fortunes[pos] = new_fortune
                        response = "Frase atualizada com sucesso."
                    else:
                        response = "Posição inválida."
            elif message == "LST-FORTUNE":
                response = "\n".join(f"{i}: {fortune}" for i, fortune in enumerate(fortunes))
            else:
                response = "Comando desconhecido."
            
            client_socket.sendall(response.encode('utf-8'))
        except Exception as e:
            print(f"[ERRO] {e}")
            break
    
    print(f"[DESCONECTADO] Cliente {client_address} desconectado.")
    client_socket.close()

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", 12345))
    server.listen(5)
    print("[SERVIDOR INICIADO] Aguardando conexões...")
    
    while True:
        client_socket, client_address = server.accept()
        client_thread = threading.Thread(target=handle_client, args=(client_socket, client_address))
        client_thread.start()

if __name__ == "__main__":
    start_server()
