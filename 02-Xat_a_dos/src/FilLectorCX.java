import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class FilLectorCX extends Thread {

  private ObjectOutputStream output;

  public FilLectorCX(ObjectOutputStream output) {
    this.output = output;
  }

  @Override
  public void run() {
    try (
        Scanner scanner = new Scanner(System.in)) {
      boolean continuar = true;
      while (continuar) {
        System.out.print("Enviar missatge: ");
        String missatge = scanner.nextLine();

        output.writeObject(missatge);
        output.flush();

        if (missatge.equalsIgnoreCase(ClientXat.MSG_SORTIR)) {
          continuar = false;
          System.out.println("Conexión cerrada por el cliente.");
        }
      }
    } catch (IOException e) {
      System.err.println("Error al enviar el mensaje: " + e.getMessage());
    }
  }
}