import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() throws IOException {
        File file = new File(nom);
        if (!file.exists()) {
            throw new IOException("El fitxer " + nom + " no existeix.");
        }

        FileInputStream fis = new FileInputStream(file);
        contingut = new byte[(int) file.length()];
        fis.read(contingut);
        fis.close();

        return contingut;
    }

    public String getNom() {
        return nom;
    }
}