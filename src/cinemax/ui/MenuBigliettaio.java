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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu per utenti con ruolo BIGLIETTAIO.
 * <p>
 * Funzionalità disponibili:
 * <ul>
 *   <li>Visualizza prenotazioni di oggi</li>
 *   <li>Cerca prenotazioni (per codice, nome cliente, titolo film, date)</li>
 *   <li>Visualizza dettaglio di una prenotazione</li>
 *   <li>Logout</li>
 * </ul>
 */
public class MenuBigliettaio {

    private final Utente bigliettaio;
    private final FilmService filmService;
    private final FilmRepository filmRepo;
    private final ProiezioneService proiezioneService;
    private final ProiezioneRepository proiezioneRepo;
    private final PrenotazioneRepository prenotazioneRepo;
    private final UtenteRepository utenteRepo;

    /**
     * Costruttore del menu bigliettaio.
     *
     * @param bigliettaio    utente autenticato con ruolo BIGLIETTAIO
     * @param filmService    service dei film
     * @param filmRepo       repository dei film
     * @param proiezioneService service delle proiezioni
     * @param proiezioneRepo repository delle proiezioni
     * @param prenotazioneRepo repository delle prenotazioni
     * @param utenteRepo     repository degli utenti
     */
    public MenuBigliettaio(Utente bigliettaio, FilmService filmService, FilmRepository filmRepo, ProiezioneService proiezioneService, ProiezioneRepository proiezioneRepo, PrenotazioneRepository prenotazioneRepo, UtenteRepository utenteRepo) {
        this.bigliettaio = bigliettaio;
        this.filmService = filmService;
        this.filmRepo = filmRepo;
        this.proiezioneService = proiezioneService;
        this.proiezioneRepo = proiezioneRepo;
        this.prenotazioneRepo = prenotazioneRepo;
        this.utenteRepo = utenteRepo;
    }
    /**
     * Avvia il loop del menu bigliettaio.
     */
    public void avvia() {
        boolean esci = false;

        while (!esci) {
            stampaMenu();
            int scelta = Input.leggiIntero("Scelta: ", 0, 3);

            switch (scelta) {
                case 1 -> visualizzaPrenotazioniOggi();
                case 2 -> cercaPrenotazione();
                case 3 -> visualizzaPrenotazioneDaId();
                case 0 -> esci = true;
            }
        }

        System.out.println("\nLogout effettuato. A presto, " + bigliettaio.getNome() + "!");
        Input.attendiInvio();
    }

    // PRENOTAZIONI DI OGGI
    /**
     * Mostra tutte le prenotazioni per le proiezioni della giornata odierna.
     */
    private void visualizzaPrenotazioniOggi() {
        MenuPrincipale.stampaSezione("PRENOTAZIONI DI OGGI - " +
                DateUtils.formatoUi(LocalDate.now()));

        LocalDate oggi = LocalDate.now();
        List<Prenotazione> risultati = new ArrayList<>();

        for (Prenotazione pr : prenotazioneRepo.trovaTutte()) {
            Proiezione p = proiezioneRepo.trovaPerId(pr.getProiezioneId());
            if (p != null && p.getData().equals(oggi)) {
                risultati.add(pr);
            }
        }

        if (risultati.isEmpty()) {
            System.out.println("  Nessuna prenotazione per oggi.");
        } else {
            System.out.printf("  Prenotazioni trovate: %d%n%n", risultati.size());
            stampaTabella(risultati);
        }

        Input.attendiInvio();
    }
    //Cerca date
    /**
     * Interfaccia di ricerca prenotazioni con filtri multipli.
     * Criteri disponibili: codice prenotazione, nome/cognome cliente,
     * titolo film (parziale), intervallo di date.
     */
    private void cercaPrenotazione() {
        MenuPrincipale.stampaSezione("CERCA PRENOTAZIONE");
        System.out.println("Seleziona il criterio di ricerca:\n");
        System.out.println("  [1] Per codice prenotazione");
        System.out.println("  [2] Per nome e cognome del cliente");
        System.out.println("  [3] Per titolo del film (anche parziale)");
        System.out.println("  [4] Per intervallo di date");
        System.out.println("  [0] Torna al menu");
        MenuPrincipale.stampaLinea();

        int criterio = Input.leggiIntero("Criterio: ", 0, 4);

        List<Prenotazione> risultati = new ArrayList<>();

        switch (criterio) {
            case 0 -> { return; }

            case 1 -> {
                // Per codice
                long codice = Input.leggiIntero("Codice prenotazione: ");
                Prenotazione pr = prenotazioneRepo.trovaPerId(codice);
                if (pr != null) risultati.add(pr);
            }

            case 2 -> {
                // Per nome e cognome cliente
                String nome    = Input.leggiStringa("Nome cliente: ");
                String cognome = Input.leggiStringa("Cognome cliente: ");

                for (Prenotazione pr : prenotazioneRepo.trovaTutte()) {
                    Utente cliente = utenteRepo.trovaPerId(pr.getClienteId());
                    if (cliente == null) continue;

                    boolean matchNome    = cliente.getNome().equalsIgnoreCase(nome);
                    boolean matchCognome = cliente.getCognome().equalsIgnoreCase(cognome);

                    if (matchNome && matchCognome) risultati.add(pr);
                }
            }

            case 3 -> {
                // Per titolo film (parziale)
                String titolo = Input.leggiStringa("Titolo film (anche parziale): ");

                for (Prenotazione pr : prenotazioneRepo.trovaTutte()) {
                    Proiezione p = proiezioneRepo.trovaPerId(pr.getProiezioneId());
                    if (p == null) continue;
                    Film film = filmService.trovaPerId(p.getFilmId());
                    if (film == null) continue;

                    if (film.getTitolo().toLowerCase().contains(titolo.toLowerCase())) {
                        risultati.add(pr);
                    }
                }
            }

            case 4 -> {
                // Per intervallo di date
                LocalDateTime dataInizio =
                        Input.leggiData("Data inizio").atStartOfDay();
                LocalDateTime dataFine   =
                        Input.leggiData("Data fine  ").atTime(LocalTime.MAX);

                for (Prenotazione pr : prenotazioneRepo.trovaTutte()) {
                    Proiezione p = proiezioneRepo.trovaPerId(pr.getProiezioneId());
                    if (p == null) continue;

                    LocalDateTime dataProiezione = p.getDataOra();
                    if (!dataProiezione.isBefore(dataInizio) &&
                            !dataProiezione.isAfter(dataFine)) {
                        risultati.add(pr);
                    }
                }
            }
        }

        // Risultati
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.printf("  Risultati trovati: %d%n", risultati.size());
        MenuPrincipale.stampaLinea();

        if (risultati.isEmpty()) {
            System.out.println("  Nessuna prenotazione corrisponde ai criteri.");
        } else {
            stampaTabella(risultati);
        }

        Input.attendiInvio();
    }

    // VISUALIZZA DETTAGLIO PRENOTAZIONE
    /**
     * Chiede il codice di una prenotazione e ne mostra il dettaglio completo.
     * Mostra: codice, cliente, data/ora proiezione, film, numero biglietti,
     * costo unitario e totale.
     */
    private void visualizzaPrenotazioneDaId() {
        MenuPrincipale.stampaSezione("DETTAGLIO PRENOTAZIONE");

        long codice = Input.leggiIntero("Codice prenotazione (0 per annullare): ");
        if (codice == 0) return;

        Prenotazione pr = prenotazioneRepo.trovaPerId(codice);
        if (pr == null) {
            System.out.println("[ERRORE] Prenotazione #" + codice + " non trovata.");
            Input.attendiInvio();
            return;
        }

        stampaDettaglioPrenotazione(pr);
        Input.attendiInvio();
    }

    // =========================================================
    // METODI TUI (visualizzazione)
    // =========================================================

    /** Stampa il menu del bigliettaio. */
    private void stampaMenu() {
        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  MENU BIGLIETTAIO  -  " +
                bigliettaio.getNome() + " " + bigliettaio.getCognome());
        MenuPrincipale.stampaLinea();
        System.out.println("  [1] Prenotazioni di oggi");
        System.out.println("  [2] Cerca prenotazione");
        System.out.println("  [3] Visualizza dettaglio prenotazione");
        System.out.println("  [0] Logout");
        MenuPrincipale.stampaLinea();
    }

    /**
     * Stampa una tabella riassuntiva di una lista di prenotazioni.
     *
     * @param prenotazioni lista di prenotazioni da visualizzare
     */
    private void stampaTabella(List<Prenotazione> prenotazioni) {
        System.out.printf("  %-6s %-22s %-26s %-18s %s%n",
                "COD.", "CLIENTE", "FILM", "DATA/ORA", "BIGL.");
        MenuPrincipale.stampaLinea();

        for (Prenotazione pr : prenotazioni) {
            Utente cliente    = utenteRepo.trovaPerId(pr.getClienteId());
            Proiezione p      = proiezioneRepo.trovaPerId(pr.getProiezioneId());
            Film film         = p != null ? filmService.trovaPerId(p.getFilmId()) : null;

            String nomeCliente = cliente != null
                    ? cliente.getNome() + " " + cliente.getCognome()
                    : "N/D";

            System.out.printf("  %-6d %-22s %-26s %-18s %d%n",
                    pr.getId(),
                    MenuPrincipale.troncaStringa(nomeCliente, 21),
                    MenuPrincipale.troncaStringa(film != null ? film.getTitolo() : "?", 25),
                    p != null ? p.getDataOraFormattata() : "?",
                    pr.getNumeroBiglietti());
        }

        MenuPrincipale.stampaLinea();
    }

    /**
     * Stampa il dettaglio completo di una prenotazione:
     * codice, cliente, proiezione, film, biglietti, costo unitario e totale.
     *
     * @param pr prenotazione da visualizzare
     */
    private void stampaDettaglioPrenotazione(Prenotazione pr) {
        Utente cliente = utenteRepo.trovaPerId(pr.getClienteId());
        Proiezione p   = proiezioneRepo.trovaPerId(pr.getProiezioneId());
        Film film      = p != null ? filmService.trovaPerId(p.getFilmId()) : null;

        double costoUnitario = p != null ? p.getCostoBiglietto() : 0.0;
        double costoTotale   = costoUnitario * pr.getNumeroBiglietti();

        System.out.println();
        MenuPrincipale.stampaLinea();
        System.out.println("  PRENOTAZIONE #" + pr.getId());
        MenuPrincipale.stampaLinea();
        System.out.printf("  Cliente   : %s%n",
                cliente != null
                        ? cliente.getNome() + " " + cliente.getCognome()
                        : "N/D");
        System.out.printf("  Film      : %s%n",
                film != null ? film.getTitolo() : "N/D");
        System.out.printf("  Genere    : %s%n",
                film != null ? film.getGenere() : "N/D");
        System.out.printf("  Data/Ora  : %s%n",
                p != null ? p.getDataOraFormattata() : "N/D");
        MenuPrincipale.stampaLinea();
        System.out.printf("  Biglietti : %d%n", pr.getNumeroBiglietti());
        System.out.printf("  Costo     : %.2fEUR x %d = %.2fEUR%n",
                costoUnitario, pr.getNumeroBiglietti(), costoTotale);
        MenuPrincipale.stampaLinea();
    }
}
