package com.beehiveproductions.huntercar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity {

    final private String RTSPStream = "http://192.168.2.9:8080/stream";  // Raspberry Pi RTSP Server Address
    float forspeed = 0f;
    float rotspeed = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JoystickView joystick = findViewById(R.id.JoystickView);
        Button redButton = findViewById(R.id.redbutton);
        Button greenButton = findViewById(R.id.greenbutton);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        WebView myWebView = findViewById(R.id.webview);
        final TextView myTextView = findViewById(R.id.textView);
        // setup scrollbars for TextView
        myTextView.setMovementMethod(new ScrollingMovementMethod());

        // This is the workaround needed to get Android to display non HTTPS content!!!
        if (Build.VERSION.SDK_INT >= 21) {
            myWebView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }
        myWebView.loadUrl(RTSPStream);

        joystick.setAutoReCenterButton(true);
        // Joystick Handler
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                // do whatever you want
                //sendMessage(String.valueOf(angle)+String.valueOf(strength), myTextView);
                //Log.d("Joystick","A: " + angle + " S: " + strength);

                // Ok, the joystick returns an angle and strength, we need to do a little bit of
                // trigonometry to figure this out!
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.HALF_UP);
                forspeed = strength / 100f;
                rotspeed = (float)(Math.cos(Math.toRadians(angle))*strength/100f);
                sendMessage(df.format(forspeed) + ":" + df.format(rotspeed),myTextView);
                Log.d("Hunter","FS: " + df.format(forspeed) + " RS: " + df.format(rotspeed));
            }
        },150);  // wait 150ms to send next data


        // Button Handlers
        redButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send shutdown raspberry pi
                sendMessage("SHUTDOWN", myTextView);
            }
        });
        greenButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("STOP", myTextView);
            }
        });

    }

    // send one UDP message using a Java Thread
    private void sendMessage(final String message, final TextView mTextViewReplyFromServer) {
        final Handler handler = new Handler();
        final String UDPServer = "192.168.2.9";  // Raspberry Pi UDP Server
        Thread thread = new Thread(new Runnable() {

            String stringData;

            @Override
            public void run() {

                DatagramSocket ds = null;
                try {
                    ds = new DatagramSocket();
                    // IP Address below is the IP address of that Device where server socket is opened.
                    InetAddress serverAddr = InetAddress.getByName(UDPServer);
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

                // get reply from RC Car udp server and display on screen
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String s = mTextViewReplyFromServer.getText().toString();
                        if (stringData.trim().length() != 0)
                            mTextViewReplyFromServer.setText(s + "\nFrom Server : " + stringData);

                    }
                });
            }
        });

        thread.start();
        }

}
