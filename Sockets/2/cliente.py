import socket

HOST = '127.0.0.1'
PORT = 1234


def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((HOST, PORT))

        numbers = input("Digite a sequência de inteiros separados por espaço: ")
        s.sendall(numbers.encode())

        s.shutdown(socket.SHUT_WR)  

        result = b''
        while True:
            chunk = s.recv(1024)
            if not chunk:
                break
            result += chunk

        print("Soma dos inteiros:", result.decode())


main()
