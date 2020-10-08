package masli.prof.chat;

class SendData extends Thread {
    MainActivity main;

    public SendData (MainActivity main) {
        this.main = main;
    }

    public void run() {
        main.writer.println(MainActivity.name + ": " + main.messageEdit.getText().toString());
        main.writer.flush();
    }
}
