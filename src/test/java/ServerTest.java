import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ServerTest {

    @Test
    public void testPort() {
        var expected = 9090;
        int actual;
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            var settings = reader.readLine().split(":");
            actual = Integer.parseInt(settings[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected, actual);
    }
}
