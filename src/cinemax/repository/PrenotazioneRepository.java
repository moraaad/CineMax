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

import cinemax.model.Prenotazione;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository per la gestione delle prenotazioni.
 * <p>
 * Si occupa di:
 * - leggere le prenotazioni dal file CSV
 * - salvare nuove prenotazioni
 * - cercare prenotazioni
 * - aggiornare o eliminare prenotazioni
 */
public class PrenotazioneRepository {
    private final CsvManager csvManager;

    public PrenotazioneRepository(String percorsoFile) {
        this.csvManager = new CsvManager(percorsoFile);
    }

    /**
     * Restituisce tutte le prenotazioni presenti nel file.
     *
     * @return lista prenotazioni
     */
    public List<Prenotazione> trovaTutte() {

        List<String[]> righe = csvManager.leggiTutto();
        List<Prenotazione> prenotazioni = new ArrayList<>();

        // Salta header CSV
        for (int i = 1; i < righe.size(); i++) {

            String[] campi = righe.get(i);

            long id = Long.parseLong(campi[0]);
            long clienteId = Long.parseLong(campi[1]);
            long proiezioneId = Long.parseLong(campi[2]);
            int numeroBiglietti = Integer.parseInt(campi[3]);

            Prenotazione prenotazione = new Prenotazione(
                    id,
                    clienteId,
                    proiezioneId,
                    numeroBiglietti
            );

            prenotazioni.add(prenotazione);
        }

        return prenotazioni;
    }

    /**
     * Salva una nuova prenotazione nel file CSV.
     *
     * @param prenotazione prenotazione da salvare
     */
    public void salva(Prenotazione prenotazione) {

        String[] riga = {
                String.valueOf(prenotazione.getId()),
                String.valueOf(prenotazione.getClienteId()),
                String.valueOf(prenotazione.getProiezioneId()),
                String.valueOf(prenotazione.getNumeroBiglietti())
        };

        csvManager.aggiungiRiga(riga);
    }

    /**
     * Cerca una prenotazione tramite ID.
     *
     * @param id id prenotazione
     * @return prenotazione trovata oppure null
     */
    public Prenotazione trovaPerId(long id) {

        List<Prenotazione> prenotazioni = trovaTutte();

        for (Prenotazione prenotazione : prenotazioni) {

            if (prenotazione.getId() == id) {
                return prenotazione;
            }
        }

        return null;
    }

    /**
     * Restituisce tutte le prenotazioni di un cliente.
     *
     * @param clienteId id cliente
     * @return lista prenotazioni cliente
     */
    public List<Prenotazione> trovaPerCliente(long clienteId) {

        List<Prenotazione> risultato = new ArrayList<>();

        for (Prenotazione prenotazione : trovaTutte()) {

            if (prenotazione.getClienteId() == clienteId) {
                risultato.add(prenotazione);
            }
        }

        return risultato;
    }

    /**
     * Restituisce tutte le proiezioni associate a quell'id.
     *
     * @param proiezioneId id proiezione
     * @return lista prenotazioni associate a proiezioneId
     */
    public List<Prenotazione> trovaPerProiezione(long proiezioneId) {
        List<Prenotazione> risultato =
                new ArrayList<>();

        for (Prenotazione prenotazione
                : trovaTutte()) {

            if (prenotazione.getProiezioneId()
                    == proiezioneId) {

                risultato.add(prenotazione);
            }
        }

        return risultato;
    }

    /**
     * Elimina una prenotazione tramite ID.
     *
     * @param id id prenotazione
     */
    public void elimina(long id) {

        List<Prenotazione> prenotazioni = trovaTutte();
        List<String[]> righeNuove = new ArrayList<>();

        // Header CSV
        righeNuove.add(new String[]{
                "id",
                "clienteId",
                "proiezioneId",
                "numeroBiglietti"
        });

        for (Prenotazione prenotazione : prenotazioni) {

            if (prenotazione.getId() != id) {
                righeNuove.add(new String[]{
                        String.valueOf(prenotazione.getId()),
                        String.valueOf(prenotazione.getClienteId()),
                        String.valueOf(prenotazione.getProiezioneId()),
                        String.valueOf(prenotazione.getNumeroBiglietti())
                });
            }
        }

        csvManager.scriviTutto(righeNuove);
    }
}