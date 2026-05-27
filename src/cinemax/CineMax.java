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
package cinemax;

import cinemax.repository.*;
import cinemax.service.*;
import cinemax.ui.MenuPrincipale;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Classe principale dell'applicazione CineMax.
 * <p>
 * Inizializza repository, service e avvia l'interfaccia utente.
 */
public class CineMax {

    /** Cartella dei dati (relativa alla working directory). */
    private static final String DATA_DIR = "data/";

    /**
     * Punto di ingresso dell'applicazione.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) throws Exception {

        // Metodi per far visualizzare nel terminale il file CineMax.jar in modo corretto (senza encoding sbagliato)
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        // --- Repository ---
        FilmRepository filmRepo =
                new FilmRepository(DATA_DIR + "film.csv");

        ProiezioneRepository proiezioneRepo =
                new ProiezioneRepository(DATA_DIR + "proiezioni.csv");

        PrenotazioneRepository prenotazioneRepo =
                new PrenotazioneRepository(DATA_DIR + "prenotazioni.csv");

        UtenteRepository utenteRepo =
                new UtenteRepository(DATA_DIR + "utenti.csv");

        // --- Service ---
        FilmService filmService =
                new FilmService(filmRepo);

        ProiezioneService proiezioneService =
                new ProiezioneService(filmRepo, proiezioneRepo, prenotazioneRepo);

        PrenotazioneService prenotazioneService =
                new PrenotazioneService(prenotazioneRepo, proiezioneRepo, filmRepo, utenteRepo);

        UtenteService utenteService =
                new UtenteService(utenteRepo);

        AuthService authService =
                new AuthService(utenteRepo, utenteService);

        // --- Avvio UI ---
        MenuPrincipale menuPrincipale = new MenuPrincipale(
                authService,
                utenteService,
                filmService,
                filmRepo,
                proiezioneService,
                proiezioneRepo,
                prenotazioneService,
                prenotazioneRepo,
                utenteRepo
        );

        menuPrincipale.avvia();
    }
}
