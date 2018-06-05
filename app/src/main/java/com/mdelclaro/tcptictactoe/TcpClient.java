package com.mdelclaro.tcptictactoe;

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    private String serverMessage;
    private static final String SERVERIP = "192.168.2.108";
    private static final int SERVERPORT = 8888;
    private OnMessageReceived mMessageListener;
    private boolean mRun = false;
    private PrintWriter out;
    BufferedReader in;

    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public void sendMessage(String message) {

        if (out != null && !out.checkError()) {
            out.flush();
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
                InputStreamReader isr = new InputStreamReader(socket.getInputStream());

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(osw), true);

                //receive the message which the server sends back
                in = new BufferedReader(isr);

                Log.e("TCP Client", "C: Done.");

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {

                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}