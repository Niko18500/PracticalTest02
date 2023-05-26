package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    // run() method: The run method is the entry point for the thread when it starts executing.
    // It's responsible for reading data from the client, interacting with the server,
    // and sending a response back to the client.
    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            // Create BufferedReader and PrintWriter instances for reading from and writing to the socket
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for information from client (operands / operation_type");

            // Read the numbers and operationType values sent by the client
            String message = bufferedReader.readLine();
            if (message == null || message.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (numbers / operation type!");
                return;
            }

            // Parse parameters by splitting the message by comma
            String[] parameters = message.split(",");
            String operationType = parameters[0];
            Integer number1 = Integer.parseInt(parameters[1]);
            Integer number2 = Integer.parseInt(parameters[2]);

            Integer result = null;
            if (operationType.equalsIgnoreCase("add")) {
                result = number1 + number2;
            } else if (operationType.equalsIgnoreCase("mul")) {
                result = number1 * number2;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (result == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Wrong operation type!");
                return;
            }

            // check for overflow and send result
            if (result < number1 || result < number2) {
                printWriter.println("overflow");
                printWriter.flush();
            } else {
                printWriter.println(result);
                printWriter.flush();
            }

        } catch (IOException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + e.getMessage());
            if (Constants.DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
