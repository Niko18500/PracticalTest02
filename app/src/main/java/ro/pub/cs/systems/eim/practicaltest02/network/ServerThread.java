package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;

    public ServerThread(Integer serverPort) {
        try {
            serverSocket = new ServerSocket(serverPort, 50, InetAddress.getByName("0.0.0.0"));
            Log.v(Constants.TAG, "[SERVER THREAD] Created server socket on address " + serverSocket.getInetAddress() + " and port " + serverSocket.getLocalPort());
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public void startThread() {
        start();
        Log.v(Constants.TAG, "[SERVER THREAD] Server started on port " + serverSocket.getLocalPort());
    }

    public void stopThread() {
        // when stopping, interrupt the current thread and close the server socket
        // called in onDestroy() method from the PracticalTest02MainActivity class
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            // when running, continuously check if the current thread is interrupted
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");

                // accept() method blocks the execution until a client connects to the server
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());

                // create a new CommunicationThread object for each client that connects to the server
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
