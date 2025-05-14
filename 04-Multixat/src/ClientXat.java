import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
  private static final String HOST = "localhost";
  private static final int PORT = 9999;

  private Socket socket;
  private ObjectOutputStream oos;
  private ObjectInputStream ois;
  private boolean sortir = false;

  public void connecta() {
    try {
      socket = new Socket(HOST, PORT);
      System.out.println("Client connectat a " + HOST + ":" + PORT);

      // Inicialitzem els streams
      oos = new ObjectOutputStream(socket.getOutputStream());

      System.out.println("Flux d'entrada i sortida creat.");
    } catch (IOException e) {
      System.err.println("Error connectant al servidor: " + e.getMessage());
    }
  }

  public void enviarMissatge(String missatge) {
    try {
      if (oos != null) {
        oos.writeObject(missatge);
        oos.flush();
        System.out.println("Enviant missatge: " + missatge);
      }
    } catch (IOException e) {
      System.err.println("Error enviant missatge: " + e.getMessage());
    }
  }

  public void tancarClient() {
    try {
      if (ois != null)
        ois.close();
      if (oos != null)
        oos.close();
      if (socket != null && !socket.isClosed())
        socket.close();

      System.out.println("Tancant client...");
    } catch (IOException e) {
      System.err.println("Error tancant el client: " + e.getMessage());
    }
  }

  public void run() {
    try {
      ois = new ObjectInputStream(socket.getInputStream());
      System.out.println("DEBUG: Iniciant recepció de missatges...");

      while (!sortir) {
        String missatgeRaw = (String) ois.readObject();

        String codi = Missatge.getCodiMissatge(missatgeRaw);

        String[] parts = Missatge.getPartsMissatge(missatgeRaw);

        if (codi == null) {
          System.out.println("ERROR: Missatge desconegut rebut.");
          continue;
        }

        switch (codi) {
          case Missatge.CODI_SORTIR_TOTS:
            sortir = true;
            System.out.println("DEBUG: multicast sortir");
            break;

          case Missatge.CODI_MSG_PERSONAL:
            if (parts.length >= 3) {
              String remitent = parts[1];
              String missatgePersonal = parts[2];
              System.out.println("Missatge personal de (" + remitent + "): " + missatgePersonal);
            } else {
              System.out.println("ERROR: Missatge personal mal format.");
            }
            break;

          case Missatge.CODI_MSG_GRUP:
            if (parts.length >= 2) {
              String missatgeGrup = parts[1];
              System.out.println("Missatge de grup: " + missatgeGrup);
            } else {
              System.out.println("ERROR: Missatge de grup mal format.");
            }
            break;

          default:
            System.out.println("ERROR: Codi desconegut: " + codi);
            break;
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error llegint missatge: " + e.getMessage());
    } finally {
      tancarClient();
    }
  }

  private void ajuda() {
    System.out.println("---------------------");
    System.out.println("Comandes disponibles:");
    System.out.println("1.- Conectar al servidor (primer pas obligatori)");
    System.out.println("2.- Enviar missatge personal");
    System.out.println("3.- Enviar missatge al grup");
    System.out.println("4.-(o línia en blanc)-> Sortir del client");
    System.out.println("5.- Finalitzar tothom");
    System.out.println("---------------------");
  }

  private String getLinea(Scanner scanner, String missatge, boolean obligatori) {
    String linia;
    do {
      System.out.print(missatge + " ");
      linia = scanner.nextLine().trim();
    } while (obligatori && linia.isEmpty());
    return linia;
  }

  public static void main(String[] args) {
    ClientXat client = new ClientXat();

    client.connecta();

    Thread filLectura = new Thread(() -> {
      try {
        client.run();
      } catch (Exception e) {
        System.err.println("Error en el fil de lectura: " + e.getMessage());
      }
    });
    filLectura.start();

    client.ajuda();

    // Scanner per llegir l'entrada de l'usuari
    Scanner scanner = new Scanner(System.in);
    boolean sortir = false;

    while (!sortir) {
      // Llegim una línia de l'usuari
      String linia = scanner.nextLine().trim();

      // Si la línia és buida, sortim
      if (linia.isEmpty()) {
        sortir = true;
      } else {
        try {
          int opcio = Integer.parseInt(linia);

          switch (opcio) {
            case 1:
              // Opció 1: Conectar al servidor
              String nom = client.getLinea(scanner, "Introdueix el nom:", true);
              String missatgeConectar = Missatge.getMissatgeConectar(nom);
              client.enviarMissatge(missatgeConectar);
              break;

            case 2:
              // Opció 2: Enviar missatge personal
              String destinatari = client.getLinea(scanner, "Destinatari:", true);
              String missatge = client.getLinea(scanner, "Missatge a enviar:", true);
              String missatgePersonal = Missatge.getMissatgePersonal(destinatari, missatge);
              client.enviarMissatge(missatgePersonal);
              break;

            case 3:
              // Opció 3: Enviar missatge al grup
              String msgGrup = client.getLinea(scanner, "Missatge a enviar al grup:", true);
              String missatgeGrup = Missatge.getMissatgeGrup(msgGrup);
              client.enviarMissatge(missatgeGrup);
              break;

            case 4:
              // Opció 4: Sortir del client
              String missatgeSortirClient = Missatge.getMissatgeSortirClient("Adéu");
              client.enviarMissatge(missatgeSortirClient);
              sortir = true;
              break;

            case 5:
              // Opció 5: Finalitzar tots els clients
              String missatgeSortirTots = Missatge.getMissatgeSortirTots("Adéu");
              client.enviarMissatge(missatgeSortirTots);
              sortir = true;
              break;

            default:
              System.out.println("Opció desconeguda.");
              break;
          }
        } catch (NumberFormatException e) {
          System.out.println("ERROR: Has d'introduir un número vàlid.");
        }
      }
    }

    scanner.close();
    client.tancarClient();
    System.out.println("Client finalitzat.");
  }
}