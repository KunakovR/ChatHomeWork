import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClientTest {

    @Test
    public void testIp() {
        var expected = "127.0.0.1";
        String actual;
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String[] settings = reader.readLine().split(":");
            actual = settings[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected, actual);
    }
}
