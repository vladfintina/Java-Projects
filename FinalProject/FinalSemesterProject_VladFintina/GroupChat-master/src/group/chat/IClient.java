package group.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

public interface IClient {
    /**
     * Sends the username of the client to the clienthandler list
     */
    void sendUsername();
    /**
     * Clients wait to receive messages from the other clients.
     * When it is able to read from the stream, it displays the message on the text area
     * Client may also receive path for a new profile picture, which is distinguished by prefix and sufix *PP*
     * Because listening to message is a blocking method (waits for a new message to be received), it runs on a separate thread.
     */
    void listenToMessage();
    /**
     * Closing the connection and the communicating streams
     * @param socket the socket on which the client is connected
     * @param reader reading channel
     * @param writer writing channel
     */
    void closeEverything(Socket socket, BufferedReader reader, BufferedWriter writer);
    /**
     * Changing group picture action is triggered by clicking on the 3 dots icon
     */
    void changeGroupPicture();
    /**
     * Changes the profile picture with the one from the path received
     * @param messageFromGroupChat photo path with delimiters received from the client that actually changed the group photo
     */
    void changePictureWithReceivedPath(String messageFromGroupChat);
    /**
     * Changes the group title
     * @param title new title
     */
    void changeGroupTitle(String title);
}
