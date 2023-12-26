package com.yubao.useopencv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.yubao.useopencv.databinding.ActivityMainBinding;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'useopencv' library on application startup.
    static {
        System.loadLibrary("useopencv");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("openCV version", OpenCVLoader.OPENCV_VERSION);

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'useopencv' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}