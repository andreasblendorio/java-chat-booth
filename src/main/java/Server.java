import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Sets up:
 * the 'startServer' method in charge of ,
 * the 'closeServerSocket' method in charge of ,
 * the 'main' method in charge of .
 */

// Server class
public class Server {

    // handles the communication w/incoming connections or Clients
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Run method
    public void startServer() {

        try {
            // if open, server socket runs indefinitely
            while(!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                // program halts here until a client is connected
                System.out.println("...Client has connected to the Server");
                ClientHandler clientHandler = new ClientHandler(socket);

                /*
                 * this class will implement the Runnable interface
                 * (it is implemented on a class whose instances will
                 * each be executed by a separate thread)
                 * specifically whatever is in the overridden run method of the class
                 */

                // To spawn a new Thread we first need to create a new thread obj and then pass in our object that is an instance of a class that implements runnable
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e){

        }
    }

    // Shutdown method
    public void closeServerSocket() {
        try {
            // Checks if Server Socket is not null, if it is we get a Null Pointer Exception
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Main
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(1234); // server (an obj) will be listening for every client that establish a connection to the selected port
        Server server = new Server(serverSocket); // Server obj takes a server socket into its constructor
        server.startServer();
    }
}
