import threading
import time
import random

class Barbearia:
    def __init__(self, num_cadeiras):
        self.num_cadeiras = num_cadeiras
        self.cadeiras_ocupadas = 0
        self.clientes = threading.Condition()
        self.barbeiro_dormindo = threading.Event()

    def entrar_na_barbearia(self, id_cliente):
        with self.clientes:
            if self.cadeiras_ocupadas < self.num_cadeiras:
                self.cadeiras_ocupadas += 1
                print(f"Cliente {id_cliente} entrou na barbearia e sentou. Cadeiras ocupadas: {self.cadeiras_ocupadas}/{self.num_cadeiras}")
                if not self.barbeiro_dormindo.is_set():
                    print(f"Cliente {id_cliente} acordou o barbeiro.")
                    self.barbeiro_dormindo.set()
                self.clientes.notify()
            else:
                print(f"Cliente {id_cliente} encontrou a barbearia cheia e foi embora.")

    def atender_cliente(self):
        with self.clientes:
            while self.cadeiras_ocupadas == 0:
                print("Barbeiro está dormindo, esperando clientes...")
                self.barbeiro_dormindo.clear()
                self.clientes.wait()

            self.cadeiras_ocupadas -= 1
            print(f"Barbeiro está atendendo um cliente. Cadeiras ocupadas: {self.cadeiras_ocupadas}/{self.num_cadeiras}")

class Cliente(threading.Thread):
    def __init__(self, id_cliente, barbearia):
        super().__init__()
        self.id_cliente = id_cliente
        self.barbearia = barbearia

    def run(self):
        print(f"Cliente {self.id_cliente} chegou na barbearia.")
        self.barbearia.entrar_na_barbearia(self.id_cliente)

class Barbeiro(threading.Thread):
    def __init__(self, barbearia):
        super().__init__()
        self.barbearia = barbearia

    def run(self):
        while True:
            self.barbearia.atender_cliente()
            print("Barbeiro está cortando cabelo...")
            time.sleep(random.uniform(1, 3))
            print("Barbeiro terminou de cortar cabelo.")

if __name__ == "__main__":
    num_cadeiras = 3
    num_clientes = 10

    barbearia = Barbearia(num_cadeiras)
    barbeiro = Barbeiro(barbearia)
    barbeiro.start()

    clientes = [Cliente(i, barbearia) for i in range(1, num_clientes + 1)]

    for cliente in clientes:
        time.sleep(random.uniform(0.2, 1))
        cliente.start()

    for cliente in clientes:
        cliente.join()
