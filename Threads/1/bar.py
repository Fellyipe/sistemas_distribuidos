import threading
import random
import time
from queue import Queue

class Cliente(threading.Thread):
    def __init__(self, id_cliente, capacidade_garcom):
        super().__init__()
        self.id_cliente = id_cliente
        self.capacidade_garcom = capacidade_garcom
        self.consumindo = threading.Event()

    def fazer_pedido(self, garcom):
        garcom.fila_pedidos.put(self.id_cliente)
        print(f"Cliente {self.id_cliente} fez um pedido ao garçom {garcom.id_garcom}.")

    def consumir_pedido(self):
        tempo_consumo = random.randint(1, 3)
        print(f"Cliente {self.id_cliente} está consumindo o pedido por {tempo_consumo} segundos.")
        time.sleep(tempo_consumo)

    def run(self):
        while True:
            self.consumindo.wait()
            self.consumir_pedido()
            self.consumindo.clear()

class Garcom(threading.Thread):
    def __init__(self, id_garcom, capacidade, bartender):
        super().__init__()
        self.id_garcom = id_garcom
        self.capacidade = capacidade
        self.fila_pedidos = Queue()
        self.bartender = bartender

    def atender_clientes(self):
        while not self.fila_pedidos.empty():
            cliente_id = self.fila_pedidos.get()
            print(f"Garçom {self.id_garcom} está atendendo o cliente {cliente_id}.")
            self.fila_pedidos.task_done()
            time.sleep(random.uniform(0.5, 1.5))

    def run(self):
        while True:
            if self.fila_pedidos.qsize() >= self.capacidade or self.bartender.acabou_rodada.is_set():
                print(f"Garçom {self.id_garcom} indo para a copa fazer o pedido de {self.fila_pedidos.qsize()} clientes.")
                self.bartender.fazer_pedido(self.id_garcom, self.fila_pedidos.qsize())
                self.fila_pedidos.join()
                self.atender_clientes()
                print(f"Garçom {self.id_garcom} terminou de atender seus clientes.")

class Bartender:
    def __init__(self):
        self.acabou_rodada = threading.Event()

    def fazer_pedido(self, id_garcom, qtd_pedidos):
        print(f"Bartender recebeu pedido do garçom {id_garcom} para {qtd_pedidos} clientes.")
        time.sleep(random.uniform(0.5, 2))
        print(f"Bartender entregou os pedidos do garçom {id_garcom}.")

class Bar:
    def __init__(self, qtd_clientes, qtd_garcons, capacidade_garcom, qtd_rodadas):
        self.qtd_clientes = qtd_clientes
        self.qtd_garcons = qtd_garcons
        self.capacidade_garcom = capacidade_garcom
        self.qtd_rodadas = qtd_rodadas
        self.bartender = Bartender()
        self.garcons = [Garcom(i, capacidade_garcom, self.bartender) for i in range(qtd_garcons)]
        self.clientes = [Cliente(i, capacidade_garcom) for i in range(qtd_clientes)]

    def iniciar(self):
        for garcom in self.garcons:
            garcom.start()
        for cliente in self.clientes:
            cliente.start()

        for rodada in range(1, self.qtd_rodadas + 1):
            print(f"\n--- Rodada {rodada} ---")
            clientes_ativos = random.sample(self.clientes, random.randint(1, self.qtd_clientes))
            for cliente in clientes_ativos:
                garcom = random.choice(self.garcons)
                cliente.fazer_pedido(garcom)

            time.sleep(5)
            self.bartender.acabou_rodada.set()
            time.sleep(2)
            self.bartender.acabou_rodada.clear()

        print("\n--- Bar fechando ---")

if __name__ == "__main__":
    qtd_clientes = 10
    qtd_garcons = 3
    capacidade_garcom = 3
    qtd_rodadas = 5

    bar = Bar(qtd_clientes, qtd_garcons, capacidade_garcom, qtd_rodadas)
    bar.iniciar()
