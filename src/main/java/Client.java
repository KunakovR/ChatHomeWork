import java.io.*;
import java.net.Socket;

public class Client {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try {
            client = new Socket(getIP(), getPort());//создаем клиентский сокет
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));//чтение входящего потока
            out = new PrintWriter(client.getOutputStream(), true);//запись текста в исходящий поток

            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    while(true){
                        String message = reader.readLine();
                        if (message.equals("/quit")){
                            out.println(message);
                            reader.close();
                            shutdown();
                        } else {
                            out.println(message);
                        }
                    }
                }catch(IOException e){
                    shutdown();
                }
            }).start(); // запускаем в поток клиентское общение

            String inMsg;
            while((inMsg = in.readLine()) != null) {
                System.out.println(inMsg); // просто вывод в консоль поступающих сообщений от сервера
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            in.close();
            out.close();
            client.close(); //закрываем входящий, исходящий поток и сокет клиента
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getIP() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String[] settings = reader.readLine().split(":");
            return settings[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getPort() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String[] settings = reader.readLine().split(":");
            return Integer.parseInt(settings[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
