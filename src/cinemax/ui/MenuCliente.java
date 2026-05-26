package cinemax.ui;

import cinemax.model.*;
import cinemax.repository.*;
import cinemax.service.*;
import cinemax.util.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu per utenti con ruolo CLIENTE.
 * <p>
 * Funzionalità disponibili:
 * <ul>
 *   <li>Cerca proiezioni</li>
 *   <li>Visualizza dettagli di una proiezione</li>
 *   <li>Effettua una prenotazione</li>
 *   <li>Visualizza le proprie prenotazioni</li>
 *   <li>Modifica una prenotazione (cambio data)</li>
 *   <li>Cancella una prenotazione</li>
 *   <li>Logout</li>
 * </ul>
 */
public class MenuCliente {

    private final Utente cliente;
    private final FilmService filmService;
    private final FilmRepository filmRepo;
    private final ProiezioneService proiezioneService;
    private final ProiezioneRepository proiezioneRepo;
    private final PrenotazioneService prenotazioneService;
    private final PrenotazioneRepository prenotazioneRepo;
    private final UtenteRepository utenteRepo;

    /**
     * Costruttore del menu cliente.
     *
     * @param cliente           utente autenticato
     * @param filmService       service dei film
     * @param filmRepo          repository dei film
     * @param proiezioneService service delle proiezioni
     * @param proiezioneRepo    repository delle proiezioni
     * @param prenotazioneService service delle prenotazioni
     * @param prenotazioneRepo  repository delle prenotazioni
     * @param utenteRepo        repository degli utenti
     */
    public MenuCliente(Utente cliente, FilmService filmService, FilmRepository filmRepo, ProiezioneService proiezioneService, ProiezioneRepository proiezioneRepo, PrenotazioneService prenotazioneService, PrenotazioneRepository prenotazioneRepo, UtenteRepository utenteRepo) {
        this.cliente = cliente;
        this.filmService = filmService;
        this.filmRepo = filmRepo;
        this.proiezioneService = proiezioneService;
        this.proiezioneRepo = proiezioneRepo;
        this.prenotazioneService = prenotazioneService;
        this.prenotazioneRepo = prenotazioneRepo;
        this.utenteRepo = utenteRepo;
    }

    // Avvia il loop del menu cliente.
    public void avvia() {
        boolean esci = false;

        while (!esci) {
            stampaMenu();
            int scelta = Input.leggiIntero("Scelta: ", 0, 6);

            switch (scelta) {
                case 1 -> cercaProiezioni();
                case 2 -> visualizzaProiezioneDaId();
                case 3 -> creaPrenotazione();
                case 4 -> visualizzaMiePrenotazioni();
                case 5 -> modificaPrenotazione();
                case 6 -> eliminaPrenotazione();
                case 0 -> esci = true;
            }
        }

        System.out.println("\nLogout effettuato. A presto, " + cliente.getNome() + "!");
        Input.attendiInvio();
    }

    /*
     Ricerca proiezioni con filtri multipli:
     titolo (parziale), genere, intervallo date, costo biglietto,
     o una combinazione di essi.

     */
    private void cercaProiezioni() {
        MenuPrincipale.stampaSezione("CERCA PROIEZIONI");
        System.out.println("Lascia vuoto un campo per non applicare quel filtro.\n");

        System.out.print("Titolo film (parziale): ");
        String filtroTitolo = MenuPrincipale.leggiStringaOpzionale();

        System.out.print("Genere (es. Drama, Action): ");
        String filtroGenere = MenuPrincipale.leggiStringaOpzionale();

        LocalDateTime dataInizio = null;
        LocalDateTime dataFine   = null;
        if (Input.leggiBoolean("Filtrare per intervallo di date?")) {
            dataInizio = Input.leggiData("Data inizio").atStartOfDay();
            dataFine   = Input.leggiData("Data fine  ").atTime(LocalTime.MAX);
        }

        double minCosto = -1, maxCosto = -1;
        if (Input.leggiBoolean("Filtrare per costo biglietto?")) {
            minCosto = Input.leggiDouble("Costo minimo (€): ");
            maxCosto = Input.leggiDouble("Costo massimo (€): ");
        }

        List<Proiezione> risultati = filtraProiezioni(
                filtroTitolo, filtroGenere, dataInizio, dataFine, minCosto, maxCosto);

        stampaListaProiezioni(risultati);
        Input.attendiInvio();
    }

    // =========================================================
    // VISUALIZZA PROIEZIONE
    // =========================================================

    /**
     * Chiede l'ID di una proiezione e ne mostra i dettagli completi.
     */
    private void visualizzaProiezioneDaId() {
        MenuPrincipale.stampaSezione("VISUALIZZA PROIEZIONE");
        long id = Input.leggiIntero("ID proiezione (0 per annullare): ");
        if (id == 0) return;

        Proiezione p = proiezioneService.trovaPerId(id);
        if (p == null) {
            System.out.println("Proiezione non trovata.");
            Input.attendiInvio();
            return;
        }
        stampaDettaglioProiezione(p);
        Input.attendiInvio();
    }


    //  Permette al cliente di prenotare posti per una proiezione.

    private void creaPrenotazione() {
        MenuPrincipale.stampaSezione("NUOVA PRENOTAZIONE");

        // Prima mostra le proiezioni disponibili
        System.out.println("Proiezioni disponibili:\n");
        List<Proiezione> future = proiezioniFuture();
        stampaListaProiezioni(future);

        if (future.isEmpty()) {
            System.out.println("Nessuna proiezione futura disponibile.");
            Input.attendiInvio();
            return;
        }

        long idProiezione = Input.leggiIntero("ID proiezione da prenotare (0 per annullare): ");
        if (idProiezione == 0) return;

        Proiezione p = proiezioneService.trovaPerId(idProiezione);
        if (p == null) {
            System.out.println("Proiezione non trovata.");
            Input.attendiInvio();
            return;
        }

        if (DateUtils.primaDiOggi(p.getDataOra())) {
            System.out.println("Non puoi prenotare una proiezione gia' passata.");
            Input.attendiInvio();
            return;
        }

        int postiDisponibili = prenotazioneService.calcolaPostiDisponibili(idProiezione);
        System.out.printf("Posti disponibili: %d%n", postiDisponibili);

        if (postiDisponibili == 0) {
            System.out.println("Nessun posto disponibile per questa proiezione.");
            Input.attendiInvio();
            return;
        }

        int numBiglietti = Input.leggiIntero(
                "Numero di biglietti (max 10): ", 1, Math.min(10, postiDisponibili));

        // Riepilogo
        Film film = filmService.trovaPerId(p.getFilmId());
        double totale = p.getCostoBiglietto() * numBiglietti;
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  RIEPILOGO PRENOTAZIONE");
        MenuPrincipale.stampaLinea();
        System.out.printf("  Film      : %s%n", film != null ? film.getTitolo() : "?");
        System.out.printf("  Data/Ora  : %s%n", p.getDataOraFormattata());
        System.out.printf("  Biglietti : %d x %.2f€ = %.2f€%n",
                numBiglietti, p.getCostoBiglietto(), totale);
        MenuPrincipale.stampaLinea();

        if (!Input.leggiBoolean("Confermi la prenotazione?")) {
            System.out.println("Prenotazione annullata.");
            Input.attendiInvio();
            return;
        }

        try {
            prenotazioneService.creaPrenotazione(cliente.getId(), idProiezione, numBiglietti);
            // Recupera l'ID dell'ultima prenotazione creata
            List<Prenotazione> mie = prenotazioneService.trovaPerCliente(cliente.getId());
            long idNuova = mie.isEmpty() ? -1 : mie.get(mie.size() - 1).getId();
            System.out.printf("%n[OK] Prenotazione confermata! Codice prenotazione: #%d%n", idNuova);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        Input.attendiInvio();
    }

    // Mostra tutte le prenotazioni effettuate dal cliente.

    private void visualizzaMiePrenotazioni() {
        MenuPrincipale.stampaSezione("LE MIE PRENOTAZIONI");

        List<Prenotazione> prenotazioni = prenotazioneService.trovaPerCliente(cliente.getId());

        if (prenotazioni.isEmpty()) {
            System.out.println("  Nessuna prenotazione trovata.");
            Input.attendiInvio();
            return;
        }

        MenuPrincipale.stampaLinea();
        System.out.printf("  %-6s %-26s %-18s %-8s %s%n",
                "COD.", "FILM", "DATA/ORA", "BIGL.", "TOTALE");
        MenuPrincipale.stampaLinea();

        for (Prenotazione pr : prenotazioni) {
            Proiezione p  = proiezioneRepo.trovaPerId(pr.getProiezioneId());
            Film film     = p != null ? filmService.trovaPerId(p.getFilmId()) : null;
            double totale = p != null ? p.getCostoBiglietto() * pr.getNumeroBiglietti() : 0;

            System.out.printf("  %-6d %-26s %-18s %-8d %.2f€%n",
                    pr.getId(),
                    MenuPrincipale.troncaStringa(film != null ? film.getTitolo() : "?", 25),
                    p != null ? p.getDataOraFormattata() : "?",
                    pr.getNumeroBiglietti(),
                    totale);
        }

        MenuPrincipale.stampaLinea();
        Input.attendiInvio();
    }

    /**
     * Permette di cambiare la data/proiezione di una prenotazione esistente.
     * Condizione: sia la data attuale della proiezione che quella nuova
     * devono essere successive a oggi.
     */
    private void modificaPrenotazione() {
        MenuPrincipale.stampaSezione("MODIFICA PRENOTAZIONE");

        List<Prenotazione> mie = prenotazioneService.trovaPerCliente(cliente.getId());
        if (mie.isEmpty()) {
            System.out.println("  Nessuna prenotazione da modificare.");
            Input.attendiInvio();
            return;
        }

        // Mostra solo le prenotazioni future
        List<Prenotazione> modificabili = new ArrayList<>();
        for (Prenotazione pr : mie) {
            Proiezione p = proiezioneRepo.trovaPerId(pr.getProiezioneId());
            if (p != null && DateUtils.dopoDiOggi(p.getDataOra())) {
                modificabili.add(pr);
            }
        }

        if (modificabili.isEmpty()) {
            System.out.println("  Nessuna prenotazione modificabile (tutte le proiezioni sono gia' passate).");
            Input.attendiInvio();
            return;
        }

        stampaListaPrenotazioniCliente(modificabili);

        long idPren = Input.leggiIntero("Codice prenotazione da modificare (0 per annullare): ");
        if (idPren == 0) return;

        Prenotazione prenotazione = trovaMiaPrenotazione(modificabili, idPren);
        if (prenotazione == null) {
            System.out.println("[ERRORE] Prenotazione non trovata o non modificabile.");
            Input.attendiInvio();
            return;
        }

        // Mostra proiezioni future disponibili per lo stesso film
        Proiezione vecchiaProiezione = proiezioneRepo.trovaPerId(prenotazione.getProiezioneId());
        System.out.println("\nProiezioni future disponibili:");
        List<Proiezione> alternative = proiezioniFuturePerFilm(
                vecchiaProiezione != null ? vecchiaProiezione.getFilmId() : -1);

        if (alternative.isEmpty()) {
            System.out.println("  Nessuna proiezione futura alternativa disponibile per questo film.");
            Input.attendiInvio();
            return;
        }

        stampaListaProiezioni(alternative);

        long idNuovaProiezione = Input.leggiIntero(
                "ID nuova proiezione (0 per annullare): ");
        if (idNuovaProiezione == 0) return;

        Proiezione nuovaProiezione = proiezioneRepo.trovaPerId(idNuovaProiezione);
        if (nuovaProiezione == null || !DateUtils.dopoDiOggi(nuovaProiezione.getDataOra())) {
            System.out.println("Proiezione non valida o gia' passata.");
            Input.attendiInvio();
            return;
        }

        // Verifica posti disponibili nella nuova proiezione
        int postiNuova = prenotazioneService.calcolaPostiDisponibili(idNuovaProiezione);
        if (prenotazione.getNumeroBiglietti() > postiNuova) {
            System.out.printf("Posti insufficienti nella nuova proiezione (%d disponibili).%n", postiNuova);
            Input.attendiInvio();
            return;
        }

        if (!Input.leggiBoolean("Confermi la modifica?")) {
            System.out.println("Modifica annullata.");
            Input.attendiInvio();
            return;
        }

        // Esecuzione: elimina vecchia prenotazione e crea nuova con stessi biglietti
        prenotazioneRepo.elimina(prenotazione.getId());
        try {
            prenotazioneService.creaPrenotazione(
                    cliente.getId(), idNuovaProiezione, prenotazione.getNumeroBiglietti());
            List<Prenotazione> aggiornate = prenotazioneService.trovaPerCliente(cliente.getId());
            long idNuova = aggiornate.isEmpty() ? -1 : aggiornate.get(aggiornate.size() - 1).getId();
            System.out.printf("[OK] Prenotazione modificata. Nuovo codice: #%d%n", idNuova);
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRORE] " + e.getMessage());
        }

        Input.attendiInvio();
    }

    /**
     * Cancella una prenotazione del cliente.
     * Condizione: la data della proiezione deve essere futura
     * (non si può cancellare una prenotazione per uno spettacolo già avvenuto).
     */
    private void eliminaPrenotazione() {
        MenuPrincipale.stampaSezione("CANCELLA PRENOTAZIONE");

        List<Prenotazione> mie = prenotazioneService.trovaPerCliente(cliente.getId());
        if (mie.isEmpty()) {
            System.out.println("  Nessuna prenotazione da cancellare.");
            Input.attendiInvio();
            return;
        }

        // Solo prenotazioni future sono cancellabili
        List<Prenotazione> cancellabili = new ArrayList<>();
        for (Prenotazione pr : mie) {
            Proiezione p = proiezioneRepo.trovaPerId(pr.getProiezioneId());
            if (p != null && DateUtils.dopoDiOggi(p.getDataOra())) {
                cancellabili.add(pr);
            }
        }

        if (cancellabili.isEmpty()) {
            System.out.println("  Nessuna prenotazione cancellabile (tutte le proiezioni sono gia' passate).");
            Input.attendiInvio();
            return;
        }

        stampaListaPrenotazioniCliente(cancellabili);

        long idPren = Input.leggiIntero("Codice prenotazione da cancellare (0 per annullare): ");
        if (idPren == 0) return;

        Prenotazione prenotazione = trovaMiaPrenotazione(cancellabili, idPren);
        if (prenotazione == null) {
            System.out.println("[ERRORE] Prenotazione non trovata o non cancellabile.");
            Input.attendiInvio();
            return;
        }

        // Riepilogo
        Proiezione p = proiezioneRepo.trovaPerId(prenotazione.getProiezioneId());
        Film film = p != null ? filmService.trovaPerId(p.getFilmId()) : null;
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.printf("  Codice    : #%d%n", prenotazione.getId());
        System.out.printf("  Film      : %s%n", film != null ? film.getTitolo() : "?");
        System.out.printf("  Data/Ora  : %s%n", p != null ? p.getDataOraFormattata() : "?");
        System.out.printf("  Biglietti : %d%n", prenotazione.getNumeroBiglietti());
        MenuPrincipale.stampaLinea();

        if (!Input.leggiBoolean("Sei sicuro di voler cancellare questa prenotazione?")) {
            System.out.println("Cancellazione annullata.");
            Input.attendiInvio();
            return;
        }

        prenotazioneService.eliminaPrenotazione(prenotazione.getId());
        System.out.println("[OK] Prenotazione #" + prenotazione.getId() + " cancellata con successo.");
        Input.attendiInvio();
    }

    // METODI DI SUPPORTO
    /**
     * Filtra le proiezioni in base ai criteri forniti.
     *
     * @param titolo      filtro sul titolo del film (parziale, case-insensitive)
     * @param genere      filtro sul genere (parziale, case-insensitive)
     * @param dataInizio  filtro data inizio (null = nessun limite)
     * @param dataFine    filtro data fine (null = nessun limite)
     * @param minCosto    filtro costo minimo (-1 = nessun limite)
     * @param maxCosto    filtro costo massimo (-1 = nessun limite)
     * @return lista proiezioni che soddisfano tutti i filtri
     */
    private List<Proiezione> filtraProiezioni(
            String titolo, String genere,
            LocalDateTime dataInizio, LocalDateTime dataFine,
            double minCosto, double maxCosto) {

        List<Proiezione> risultati = new ArrayList<>();

        for (Proiezione p : proiezioneService.trovaTutte()) {
            Film f = filmService.trovaPerId(p.getFilmId());
            if (f == null) continue;

            if (!titolo.isEmpty() &&
                    !f.getTitolo().toLowerCase().contains(titolo.toLowerCase())) continue;
            if (!genere.isEmpty() &&
                    !f.getGenere().toLowerCase().contains(genere.toLowerCase())) continue;
            if (dataInizio != null && p.getDataOra().isBefore(dataInizio)) continue;
            if (dataFine   != null && p.getDataOra().isAfter(dataFine))   continue;
            if (minCosto >= 0 && p.getCostoBiglietto() < minCosto) continue;
            if (maxCosto >= 0 && p.getCostoBiglietto() > maxCosto) continue;

            risultati.add(p);
        }

        return risultati;
    }

    /**
     * Restituisce tutte le proiezioni con data futura.
     *
     * @return lista proiezioni future
     */
    private List<Proiezione> proiezioniFuture() {
        List<Proiezione> future = new ArrayList<>();
        for (Proiezione p : proiezioneService.trovaTutte()) {
            if (DateUtils.dopoDiOggi(p.getDataOra())) future.add(p);
        }
        return future;
    }

    /**
     * Restituisce le proiezioni future per un film specifico.
     *
     * @param filmId ID del film
     * @return lista proiezioni future per quel film
     */
    private List<Proiezione> proiezioniFuturePerFilm(long filmId) {
        List<Proiezione> risultato = new ArrayList<>();
        for (Proiezione p : proiezioneRepo.trovaPerFilmId(filmId)) {
            if (DateUtils.dopoDiOggi(p.getDataOra())) risultato.add(p);
        }
        return risultato;
    }

    /**
     * Cerca una prenotazione per ID all'interno di una lista.
     *
     * @param lista lista in cui cercare
     * @param id    ID della prenotazione
     * @return prenotazione trovata, o null
     */
    private Prenotazione trovaMiaPrenotazione(List<Prenotazione> lista, long id) {
        for (Prenotazione pr : lista) {
            if (pr.getId() == id) return pr;
        }
        return null;
    }

    // =========================================================
    // METODI TUI (visualizzazione)
    // =========================================================

    /** Stampa il menu del cliente. */
    private void stampaMenu() {
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  MENU CLIENTE  —  " + cliente.getNome() + " " + cliente.getCognome());
        MenuPrincipale.stampaLinea();
        System.out.println("  [1] Cerca proiezioni");
        System.out.println("  [2] Visualizza dettagli proiezione");
        System.out.println("  [3] Effettua una prenotazione");
        System.out.println("  [4] Le mie prenotazioni");
        System.out.println("  [5] Modifica una prenotazione");
        System.out.println("  [6] Cancella una prenotazione");
        System.out.println("  [0] Logout");
        MenuPrincipale.stampaLinea();
    }

    /**
     * Stampa una tabella con la lista delle proiezioni.
     *
     * @param proiezioni lista proiezioni da visualizzare
     */
    private void stampaListaProiezioni(List<Proiezione> proiezioni) {
        System.out.println();
        MenuPrincipale.stampaLinea();
        if (proiezioni.isEmpty()) {
            System.out.println("  Nessuna proiezione trovata.");
        } else {
            System.out.printf("  %-4s %-28s %-14s %-18s %-8s %s%n",
                    "ID", "FILM", "GENERE", "DATA/ORA", "COSTO", "POSTI");
            MenuPrincipale.stampaLinea();
            for (Proiezione p : proiezioni) {
                Film f = filmService.trovaPerId(p.getFilmId());
                int posti = prenotazioneService.calcolaPostiDisponibili(p.getId());
                System.out.printf("  %-4d %-28s %-14s %-18s %-8.2f %d%n",
                        p.getId(),
                        MenuPrincipale.troncaStringa(f != null ? f.getTitolo() : "?", 27),
                        MenuPrincipale.troncaStringa(f != null ? f.getGenere() : "?", 13),
                        p.getDataOraFormattata(),
                        p.getCostoBiglietto(),
                        posti);
            }
        }
        MenuPrincipale.stampaLinea();
    }

    /**
     * Stampa i dettagli completi di una proiezione.
     *
     * @param p proiezione da visualizzare
     */
    private void stampaDettaglioProiezione(Proiezione p) {
        Film film = filmService.trovaPerId(p.getFilmId());
        int postiLiberi = prenotazioneService.calcolaPostiDisponibili(p.getId());

        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  DETTAGLI PROIEZIONE #" + p.getId());
        MenuPrincipale.stampaLinea();
        if (film != null) {
            System.out.printf("  Titolo    : %s%n", film.getTitolo());
            System.out.printf("  Genere    : %s%n", film.getGenere());
            System.out.printf("  Regista   : %s%n", film.getRegista());
            System.out.printf("  Anno      : %d%n", film.getAnno());
            System.out.printf("  Durata    : %d min%n", film.getDurataMinuti());
            System.out.printf("  Eta' min. : %s%n",
                    film.getEtaMinima() == 0 ? "Per tutti" : film.getEtaMinima() + " anni");
        } else {
            System.out.println("  [ATTENZIONE] Dati film non disponibili.");
        }
        MenuPrincipale.stampaLinea();
        System.out.printf("  Data/Ora  : %s%n", p.getDataOraFormattata());
        System.out.printf("  Biglietto : %.2f€%n", p.getCostoBiglietto());
        System.out.printf("  Posti lib.: %d / %d%n", postiLiberi, Proiezione.CAPACITA_SALA);
        MenuPrincipale.stampaLinea();
    }

    /**
     * Stampa una tabella con le prenotazioni del cliente.
     *
     * @param prenotazioni lista prenotazioni da visualizzare
     */
    private void stampaListaPrenotazioniCliente(List<Prenotazione> prenotazioni) {
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.printf("  %-6s %-26s %-18s %s%n",
                "COD.", "FILM", "DATA/ORA", "BIGL.");
        MenuPrincipale.stampaLinea();

        for (Prenotazione pr : prenotazioni) {
            Proiezione p = proiezioneRepo.trovaPerId(pr.getProiezioneId());
            Film film    = p != null ? filmService.trovaPerId(p.getFilmId()) : null;
            System.out.printf("  %-6d %-26s %-18s %d%n",
                    pr.getId(),
                    MenuPrincipale.troncaStringa(film != null ? film.getTitolo() : "?", 25),
                    p != null ? p.getDataOraFormattata() : "?",
                    pr.getNumeroBiglietti());
        }

        MenuPrincipale.stampaLinea();
    }
}
