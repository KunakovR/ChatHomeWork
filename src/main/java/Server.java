import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static CopyOnWriteArraySet<Connection> connections; //храним соединения
    private static final Logger logger = Logger.getInstance();//логгируем записи
    private ServerSocket serverSocket; //серверный сокет
    private ExecutorService pool; // пул потоков

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(getPort()); // запустили серверный сокет
            pool = Executors.newCachedThreadPool(); // пул потоков переменного кол-ва
            connections = new CopyOnWriteArraySet(); // создали коллекцию для будущих подключений
            System.out.println("Server started"); //вывели в консоль сообщение о запуске сервера
            logger.log(getDate() + " " + "Server started" );
            while(true){ //запустили бесконечный цикл, must have
                Socket clientSocket = serverSocket.accept(); // ждем подключения
                Connection connection = new Connection(clientSocket); // создаем новое подключение
                connections.add(connection); // добавляем в коллекцию
                pool.submit(connection); // отправляем на исполнение в пул потоков
            }
        } catch (IOException e) {
            shutdown(); // если выбросит исключение, закрываем сервер
        }
    }

    public static CopyOnWriteArraySet<Connection> getConnections() {
        return connections;
    }

    public static synchronized void printServer(String msg){
        System.out.println(msg);
    }

    public static synchronized void sendToChat(String msg) {
        for (Connection cc : connections) {
            if (cc != null) {
                cc.sendMSG(getTime() + " " + msg);
            }
        }
        printServer(msg);
    }

    public void shutdown(){
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close(); //если серверный сокет не закрыт - закрываем
            }
            for (Connection cn : connections){
                cn.shutdown(); //пробегаемся по соединениям и закрываем их тоже
            }
            getConnections().removeAll(connections); //зачищаем коллекцию
            pool.shutdown(); //обнуляем пул потоков
            logger.log(getDate() + " " + "Server is out");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static int getPort() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String[] settings = reader.readLine().split(":");
            return Integer.parseInt(settings[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTime() {
        return "[" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                "] ";
    }

    public static String getDate() {
        return "[" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                "] ";
    }
}
