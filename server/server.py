from Socket import Socket
import threading

class Server(Socket):
    def __init__(self):
        super(Server, self).__init__()

        print("server listening")

        self.users = []

    def set_up(self):
        self.bind(("192.168.0.109", 1337))
        self.listen(0)
        self.accept_sockets()

    def send_data(self, data):
        for user in self.users:
            try:
                user.send(data)
            except ConnectionResetError:
                self.users.pop(self.users.index(user))
                pass

    def listen_socket(self, listened_socket=None):
        countForDel = 0
        while True:
            data = listened_socket.recv(2048)
            if data.decode("utf-8")[0:-2] == '':
                countForDel += 1
            if countForDel > 5:
                print("deleting user: Antispam")
                self.users.pop(self.users.index(listened_socket))
                raise ConnectionResetError
                
            print(f"User sent {data}")
            self.send_data(data)

    def accept_sockets(self):
        while True:
            user_socket, address = self.accept()
            print(f"User <{address[0]}> connected!")
            self.users.append(user_socket)  # добавляется юзер
            print(len(self.users))

            listen_accepted_user = threading.Thread(
                target=self.listen_socket,
                args=(user_socket,))

            listen_accepted_user.start()


if __name__ == '__main__':
    server = Server()
    server.set_up()
