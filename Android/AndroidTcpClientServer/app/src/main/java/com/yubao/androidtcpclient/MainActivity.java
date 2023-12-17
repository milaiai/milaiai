package com.yubao.androidtcpclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yubao.androidtcpclient.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'androidtcpclient' library on application startup.
    static {
        System.loadLibrary("androidtcpclient");
    }

    private ActivityMainBinding binding;

    // client example
    private  TextView tvReceivedData;
    private EditText etServerName, etServerPort;
    private Button btnClientConnect;
    private String serverName;
    private int serverPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvReceivedData = findViewById(R.id.tvReceivedData);
        etServerName = findViewById(R.id.etServerName);
        etServerPort = findViewById(R.id.etServerPort);
        btnClientConnect = findViewById(R.id.btnClientConnect);

        // Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
    }

    public void onClictConnect(View view)
    {
        serverName = etServerName.getText().toString();
        serverPort = Integer.valueOf(etServerPort.getText().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(serverName, serverPort);

                    BufferedReader br_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String txtFromServer = br_input.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvReceivedData.setText(txtFromServer);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    /**
     * A native method that is implemented by the 'androidtcpclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}