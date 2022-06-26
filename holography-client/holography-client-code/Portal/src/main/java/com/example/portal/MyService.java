package com.example.portal;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class MyService extends Service {
    private static final String EXTRA_RESULT_CODE = "result-code";
    private static final String EXTRA_DATA = "data";
    public int i=1;
    public int j=1;
    WindowManager wm;
    Display display;
    DisplayMetrics metrics;
    Point size;
    int mWidth;
    int mHeight;
    int mDensity;
    Handler handler;
    ImageReader mImageReader;
    MediaProjectionManager projectionManager;
    MediaProjection mProjection;
    int resultCode;
    Intent data;
    int stopThread=0;
    TakeScreenshoot takeScreenshoot;
    private VirtualDisplay vdisplay;
    String stopSendImage;
    GiveImages giveImages;
    metod3 metod31;
    String GI;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) MyService.this.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        size = new Point();
        display.getRealSize(size);
        mWidth = size.x;
        mHeight = size.y;
        mDensity = metrics.densityDpi;
    }

    @Override
    public void onDestroy()
    {
        StopScreenStream stopScreenStream = new StopScreenStream();
        stopScreenStream.start();
    }

    public MyService() {}
    static Intent newIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, MyService.class);
        intent.putExtra(EXTRA_RESULT_CODE, resultCode);
        intent.putExtra(EXTRA_DATA, data);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId) {
        stopSendImage="0";
        GI="0";
        stopThread=0;
        resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0);
        data = intent.getParcelableExtra(EXTRA_DATA);
        Log.d("myLog","OnStartCommand");
        projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Log.d("myLog","OnStartCommand0");
        mProjection = projectionManager.getMediaProjection(resultCode, data);
        Log.d("myLog","OnStartCommand1");
        handler = new Handler();
        StartScreenStream();
        return super.onStartCommand(intent, flags, startId);
    }

    protected ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * metrics.widthPixels;
            Bitmap bmp = Bitmap.createBitmap(metrics.widthPixels + (int) ((float) rowPadding / (float) pixelStride), metrics.heightPixels, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buffer);
            image.close();
            reader.close();
            Bitmap realSizeBitmap = Bitmap.createBitmap(bmp, 0, 0, metrics.widthPixels/8, bmp.getHeight()/8);
            File picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(picture, "screen_any_where" + String.valueOf(i) + ".jpg");
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                realSizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                Log.d("myLog", "Screenshoot NOT have done");
            }
            Log.d("myLog", String.valueOf(i) +" Screenshoot have done");
            if (i == 30) {
                i = 1;
            } else {
                i++;
            }
            bmp.recycle();
            realSizeBitmap.recycle();
            vdisplay.release();
        }
    };

    void StartScreenStream() { takeScreenshoot =  new TakeScreenshoot(); takeScreenshoot.start();}
    class metod3 extends Thread {
        public void run() {
            super.run();
            Log.d("myLog","TakeScreenshoot is run");
            mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
            vdisplay =  mProjection.createVirtualDisplay("screen-mirror", mWidth/8, mHeight/8, mDensity/8, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, handler);
            mImageReader.setOnImageAvailableListener(onImageAvailableListener,handler);
        }
    }

    class TakeScreenshoot extends Thread {
        public void run() {
            super.run();

            // Начальная установка 30 изображений для дальнейшей нормальной работы
            for (int a=1;a!=30;a++) {
                metod31 = new metod3();
                metod31.start();
                try {
                    sleep(150); // чтобы сервер мог перехватывать управление
                    Log.d("myLog", "sleep...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            giveImages = new GiveImages();
            giveImages.start();
            try { sleep(350);
                Log.d("myLog","sleep...");
            } catch (InterruptedException e) {Log.d("myLog","stopTakeScreenshoot ERROR");}
            Log.d("myLog","TakeScreenshoot is run");
            while (stopThread!=1)
            {
                Log.d("myLog","stopTakeScreenshoot==012");
                metod31 = new metod3();
                metod31.start();
                metod31.interrupt();
                try { metod31.join();} catch (InterruptedException e) {}
                try { 
                    sleep(150);
                    Log.d("myLog","sleep...");
                } catch (InterruptedException e) {Log.d("myLog","stopTakeScreenshoot ERROR");}
            }
        }
    }

    class StopScreenStream extends Thread {
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    stopThread=1;
                    stopSendImage="1";
                    metod31.interrupt();
                    try{
                        metod31.join();
                    } catch (InterruptedException e) {}
                    giveImages.interrupt();
                    try{
                        giveImages.join();
                    } catch (InterruptedException e) {}
                    Log.d("myLog","Thread's stop!");
                }
            }).start();
        }
    }

    class GiveImages extends Thread {
        public void run() {
            super.run();
            try {
                final  File picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                ServerSocket ss1 = new ServerSocket(6679);
                while (GI.equals("0")) {
                    Log.d("myLog","GiveImage is run");
                    final Socket socket1 = ss1.accept();
                    Log.d("myLog","Client is connected");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                File file;
                                FileInputStream fileStream;
                                byte[] buffer;
                                int num_img=0;
                                int bytesRead;
                                String sss = socket1.getInetAddress().toString();
                                DataOutputStream outputStream = new DataOutputStream(socket1.getOutputStream());
                                int i1=1;
                                while (stopSendImage.equals("0")) {
                                   if (i1!=i) {
                                        if (i > 2) { //2
                                            j = i - 2; //2
                                        } else {
                                            if (i == 1) {
                                                j = 29;
                                            } else {
                                                j = 30;
                                            }
                                        }
                                        i1=i;
                                        file = new File(picture, "screen_any_where" + String.valueOf(j) + ".jpg");
                                        fileStream = new FileInputStream(file);
                                        buffer = new byte[(int) file.length()];
                                        bytesRead = 0;
                                        outputStream.writeInt((int) file.length());
                                        while ((bytesRead = fileStream.read(buffer)) > 0) {
                                            outputStream.write(buffer, 0, bytesRead);
                                        }
                                        fileStream.close();
                                        Log.d("myLog", "file " + String.valueOf(j) + " send");
                                   }
                                }
                            } catch (IOException e) {}
                        }
                    }).start();
                }
            } catch (IOException e) {}
        }
    }



}
