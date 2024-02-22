package Client;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 12345;
    private static final int NUMBER_OF_PARTS = 5; // Assuming split into 5 parts, adjust based on your needs
    private static List<JobPart> jobParts; // List to store text parts
    private static int[] results; // Store results from each client

    public static void main(String[] args) {
        // Initialize job parts and results
        String text = "The complete text of The Adventures of Sherlock Holmes"; // Placeholder, load actual text here
        jobParts = splitTextIntoParts(text, NUMBER_OF_PARTS);
        results = new int[NUMBER_OF_PARTS];

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Listening on Port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            System.err.println("Server Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<JobPart> splitTextIntoParts(String text, int numberOfParts) {
        List<JobPart> parts = new ArrayList<>();
        int partSize = text.length() / numberOfParts;
        for (int i = 0; i < numberOfParts; i++) {
            int start = i * partSize;
            int end = (i == numberOfParts - 1) ? text.length() : (i + 1) * partSize;
            // Ensure we don't split in the middle of a word
            while (end < text.length() && text.charAt(end) != ' ' && text.charAt(end) != '\n') {
                end++;
            }
            String partText = text.substring(start, end);
            parts.add(new JobPart(partText, i)); // 'i' can serve as partId
        }
        return parts;
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

                // Send job part to client
                // For simplicity, sending the first unassigned part, implement a better mechanism for production
                for (int i = 0; i < jobParts.size(); i++) {
                    if (results[i] == 0) { // Assuming 0 as unprocessed
                        oos.writeObject(jobParts.get(i));
                        break;
                    }
                }

                // Receive result from client
                int wordCount = (Integer) ois.readObject();
                // Here, you should identify which part this wordCount corresponds to and update 'results'
                // For simplicity, this example just assigns it assuming sequential processing
                for (int i = 0; i < results.length; i++) {
                    if (results[i] == 0) {
                        results[i] = wordCount;
                        break;
                    }
                }

                // Send confirmation back to client
                oos.writeObject(new String("Result received"));

            } catch (Exception e) {
                System.err.println("Error handling client: " + e.getMessage());
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


