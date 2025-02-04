import socket

HOST = '127.0.0.1'
PORT = 1234


def conexao_cliente(conn):
    data = b''
    while True:
        chunk = conn.recv(1024)
        if not chunk:
            break
        data += chunk

    integers = data.decode().split()
    integers = [int(num) for num in integers]

    result = sum(integers)

    conn.sendall(str(result).encode())
    conn.close()


def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen(1)

        print(f"Servidor escutando em {HOST}:{PORT}")

        while True:
            conn, addr = s.accept()
            print(f"Conexão estabelecida com {addr}")

            conexao_cliente(conn)
        

main()
