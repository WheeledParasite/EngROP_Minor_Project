package com.beehiveproductions.huntercar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoystickView joystick = (JoystickView) findViewById(R.id.JoystickView);
        Button redButton = (Button) findViewById(R.id.redbutton);
        Button greenButton = (Button) findViewById(R.id.greenbutton);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= 21) {
            myWebView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }
        myWebView.loadUrl("http://192.168.1.115:8080/stream");

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                sendMessage(String.valueOf(angle)+String.valueOf(strength));
            }
        });
        redButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage("R");
            }
        });
        greenButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage("R");
            }
        });

    }


    private void sendMessage(final String message) {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {

            String stringData;

            @Override
            public void run() {

                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    // IP Address below is the IP address of that Device where server socket is opened.
                    InetAddress serverAddr = InetAddress.getByName("192.168.1.115");
                    DatagramPacket dp;
                    dp = new DatagramPacket(message.getBytes(), message.length(), serverAddr, 61625);
                    ds.send(dp);

                    byte[] lMsg = new byte[1000];
                    dp = new DatagramPacket(lMsg, lMsg.length);
                    ds.receive(dp);
                    stringData = new String(lMsg, 0, dp.getLength());

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        //String s = mTextViewReplyFromServer.getText().toString();
                        //if (stringData.trim().length() != 0)
                        //    mTextViewReplyFromServer.setText(s + "\nFrom Server : " + stringData);

                    }
                });
            }
        });

        thread.start();
        }

}
