import socket
import threading

# Banco de dados de contas
accounts = {"12345": 1000.0, "67890": 500.0}
lock = threading.Lock()

def handle_client(client_socket):
    client_socket.sendall(b"Bem-vindo ao sistema financeiro.\n")
    client_socket.sendall(b"Insira o numero da conta: ")
    account_number = client_socket.recv(1024).decode("utf-8").strip()

    if account_number not in accounts:
        client_socket.sendall(b"Conta invvlida. Conexao encerrada.\n")
        client_socket.close()
        return

    client_socket.sendall(b"Autenticado com sucesso.\n")
    while True:
        try:
            client_socket.sendall(
                b"Escolha uma operacao:\n1. Deposito\n2. Saque\n3. Saldo\n4. Sair\n"
            )
            choice = client_socket.recv(1024).decode("utf-8").strip()

            if choice == "1":  # Depósito
                client_socket.sendall(b"Digite o valor do deposito: ")
                try:
                    amount = float(client_socket.recv(1024).decode("utf-8").strip())
                    if amount <= 0:
                        client_socket.sendall(b"Valor invalido.\n")
                    else:
                        with lock:
                            accounts[account_number] += amount
                        client_socket.sendall(
                            f"Depósito de R${amount:.2f} realizado com sucesso.\n".encode(
                                "utf-8"
                            )
                        )
                except ValueError:
                    client_socket.sendall(b"Valor invalido.\n")

            elif choice == "2":  # Saque
                client_socket.sendall(b"Digite o valor do saque: ")
                try:
                    amount = float(client_socket.recv(1024).decode("utf-8").strip())
                    if amount <= 0:
                        client_socket.sendall(b"Valor invalido.\n")
                    else:
                        with lock:
                            if accounts[account_number] >= amount:
                                accounts[account_number] -= amount
                                client_socket.sendall(
                                    f"Saque de R${amount:.2f} realizado com sucesso.\n".encode(
                                        "utf-8"
                                    )
                                )
                            else:
                                client_socket.sendall(b"Saldo insuficiente.\n")
                except ValueError:
                    client_socket.sendall(b"Valor invalido.\n")

            elif choice == "3":  # Saldo
                with lock:
                    balance = accounts[account_number]
                client_socket.sendall(
                    f"Seu saldo é R${balance:.2f}.\n".encode("utf-8")
                )

            elif choice == "4":  # Sair
                client_socket.sendall(b"Saindo do sistema. Ate mais!\n")
                break

            else:
                client_socket.sendall(b"Opcao invalida.\n")
        except Exception as e:
            print(f"[ERRO] {e}")
            break

    client_socket.close()

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", 12345))
    server.listen(5)
    print("[SERVIDOR INICIADO] Aguardando conexões...")

    while True:
        client_socket, client_address = server.accept()
        print(f"[CONEXÃO] Cliente conectado: {client_address}")
        threading.Thread(target=handle_client, args=(client_socket,)).start()

if __name__ == "__main__":
    start_server()
