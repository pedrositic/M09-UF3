import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {

  public static final String MSG_SORTIR = "sortir";

  private Socket socket;
  private ObjectOutputStream output;
  private ObjectInputStream input;

  public void connecta() {
    try {
      socket = new Socket("localhost", 9999);
      System.out.println("Conectat al servidor.");

      output = new ObjectOutputStream(socket.getOutputStream());
      input = new ObjectInputStream(socket.getInputStream());
    } catch (IOException e) {
      System.err.println("Error al conectar al servidor: " + e.getMessage());
    }
  }

  public void enviarMissatge(String missatge) {
    try {
      output.writeObject(missatge);
      output.flush();
    } catch (IOException e) {
      System.err.println("Error al enviar el missatge: " + e.getMessage());
    }
  }

  public void tancarClient() {
    try {
      if (socket != null && !socket.isClosed()) {
        socket.close();
        System.out.println("Conexió tancada.");
      }
    } catch (IOException e) {
      System.err.println("Error al tancar el socket: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    ClientXat client = new ClientXat();

    try {
      client.connecta();

      FilLectorCX filLectorCX = new FilLectorCX(client.output);
      filLectorCX.start();

      Scanner scanner = new Scanner(System.in);
      boolean continuar = true;
      while (continuar) {
        System.out.print("Enviar missatge (o 'sortir' per tancar): ");
        String missatge = scanner.nextLine();

        client.enviarMissatge(missatge);

        if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
          continuar = false;
        }
      }

      scanner.close();

    } catch (Exception e) {
      System.err.println("Error en el client: " + e.getMessage());
    } finally {
      client.tancarClient();
    }
  }
}