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

import cinemax.model.Ruolo;
import cinemax.model.Utente;
import cinemax.util.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository per la gestione degli utenti.
 */
public class UtenteRepository {

    private final CsvManager csvManager;

    public UtenteRepository(String percorsoFile) {
        this.csvManager = new CsvManager(percorsoFile);
    }

    /**
     * Restituisce tutti gli utenti.
     *
     * @return lista utenti
     */
    public List<Utente> trovaTutti() {

        List<String[]> righe = csvManager.leggiTutto();
        List<Utente> utenti = new ArrayList<>();

        // Salta header CSV
        for (int i = 1; i < righe.size(); i++) {

            String[] campi = righe.get(i);

            long id = Long.parseLong(campi[0]);

            String nome = campi[1];
            String cognome = campi[2];
            String username = campi[3];
            String password = campi[4];

            LocalDate dataNascita = DateUtils.convertiDataPerCSV(campi[5]);

            String domicilio = campi[6];

            Ruolo ruolo = Ruolo.formatoCsv(campi[7]);

            Utente utente = new Utente(
                    id,
                    nome,
                    cognome,
                    username,
                    password,
                    dataNascita,
                    domicilio,
                    ruolo
            );

            utenti.add(utente);
        }

        return utenti;
    }

    /**
     * Cerca un utente tramite ID.
     *
     * @param id id utente
     * @return utente trovato oppure null
     */
    public Utente trovaPerId(long id) {

        for (Utente utente : trovaTutti()) {

            if (utente.getId() == id) {
                return utente;
            }
        }

        return null;
    }

    /**
     * Cerca un utente tramite username.
     *
     * @param username username utente
     * @return utente trovato oppure null
     */
    public Utente trovaPerUsername(String username) {

        for (Utente utente : trovaTutti()) {

            if (utente.getUsername()
                    .equalsIgnoreCase(username)) {

                return utente;
            }
        }

        return null;
    }

    /**
     * Salva un nuovo utente.
     *
     * @param utente utente da salvare
     */
    public void salva(Utente utente) {

        String[] riga = {
                String.valueOf(utente.getId()),
                utente.getNome(),
                utente.getCognome(),
                utente.getUsername(),
                utente.getPassword(),
                DateUtils.formatoCsv(
                        utente.getDataNascita()
                ),
                utente.getDomicilio(),
                utente.getRuolo().name()
        };

        csvManager.aggiungiRiga(riga);
    }

    /**
     * Verifica se uno username esiste già.
     *
     * @param username username da controllare
     * @return true se esiste
     */
    public boolean usernameEsiste(String username) {

        return trovaPerUsername(username) != null;
    }

    /**
     * Aggiorna completamente il file utenti.
     *
     * @param utenti lista aggiornata utenti
     */
    public void aggiorna(List<Utente> utenti) {

        List<String[]> righe = new ArrayList<>();

        // Header CSV
        righe.add(new String[]{
                "id",
                "nome",
                "cognome",
                "username",
                "password",
                "dataNascita",
                "domicilio",
                "ruolo"
        });

        for (Utente utente : utenti) {

            righe.add(new String[]{
                    String.valueOf(utente.getId()),
                    utente.getNome(),
                    utente.getCognome(),
                    utente.getUsername(),
                    utente.getPassword(),
                    DateUtils.formatoCsv(
                            utente.getDataNascita()
                    ),
                    utente.getDomicilio(),
                    utente.getRuolo().name()
            });
        }

        csvManager.scriviTutto(righe);
    }
}