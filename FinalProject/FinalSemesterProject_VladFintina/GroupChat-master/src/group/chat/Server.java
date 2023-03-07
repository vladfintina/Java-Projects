package group.chat;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author vlad
 */
public class Server implements IServer {
    private ServerSocket serverSocket;

    /**
     * Server constructor
     * @param serverSocket provides server-side Socket connection
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


    /**
     * Closes the server connection
     */
    @Override
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the server socket
     * @return the serverSocket
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * After a client connected, gets the client-side socket of the connection
     * @return the socket on which the client connected
     * @throws IOException in case any error occurs
     */
    @Override
    public Socket acceptConnection() throws IOException {
        return serverSocket.accept();
    }

    /*public static void main(String[] args) {
        //our server will be listening for clients that are making a connection to this port number
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            //server.runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

}
