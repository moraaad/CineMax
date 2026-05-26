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

import cinemax.model.Proiezione;
import cinemax.util.DateUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository per la gestione delle proiezioni.
 */
public class ProiezioneRepository {

    private final CsvManager csvManager;

    public ProiezioneRepository(String percorsoFile) {
        this.csvManager = new CsvManager(percorsoFile);
    }

    /**
     * Restituisce tutte le proiezioni.
     *
     * @return lista proiezioni
     */
    public List<Proiezione> trovaTutte() {

        List<String[]> righe = csvManager.leggiTutto();
        List<Proiezione> proiezioni = new ArrayList<>();

        // Salta header CSV
        for (int i = 1; i < righe.size(); i++) {

            String[] campi = righe.get(i);

            long id = Long.parseLong(campi[0]);
            long filmId = Long.parseLong(campi[1]);

            LocalDateTime dataOra =
                    DateUtils.convertiDataOraPerCSV(campi[2]);

            double costoBiglietto =
                    Double.parseDouble(campi[3]);

            Proiezione proiezione = new Proiezione(
                    id,
                    filmId,
                    dataOra,
                    costoBiglietto
            );

            proiezioni.add(proiezione);
        }

        return proiezioni;
    }

    /**
     * Cerca una proiezione tramite ID.
     *
     * @param id id proiezione
     * @return proiezione trovata oppure null
     */
    public Proiezione trovaPerId(long id) {
        for (Proiezione proiezione : trovaTutte()) {

            if (proiezione.getId() == id) {
                return proiezione;
            }
        }

        return null;
    }

    /**
     * Cerca un film tramite ID.
     * <p>
     * Utile per mostrare le proiezioni collegate al film
     *
     * @param filmId id film
     * @return film trovati
     */
    public List<Proiezione> trovaPerFilmId(long filmId) {
        List<Proiezione> result = new ArrayList<>();

        for (Proiezione p : trovaTutte()) {
            if (p.getFilmId() == filmId) {
                result.add(p);
            }
        }

        return result;
    }

    /**
     * Salva una nuova proiezione.
     *
     * @param proiezione proiezione da salvare
     */
    public void salva(Proiezione proiezione) {

        String[] riga = {
                String.valueOf(proiezione.getId()),
                String.valueOf(proiezione.getFilmId()),
                DateUtils.formatoCsv(
                        proiezione.getDataOra()
                ),
                String.valueOf(proiezione.getCostoBiglietto())
        };

        csvManager.aggiungiRiga(riga);
    }

    /**
     * Aggiorna completamente il file CSV.
     *
     * @param proiezioni lista aggiornata
     */
    public void aggiorna(List<Proiezione> proiezioni) {

        List<String[]> righe = new ArrayList<>();

        // Header CSV
        righe.add(new String[]{
                "id",
                "filmId",
                "dataOra",
                "costoBiglietto"
        });

        for (Proiezione proiezione : proiezioni) {

            righe.add(new String[]{
                    String.valueOf(proiezione.getId()),
                    String.valueOf(proiezione.getFilmId()),
                    DateUtils.formatoCsv(
                            proiezione.getDataOra()
                    ),
                    String.valueOf(proiezione.getCostoBiglietto())
            });
        }

        csvManager.scriviTutto(righe);
    }

    /**
     * Elimina una proiezione tramite ID.
     *
     * @param id id proiezione
     * @return true se tutto e' andato a buon fine
     */
    public boolean elimina(long id) {

        List<Proiezione> nuoveProiezioni = new ArrayList<>();

        for (Proiezione proiezione : trovaTutte()) {

            if (proiezione.getId() != id) {
                nuoveProiezioni.add(proiezione);
            }
        }

        aggiorna(nuoveProiezioni);
        return true;
    }
}