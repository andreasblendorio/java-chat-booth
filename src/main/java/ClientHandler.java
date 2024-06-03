import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

// NOTE: ClientHandler class has not a main method to be run, cause it will be run from the thread defined in line 36 of Server class (thread.start())

public class ClientHandler implements Runnable {

    // Client Array
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // keeps track of all the clients so that when a client sends a msg, Server loops through this array list and sends a msg to every client

    // Socket
    private Socket socket; // passed from our Server class

    // Reads data/msg
    private BufferedReader bufferedReader; // sent from a client

    // Sends data/msg
    private BufferedWriter bufferedWriter; // sent to a client

    // Client Username
    private String clientUsername;

    // Client Handler Constructor
    public ClientHandler(Socket socket) {
        // Instances of this class will pass socket obj from our Server class, so we need to accept that in our Constructor
        try {

            /*
             * Socket: represent a connection between our server or client handler and our client,
             * each socket connection has an output stream
             * that you can use to send data to whatever it is you're connected to.
             */

            this.socket = socket; // which means this is the object that is being made from this class, for that object set the socket of it equal to what is passed into the constructor
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //in java there are two types of stream a byte stream and a character stream (which is the one we want)
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine(); // reads from the stream up until a newline char
            clientHandlers.add(this); // adds the client to the array list so that it can receive msg from other users
            broadcastMessage("SERVER: " + clientUsername + " has joined the chat!");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    // Main
    @Override
    public void run() {

        /*
         * Everything in this run method is what is run on a separate thread
         * and what we want to do on a separate thread is listening for messages
         * BUT listening for messages is a "blocking operation"
         * "blocking operation means that the program will be stuck until the operation is completed
         * so if we weren't using multiple threads our program would be stuck,
         * waiting for a message from a client, that's why we're going to have a separate thread waiting for messages
         * and another one working with the rest of our application
         * We don't want to wait for someone to send a msg before we can actually send one.
         */

        String messageFromClient; // string var holding msg from client

        // while there's a connection with a client, we listen for msg
        while (socket.isConnected()) {
            try {
                // Read!
                messageFromClient = bufferedReader.readLine(); // listen = read from buffer, IMPORTANT: program will hold here until there's a message received from the client
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Broadcast method
    public void broadcastMessage(String messageToSend) {
        // Looping through the array and send a message to each client connected
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Broadcasting the msg to everyone (except the user that has sent the msg)
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    /*
                     * because on the line 62 (on the client) they use the method .readline
                     * they will be waiting for a newline char and this line here does not send a newline char,
                     * we have to explicitly do that
                     * newline char is equivalent to press Enter key
                     * newline says: "I'm done sending over data, no need to wait for more"
                     */
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush(); // because a buffer will not be sent down its output stream unless it's full
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Remove method
    public void removeClientHandler() {
        clientHandlers.remove(this); // remove the current ("this") clienthandler from the arraylist
        broadcastMessage("SERVER: " + clientUsername + " has left the group");
    }

    // Close Connections + Streams
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); // only need to close the outer wrapper, output stream writer will follow as consequence
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
}
