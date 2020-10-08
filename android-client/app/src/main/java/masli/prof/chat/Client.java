package masli.prof.chat;

import android.util.Log;
import android.widget.ScrollView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

    private MainActivity main;

    public Client(MainActivity main) {
        this.main = main;
    }

    @Override
    public void run() {
        try {
            main.client = new Socket("46.181.131.231", 1337);
            main.writer = new PrintWriter(main.client.getOutputStream());

            DataInputStream dis = new DataInputStream(main.client.getInputStream());

            while (true) {
                final String message = dis.readLine(); // общем надо после каждого сообщения \n ставить иначе никак
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] str = message.split(":");
                        main.printMessage(str[0], str[1].substring(1));
                    }
                });

                main.scrollView.post(new Runnable() { // автоматическая прокрутка вниз в отдельном потоке
                    @Override
                    public void run() {
                        main.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        } catch (IOException e) {
            Log.e("LOG", "error lol");
        }
    }
}
