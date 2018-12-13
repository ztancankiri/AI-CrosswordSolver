package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class PyConnector extends Thread {

    private Process p;
    private String serverIP;
    private int serverPort;
    private Socket socket;
    private BufferedReader incoming;
    private PrintWriter outgoing;
    private AtomicBoolean isActive;
    private MessageListener listener;

    public PyConnector(String serverIP, int serverPort, MessageListener listener) throws IOException, InterruptedException {
        super("PyConnectorThread");

        this.p = Runtime.getRuntime().exec("python3 nltkModule.py");
        sleep(500);
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.isActive = new AtomicBoolean(false);
        this.listener = listener;

        this.socket = new Socket(serverIP, serverPort);
        this.outgoing = new PrintWriter(socket.getOutputStream(), true);
        this.incoming = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public synchronized void start() {
        super.start();
        isActive.set(true);
    }

    @Override
    public void run() {
        try {
            while (isActive.get()) {
                String text = incoming.readLine();

                if (text != null) {
                    listener.onMessageReceived(text);
                }
                else {
                    isActive.set(false);
                    System.out.println("PyConnector: Connection closed.");
                }
            }

            incoming.close();
            outgoing.close();
            socket.close();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        outgoing.println(message);
    }

}
