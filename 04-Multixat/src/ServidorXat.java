import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
  public static final int PORT = 9999;
  public static final String HOST = "localhost";
  public static final String MSG_SORTIR = "sortir";

  private Hashtable<String, GestorClients> clients = new Hashtable<>();
  private ServerSocket serverSocket;
  private boolean sortir = false;

  public void servidorAEscoltar() throws IOException {
    serverSocket = new ServerSocket(PORT);
    System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
  }

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

  public void finalitzarXat() {
    enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
    clients.clear();
    sortir = true;
    pararServidor();
    System.out.println("Tancant tots els clients.");
  }

  public void afegirClient(GestorClients gestor) {
    String nom = gestor.getNom();
    if (nom != null && !clients.containsKey(nom)) {
      clients.put(nom, gestor);
      enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " entra al xat."));
      System.out.println("DEBUG: multicast Entra: " + nom);
    }
  }

  public void eliminarClient(String nom) {
    if (clients.containsKey(nom)) {
      clients.remove(nom);
      enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " surt del xat."));
      System.out.println("DEBUG: multicast Surt: " + nom);
    }
  }

  public void enviarMissatgeGrup(String missatge) {
    for (GestorClients gestor : clients.values()) {
      gestor.enviarMissatge(missatge);
    }
  }

  public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
    if (clients.containsKey(destinatari)) {
      clients.get(destinatari).enviarMissatge(Missatge.getMissatgePersonal(remitent, missatge));
    } else {
      System.out.println("Destinatari no trobat: " + destinatari);
    }
  }

  public static void main(String[] args) {
    ServidorXat servidor = new ServidorXat();

    try {
      servidor.servidorAEscoltar();

      while (!servidor.sortir) {
        Socket clientSocket = servidor.serverSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress());

        GestorClients gestor = new GestorClients(clientSocket, servidor);
        servidor.afegirClient(gestor);
        gestor.start();
      }
    } catch (IOException e) {
      System.err.println("Error en el servidor: " + e.getMessage());
    } finally {
      servidor.pararServidor();
    }
  }
}