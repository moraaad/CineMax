/**
 * CineMax - Laboratorio Interdisciplinare A
 * <p>
 * Autori:
 * - Trupia Giovanni 766370 Como
 * - Mahhay Harman 762686 Como
 * - Ait Laarabi Morad 762740 Como
 * - Maatouch Ayman 766465 Como
 * <p>
 * JDK 21
 */
package cinemax.service;

/**
 * Servizio per l'autenticazione e la gestione degli utenti.
 * Legge e scrive sul file CSV tramite CsvManager.
 * <p>
 * Formato CSV atteso:
 * id,nome,cognome,username,password,dataNascita,domicilio,ruolo
 */
import cinemax.model.Ruolo;
import cinemax.model.Utente;
import cinemax.repository.UtenteRepository;
import cinemax.util.Password;

/**
 * Service per autenticazione e registrazione utenti.
 */
public class AuthService {
    private final UtenteRepository utenteRepository;
    private final UtenteService utenteService;

    public AuthService(UtenteRepository repo, UtenteService service) {
        this.utenteRepository = repo;
        this.utenteService = service;
    }

    public Utente login(String username, String password) throws Exception {
        Utente utente = utenteRepository.trovaPerUsername(username);

        if (utente == null) return null;

        if (!Password.checkPassword(password, utente.getPassword())) return null;

        return utente;
    }

    public Utente registraUtente(
            String nome,
            String cognome,
            String username,
            String password,
            java.time.LocalDate dataNascita,
            String domicilio,
            Ruolo ruolo
    ) throws Exception {

        if (utenteService.esisteUsername(username)) {
            throw new IllegalArgumentException("Username già esistente.");
        }

        long id = utenteService.generaId();

        Utente utente = new Utente(
                id,
                nome,
                cognome,
                username,
                Password.hashPassword(password),
                dataNascita,
                domicilio,
                ruolo
        );

        utenteRepository.salva(utente);

        return utente;
    }
}