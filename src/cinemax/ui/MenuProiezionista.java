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
package cinemax.ui;

import cinemax.model.*;
import cinemax.repository.*;
import cinemax.service.*;
import cinemax.util.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu per utenti con ruolo PROIEZIONISTA.
 * <p>
 * Funzionalità disponibili:
 * <ul>
 *   <li>Visualizza palinsesto (tutte le proiezioni)</li>
 *   <li>Aggiungi una proiezione (selezionando o inserendo un film)</li>
 *   <li>Modifica data/costo di una proiezione (solo se senza prenotazioni)</li>
 *   <li>Elimina una proiezione (solo se senza prenotazioni)</li>
 *   <li>Logout</li>
 * </ul>
 */
public class MenuProiezionista {

    private final Utente proiezionista;
    private final FilmService filmService;
    private final FilmRepository filmRepo;
    private final ProiezioneService proiezioneService;
    private final ProiezioneRepository proiezioneRepo;
    private final PrenotazioneRepository prenotazioneRepo;

    /**
     * Costruttore del menu proiezionista.
     *
     * @param proiezionista  utente autenticato con ruolo PROIEZIONISTA
     * @param filmService    service dei film
     * @param filmRepo       repository dei film
     * @param proiezioneService service delle proiezioni
     * @param proiezioneRepo repository delle proiezioni
     * @param prenotazioneRepo repository delle prenotazioni
     */
    public MenuProiezionista(Utente proiezionista, FilmService filmService, FilmRepository filmRepo, ProiezioneService proiezioneService, ProiezioneRepository proiezioneRepo, PrenotazioneRepository prenotazioneRepo) {
        this.proiezionista = proiezionista;
        this.filmService = filmService;
        this.filmRepo = filmRepo;
        this.proiezioneService = proiezioneService;
        this.proiezioneRepo = proiezioneRepo;
        this.prenotazioneRepo = prenotazioneRepo;
    }

    /**
     * Avvia il loop del menu proiezionista.
     */
    public void avvia() {
        boolean esci = false;

        while (!esci) {
            stampaMenu();
            int scelta = Input.leggiIntero("Scelta: ", 0, 4);

            switch (scelta) {
                case 1 -> visualizzaPalinsesto();
                case 2 -> aggiungiProiezione();
                case 3 -> modificaProiezione();
                case 4 -> eliminaProiezione();
                case 0 -> esci = true;
            }
        }

        System.out.println("\nLogout effettuato. A presto, " + proiezionista.getNome() + "!");
        Input.attendiInvio();
    }


    // VISUALIZZA PALINSESTO
    /*
        Mostra tutte le proiezioni in programma, ordinate per data.
     */
    private void visualizzaPalinsesto() {
        MenuPrincipale.stampaSezione("PALINSESTO — TUTTE LE PROIEZIONI");

        List<Proiezione> proiezioni = proiezioneService.trovaTutte();

        if (proiezioni.isEmpty()) {
            System.out.println("  Nessuna proiezione in programma.");
            Input.attendiInvio();
            return;
        }

        System.out.printf("  %-4s %-28s %-14s %-18s %-8s %s%n",
                "ID", "FILM", "GENERE", "DATA/ORA", "COSTO", "PREN.");
        MenuPrincipale.stampaLinea();

        for (Proiezione p : proiezioni) {
            Film film = filmService.trovaPerId(p.getFilmId());
            int nPren = prenotazioneRepo.trovaPerProiezione(p.getId()).size();

            System.out.printf("  %-4d %-28s %-14s %-18s %-8.2f %d%n",
                    p.getId(),
                    MenuPrincipale.troncaStringa(film != null ? film.getTitolo() : "?", 27),
                    MenuPrincipale.troncaStringa(film != null ? film.getGenere() : "?", 13),
                    p.getDataOraFormattata(),
                    p.getCostoBiglietto(),
                    nPren);
        }

        MenuPrincipale.stampaLinea();
        Input.attendiInvio();
    }


    // AGGIUNGI PROIEZIONE
    /**
     * Aggiunge una nuova proiezione al palinsesto.
     * <p>
     * Il proiezionista può scegliere un film esistente oppure
     * inserirne uno nuovo. Successivamente inserisce data/ora
     * e costo del biglietto. La proiezione non deve sovrapporsi
     * con un'altra già programmata.
     */
    private void aggiungiProiezione() {
        MenuPrincipale.stampaSezione("AGGIUNGI PROIEZIONE");

        // --- Selezione film ---
        System.out.println("  [1] Scegli un film esistente");
        System.out.println("  [2] Inserisci un nuovo film");
        System.out.println("  [0] Annulla");
        MenuPrincipale.stampaLinea();

        int sceltaFilm = Input.leggiIntero("Scelta: ", 0, 2);
        if (sceltaFilm == 0) return;

        Film film;

        if (sceltaFilm == 1) {
            film = selezionaFilmEsistente();
        } else {
            film = inserisciNuovoFilm();
        }

        if (film == null) return;

        // --- Data/ora ---
        LocalDateTime dataOra = Input.leggiDataOra("Data e ora proiezione");

        // Controllo sovrapposizione (stessa giornata = stesso slot)
        if (proiezioneEsistenteInData(dataOra)) {
            System.out.println("[ERRORE] Esiste gia' una proiezione programmata per quel giorno e orario.");
            Input.attendiInvio();
            return;
        }

        // --- Costo biglietto ---
        double costo = Input.leggiDouble("Costo biglietto (€): ");
        while (costo <= 0) {
            System.out.println("[ERRORE] Il costo deve essere positivo.");
            costo = Input.leggiDouble("Costo biglietto (€): ");
        }

        // --- Riepilogo e conferma ---
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  RIEPILOGO NUOVA PROIEZIONE");
        MenuPrincipale.stampaLinea();
        System.out.printf("  Film      : %s (%d)%n", film.getTitolo(), film.getAnno());
        System.out.printf("  Genere    : %s%n", film.getGenere());
        System.out.printf("  Regista   : %s%n", film.getRegista());
        System.out.printf("  Data/Ora  : %s%n", DateUtils.formatoUi(dataOra));
        System.out.printf("  Biglietto : %.2f€%n", costo);
        MenuPrincipale.stampaLinea();

        if (!Input.leggiBoolean("Confermi l'inserimento?")) {
            System.out.println("Operazione annullata.");
            Input.attendiInvio();
            return;
        }

        proiezioneService.aggiungiProiezione(film.getId(), dataOra, costo);
        System.out.println("[OK] Proiezione aggiunta al palinsesto con successo.");
        Input.attendiInvio();
    }

    // MODIFICA PROIEZIONE
    /**
     * Modifica la data/ora e/o il costo di una proiezione esistente.
     * <p>
     * La modifica è consentita solo se non esistono prenotazioni
     * per quella proiezione.
     */
    private void modificaProiezione() {
        MenuPrincipale.stampaSezione("MODIFICA PROIEZIONE");

        visualizzaPalinsestoCompatto();

        long id = Input.leggiIntero("ID proiezione da modificare (0 per annullare): ");
        if (id == 0) return;

        Proiezione proiezione = proiezioneRepo.trovaPerId(id);
        if (proiezione == null) {
            System.out.println("[ERRORE] Proiezione non trovata.");
            Input.attendiInvio();
            return;
        }

        // Controllo prenotazioni esistenti
        if (haPrenotazioni(id)) {
            System.out.println("[ERRORE] Impossibile modificare: esistono prenotazioni per questa proiezione.");
            Input.attendiInvio();
            return;
        }

        Film film = filmService.trovaPerId(proiezione.getFilmId());
        System.out.println();
        System.out.printf("  Film corrente : %s%n", film != null ? film.getTitolo() : "?");
        System.out.printf("  Data/Ora att. : %s%n", proiezione.getDataOraFormattata());
        System.out.printf("  Costo att.    : %.2f€%n", proiezione.getCostoBiglietto());
        System.out.println();

        // --- Nuova data/ora ---
        LocalDateTime nuovaDataOra = Input.leggiDataOra("Nuova data e ora");

        // Controllo sovrapposizione (esclude la proiezione stessa)
        if (proiezioneEsistenteInDataEscluso(nuovaDataOra, id)) {
            System.out.println("Esiste gia' un'altra proiezione in quel giorno e orario.");
            Input.attendiInvio();
            return;
        }

        // --- Nuovo costo ---
        double nuovoCosto = Input.leggiDouble("Nuovo costo biglietto (€): ");
        while (nuovoCosto <= 0) {
            System.out.println("Il costo deve essere positivo.");
            nuovoCosto = Input.leggiDouble("Nuovo costo biglietto (€): ");
        }

        // --- Riepilogo e conferma ---
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  RIEPILOGO MODIFICA");
        MenuPrincipale.stampaLinea();
        System.out.printf("  Proiezione #%d — %s%n", id, film != null ? film.getTitolo() : "?");
        System.out.printf("  Data/Ora   : %s  →  %s%n",
                proiezione.getDataOraFormattata(), DateUtils.formatoUi(nuovaDataOra));
        System.out.printf("  Costo      : %.2f€  →  %.2f€%n",
                proiezione.getCostoBiglietto(), nuovoCosto);
        MenuPrincipale.stampaLinea();

        if (!Input.leggiBoolean("Confermi la modifica?")) {
            System.out.println("Modifica annullata.");
            Input.attendiInvio();
            return;
        }

        // Aggiornamento nel repository
        proiezione.setDataOra(nuovaDataOra);
        proiezione.setCostoBiglietto(nuovoCosto);
        // Sostituzione in-place: ricarica, modifica, riscrivi
        List<Proiezione> tutte = proiezioneRepo.trovaTutte();
        for (int i = 0; i < tutte.size(); i++) {
            if (tutte.get(i).getId() == id) {
                tutte.set(i, proiezione);
                break;
            }
        }
        proiezioneRepo.aggiorna(tutte);

        System.out.println("[OK] Proiezione #" + id + " modificata con successo.");
        Input.attendiInvio();
    }

    // ELIMINA PROIEZIONE
    /**
     * Elimina una proiezione dal palinsesto.
     * <p>
     * L'eliminazione è consentita solo se non esistono prenotazioni
     * per quella proiezione.
     */
    private void eliminaProiezione() {
        MenuPrincipale.stampaSezione("ELIMINA PROIEZIONE");

        visualizzaPalinsestoCompatto();

        long id = Input.leggiIntero("ID proiezione da eliminare (0 per annullare): ");
        if (id == 0) return;

        Proiezione proiezione = proiezioneRepo.trovaPerId(id);
        if (proiezione == null) {
            System.out.println("[ERRORE] Proiezione non trovata.");
            Input.attendiInvio();
            return;
        }

        // Controllo prenotazioni
        if (haPrenotazioni(id)) {
            System.out.println("[ERRORE] Impossibile eliminare: esistono prenotazioni per questa proiezione.");
            Input.attendiInvio();
            return;
        }

        Film film = filmService.trovaPerId(proiezione.getFilmId());

        // --- Riepilogo e conferma ---
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  PROIEZIONE DA ELIMINARE");
        MenuPrincipale.stampaLinea();
        System.out.printf("  ID        : #%d%n", proiezione.getId());
        System.out.printf("  Film      : %s%n", film != null ? film.getTitolo() : "?");
        System.out.printf("  Data/Ora  : %s%n", proiezione.getDataOraFormattata());
        System.out.printf("  Biglietto : %.2f€%n", proiezione.getCostoBiglietto());
        MenuPrincipale.stampaLinea();

        if (!Input.leggiBoolean("Sei sicuro di voler eliminare questa proiezione?")) {
            System.out.println("Eliminazione annullata.");
            Input.attendiInvio();
            return;
        }

        proiezioneService.eliminaProiezione(id);
        System.out.println("[OK] Proiezione #" + id + " eliminata con successo.");
        Input.attendiInvio();
    }

    // METODI DI SUPPORTO
    /**
     * Permette al proiezionista di selezionare un film dall'archivio.
     *
     * @return film selezionato, o null se annullato
     */
    private Film selezionaFilmEsistente() {
        List<Film> filmList = filmService.trovaTutti();

        if (filmList.isEmpty()) {
            System.out.println("[AVVISO] Nessun film nell'archivio. Inserisci un nuovo film.");
            return inserisciNuovoFilm();
        }

        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.printf("  %-4s %-28s %-14s %-6s %s%n",
                "ID", "TITOLO", "GENERE", "ANNO", "DURATA");
        MenuPrincipale.stampaLinea();

        for (Film f : filmList) {
            System.out.printf("  %-4d %-28s %-14s %-6d %d min%n",
                    f.getId(),
                    MenuPrincipale.troncaStringa(f.getTitolo(), 27),
                    MenuPrincipale.troncaStringa(f.getGenere(), 13),
                    f.getAnno(),
                    f.getDurataMinuti());
        }

        MenuPrincipale.stampaLinea();
        long idFilm = Input.leggiIntero("ID film (0 per annullare): ");
        if (idFilm == 0) return null;

        Film film = filmService.trovaPerId(idFilm);
        if (film == null) {
            System.out.println("Film non trovato.");
        }
        return film;
    }

    /**
     * Guida il proiezionista nell'inserimento di un nuovo film nell'archivio.
     *
     * @return film creato e salvato
     */
    private Film inserisciNuovoFilm() {
        MenuPrincipale.stampaSezione("INSERISCI NUOVO FILM");

        String titolo  = Input.leggiStringa("Titolo: ");
        String genere  = Input.leggiStringa("Genere (es. Drama, Action, Thriller): ");
        String regista = Input.leggiStringa("Regista: ");
        int anno       = Input.leggiIntero("Anno di uscita: ", 1888, 2100);
        int durata     = Input.leggiIntero("Durata (minuti): ", 1, 600);
        int etaMinima  = Input.leggiIntero("Eta' minima (0 = per tutti): ", 0, 18);

        // Riepilogo
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  RIEPILOGO NUOVO FILM");
        MenuPrincipale.stampaLinea();
        System.out.printf("  Titolo    : %s%n", titolo);
        System.out.printf("  Genere    : %s%n", genere);
        System.out.printf("  Regista   : %s%n", regista);
        System.out.printf("  Anno      : %d%n", anno);
        System.out.printf("  Durata    : %d min%n", durata);
        System.out.printf("  Eta' min. : %s%n",
                etaMinima == 0 ? "Per tutti" : etaMinima + " anni");
        MenuPrincipale.stampaLinea();

        if (!Input.leggiBoolean("Confermi l'inserimento del film?")) {
            System.out.println("Inserimento annullato.");
            return null;
        }

        Film nuovoFilm = filmService.creaFilm(titolo, genere, regista, anno, durata, etaMinima);
        System.out.println("[OK] Film '" + nuovoFilm.getTitolo() + "' aggiunto all'archivio (ID: " + nuovoFilm.getId() + ").");
        return nuovoFilm;
    }

    /**
     * Verifica se esiste già una proiezione programmata nella stessa data e orario.
     * Considera sovrapposta una proiezione nello stesso giorno entro un'ora di distanza.
     *
     * @param dataOra data e ora da controllare
     * @return true se esiste una sovrapposizione
     */
    private boolean proiezioneEsistenteInData(LocalDateTime dataOra) {
        for (Proiezione p : proiezioneRepo.trovaTutte()) {
            long diffMinuti = Math.abs(
                    java.time.Duration.between(p.getDataOra(), dataOra).toMinutes());
            if (diffMinuti < 60) return true;
        }
        return false;
    }

    /**
     * Come {@link #proiezioneEsistenteInData(LocalDateTime)}, ma esclude
     * la proiezione con l'ID specificato (usato durante la modifica).
     *
     * @param dataOra  data e ora da controllare
     * @param escludi  ID della proiezione da escludere dal controllo
     * @return true se esiste una sovrapposizione con un'altra proiezione
     */
    private boolean proiezioneEsistenteInDataEscluso(LocalDateTime dataOra, long escludi) {
        for (Proiezione p : proiezioneRepo.trovaTutte()) {
            if (p.getId() == escludi) continue;
            long diffMinuti = Math.abs(
                    java.time.Duration.between(p.getDataOra(), dataOra).toMinutes());
            if (diffMinuti < 60) return true;
        }
        return false;
    }

    /**
     * Verifica se una proiezione ha prenotazioni associate.
     *
     * @param proiezioneId ID della proiezione
     * @return true se esistono prenotazioni
     */
    private boolean haPrenotazioni(long proiezioneId) {
        return !prenotazioneRepo.trovaPerProiezione(proiezioneId).isEmpty();
    }


    // METODI TUI (visualizzazione)
    /** Stampa il menu del proiezionista. */
    private void stampaMenu() {
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  MENU PROIEZIONISTA  —  " +
                proiezionista.getNome() + " " + proiezionista.getCognome());
        MenuPrincipale.stampaLinea();
        System.out.println("  [1] Visualizza palinsesto");
        System.out.println("  [2] Aggiungi proiezione");
        System.out.println("  [3] Modifica proiezione");
        System.out.println("  [4] Elimina proiezione");
        System.out.println("  [0] Logout");
        MenuPrincipale.stampaLinea();
    }

    /**
     * Versione compatta del palinsesto (solo ID, film, data/ora),
     * usata come riferimento prima di modificare o eliminare.
     */
    private void visualizzaPalinsestoCompatto() {
        List<Proiezione> proiezioni = proiezioneService.trovaTutte();

        if (proiezioni.isEmpty()) {
            System.out.println("\n  Nessuna proiezione in programma.\n");
            return;
        }

        System.out.println();
        System.out.printf("  %-4s %-30s %-18s %-8s %s%n",
                "ID", "FILM", "DATA/ORA", "COSTO", "PREN.");
        MenuPrincipale.stampaLinea();

        for (Proiezione p : proiezioni) {
            Film film = filmService.trovaPerId(p.getFilmId());
            int nPren = prenotazioneRepo.trovaPerProiezione(p.getId()).size();

            System.out.printf("  %-4d %-30s %-18s %-8.2f %d%n",
                    p.getId(),
                    MenuPrincipale.troncaStringa(film != null ? film.getTitolo() : "?", 29),
                    p.getDataOraFormattata(),
                    p.getCostoBiglietto(),
                    nPren);
        }

        MenuPrincipale.stampaLinea();
    }
}
