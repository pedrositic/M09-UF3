import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
  // Constants
  public static final int PORT = 9999;

  // Variables
  private ServerSocket serverSocket;
  private boolean sortir = false;

  // Mètode per iniciar l'escolta del servidor
  public void servidorAEscoltar() throws IOException {
    serverSocket = new ServerSocket(PORT);
    System.out.println("Servidor iniciat a localhost:" + PORT);
  }

  // Mètode per aturar el servidor
  public void pararServidor() {
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
        System.out.println("Servidor aturat.");
      }
    } catch (IOException e) {
      System.err.println("Error tancant el servidor: " + e.getMessage());
    }
  }

  // Mètode principal
  public static void main(String[] args) {
    ServidorXat servidor = new ServidorXat();

    try {
      // Iniciar el servidor
      servidor.servidorAEscoltar();

      // Bucle principal del servidor
      while (!servidor.sortir) {
        Socket clientSocket = servidor.serverSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress());

        // Aquí encara no gestionem els clients, només acceptem connexions
      }
    } catch (IOException e) {
      System.err.println("Error en el servidor: " + e.getMessage());
    } finally {
      // Aturar el servidor quan acabi el bucle
      servidor.pararServidor();
    }
  }
}