import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private static Logger instance = null;

    private Logger() {}

    public synchronized static Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance; //реализация через синглтон
    }

    public synchronized void log(String msg) throws IOException {
        FileWriter writer = new FileWriter("File.log", true);
        writer.write(msg + "\r\n");
        writer.flush(); //функция логирования
    }
}
