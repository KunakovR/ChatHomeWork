import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionTest {

    @BeforeAll
    public static void setServer() {

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(9999);
                Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("test");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testSendingMSG() {
        var expected = "test";

        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 9999);
                BufferedReader conn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String actual;
                actual = conn.readLine();
                Assertions.assertEquals(expected, actual);
                System.out.println("Expected: " + expected);
                System.out.println("Actual: " + actual);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
