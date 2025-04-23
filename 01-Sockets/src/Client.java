import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
  public static final int PORT = ServidorXat.PORT;
  public static final String HOST = ServidorXat.HOST;
  private Socket socket;
  private PrintWriter out;

  public void connecta() {
    try {
      socket = new Socket(HOST, PORT);
      out = new PrintWriter(socket.getOutputStream(), true); // Activem autoflush
      System.out.println("Connectat al servidor en " + HOST + ":" + PORT);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void tanca() {
    try {
      socket.close();
      out.close();
      System.out.println("Client tancat");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void envia(String message) {
    if (!socket.isClosed()) {
      out.println(message);
      System.out.println("Enviat al servidor: " + message);
    }
  }

public static void main(String[] args) {
    ClientXat client = new ClientXat();

    client.connecta();

    client.envia("Prova d'enviament 1");
    client.envia("Prova d'enviament 2");
    client.envia("Adeu!");

    System.out.println("Prem ENTER per tancar el client...");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      String line = reader.readLine();
      while (line != null && !line.trim().isEmpty()) {
        line = reader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    client.tanca();
  }



}
