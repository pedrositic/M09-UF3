import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
  public static final int PORT = 7777;
  public static final String HOST = "localhost";

  private ServerSocket srvSocket;
  private Socket clientSocket;

  public void connecta() {
    srvSocket = null;
    clientSocket = null;
    try {
      srvSocket = new ServerSocket(PORT);
      System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
      System.out.println("Esperant connexions a " + HOST + ":" + PORT);
      clientSocket = srvSocket.accept();
      System.out.println("Client connectat: " + clientSocket.getInetAddress());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void repDades() {
    try (BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
      String input = bf.readLine();
      while (input != null) {
        System.out.println("Rebut: " + input);
        input = bf.readLine();
      }
    } catch (Exception e) {
      System.out.println("Error al rebre les dades: " + e.getStackTrace());
    }
  }

  public void tanca() {
    try {
      clientSocket.close();
      srvSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Servidor srv = new Servidor();
    srv.connecta();
    srv.repDades();
    srv.tanca();
  }

}
