import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
  private static final int PORT = 9999;
  private static final String HOST = "localhost";

  public Socket connectar() throws IOException {
    ServerSocket serverSocket = new ServerSocket(PORT);
    System.out.println("Acceptant connexions en -> "+ HOST + ":" + PORT);
    System.out.println("Esperant connexió...");
    Socket socket = serverSocket.accept();
    System.out.println("Connexió acceptada: " + socket.getInetAddress());
    return socket;
  }

  public void tancarConnexio(Socket socket) throws IOException {
    if (socket != null && !socket.isClosed()) {
      socket.close();
      System.out.println("Tancant connexió amb el client: " + socket.getInetAddress());
    }
  }

  public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

    String nomFitxer = (String) input.readObject();
    System.out.println("Nomfitxer rebut: " + nomFitxer);

    Fitxer fitxer = new Fitxer(nomFitxer);

    try {
      byte[] contingut = fitxer.getContingut();
      long tamany = contingut.length;

      System.out.println("Contingut del fitxer a enviar: " + tamany + " bytes");

      output.writeObject(contingut);
      output.flush();
      System.out.println("Fitxer enviat al client: " + nomFitxer);
    } catch (IOException e) {
      System.out.println("Error llegint el fitxer: " + e.getMessage());
      output.writeObject(null); // Enviem null si hi ha un error
      System.out.println("Nom del fitxer buit o nul. Sortint...");
    }
  }

  public static void main(String[] args) {
    Servidor servidor = new Servidor();
    Socket socket = null;

    try {
      socket = servidor.connectar();

      servidor.enviarFitxers(socket);

    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    } finally {
      try {
        servidor.tancarConnexio(socket);
      } catch (IOException e) {
        System.out.println("Error tancant la connexió: " + e.getMessage());
      }
    }
  }

}
