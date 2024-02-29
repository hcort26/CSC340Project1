package Client;

import java.net.Socket;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

public class Client {
    private static final int PORT = 12345;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in); // Create a Scanner object for input
        System.out.print("Enter server IP address: "); // Prompt user for server IP
        String HOST = scanner.nextLine(); // Read server IP from user
        
        try (Socket socket = new Socket(HOST, PORT);
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             FileOutputStream fos = new FileOutputStream("received_words.txt")) {
            byte[] buffer = new byte[4096];
            int bytesRead;
    
            // Read the file from the server and write it to disk
            while ((bytesRead = ois.readInt()) != -1) {
                ois.readFully(buffer, 0, bytesRead); // Read the exact number of bytes
                fos.write(buffer, 0, bytesRead); // Write bytes to file
            }
            System.out.println("File received from server and saved as received_words.txt");

            // Error Handling for Client Exception
            } catch (Exception e) {
            System.err.println("Client Exception: " + e.getMessage());
            e.printStackTrace();
            }
    }
}
