import java.io.*;

public class FilServidorXat extends Thread {

  private static final String MSG_SORTIR = "sortir";
  private ObjectInputStream input;
  private String nom;

  public FilServidorXat(ObjectInputStream input, String nom) {
    this.input = input;
    this.nom = nom;
  }

  @Override
  public void run() {
    boolean continua = true;
    try {
      while (continua) {
        String missatge = (String) input.readObject();
        System.out.println("Rebut: " + missatge);

        if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
          continua = false;
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
        System.err.println("Error al cerrar el ObjectInputStream del cliente " + nom + ": " + e.getMessage());
      }
    }

  }
}