import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
  private static final String DIR_ARRIBADA = "/tmp";
  private ObjectOutputStream output;
  private ObjectInputStream input;

  public Socket connectar() throws IOException {
    Socket socket = new Socket("localhost", 9999);
    System.out.println("Connectant a -> localhost:9999");
    System.out.println("Connexió acceptada: " + socket.getInetAddress());
    return socket;
  }

  public void rebreFixters(Socket socket) throws IOException, ClassNotFoundException {
    output = new ObjectOutputStream(socket.getOutputStream());
    input = new ObjectInputStream(socket.getInputStream());

    // Demanem el nom del fitxer a rebre
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
    String nomFitxer = reader.readLine();

    // Si el nom és "sortir", tanquem la connexió
    if ("sortir".equalsIgnoreCase(nomFitxer)) {
      System.out.println("Sortint...");
      return;
    }

    output.writeObject(nomFitxer);
    output.flush();

    byte[] contingut = (byte[]) input.readObject();

    if (contingut != null) {
      File file = new File(DIR_ARRIBADA, new File(nomFitxer).getName());
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(contingut);
      fos.close();
      System.out.println("Nom del fitxer a guardar: " + file.getAbsolutePath());
      System.out.println("Fitxer rebut i guardat com: " + file.getAbsolutePath());
    } else {
      System.out.println("El servidor no ha pogut trobar el fitxer.");
    }
  }

  public void tancarConnexio(Socket socket) throws IOException {
    if (socket != null && !socket.isClosed()) {
      socket.close();
      System.out.println("Connexió tancada.");
    }
  }

  public static void main(String[] args) {
    Client client = new Client();
    Socket socket = null;

    try {
      socket = client.connectar();

      client.rebreFixters(socket);

    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    } finally {
      try {
        client.tancarConnexio(socket);
      } catch (IOException e) {
        System.out.println("Error tancant la connexió: " + e.getMessage());
      }
    }
  }
}
