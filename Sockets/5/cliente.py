import socket
import threading
import time
from random import randint

def simulate_activity():
    activities = ["compra", "venda"]
    return activities[randint(0, 1)], randint(1, 100)

def connect_to_server(filial_id):
    host = 'localhost'
    port = 5000
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect((host, port))
    
    try:
        for _ in range(1500):  # Número de ocorrências diárias
            activity, quantity = simulate_activity()
            message = f"Filial {filial_id}: {activity} {quantity} unidades"
            print(message)
            client_socket.send(message.encode())
            time.sleep(2)  # Delay para simular tempo de ocorrência
    finally:
        print(f"Ocorrencias da filial {filial_id} concluídas.")
        client_socket.close()

def start_clients():
    threads = []
    for i in range(1, 6):  # Número de filiais
        thread = threading.Thread(target=connect_to_server, args=(i,))
        thread.start()
        threads.append(thread)
    
    for thread in threads:
        thread.join()

if __name__ == "__main__":
    start_clients()
