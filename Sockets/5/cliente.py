import socket
import random
import time

def generate_transaction():
    product_id = random.randint(1000, 9999)
    quantity = random.randint(1, 10)
    operation = random.choice(["COMPRA", "VENDA"])
    return f"{operation} | Produto: {product_id} | Quantidade: {quantity}"

def connect_to_central(server_ip, server_port, branch_id):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((server_ip, server_port))
            print(f"[FILIAL {branch_id}] Conectada ao servidor central.")

            for _ in range(1500):  # 1500 movimentações diárias
                transaction = generate_transaction()
                client_socket.sendall(transaction.encode("utf-8"))
                time.sleep(random.uniform(0.01, 0.1))  # Delay entre transações

            print(f"[FILIAL {branch_id}] Transações diárias concluídas.")
    except Exception as e:
        print(f"[ERRO FILIAL {branch_id}] {e}")

if __name__ == "__main__":
    server_ip = "127.0.0.1"
    server_port = 12345
    branch_id = random.randint(1, 100)
    connect_to_central(server_ip, server_port, branch_id)
