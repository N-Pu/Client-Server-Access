import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.awt.image.ImageObserver.HEIGHT;
import static java.awt.image.ImageObserver.WIDTH;

public class ClientMainFrame extends Thread {
    private JFrame frame;
    private MyCanvas canvas;
    private final static int PORT = 6666;
    private final static String LOCALHOST = "127.0.0.1";
    private InetAddress ipAddress;
    private Socket socket = null;
    private String point;
    private int x, y;

    ClientMainFrame() {
        InitializeFrame();
        InitializeSocket();
        canvas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (socket != null) {
                    point = me.getX() + " " + me.getY();
                    x = me.getX();
                    y = me.getY();
                    canvas.setValues(point, x, y);
                    OutputStream outputStream = null;
                    try {
                        outputStream = socket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(outputStream);
                        dos.writeUTF(point);
                        dos.flush();
                    } catch (IOException ex) {
                        System.err.println("Exception os:" + ex.toString());
                    }
                }
            }
        });
        try {
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);

            System.out.println("The connection is established.");
            System.out.println("\tLocalPort = " + socket.getLocalPort() + "\n\tInetAddress.HostAddress = " + socket.getInetAddress()
                    .getHostAddress());

            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        point = dis.readUTF();
                        String[] tmp = point.split(" ");
                        x = Integer.parseInt(tmp[0]);
                        y = Integer.parseInt(tmp[1]);
                        System.out.println("Point position: " + point);
                        canvas.setValues(point, x, y);
                    } catch (IOException ex) {
                        System.err.println("\n!!!!!!!!!  ERROR: LOST SERVER  !!!!!!!!!!!!!!!!!!!!!!!!\n" + socket + " will be extremaly closed\n" + "Reason: " + ex.toString() +
                                "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        interrupt();
                        frame.dispose();
                        System.exit(0);
                    }
                }
            });
            thread.start();

        } catch (IOException ex) {
            System.err.println("Exception in constructor ClientMainFrame" + ex.toString());
        }
    }

    private void InitializeFrame() {

        frame = new JFrame("Client GUi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas = new MyCanvas(500, 500);
        frame.setContentPane(canvas);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }


    private void InitializeSocket() {
        try {

            ipAddress = InetAddress.getByName(LOCALHOST);
        } catch (UnknownHostException ex) {
            System.err.println("Exception in initializeClient: " + ex.toString());
        }
        System.out.println(LOCALHOST + "\n" + ipAddress);
        try {
            socket = new Socket(LOCALHOST, PORT);
        } catch (IOException ex) {
            System.err.println("Exception in create socket client: " + ex.toString());
        }

    }

    public static void main(String[] args) {
        new ClientMainFrame();
    }
};