package com.example.portal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static com.example.portal.R.*;

public class MainActivity extends AppCompatActivity {
    String ipAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        ipAddress = Formatter.formatIpAddress(ip);
        if (ipAddress.equals("0.0.0.0")) ipAddress="192.168.43.1";
        TextView ip_text = (TextView) findViewById(id.textView);
        ip_text.setText(ipAddress);
        ImageView imButton = (ImageView) findViewById(id.imageView);
        imButton.setOnClickListener(itemClickListener);
        ImageView imButton2 = (ImageView) findViewById(id.imageView2);
        imButton2.setOnClickListener(itemClickListener);
        Log.d("myLog", "OnCreate has worked");
        imButton2.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1)
        {
            startService(MyService.newIntent(MainActivity.this, resultCode, data));
        }
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case id.imageView: {
                    MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                    startActivityForResult(projectionManager.createScreenCaptureIntent(), 1 /*YOUR_REQUEST_CODE*/);
                    TextView ip_text = (TextView) findViewById(id.textView);
                    ip_text.setText(ipAddress);
                    ImageView imButton = (ImageView) findViewById(id.imageView);
                    ImageView imButton2 = (ImageView) findViewById(id.imageView2);
                    imButton.setEnabled(false);
                    imButton2.setEnabled(false);
                    imButton.setImageResource(drawable.ww4);
                    imButton2.setImageResource(drawable.ww2);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    TextView text = (TextView) findViewById(id.textView4);
                    text.setText("YES");
                    imButton2.setEnabled(true);
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Screen stream is running!", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
                case id.imageView2: {
                    stopService(new Intent(MainActivity.this, MyService.class));
                    ImageView imButton = (ImageView) findViewById(id.imageView);
                    imButton.setEnabled(true);
                    ImageView imButton2 = (ImageView) findViewById(id.imageView2);
                    imButton2.setEnabled(false);
                    imButton.setImageResource(drawable.ww1);
                    imButton2.setImageResource(drawable.ww3);
                    TextView text = (TextView) findViewById(id.textView4);
                    text.setText("NO");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Screen stream is stoped!", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
                default:
                    break;
            }
        }
    };
}
