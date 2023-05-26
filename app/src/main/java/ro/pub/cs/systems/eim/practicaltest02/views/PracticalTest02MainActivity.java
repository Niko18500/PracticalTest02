package ro.pub.cs.systems.eim.practicaltest02.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.R;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button serverStartButton;

    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText number1EditText;
    private EditText number2EditText;
    private Button addButton;
    private Button multiplyButton;

    private TextView resultTextView;

    private ServerThread serverThread;

    private final ServerButtonClickListener serverStartButtonClickListener = new ServerButtonClickListener();
    private class ServerButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Start server
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.startThread();
        }
    }

    private final OperationButtonClickListener operationButtonClickListener = new OperationButtonClickListener();
    private class OperationButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Retrieves the client address and port. Checks if they are empty or not
            //  Checks if the server thread is alive. Then creates a new client thread with the address, port, city and information type
            //  and starts it
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (number1EditText.getText().toString().isEmpty() || number2EditText.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (numbers) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer number1, number2;
            try {
                number1 = Integer.parseInt(number1EditText.getText().toString());
                number2 = Integer.parseInt(number2EditText.getText().toString());
            } catch (NumberFormatException e) {
                resultTextView.setText("overflow");
                return;
            }
            String operationType = "";
            if (view.getId() == R.id.add_button) {
                operationType = "add";
            } else if (view.getId() == R.id.multiply_button) {
                operationType = "mul";
            }
            if (number1 == null || number2 == null || operationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (number1 / number2 / operation type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            resultTextView.setText(Constants.EMPTY_STRING);

            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), number1, number2, operationType, resultTextView);
            clientThread.startThread();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        serverStartButton = (Button)findViewById(R.id.server_start_button);
        serverStartButton.setOnClickListener(serverStartButtonClickListener);

        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        number1EditText = (EditText)findViewById(R.id.number_1_edit_text);
        number2EditText = (EditText)findViewById(R.id.number_2_edit_text);
        addButton = (Button)findViewById(R.id.add_button);
        addButton.setOnClickListener(operationButtonClickListener);
        multiplyButton = (Button)findViewById(R.id.multiply_button);
        multiplyButton.setOnClickListener(operationButtonClickListener);

        resultTextView = (TextView) findViewById(R.id.result_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}