/**
 * CineMax - Laboratorio Interdisciplinare A
 *
 * Autori:
 * - Trupia Giovanni 766370 Como
 * - Mahhay Harman 762686 Como
 * - Ait Laarabi Morad 762740 Como
 * - Maatouch Ayman 766465 Como
 *
 * JDK 21
 */
package cinemax.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utility per leggere e scrivere file CSV.
 * <p>
 * Gestisce:
 * - Lettura completa di un file CSV
 * - Scrittura completa
 * - Aggiunta di nuove righe
 */
public class CsvManager {

    private final String percorsoFile;
    private final String SEPARATORE = ",";

    /**
     * Costruttore standard.
     *
     * @param percorsoFile percorso del file CSV
     */
    public CsvManager(String percorsoFile) {
        this.percorsoFile = percorsoFile;
    }

    /**
     * Legge tutte le righe del file CSV.
     *
     * @return lista di righe, dove ogni riga è un array di stringhe
     */
    public List<String[]> leggiTutto() {
        List<String[]> righe = new ArrayList<>();

        // try che chiude in automatico la stream della lettura (alternativa a finally)
        try (BufferedReader reader = new BufferedReader(new FileReader(percorsoFile))) {
            String riga;

            while ((riga = reader.readLine()) != null) {
                righe.add(riga.split(SEPARATORE, -1));
            }

        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + percorsoFile);
        }

        return righe;
    }

    /**
     * Sovrascrive il file CSV, partendo dalla seconda riga (la prima contiene l'header).
     *
     * @param righe lista di righe da scrivere
     */
    public void scriviTutto(List<String[]> righe) {
        if (righe == null || righe.isEmpty()) {
            return;
        }

        // try che chiude in automatico la stream della scrittura (alternativa a finally)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(percorsoFile))) {

            writer.write(String.join(SEPARATORE, righe.getFirst()));
            writer.newLine();

            // Scrive solo i dati (dalla seconda riga in poi)
            for (int i = 1; i < righe.size(); i++) {
                String riga = String.join(SEPARATORE, righe.get(i));
                writer.write(riga);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + percorsoFile);
        }
    }

    /**
     * Aggiunge una nuova riga al file CSV.
     *
     * @param colonne dati della riga
     */
    public void aggiungiRiga(String[] colonne) {
        // try che chiude in automatico la stream della scrittura (alternativa a finally)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(percorsoFile, true))) {

            String riga = String.join(SEPARATORE, colonne);

            writer.write(riga);
            writer.newLine();

        } catch (IOException e) {
            System.err.println("Errore durante l'aggiunta della riga.");
        }
    }

    public String getPercorsoFile() { return percorsoFile; }
}