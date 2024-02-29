package Client;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;

public class Server {
    private static final int PORT = 12345;
    private static int clientCount = 0;
    private static boolean running = true;

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis(); // Start timer
        System.out.println("Server started. Listening on Port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            
            while (true) {

                if (clientCount >= 5) {
                    // If five clients have connected, stop accepting new connections.
                    System.out.println("Maximum client limit reached. Stopping server...");
                    running = false; 
                    break; 
                }

                Socket clientSocket = serverSocket.accept();
                clientCount++;
                int currentClientNumber = clientCount;
                System.out.println("Client " + currentClientNumber + " connected");
                System.out.println("Client " + currentClientNumber + " recieved .txt file");

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            System.err.println("Server Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis(); // End timer
            long duration = endTime - startTime; // Calculate duration
            System.out.println("Server ended. Total runtime was " + duration + " milliseconds.");
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private int clientNumber;  // Add this line
    
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            //this.clientNumber = clientNumber;  // Add this line
        }
    
        public void run() {
            try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                 FileInputStream fis = new FileInputStream("words.txt")) {
                byte[] buffer = new byte[4096];
                int bytesRead;
    
                // While there are still bytes to send, read from the file and send to client
                while ((bytesRead = fis.read(buffer)) != -1) {
                    oos.writeInt(bytesRead); // Send the number of bytes to be read
                    oos.write(buffer, 0, bytesRead); // Send the bytes
                }
                oos.writeInt(-1); // Indicate that the file is completely sent
    
            } catch (Exception e) {
                System.err.println("Error handling client #" + clientNumber + ": " + e.getMessage());
            e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


