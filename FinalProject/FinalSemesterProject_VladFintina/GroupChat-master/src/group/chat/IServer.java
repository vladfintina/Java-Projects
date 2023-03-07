package group.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public interface IServer {
    /**
     * Closes the server connection
     */
    void closeServerSocket();
    /**
     * After a client connected, gets the client-side socket of the connection
     * @return the socket on which the client connected
     * @throws IOException in case any error occurs
     */
    Socket acceptConnection() throws IOException;
}
