package com.example.golography_portal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    static {
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCv Sukses di Install");
        }else {
            Log.d(TAG, "OpenCv Gagal di Install");
        }
    }

    String stopAskImage;
    AskImages askImages;
    String IP_SERV;
    int vis;
    int white_black=0;
    int background=1;
    int rasniza=20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        TextView ip = (TextView) findViewById(R.id.editText);
        vis=0;
        IP_SERV=ip.getText().toString();
        stopAskImage="0";
        askImages = new AskImages();
        askImages.start();
        ImageView imButton = (ImageView) findViewById(R.id.imageView);
        imButton.setOnClickListener(itemClickListener);
        ImageView imButton7 = (ImageView) findViewById(R.id.imageView7);
        imButton7.setOnClickListener(itemClickListener);
        ImageView imButton6 = (ImageView) findViewById(R.id.imageView6);
        imButton6.setOnClickListener(itemClickListener);
        ImageView imButton8 = (ImageView) findViewById(R.id.imageView8);
        imButton8.setOnClickListener(itemClickListener);
        ImageView imButton9 = (ImageView) findViewById(R.id.imageView9);
        imButton9.setOnClickListener(itemClickListener);
        ImageView imButton10 = (ImageView) findViewById(R.id.imageView10);
        imButton10.setOnClickListener(itemClickListener);
        ImageView imButton11 = (ImageView) findViewById(R.id.imageView11);
        imButton11.setOnClickListener(itemClickListener);
        ImageView imButton12 = (ImageView) findViewById(R.id.imageView12);
        imButton12.setOnClickListener(itemClickListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageView imageView1 = (ImageView) findViewById(R.id.imageView3);
                ImageView imageView2 = (ImageView) findViewById(R.id.imageView4);
                ImageView imageView3 = (ImageView) findViewById(R.id.imageView5);
                imageView1.setRotation(270);
                imageView2.setRotation(90);
                imageView3.setRotation(180);
            }
        }).start();
        Log.d("myLog","OnCreated have done");
    }

    class AskImages extends Thread {

        public void run() {
            super.run();
            try {
                Log.d("010101", "102");
                Log.d("myLog","AskImages is run");
                Socket socket = new Socket(InetAddress.getByName(IP_SERV), 6679); // this port need for request images
                Log.d("myLog","AskImages is connected");
                DataInputStream inStream = new DataInputStream(socket.getInputStream());
                File picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file;
                File file1;
                byte[] buffer;
                int imagesize;
                while (stopAskImage.equals("0")) // пока не конец передачи, делать следующее: (пока открыто Talking Activity)
                {
                    Log.d("myLog", "while stopAskImage == 0");
                    file = new File(picture, "stream_" + ".jpg");
                    FileOutputStream fileStream = new FileOutputStream(file);
                    while (inStream.available() < 4) {
                    }
                    imagesize = inStream.readInt();
                        buffer = new byte[imagesize];
                    while (inStream.available() < imagesize) {
                    }
                    inStream.readFully(buffer);
                    fileStream.write(buffer, 0, imagesize);
                    fileStream.flush();
                    fileStream.close();
                    File file2 = new File(picture, "stream_back" + ".jpg");
                    File file3 = new File(picture, "stream_withou_back" + ".jpg");
                    ProcessingImages(file,file2,file3);
                    try
                    {sleep(0);} catch (InterruptedException e) {}
                    Bitmap avatar0;
                    if (background==0) {
                        avatar0 = BitmapFactory.decodeFile(file3.getAbsolutePath());
                    } else {
                        avatar0 = BitmapFactory.decodeFile(file.getAbsolutePath());
                    }
                    final Bitmap avatar1 =  avatar0;

                    Mat src = new Mat();
                    Utils.bitmapToMat(avatar1,src);
                    Mat dst = new Mat();
                    Imgproc.Sobel(src,dst,-1,1,1,3,3.0,1,1);  // можем менять scale от 0.1 до 10.0 и больше, ksize =1,3,5,7; delta -любое
                    final Bitmap avatar = Bitmap.createBitmap(dst.cols(), dst.rows(),Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(dst, avatar);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoundedBitmapDrawable roundDrawable;
                            if (white_black==1)
                            {  roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar);}
                            else {
                                roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar1);}
                                roundDrawable.setCircular(true);
                                ImageView imageView1 = (ImageView) findViewById(R.id.imageView2);
                                ImageView imageView2 = (ImageView) findViewById(R.id.imageView3);
                                ImageView imageView3 = (ImageView) findViewById(R.id.imageView4);
                                ImageView imageView4 = (ImageView) findViewById(R.id.imageView5);
                                imageView1.setImageDrawable(roundDrawable);
                                imageView2.setImageDrawable(roundDrawable);
                                imageView3.setImageDrawable(roundDrawable);
                                imageView4.setImageDrawable(roundDrawable);
                        }
                    });
                    try { sleep(0);
                        Log.d("myLog","sleep...");
                    } catch (InterruptedException e) {Log.d("myLog","recieve ERROR");}
                    Log.d("myLog","Screenshoot have received");
                }
            }catch (IOException e) {}
        }
    }

    View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageView:
                {
		    stopAskImage = "1";
		    askImages.interrupt();
		    try {
		         askImages.join();
		    } catch (InterruptedException e) {
		    }
	            stopAskImage="0";
	            TextView ip = (TextView) findViewById(R.id.editText);
	            IP_SERV=ip.getText().toString();
	            askImages = new AskImages();
	            askImages.start();
                }
                break;
                case R.id.imageView7:
                {
                    if (vis==1) {
                        EditText editText = (EditText) findViewById(R.id.editText);
                        editText.setVisibility(View.INVISIBLE);
                        TextView textView = (TextView) findViewById(R.id.textView2);
                        textView.setVisibility(View.INVISIBLE);
                        vis=0;
                    }
                    else {
                        EditText editText = (EditText) findViewById(R.id.editText);
                        editText.setVisibility(View.VISIBLE);
                        TextView textView = (TextView) findViewById(R.id.textView2);
                        textView.setVisibility(View.VISIBLE);
                        vis=1;
                    }
                }
                break;
                case R.id.imageView6:
                {
                    ImageView imageView6 = (ImageView) findViewById(R.id.imageView6);
                    if (white_black==0)
                    {
                   white_black=1;
                        imageView6.setImageResource(R.drawable.e5);}
                    else {white_black=0;
                        imageView6.setImageResource(R.drawable.e3);}
                }
                break;
                case R.id.imageView8:
                {   ImageView imageView8 = (ImageView) findViewById(R.id.imageView8);
                    imageView8.setEnabled(false);
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {}
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Rotation();
                        }
                    }).start();
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {}
                    imageView8.setEnabled(true);
                }
                break;
                case R.id.imageView9: // сохранение заднего вида собеседника (после того, как попросил его отойти)
                {
                    stopAskImage = "1";
                    askImages.interrupt();
                    try {
                        askImages.join();
                    } catch (InterruptedException e) {
                    }
                    stopAskImage="0";
                    TextView ip = (TextView) findViewById(R.id.editText);
                    IP_SERV=ip.getText().toString();
                    askImages = new AskImages();
                    File picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File file1 = new File(picture, "stream_" + ".jpg");
                    File file2 = new File(picture, "stream_back" + ".jpg");

		try {
		    FileInputStream fileStream1;
		    fileStream1 = new FileInputStream(file1);
		    byte[] buffer = new byte[(int) file1.length()];//BBB.length
		    int bytesRead = 0;
		    while ((bytesRead = fileStream1.read(buffer)) > 0) {
		    }


		    FileOutputStream fileStream = new FileOutputStream(file2);
		    fileStream.write(buffer, 0, (int) file1.length());
		    fileStream.flush();
		    fileStream.close();
		    fileStream1.close();
		}catch (IOException e) {}
                    askImages.start();
                    Toast toast1 = Toast.makeText(getApplicationContext(),
                            "Backgroung fixed", Toast.LENGTH_SHORT);
                    toast1.show();
                }
                break;
                case R.id.imageView10:
                {
                    ImageView imageView10 = (ImageView) findViewById(R.id.imageView10);
                    if (background==0)
                    {
                        background=1;
                        imageView10.setImageResource(R.drawable.m2);
                      //  Toast toast1 = Toast.makeText(getApplicationContext(),
                      //           "Streeming with background", Toast.LENGTH_SHORT);
                      //  toast1.show();
                        }
                    else {
                        background=0;
                        imageView10.setImageResource(R.drawable.m1);
                     //   Toast toast1 = Toast.makeText(getApplicationContext(),
                     //            "Streeming without background", Toast.LENGTH_SHORT);
                     //    toast1.show();
                    }
                }
                break;
                case R.id.imageView11:
                {
                    if (rasniza!=95)
                    rasniza = rasniza +5 ;
                }
                break;
                case R.id.imageView12:
                {
                    if (rasniza!=5)
                        rasniza = rasniza -5 ;
                }
                break;
                default:
                    break;
            }
        }
    };

 void Rotation()
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView3);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView4);
        ImageView imageView3 = (ImageView) findViewById(R.id.imageView5);
        imageView.animate().rotation(imageView.getRotation() + 90);
        imageView1.animate().rotation(imageView1.getRotation() + 90);
        imageView2.animate().rotation(imageView2.getRotation() + 90);
        imageView3.animate().rotation(imageView3.getRotation() + 90);
    }
    
    synchronized void ProcessingImages (File imgFile1,File imgFile2, File fileResult)
    {
        Bitmap image1 = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
        Bitmap image2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
        Bitmap image3 = Bitmap.createBitmap(image1.getWidth(), image1.getHeight(), Bitmap.Config.ARGB_8888);
        for(int x = 0; x < image1.getWidth(); x++)
            for(int y = 0; y < image1.getHeight(); y++) {
                int argb1 = image1.getPixel(x, y);
                int argb2 = image2.getPixel(x, y);

                int r1 = (argb1 >> 16) & 0xFF;
                int g1 = (argb1 >>  8) & 0xFF;
                int b1 = argb1 & 0xFF;

                int r2 = (argb2 >> 16) & 0xFF;
                int g2 = (argb2 >>  8) & 0xFF;
                int b2 = argb2 & 0xFF;

                int aDiff = Math.abs(255);
                int rDiff = Math.abs(r2 - r1);
                int gDiff = Math.abs(g2 - g1);
                int bDiff = Math.abs(b2 - b1);

                if ((rDiff>rasniza) & (gDiff>rasniza) & (bDiff>rasniza))  // 25 - приемлимое качество
                {
                    rDiff=r1; gDiff=g1; bDiff=b1;
                } else
                {
                    rDiff=0;
                    gDiff=0;
                    bDiff=0;
                }

                int diff =
                        (aDiff<<24) |
                                (rDiff << 16) | (gDiff << 8) | bDiff;
                image3.setPixel(x, y, diff);
            }

        try (FileOutputStream out = new FileOutputStream(fileResult)) {
            image3.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
