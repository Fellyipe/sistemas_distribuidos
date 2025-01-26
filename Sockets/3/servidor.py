import socket
import threading

# Palavra secreta do jogo
SECRET_WORD = "sistemasdistribuidos"
MAX_ATTEMPTS = 6

def process_guess(guess, word, guessed_word, wrong_guesses):
    if guess in word:
        guessed_word = "".join(
            guess if word[i] == guess else guessed_word[i]
            for i in range(len(word))
        )
        return guessed_word, wrong_guesses, False
    else:
        wrong_guesses += 1
        return guessed_word, wrong_guesses, True

def generate_hangman(wrong_guesses):
    stages = [
        "",
        " O",
        " O\n |",
        " O\n/|",
        " O\n/|\\",
        " O\n/|\\\n/",
        " O\n/|\\\n/ \\",
    ]
    return stages[wrong_guesses]

def handle_client(client_socket):
    guessed_word = "_" * len(SECRET_WORD)
    wrong_guesses = 0
    guessed_letters = set()

    client_socket.sendall(f"Bem-vindo ao jogo da forca! A palavra tem {len(SECRET_WORD)} letras.\n".encode('utf-8'))

    while wrong_guesses < MAX_ATTEMPTS and guessed_word != SECRET_WORD:
        hangman_state = generate_hangman(wrong_guesses)
        client_socket.sendall(
            f"\n{hangman_state}\nPalavra: {guessed_word}\nErros: {wrong_guesses}/{MAX_ATTEMPTS}\n".encode('utf-8')
        )

        try:
            client_socket.sendall(b"Tente uma letra: ")
            guess = client_socket.recv(1024).decode('utf-8').strip().lower()

            if len(guess) != 1 or not guess.isalpha():
                client_socket.sendall(b"Entrada invalida. Tente apenas uma letra.\n")
                continue

            if guess in guessed_letters:
                client_socket.sendall(b"Voce ja tentou essa letra. Tente outra.\n")
                continue

            guessed_letters.add(guess)
            guessed_word, wrong_guesses, incorrect = process_guess(guess, SECRET_WORD, guessed_word, wrong_guesses)

            if incorrect:
                client_socket.sendall(f"A letra '{guess}' não está na palavra.\n".encode('utf-8'))
            else:
                client_socket.sendall(f"A letra '{guess}' está na palavra!\n".encode('utf-8'))
        except Exception as e:
            print(f"[ERRO] {e}")
            break

    if guessed_word == SECRET_WORD:
        client_socket.sendall(f"Parabéns! Você adivinhou a palavra: {SECRET_WORD}\n".encode('utf-8'))
    else:
        client_socket.sendall(f"\nVocê perdeu! A palavra era: {SECRET_WORD}\n".encode('utf-8'))

    client_socket.close()

def start_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", 12345))
    server.listen(1)
    print("[SERVIDOR INICIADO] Aguardando conexões...")

    while True:
        client_socket, client_address = server.accept()
        print(f"[CONEXÃO] Cliente conectado: {client_address}")
        threading.Thread(target=handle_client, args=(client_socket,)).start()

if __name__ == "__main__":
    start_server()
