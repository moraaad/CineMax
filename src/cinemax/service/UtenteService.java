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

import cinemax.model.Utente;
import cinemax.model.Ruolo;
import cinemax.repository.UtenteRepository;
import cinemax.util.IdGenerator;

import java.util.List;

/**
 * Service per la gestione logica degli utenti.
 * Separato dall'autenticazione.
 */
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public boolean isBigliettaio(Utente u) {
        return u.getRuolo() == Ruolo.BIGLIETTAIO;
    }

    public boolean isProiezionista(Utente u) {
        return u.getRuolo() == Ruolo.PROIEZIONISTA;
    }

    public boolean isCliente(Utente u) {
        return u.getRuolo() == Ruolo.CLIENTE;
    }

    /**
     * Restituisce tutti gli utenti.
     */
    public List<Utente> trovaTutti() {
        return utenteRepository.trovaTutti();
    }

    /**
     * Cerca utente per ID.
     */
    public Utente trovaPerId(long id) {
        return utenteRepository.trovaPerId(id);
    }

    /**
     * Verifica se uno username esiste.
     */
    public boolean esisteUsername(String username) {
        return utenteRepository.trovaPerUsername(username) != null;
    }

    /**
     * Genera nuovo ID utente.
     */
    public long generaId() {
        return IdGenerator.nextId(
                utenteRepository.trovaTutti()
                        .stream()
                        .map(Utente::getId)
                        .toList()
        );
    }
}