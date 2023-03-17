import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Connection implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;
    private Connection connection;
    private static final Logger logger = Logger.getInstance();

    public Connection(Socket client) {
        this.client = client;
        this.connection = Connection.this;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); //чтение входящего потока
            out = new PrintWriter(client.getOutputStream(), true);//запись текста в исходящий поток
            sendMSG("Hello stranger!!! Enter your nickname");
            nickname = in.readLine(); // запоминаем имя пользователя
            while (true) {
                if (nickname.isEmpty() || nickname.trim().length() == 0) { // проверка на пустое имя и имя состоящее полностью из пробелов
                    sendMSG("Try again!!! Please type your nickname");
                    logger.log(getTime() + " " + "Failed login attempt");
                    nickname = in.readLine();
                } else break;
            }
            sendMSG("Welcome to magic chat!!! To leave chat type /quit" );
            Server.sendToChat(nickname + " joined to chat!");
            logger.log(getTime() + " " + nickname + " joined to chat!");
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("/quit")) {
                    Server.sendToChat(nickname + " left the chat");
                    logger.log(getTime() + " " + nickname + " left the chat");
                    shutdown();
                    Server.getConnections().remove(connection);
                } else {
                    Server.sendToChat(nickname + ": " + message);
                    logger.log(getTime() + " " + nickname + ": " + message);
                }
            }
            Server.sendToChat("SERVER: " + nickname + " was disconnected");
            logger.log(getTime() + " " + nickname + " was disconnected");
            shutdown();
            Server.getConnections().remove(connection);
        } catch (Exception e) {
            shutdown();
        }
    }

    public void sendMSG(String message) {
        out.println(message);
    }

    public void shutdown() {
        try {
            in.close();
            out.close();
            client.close(); //у соединения закрываем входящий, исходящий поток и сокет клиента
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTime() {
        return "[" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +
                "] ";
    }
}
