import threading
import random
import time

class Produtor(threading.Thread):
    def __init__(self, buffer, id_produtor):
        super().__init__()
        self.buffer = buffer
        self.id_produtor = id_produtor

    def run(self):
        for _ in range(5):
            item = f"Item-{self.id_produtor}-{random.randint(1, 100)}"
            self.buffer.produzir(item)
            time.sleep(random.uniform(0.1, 1))

class Consumidor(threading.Thread):
    def __init__(self, buffer, id_consumidor):
        super().__init__()
        self.buffer = buffer
        self.id_consumidor = id_consumidor

    def run(self):
        for _ in range(5):
            self.buffer.consumir()
            time.sleep(random.uniform(0.2, 1.5))

class BufferMonitor:
    def __init__(self, tamanho):
        self.buffer = []
        self.tamanho = tamanho
        self.cond = threading.Condition()

    def produzir(self, item):
        with self.cond:
            while len(self.buffer) >= self.tamanho:
                self.cond.wait()
            self.buffer.append(item)
            print(f"[Monitor] Produzido: {item}. Buffer: {self.buffer}")
            self.cond.notifyAll()

    def consumir(self):
        with self.cond:
            while not self.buffer:
                self.cond.wait()
            item = self.buffer.pop(0)
            print(f"[Monitor] Consumido: {item}. Buffer: {self.buffer}")
            self.cond.notifyAll()
            return item

if __name__ == "__main__":
    buffer_monitor = BufferMonitor(5)
    produtores_monitor = [Produtor(buffer_monitor, i) for i in range(2)]
    consumidores_monitor = [Consumidor(buffer_monitor, i) for i in range(2)]
    for produtor in produtores_monitor:
        produtor.start()
    for consumidor in consumidores_monitor:
        consumidor.start()
    for produtor in produtores_monitor:
        produtor.join()
    for consumidor in consumidores_monitor:
        consumidor.join()