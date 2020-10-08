package masli.prof.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static String name = "user";

    public ScrollView scrollView;
    private Button sendButton;
    public EditText messageEdit;
    private LinearLayout messageContainer;

    private View dialogView;

    Socket client;
    PrintWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // запрещает поворот экрана и пофик что ругается

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        messageContainer = (LinearLayout) findViewById(R.id.message_container);
        sendButton = (Button) findViewById(R.id.send_button);
        messageEdit = (EditText) findViewById(R.id.message_edit);
        dialogView = getLayoutInflater().inflate(R.layout.settings_dialog, null);
        sendButton.setOnClickListener(this);

        //запускаем клиентскую часть
        Client cl = new Client(this);
        cl.start();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:

                new SendData(this).start();

                //printMessage(name, messageEdit.getText().toString());
                messageEdit.setText(""); //при отправке сообщение в строчке удаляется

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                createDialogSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void printMessage(String name, String message) {
        View message_view = getLayoutInflater().inflate(R.layout.item_message, null);
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lParams.bottomMargin = 8;

        TextView nameView = (TextView) message_view.findViewById(R.id.name_text);
        nameView.setText(name);

        TextView messageView = (TextView) message_view.findViewById(R.id.message_text);
        messageView.setText(message);

        messageContainer.addView(message_view, lParams);

        scrollView.post(new Runnable() { // автоматическая прокрутка вниз в отдельном потоке
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void createDialogSettings() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        final EditText userInput = (EditText) dialogView.findViewById(R.id.name_edit);

        dialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        name = userInput.getText().toString();
                        removeParent();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        removeParent();
                    }
                });

        AlertDialog settingsDialog = dialogBuilder.create();
        settingsDialog.show();

    }

    private void removeParent() {
        if (dialogView.getParent() != null) { // убираем родителя иначе ругается
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
    }
}
