import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Sets up:
 * the Client object ,
 * the 'SendMessage' method in charge of ,
 * the 'listenForMessage' method in charge of ,
 * the 'closeAll' method in charge of ,
 * the 'main' method in charge of .
 */
public class Client {

    // Socket
    private Socket socket;

    // Inbound
    private BufferedReader bufferedReader;

    // Outbound
    private BufferedWriter bufferedWriter;

    // User
    private String username;

    // Client
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // create this from our socket obj
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    // Send method
    public void sendMessage() {
        // Sending msg (to ClientHandler)
        try {
            // Provides the username
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            // what we're sending here is what the ClientHandler will be waiting for on line 37 (this.clientUsername = bufferedReader.readLine();)

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket,bufferedReader, bufferedWriter);
        }
    }

    // Listen method
    public void listenForMessage() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    // Close method
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    // Main
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        // making a connection to the port the server is listening on, by creating a Socket obj
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage(); // NOTE: technically these are infinite while loops that runs while they're still connected
        client.sendMessage();
    }
}
