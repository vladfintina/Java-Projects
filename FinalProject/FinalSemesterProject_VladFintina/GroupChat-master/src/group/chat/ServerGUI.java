package group.chat;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Graphical user interface for the server-side of the application
 * @author vlad
 */
public class ServerGUI extends JFrame implements ActionListener {
    private final Server server;
    private final JPanel panel;
    private final JButton resetSettings;
    private final JTextArea connectionsArea;

    public ServerGUI(Server server) {
        this.server = server;
        setSize(460, 500);
        this.panel = new JPanel();
        this.panel.setLayout(null);
        this.panel.setBounds(0, 0, 450, 70);
        add(this.panel);

        this.resetSettings = new JButton("Reset Settings");
        this.resetSettings.setBounds(30, 10, 390, 40);
        this.resetSettings.addActionListener(this);
        this.panel.add(resetSettings);
        connectionsArea = new JTextArea();
        connectionsArea.setEditable(false);
        connectionsArea.setBackground(this.getBackground());
        connectionsArea.setBounds(30, 60, 390, 350);
        this.panel.add(connectionsArea);
        this.setLocation(450, 70);
        this.setVisible(true);
    }

    /**
     * Waits for clients to connect to the server.
     * When that happens, it starts a new thread to handle the respective client.
     */
    public void runServer() {
        try {
            while (!server.getServerSocket().isClosed()) {
                //blocks the function until a new connection is made
                Socket socket = server.acceptConnection();
                //System.out.println("A new client has connected!");
                connectionsArea.setText(connectionsArea.getText() + "\n" + "A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            server.closeServerSocket();
        }
    }

    /**
     * Whenever we press the reset button, current application changes are discarded
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try(BufferedWriter profilePictureFile = new BufferedWriter(new FileWriter("src/group/chat/settings/profile-picture-path.txt"));
            BufferedWriter titleFile = new BufferedWriter(new FileWriter("src/group/chat/settings/group-title.txt"))) {
            profilePictureFile.write("");
            titleFile.write("");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * Main method from which the server side runs. Creates a server-side socket which runs on port 1234
     * @param args
     */
    public static void main(String[] args) {
        //our server will be listening for clients that are making a connection to this port number
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            ServerGUI serverWindow = new ServerGUI(server);
            serverWindow.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
