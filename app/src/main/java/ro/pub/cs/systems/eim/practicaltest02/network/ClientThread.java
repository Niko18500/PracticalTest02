package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {
    private final String address;
    private final int port;
    private final Integer number1;
    private final Integer number2;
    private final String operationType;
    private final TextView resultTextView;

    private Socket socket;

    public ClientThread(String address, int port, Integer number1, Integer number2, String operationType, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.number1 = number1;
        this.number2 = number2;
        this.operationType = operationType;
        this.resultTextView = resultTextView;
    }

    public void startThread() {
        start();
        Log.v(Constants.TAG, "[CLIENT THREAD] Client started");
    }

    @Override
    public void run() {
        try {
            // tries to establish a socket connection to the server
            socket = new Socket(address, port);

            // gets the reader and writer for the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            // sends the numbers and information type to the server
            String message = operationType + "," + number1 + "," + number2;
            printWriter.println(message);
            printWriter.flush();

            String result;

            // reads the result from the server
            while ((result = bufferedReader.readLine()) != null) {
                final String finalResult = result;

                // updates the UI with the result information. This is done using post() method to ensure it is executed on UI thread
                resultTextView.post(() -> resultTextView.setText(finalResult));
            }
        } // if an exception occurs, it is logged
        catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
