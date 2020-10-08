import socket
import threading
from tkinter import *

class Socket(socket.socket):

    def __init__(self):
        super(Socket, self).__init__(socket.AF_INET, socket.SOCK_STREAM)

    def send_data(self, data):
        raise NotImplementedError()

    def listen_socket(self, listened_socket=None):
        raise NotImplementedError()

    def set_up(self):
        raise NotImplementedError()


class Client(Socket):

    def __init__(self, chat):
        super(Client, self).__init__()
        self.chat = chat

    def set_up(self):
        self.connect(("46.181.131.231", 1337))

        listen_thread = threading.Thread(target=self.listen_socket)
        listen_thread.start()

    def listen_socket(self, listened_socket=None):
        while True:
            data = self.recv(2048)
            self.chat.write_simple_message(data.decode("utf-8"))


    def send_data(self, data):
        self.send(data.encode("utf-8"))

class Chat:
    def __init__(self, root):
        self.client = Client(self)
        self.root = root

        self.client_start_thread = threading.Thread(target=self.set_up_client)
        self.client_start_thread.start()

        # верхний фрейм
        self.up_frame = Frame(self.root)  # фрейм в котором содерджится поле для сообщений

        self.messages_text = Text(self.up_frame, bg="darkgray", state=DISABLED)
        self.messages_text.pack(side=LEFT)

        self.scroll = Scrollbar(self.up_frame, command=self.messages_text.yview)
        self.scroll.pack(side=RIGHT, fill=Y)

        self.messages_text.config(yscrollcommand=self.scroll.set)

        self.up_frame.pack(fill=X)
        # конец верхнего фрейма

        # фрейм в котором содерджится 2 строчки и кнопка
        self.bottom_frame = Frame(self.root)

        self.entry_message = Entry(self.bottom_frame)
        self.entry_message.pack(fill=X, side=TOP)

        self.root.bind('<Return>', self.pressed_send)  # биндится на ентер

        self.entry_name = Entry(self.bottom_frame)
        self.entry_name.pack(fill=X, side=LEFT)

        self.bottom_frame.pack(side=BOTTOM, fill=X)
        # фрейм пакуется

    def set_up_client(self):
        self.client.set_up()

    def pressed_send(self, event):
        message = self.entry_message.get()
        name = self.entry_name.get()
        # self.write_message(name, message)
        self.entry_message.delete(0, "end")

        send_thread = threading.Thread(target=self.client.send_data, args=(name+": "+message + "\n",))
        send_thread.start()


    def write_message(self, name, message):
        self.messages_text.configure(state=NORMAL)
        self.messages_text.insert(END, name + ": " + message)
        self.messages_text.configure(state=DISABLED)
        self.messages_text.yview_scroll(1, "units")

    def write_simple_message(self, message):
        self.messages_text.configure(state=NORMAL)
        self.messages_text.insert(END, message)
        self.messages_text.configure(state=DISABLED)
        self.messages_text.yview_scroll(1, "units")


root = Tk()
root.title("LolChat")
root.resizable(False, False)

chat = Chat(root)

root.mainloop()
