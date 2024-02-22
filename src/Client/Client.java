package Client;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Connected to server");

            // Receive job part from server
            JobPart jobPart = (JobPart) ois.readObject();

            // Process job part (count words in the text part)
            int wordCount = countWordsInText(jobPart.getTextPart());

            // Send result (word count) back to server
            oos.writeObject(wordCount);

            // Receive confirmation from server
            String confirmation = (String) ois.readObject();
            System.out.println("Server says: " + confirmation);
        } catch (Exception e) {
            System.err.println("Client Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int countWordsInText(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        String[] words = text.trim().split("\\s+");
        return words.length;
    }
}
