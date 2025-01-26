import socket

def process_data(data):
    try:
        # Divide a sequência recebida em números e operador
        tokens = data.strip().split()
        numbers = list(map(int, tokens[:-1]))
        operator = tokens[-1]

        if operator == "+":
            result = sum(numbers)
        elif operator == "-":
            result = numbers[0] - sum(numbers[1:])
        elif operator == "*":
            result = 1
            for num in numbers:
                result *= num
        elif operator == "/":
            result = numbers[0]
            for num in numbers[1:]:
                if num == 0:
                    return "Erro: Divisão por zero."
                result /= num
        else:
            return "Erro: Operador desconhecido."
        return f"Resultado: {result}"
    except Exception as e:
        return f"Erro ao processar dados: {e}"

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", 12345))
    server.listen(1)  # Aceita um cliente de cada vez
    print("[SERVIDOR INICIADO] Aguardando conexão...")

    while True:
        client_socket, client_address = server.accept()
        print(f"[CONEXÃO] Cliente conectado: {client_address}")
        try:
            data = ""
            while True:
                chunk = client_socket.recv(1024).decode('utf-8')
                if not chunk:  # EOF detectado
                    break
                data += chunk

            print(f"[DADOS RECEBIDOS] {data}")
            response = process_data(data)
            client_socket.sendall(response.encode('utf-8'))
        except Exception as e:
            print(f"[ERRO] {e}")
        finally:
            print(f"[DESCONECTADO] Cliente {client_address} desconectado.")
            client_socket.close()

if __name__ == "__main__":
    start_server()
