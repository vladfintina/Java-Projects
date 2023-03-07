package group.chat;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ClientHandler class helps us broadcast the messages/signals between the clients.
 * @author vlad
 */
public class ClientHandler implements IClientHandler, Runnable {
    //used to broadcast messages to the other clients
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader; // used to read messages that have been sent from one client
    private BufferedWriter bufferedWriter; //used to send data to the other clients
    private String clientUsername;

    /**
     * Constructor of the ClientHandler class. Sets up the communication streams and adds the current instance to the clientHandlers list.
     * @param socket the socket on which the respective client connected.
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            //setting up the communicating stream channels
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //when a new client connects, the first message that is sent through the "pipe" is its username
            //we store that username
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    /**
     * Sends the message received to all the other clients
     * @param messageToSend message to be sent to the other clients
     */
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine(); //notifies that we are done sending data, so there is no need to wait anymore
                    clientHandler.bufferedWriter.flush(); //fulfills the buffer to be complete
                }
            }catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Removes the current reference from the clientHandler list
     */
    @Override
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    /**
     * Closing the connection and the communicating streams
     * @param socket - the socket on which the client is connected
     * @param reader - reading channel
     * @param writer - writing channel
     */
    @Override
    public void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer) {
        this.removeClientHandler();
        try {
            if (bufferedReader != null) {
                 bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Working on a separate thread, as listening for a new message will block the rest of the application
     */
    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
}
