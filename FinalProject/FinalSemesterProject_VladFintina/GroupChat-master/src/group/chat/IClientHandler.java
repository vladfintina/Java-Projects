package group.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public interface IClientHandler {
    /**
     * Sends the message received to all the other clients
     * @param messageToSend message to be sent to the other clients
     */
    void broadcastMessage(String messageToSend);

    /**
     * Closing the connection and the communicating streams
     * @param socket the socket on which the client is connected
     * @param reader reading channel
     * @param writer writing channel
     */
    void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer);

    /**
     * Removes the current reference from the clientHandler list
     */
    void removeClientHandler();
}
