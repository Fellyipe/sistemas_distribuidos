import threading
import time
import random

class JantarDosFilosofos:
    def __init__(self):
        self.n_filosofos = 5
        self.hashis = [threading.Semaphore(1) for _ in range(self.n_filosofos)]

    def pegar_hashis(self, id_filosofo):
        primeiro = id_filosofo
        segundo = (id_filosofo + 1) % self.n_filosofos

        if id_filosofo % 2 == 0:
            self.hashis[primeiro].acquire()
            self.hashis[segundo].acquire()
        else:
            self.hashis[segundo].acquire()
            self.hashis[primeiro].acquire()

    def liberar_hashis(self, id_filosofo):
        primeiro = id_filosofo
        segundo = (id_filosofo + 1) % self.n_filosofos

        self.hashis[primeiro].release()
        self.hashis[segundo].release()

class Filosofo(threading.Thread):
    def __init__(self, id_filosofo, jantar):
        super().__init__()
        self.id_filosofo = id_filosofo
        self.jantar = jantar

    def meditar(self):
        print(f"Filósofo {self.id_filosofo} está meditando.")
        time.sleep(random.uniform(1, 3))

    def comer(self):
        print(f"Filósofo {self.id_filosofo} está comendo.")
        time.sleep(random.uniform(1, 2))

    def run(self):
        while True:
            self.meditar()
            print(f"Filósofo {self.id_filosofo} tenta pegar os hashis.")
            self.jantar.pegar_hashis(self.id_filosofo)
            self.comer()
            self.jantar.liberar_hashis(self.id_filosofo)
            print(f"Filósofo {self.id_filosofo} liberou os hashis.")

if __name__ == "__main__":
    jantar = JantarDosFilosofos()
    filosofos = [Filosofo(i, jantar) for i in range(5)]

    for filosofo in filosofos:
        filosofo.start()
