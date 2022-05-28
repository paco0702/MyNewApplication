package com.example.mynewapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatMainActivity extends AppCompatActivity {

    private ServerSocket serverSocket;
    private Socket tempClientSocket;
    Thread serverThread = null;
    public static final int SERVER_PORT = 3004;
    private LinearLayout msgList;
    private Handler handler;
    private int greenColor;
    private EditText edMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        greenColor = ContextCompat.getColor(this, R.color.green);
        handler = new Handler();
        msgList = findViewById(R.id.msgList);
        edMessage = findViewById(R.id.editTextMsg);
    }

    public TextView textView(String message, int color){
        if(null == message || message.trim().isEmpty()){
            message = "<Empty Message>";
        }
        TextView tv = new TextView ( this);
        tv.setTextColor(color);
         //tv.setText(message +"[" + getTime()+"]");
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
    }

    public void showMessage(final String message, final int color){
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
    }

    public void onClick(View view){
        if(view.getId()==R.id.chat_main_start_server){
            msgList.removeAllViews();
            showMessage("Server Started.", Color.BLACK);
            this.serverThread = new Thread(new ServerThread());
            this.serverThread.start();
            return;
        }
        if(view.getId() == R.id.chat_main_sendMsg_btn){
            String msg = edMessage.getText().toString().trim();
            showMessage("Server: "+ msg, Color.BLUE);
            sendMessage(msg);
        }
    }

    private void sendMessage(final String message){
        
    }

    class ServerThread implements  Runnable {

        @Override
        public void run() {
            Socket socket;
            try{
                serverSocket = new ServerSocket(SERVER_PORT);
                findViewById(R.id.chat_main_start_server).setVisibility(View.GONE);
            }catch(IOException e){
                e.printStackTrace();
                showMessage("Error starting server: " + e.getMessage(), Color.RED);

            }
            if(null != serverSocket){
                while(!Thread.currentThread().isInterrupted()){
                    try{
                        socket = serverSocket.accept();
                        CommunicationThread commThread = new CommunicationThread(socket);

                    }catch(IOException e){
                        e.printStackTrace();
                        showMessage("Error Communicating to Client:" +e.getMessage(), Color.RED);
                    }
                }
            }
        }
    }

    class CommunicationThread implements Runnable {
        private  Socket clientSocket;
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket){
            this.clientSocket = clientSocket;
            tempClientSocket = clientSocket;
            try {
                this.input = new BufferedReader (new InputStreamReader(this.clientSocket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
                showMessage("Error Connecting to Client!" , Color.RED);

            }
            showMessage("Connect to Client!", greenColor);
        }
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try{
                    String read = input.readLine();
                    if(null == read || "Disconnect".contentEquals(read)){
                        Thread.interrupted();
                        read = "Client Disconnected";
                        showMessage("Client: " +read, greenColor);
                        break;
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    protected void onDestroy(){
        super.onDestroy();
        if (null != serverThread) {
            sendMessage("Disconnect");
            serverThread.interrupt();
            serverThread = null;
        }
    }



}