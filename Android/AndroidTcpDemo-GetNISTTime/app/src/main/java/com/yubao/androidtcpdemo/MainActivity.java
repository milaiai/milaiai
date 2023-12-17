package com.yubao.androidtcpdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yubao.androidtcpdemo.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'androidtcpdemo' library on application startup.
    static {
        System.loadLibrary("androidtcpdemo");
    }

    private ActivityMainBinding binding;

    // to reques nist server time
    private TextView tvTime;
    private String serverName_nist = "time.nist.gov";
    private int serverPort_nist = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
//        TextView tv = binding.sampleText;
//        tv.setText(stringFromJNI());
    }



    public void onClickGetTime(View view)
    {
        tvTime = findViewById(R.id.tvTime);
        NistTimeClient runable = new NistTimeClient(serverName_nist, serverPort_nist);
        new Thread(runable).start();
    }

    private class NistTimeClient implements Runnable{

        private String serverName_nist;
        private int serverPort_nist;

        public NistTimeClient(String serverName, int serverPort) {
            this.serverName_nist = serverName;
            this.serverPort_nist = serverPort;
        }

        @Override
        public void run() {
            try {
                Socket socket = new Socket(serverName_nist, serverPort_nist);
                BufferedReader br = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                br.readLine();
                String recTime = br.readLine().substring(6, 23);
                socket.close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTime.setText(recTime);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
    /**
     * A native method that is implemented by the 'androidtcpdemo' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}