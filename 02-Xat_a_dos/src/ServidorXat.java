import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
  public static final int PORT = 9999;
  public static final String HOST = "localhost";
  public static final String MSG_SORTIR = "sortir";

  private ServerSocket serverSocket;

  public void iniciarServidor() {
    try {
      serverSocket = new ServerSocket(PORT);
      System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    } catch (IOException e) {
      System.err.println("Error al iniciar el servidor: " + e.getMessage());
    }
  }

  public void pararServidor() {
    try {
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
        System.out.println("Servidor parat.");
      }
    } catch (IOException e) {
      System.err.println("Error al parar el servidor: " + e.getMessage());
    }
  }

  public String getNom(ObjectInputStream input, ObjectOutputStream output) {
    try {
      output.writeObject("Hola! Introdueix el teu nom:");
      output.flush();

      return (String) input.readObject();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error al obtenir el nom del client: " + e.getMessage());
      return null;
    }
  }

  public static void main(String[] args) {
    ServidorXat servidor = new ServidorXat();

    try {
      servidor.iniciarServidor();

      Socket clientSocket = servidor.serverSocket.accept();
      System.out.println("Client conectat: " + clientSocket.getInetAddress());

      ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
      ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());

      String nom = servidor.getNom(input, output);
      if (nom == null) {
        System.err.println("No s'ha pogut obtenir el nom del client.");
        return;
      }

      FilServidorXat filServidorXat = new FilServidorXat(input, nom);
      filServidorXat.start();

      filServidorXat.join();

      clientSocket.close();

    } catch (IOException | InterruptedException e) {
      System.err.println("Error en el servidor: " + e.getMessage());
    } finally {
      servidor.pararServidor();
    }
  }
}
