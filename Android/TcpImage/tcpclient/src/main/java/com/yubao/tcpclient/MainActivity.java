package com.yubao.tcpclient;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java4");
    }

    private ImageView imView;
    private String server_ip ="127.0.0.1";
    private boolean isRunning = true;
    private int cnt = 0;
    private TextView tv;
    private String TAG="yubao";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv = findViewById(R.id.textView);

        Log.d("openCV", OpenCVLoader.OPENCV_VERSION);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);

        // Scale down the image if it's too large
//        if (mat.width() > 2048 || mat.height() > 2048) {
//            int newWidth = mat.width() > 2048 ? 2048 : mat.width();
//            int newHeight = mat.height() > 2048 ? 2048 : mat.height();
//
//            Size newSize = new Size(newWidth, newHeight);
//            Imgproc.resize(mat, mat, newSize);
//        }
        Size newSize = new Size(640, 640);
        Imgproc.resize(mat, mat, newSize);

        Bitmap scaledBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        // Convert the Mat back to a Bitmap
        Utils.matToBitmap(mat, scaledBitmap);

        // Set the Bitmap to an ImageView
//        imView = findViewById(R.id.imageView);
//        imView.setImageBitmap(scaledBitmap);

        new Thread(() -> {
            // Connect to the server
            Socket socket = null;
            try {
                socket = new Socket(server_ip, 8089);

                    OutputStream outputStream = socket.getOutputStream();
                    // Convert the Bitmap to a byte array
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    byte[] byteArray = stream.toByteArray();

//                    if(byteArray.length <= 1)
//                    {
//                        Log.e(TAG, "ERROR");
//                        return;
//                    }
//                    Log.i(TAG, "byteArray: " + String.valueOf(byteArray.length));

                    byte[] size = ByteBuffer.allocate(4).putInt(byteArray.length).array();

                    while (isRunning) {
                        outputStream.write(size);
                        // Send the byte array over the TCP connection
                        outputStream.write(byteArray);
    //                    outputStream.flush();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(String.valueOf(cnt++));
                            }
                        });
                        Thread.sleep(100);
                    }
                outputStream.close();
            // Close the connections
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                    isRunning = false;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}