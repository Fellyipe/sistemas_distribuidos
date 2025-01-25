import threading

class ContaBancaria:
    def __init__(self, saldo_inicial):
        self.saldo = saldo_inicial
        self.lock = threading.Lock()

    def transferir(self, valor, destino):
        with self.lock:
            if self.saldo >= valor:
                self.saldo -= valor
                destino.depositar(valor)
                print(f"Transferência de {valor} realizada. Saldo atual: {self.saldo}")

    def depositar(self, valor):
        with self.lock:
            self.saldo += valor
            print(f"Depósito de {valor} realizado. Saldo atual: {self.saldo}")

    def sacar(self, valor):
        with self.lock:
            if self.saldo >= valor:
                self.saldo -= valor
                print(f"Saque de {valor} realizado. Saldo atual: {self.saldo}")

    def creditar_juros(self, taxa):
        with self.lock:
            juros = self.saldo * taxa
            self.saldo += juros
            print(f"Crédito de juros ({juros}). Saldo atual: {self.saldo}")

class AcaoBancaria(threading.Thread):
    def __init__(self, conta, tipo, valor=None, destino=None, taxa=None):
        super().__init__()
        self.conta = conta
        self.tipo = tipo
        self.valor = valor
        self.destino = destino
        self.taxa = taxa

    def run(self):
        if self.tipo == "deposito":
            self.conta.depositar(self.valor)
        elif self.tipo == "saque":
            self.conta.sacar(self.valor)
        elif self.tipo == "transferencia":
            self.conta.transferir(self.valor, self.destino)
        elif self.tipo == "juros":
            self.conta.creditar_juros(self.taxa)

if __name__ == "__main__":
    conta1 = ContaBancaria(1000)
    conta2 = ContaBancaria(500)
    acoes = [
        AcaoBancaria(conta1, "deposito", valor=200),
        AcaoBancaria(conta1, "saque", valor=100),
        AcaoBancaria(conta1, "juros", taxa=0.05),
        AcaoBancaria(conta1, "transferencia", valor=150, destino=conta2)
    ]
    for acao in acoes:
        acao.start()
    for acao in acoes:
        acao.join()