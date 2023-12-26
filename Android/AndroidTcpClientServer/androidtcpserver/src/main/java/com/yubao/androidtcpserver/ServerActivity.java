package com.yubao.androidtcpserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    private TextView tvServerName, tvServerPort, tvStatus;
    private String serverIP = "127.0.0.1";
    private int serverPort = 8899;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        tvServerName = findViewById(R.id.tvServerName);
        tvServerPort = findViewById(R.id.tvServerPort);
        tvStatus = findViewById(R.id.tvStatus);

        tvServerName.setText(serverIP );
        tvServerPort.setText(String.valueOf(serverPort));
    }

    private ServerThread serverThread;
    public void onCLiickServer(View view)
    {
        serverThread = new ServerThread();
        serverThread.StartServer();
    }

    public void onClickStopServer(View view){
        serverThread.StopServer();
    }

    class ServerThread extends Thread implements Runnable{
        private boolean serverRunning;
        private ServerSocket serverSocket;
        private int count =0;

        public void StartServer()
        {
            serverRunning = true;
            start();
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(serverPort);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvStatus.setText("Waiting for clients");

                    }
                });
                while(serverRunning)
                {
                    Socket socket = serverSocket.accept();
                    count++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText("Connect to: " + socket.getInetAddress() + " : " +socket.getLocalPort());
                        }
                    });
                    PrintWriter output_server = new PrintWriter(socket.getOutputStream());
                    output_server.write("Welcome to Server:" + count);
                    output_server.flush();
                    socket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void StopServer(){
            serverRunning = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvStatus.setText("Server Stopped");
                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }
    }// class ServerThread

}