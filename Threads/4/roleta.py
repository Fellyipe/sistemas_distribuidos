import threading
import random
import time

class Roletas:
    def __init__(self, num_roletas):
        self.num_roletas = num_roletas
        self.roletas = [threading.Semaphore(1) for _ in range(num_roletas)]

    def usar_roleta(self, id_roleta, id_usuario):
        self.roletas[id_roleta].acquire()
        print(f"Usuário {id_usuario} está usando a roleta {id_roleta}.")
        time.sleep(random.uniform(0.5, 2))
        print(f"Usuário {id_usuario} liberou a roleta {id_roleta}.")
        self.roletas[id_roleta].release()

class Usuario(threading.Thread):
    def __init__(self, id_usuario, roletas):
        super().__init__()
        self.id_usuario = id_usuario
        self.roletas = roletas

    def run(self):
        id_roleta = random.randint(0, self.roletas.num_roletas - 1)
        self.roletas.usar_roleta(id_roleta, self.id_usuario)

if __name__ == "__main__":
    # Teste das Roletas
    roletas = Roletas(3)
    usuarios = [Usuario(i, roletas) for i in range(10)]
    for usuario in usuarios:
        usuario.start()
    for usuario in usuarios:
        usuario.join()