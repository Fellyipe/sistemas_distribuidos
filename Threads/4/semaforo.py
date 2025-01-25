import threading
import random
import time

class Buffer:
    def __init__(self, tamanho):
        self.buffer = []
        self.tamanho = tamanho
        self.mutex = threading.Semaphore(1)
        self.itens = threading.Semaphore(0)
        self.espacos = threading.Semaphore(tamanho)

    def produzir(self, item):
        self.espacos.acquire()
        self.mutex.acquire()
        self.buffer.append(item)
        print(f"Produzido: {item}. Buffer: {self.buffer}")
        self.mutex.release()
        self.itens.release()

    def consumir(self):
        self.itens.acquire()
        self.mutex.acquire()
        item = self.buffer.pop(0)
        print(f"Consumido: {item}. Buffer: {self.buffer}")
        self.mutex.release()
        self.espacos.release()
        return item

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

if __name__ == "__main__":
    buffer = Buffer(5)
    produtores = [Produtor(buffer, i) for i in range(2)]
    consumidores = [Consumidor(buffer, i) for i in range(2)]
    for produtor in produtores:
        produtor.start()
    for consumidor in consumidores:
        consumidor.start()
    for produtor in produtores:
        produtor.join()
    for consumidor in consumidores:
        consumidor.join()