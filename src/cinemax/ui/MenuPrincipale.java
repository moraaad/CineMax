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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipale {

    private final AuthService authService;
    private final UtenteService utenteService;
    private final FilmService filmService;
    private final FilmRepository filmRepo;
    private final ProiezioneService proiezioneService;
    private final ProiezioneRepository proiezioneRepo;
    private final PrenotazioneService prenotazioneService;
    private final PrenotazioneRepository prenotazioneRepo;
    private final UtenteRepository utenteRepo;

    //Costruttore
    public MenuPrincipale(AuthService authService, UtenteService utenteService, FilmService filmService, FilmRepository filmRepo, ProiezioneService proiezioneService, ProiezioneRepository proiezioneRepo, PrenotazioneService prenotazioneService, PrenotazioneRepository prenotazioneRepo, UtenteRepository utenteRepo) {
        this.authService = authService;
        this.utenteService = utenteService;
        this.filmService = filmService;
        this.filmRepo = filmRepo;
        this.proiezioneService = proiezioneService;
        this.proiezioneRepo = proiezioneRepo;
        this.prenotazioneService = prenotazioneService;
        this.prenotazioneRepo = prenotazioneRepo;
        this.utenteRepo = utenteRepo;
    }


    // Avvio menu principale
    public void avvia() {
        System.out.println("Benvenuto in CineMax!");
        boolean esci = false;

        while (!esci) {
            stampaMenuPrincipale();
            int scelta = Input.leggiIntero("Scelta: ", 0, 3);

            switch (scelta) {
                case 1 -> eseguiLogin();
                case 2 -> eseguiRegistrazione();
                case 3 -> modalitaOspite();
                case 0 -> esci = true;
            }
        }

        System.out.println("\nArrivederci! Grazie per aver usato CineMax.");
    }

    // Login
    /**
     * Gestisce il flusso di login.
     * Verifica username e password (hash SHA-256 con salt),
     * poi delega al menu del ruolo corrispondente.
     */
    private void eseguiLogin() {
        stampaSezione("LOGIN");

        String username = Input.leggiStringa("Username: ");
        String password = Input.leggiStringa("Password: ");

        Utente utente = null;

        try {
            utente = authService.login(username, password);
        } catch (Exception e) {
            System.out.println("Errore durante il login.");
            Input.attendiInvio();
            return;
        }

        if (utente == null) {
            System.out.println("\nUsername o password non corretti.");
            Input.attendiInvio();
            return;
        }

        // Login riuscito
        System.out.println("\nBenvenuto/a, " + utente.getNome() + " " + utente.getCognome() + "!");
        System.out.println("Ruolo: " + utente.getRuolo().name().toLowerCase());
        Input.attendiInvio();

        // Delega al menu corretto
        switch (utente.getRuolo()) {
            case CLIENTE -> new MenuCliente(utente, filmService, filmRepo,
                    proiezioneService, proiezioneRepo,
                    prenotazioneService, prenotazioneRepo, utenteRepo).avvia();

            case BIGLIETTAIO -> new MenuBigliettaio(utente, filmService, filmRepo,
                    proiezioneService, proiezioneRepo,
                    prenotazioneRepo, utenteRepo).avvia();

            case PROIEZIONISTA -> new MenuProiezionista(utente, filmService, filmRepo,
                    proiezioneService, proiezioneRepo, prenotazioneRepo).avvia();
        }
    }

    /*
        REGISTRAZIONE
        Gestisce la registrazione di un nuovo cliente.
        Raccoglie tutti i dati, verifica che lo username non esista già
        e salva l'utente con password cifrata (SHA-256 + salt).
     */
    private void eseguiRegistrazione() {
        stampaSezione("REGISTRAZIONE NUOVO CLIENTE");

        String nome      = Input.leggiStringa("Nome: ");
        String cognome   = Input.leggiStringa("Cognome: ");
        String username  = Input.leggiStringa("Username: ");

        // controlla unicità username
        if (utenteService.esisteUsername(username)) {
            System.out.println("\n Username '" + username + "' già in uso. Scegli un altro username.");
            Input.attendiInvio();
            return;
        }

        String password  = Input.leggiStringa("Password: ");
        String domicilio = Input.leggiStringa("Luogo di domicilio: ");

        // Data di nascita (facoltativa)
        LocalDate dataNascita = null;
        boolean inserisciData = Input.leggiBoolean("Inserire data di nascita?");
        if (inserisciData) {
            dataNascita = Input.leggiData("Data di nascita");
        }

        try {
            authService.registraUtente(nome, cognome, username, password, dataNascita, domicilio, Ruolo.CLIENTE);
            System.out.println("\n[OK] Registrazione completata! Puoi ora effettuare il login.");
        } catch (Exception e) {
            System.out.println("\n[ERRORE] " + e.getMessage());
        }
        Input.attendiInvio();
    }
    /*
        Modalità ospite: consente la ricerca e la visualizzazione
        delle proiezioni senza autenticazione.
     */
    private void modalitaOspite() {
        stampaSezione("MODALITA' OSPITE");
        System.out.println("Puoi cercare e visualizzare proiezioni senza registrarti.\n");

        String titoloIniziale = Input.leggiStringa("Inserisci il titolo (anche parziale) del film da cercare: ");

        boolean esci = false;
        while (!esci) {
            System.out.println();
            stampaLinea();
            System.out.println("  MENU OSPITE");
            stampaLinea();
            System.out.println("  [1] Cerca proiezioni");
            System.out.println("  [2] Visualizza dettagli proiezione");
            System.out.println("  [0] Torna al menu principale");
            stampaLinea();

            int scelta = Input.leggiIntero("Scelta: ", 0, 2);
            switch (scelta) {
                case 1 -> {
                    titoloIniziale = cercaProiezioni(titoloIniziale);
                }
                case 2 -> selezionaEVisualizzaProiezione(titoloIniziale);
                case 0 -> esci = true;
            }
        }
    }


    // CERCA PROIEZIONI (condiviso con modalità ospite)
    /**
     * Interfaccia di ricerca proiezioni con filtri multipli.
     * Ricerca per titolo (anche parziale), genere, intervallo date, costo biglietto
     * o una combinazione degli stessi.
     *
     * @param titoloPreimpostato titolo già inserito dall'ospite (può essere vuoto)
     * @return titolo usato per la ricerca (per riutilizzarlo)
     */
    public String cercaProiezioni(String titoloPreimpostato) {
        stampaSezione("CERCA PROIEZIONI");

        System.out.println("Lascia vuoto un campo per non applicare quel filtro.\n");

        // --- Raccolta filtri ---
        System.out.print("Titolo film (parziale) [" + titoloPreimpostato + "]: ");
        String inputTitolo = leggiStringaOpzionale();
        String filtroTitolo = inputTitolo.isEmpty() ? titoloPreimpostato : inputTitolo;

        System.out.print("Genere (es. Drama, Action, Thriller): ");
        String filtroGenere = leggiStringaOpzionale();

        // Date
        LocalDateTime filtroDataInizio = null;
        LocalDateTime filtroDataFine   = null;
        boolean inserisciDate = Input.leggiBoolean("Filtrare per intervallo di date?");
        if (inserisciDate) {
            LocalDate dataInizio = Input.leggiData("Data inizio");
            LocalDate dataFine   = Input.leggiData("Data fine  ");
            filtroDataInizio = dataInizio.atStartOfDay();
            filtroDataFine   = dataFine.atTime(LocalTime.MAX);
        }

        // Costo biglietto
        double filtroMinCosto = -1;
        double filtroMaxCosto = -1;
        boolean inserisciCosto = Input.leggiBoolean("Filtrare per costo biglietto?");
        if (inserisciCosto) {
            filtroMinCosto = Input.leggiDouble("Costo minimo (EUR): ");
            filtroMaxCosto = Input.leggiDouble("Costo massimo (EUR): ");
        }

        // --- Applicazione filtri ---
        List<Proiezione> tutteProiezioni = proiezioneService.trovaTutte();
        List<Proiezione> risultati = new ArrayList<>();

        for (Proiezione p : tutteProiezioni) {
            Film film = filmService.trovaPerId(p.getFilmId());
            if (film == null) continue;

            // Filtro titolo
            if (!filtroTitolo.isEmpty() &&
                    !film.getTitolo().toLowerCase().contains(filtroTitolo.toLowerCase())) continue;

            // Filtro genere
            if (!filtroGenere.isEmpty() &&
                    !film.getGenere().toLowerCase().contains(filtroGenere.toLowerCase())) continue;

            // Filtro date
            if (filtroDataInizio != null &&
                    p.getDataOra().isBefore(filtroDataInizio)) continue;
            if (filtroDataFine != null &&
                    p.getDataOra().isAfter(filtroDataFine)) continue;

            // Filtro costo
            if (filtroMinCosto >= 0 && p.getCostoBiglietto() < filtroMinCosto) continue;
            if (filtroMaxCosto >= 0 && p.getCostoBiglietto() > filtroMaxCosto) continue;

            risultati.add(p);
        }

        // --- Visualizzazione risultati ---
        System.out.println();
        stampaLinea();
        System.out.printf("  Risultati trovati: %d%n", risultati.size());
        stampaLinea();

        if (risultati.isEmpty()) {
            System.out.println("  Nessuna proiezione corrisponde ai criteri di ricerca.");
        } else {
            System.out.printf("  %-4s %-28s %-18s %-18s %s%n",
                    "ID", "FILM", "GENERE", "DATA/ORA", "COSTO");
            stampaLinea();
            for (Proiezione p : risultati) {
                Film film = filmService.trovaPerId(p.getFilmId());
                System.out.printf("  %-4d %-28s %-14s %-18s %.2fEUR%n",
                        p.getId(),
                        troncaStringa(film != null ? film.getTitolo() : "?", 27),
                        troncaStringa(film != null ? film.getGenere() : "?", 13),
                        p.getDataOraFormattata(),
                        p.getCostoBiglietto());
            }
        }
        stampaLinea();
        Input.attendiInvio();

        return filtroTitolo;
    }

    /**
     * Permette di selezionare una proiezione (per ID) e visualizzarne i dettagli.
     *
     * @param titoloFiltro titolo film usato come filtro iniziale
     */
    public void selezionaEVisualizzaProiezione(String titoloFiltro) {
        long id = Input.leggiIntero("ID proiezione da visualizzare (0 per annullare): ");
        if (id == 0) return;

        Proiezione p = proiezioneService.trovaPerId(id);
        if (p == null) {
            System.out.println("[ERRORE] Proiezione non trovata.");
            Input.attendiInvio();
            return;
        }

        visualizzaProiezione(p);
    }

    /**
     * Visualizza tutti i dettagli di una proiezione.
     *
     * @param p la proiezione da visualizzare
     */
    public void visualizzaProiezione(Proiezione p) {
        Film film = filmService.trovaPerId(p.getFilmId());
        int postiLiberi = prenotazioneService.calcolaPostiDisponibili(p.getId());

        System.out.println();
        stampaLinea();
        System.out.println("  DETTAGLI PROIEZIONE #" + p.getId());
        stampaLinea();
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
        stampaLinea();
        System.out.printf("  Data/Ora  : %s%n", p.getDataOraFormattata());
        String euroSimbolo = "EUR";
        System.out.printf("  Biglietto : %.2f%s%n", p.getCostoBiglietto(), euroSimbolo);
        System.out.printf("  Posti lib.: %d / %d%n", postiLiberi, Proiezione.CAPACITA_SALA);
        stampaLinea();
        Input.attendiInvio();
    }

    // METODI TUI (visualizzazione)
    /** Stampa il menu principale con le opzioni di accesso. */
    private void stampaMenuPrincipale() {
        stampaLinea();
        System.out.println("  MENU PRINCIPALE");
        stampaLinea();
        System.out.println("  [1] Login");
        System.out.println("  [2] Registrati come cliente");
        System.out.println("  [3] Continua come ospite");
        System.out.println("  [0] Esci");
        stampaLinea();
    }

    /** Stampa un separatore orizzontale. */
    static void stampaLinea() {
        System.out.println("  " + "-".repeat(100));
    }

    /** Stampa il titolo di una sezione con separatori. */
    static void stampaSezione(String titolo) {
        System.out.println();
        stampaLinea();
        System.out.println("  " + titolo);
        stampaLinea();
    }

    /**
     * Legge una stringa opzionale (può essere vuota).
     *
     * @return stringa inserita (può essere "")
     */
    static String leggiStringaOpzionale() {
        try {
            String line = new BufferedReader(
                    new InputStreamReader(System.in)).readLine();
            return line == null ? "" : line.trim();
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Tronca una stringa alla lunghezza massima specificata.
     *
     * @param s   stringa da troncare
     * @param max lunghezza massima
     * @return stringa troncata (con "..." se necessario)
     */
    static String troncaStringa(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 1) + "...";
    }
}
