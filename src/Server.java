import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server extends Thread {
    private String point = null;
    private static final int PORT = 6666;
    private static InetAddress host;
    private ServerSocket serverSocket = null;
    private static ArrayList<Socket> allSocket = null;
    private int idSocket;
    private Socket currSocket;
    private static int countClients;

    Server() {
        if (allSocket == null)
            allSocket = new ArrayList<Socket>();
    }

    private void addSocket(int idSocket, Socket socket) {
        this.idSocket = idSocket;
        this.currSocket = socket;
        countClients++;
        System.out.println("Accept new client: " + currSocket);
        allSocket.add(socket);
        setDaemon(true);
        setPriority(NORM_PRIORITY);
        this.start();
    }

    private void broadcastSocket(String message) {
        try {
            System.out.println("\nGet point: " + message + "\nBroadcast " + allSocket.size() + " clients:");
            for (Socket socket : allSocket) {
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(message);
                System.out.println(" Socket " + socket);
                dos.flush();
            }
        } catch (Exception e) {
            System.out.println("Exception in broadcast socket : " + e.toString());
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(currSocket.getInputStream());

            while (true) {
                point = dis.readUTF();
                broadcastSocket(point);
            }
        } catch (Exception e) {
            try {
                currSocket.close();
            } catch (IOException exception) {
                System.err.println("Exception close socket server");

            }
            System.err.println("\n!!!!!!!!!       WARNING       !!!!!!!!!!!!!!!!!!!!!!!!\n" + currSocket + " was extremaly closed\n" + "Reason: " + e.toString() +
                    "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            allSocket.remove(currSocket);
            countClients--;
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            System.err.println("Exception host: " + ex);
        }
        try {
            InetAddress ia = InetAddress.getByName("localhost");
            serverSocket = new ServerSocket(PORT, 0, ia);
            System.out.println("Server started host: " + host + " port: " + PORT);
            countClients = 0;
            while (true) {
                Socket socket = serverSocket.accept();
                new Server().addSocket(countClients, socket);
            }
        } catch (IOException ex) {
            System.err.println("Exception in serverSocket: " + ex);
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException ex) {
                System.err.println("Exception in close serverSocket: " + ex);

            }
        }
    }
}


