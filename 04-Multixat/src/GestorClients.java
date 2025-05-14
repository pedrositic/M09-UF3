import java.io.*;
import java.net.Socket;

public class GestorClients extends Thread {
  private Socket clientSocket;
  private ObjectOutputStream oos;
  private ObjectInputStream ois;
  private ServidorXat servidorXat;
  private String nom;
  private boolean sortir = false;

  public GestorClients(Socket clientSocket, ServidorXat servidorXat) {
    this.clientSocket = clientSocket;
    this.servidorXat = servidorXat;
    try {
      this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
      this.ois = new ObjectInputStream(clientSocket.getInputStream());
    } catch (IOException e) {
      System.err.println("Error inicialitzant els streams: " + e.getMessage());
    }
  }

  public String getNom() {
    return nom;
  }

  @Override
  public void run() {
    try {
      while (!sortir) {
        String missatgeRaw = (String) ois.readObject();
        System.out.println("Missatge rebut: " + missatgeRaw);

        processaMissatge(missatgeRaw);
      }
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error llegint missatge: " + e.getMessage());
    } finally {
      tancarSocket();
    }
  }

  public void enviarMissatge(String missatge) {
    try {
      if (oos != null) {
        oos.writeObject(missatge);
        oos.flush();
      }
    } catch (IOException e) {
      System.err.println("Error enviant missatge: " + e.getMessage());
    }
  }

  private void processaMissatge(String missatgeRaw) {
    String codi = Missatge.getCodiMissatge(missatgeRaw);

    if (codi == null) {
      System.out.println("Missatge desconegut rebut.");
      return;
    }

    switch (codi) {
      case Missatge.CODI_CONECTAR:
        String[] parts = Missatge.getPartsMissatge(missatgeRaw);
        this.nom = parts[1];
        servidorXat.afegirClient(this);
        break;

      case Missatge.CODI_SORTIR_CLIENT:
        servidorXat.eliminarClient(this.nom);
        sortir = true;
        break;

      case Missatge.CODI_SORTIR_TOTS:
        sortir = true;
        servidorXat.finalitzarXat();
        break;

      case Missatge.CODI_MSG_PERSONAL:
        String destinatari = Missatge.getPartsMissatge(missatgeRaw)[1];
        String missatge = Missatge.getPartsMissatge(missatgeRaw)[2];
        servidorXat.enviarMissatgePersonal(destinatari, this.nom, missatge);
        break;

      case Missatge.CODI_MSG_GRUP:
        String missatgeGrup = Missatge.getPartsMissatge(missatgeRaw)[1];
        servidorXat.enviarMissatgeGrup(Missatge.getMissatgeGrup(this.nom + ": " + missatgeGrup));
        break;

      default:
        System.out.println("Codi desconegut: " + codi);
        break;
    }
  }

  private void tancarSocket() {
    try {
      if (clientSocket != null && !clientSocket.isClosed()) {
        clientSocket.close();
        System.out.println("Socket del client tancat: " + nom);
      }
    } catch (IOException e) {
      System.err.println("Error tancant el socket: " + e.getMessage());
    }
  }
}