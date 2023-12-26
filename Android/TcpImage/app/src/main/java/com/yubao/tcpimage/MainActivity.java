package com.yubao.tcpimage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.yubao.tcpimage.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'tcpimage' library on application startup.
    static {
        System.loadLibrary("tcpimage");
        System.loadLibrary("opencv_java4");
    }

    private ActivityMainBinding binding;
    private ImageView imView;
    private String TAG="yubao";
    private boolean isRunning = true;
    private int cnt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("openCV", OpenCVLoader.OPENCV_VERSION);

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
//
//        Mat mat = new Mat();
//        Utils.bitmapToMat(bitmap, mat);
//
//        // Scale down the image if it's too large
//        if (mat.width() > 2048 || mat.height() > 2048) {
//            int newWidth = mat.width() > 2048 ? 2048 : mat.width();
//            int newHeight = mat.height() > 2048 ? 2048 : mat.height();
//
//            Size newSize = new Size(newWidth, newHeight);
//            Imgproc.resize(mat, mat, newSize);
//        }
//        Bitmap scaledBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
//        // Convert the Mat back to a Bitmap
//        Utils.matToBitmap(mat, scaledBitmap);
        imView = findViewById(R.id.imageView);
        TextView tv = binding.sampleText;

        new Thread(() -> {
            try {
                // Create a ServerSocket and wait for a client to connect
                ServerSocket serverSocket = new ServerSocket(8089);
                Socket socket = serverSocket.accept();

                // Receive the byte array from the client
                InputStream inputStream = socket.getInputStream();

                Log.i(TAG, "Connected");
                while (isRunning) {
                    byte[] sizeBytes = new byte[4];
                    inputStream.read(sizeBytes);
                    int size = ByteBuffer.wrap(sizeBytes).getInt();
                    Log.i(TAG,"-----------------");
                    Log.i(TAG, "size: " + String.valueOf(size));

//                    if(size <= 1)
//                    {
//                        Log.i(TAG, "size <= 1 " + String.valueOf(size));
//                        continue;
//                    }

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead = 0;
                    int total_received =0;
                    byte[] data = new byte[640*640];
                    while ( ((nRead = inputStream.read(data, 0, data.length)) != -1 ) && (total_received <= size) ) {
                        buffer.write(data, 0, nRead);
                        total_received += nRead;
//                        Log.i(TAG, "total_received: " + String.valueOf(total_received));
                    }

                    Log.i(TAG, "Buffer size: "+ String.valueOf(buffer.size()));
                    if (buffer.size() > 0) {
                        // Convert the byte array back to a Mat
                        MatOfByte matOfByte = new MatOfByte(buffer.toByteArray());
                        Mat mat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED);

                        // Convert the Mat to a Bitmap
                        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(mat, bitmap);

                        buffer.close();
                        // Update the ImageView on the main UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imView.setImageBitmap(bitmap);
                                tv.setText(String.valueOf(cnt++));
                            }
                        });
                    }
//                    buffer.close();
                }// while

                // Close the connections
                inputStream.close();
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


        // Example of a call to a native method
//        tv.setText(stringFromJNI());


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

    /**
     * A native method that is implemented by the 'tcpimage' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}