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

import cinemax.model.Film;
import cinemax.model.Prenotazione;
import cinemax.model.Proiezione;
import cinemax.model.Utente;
import cinemax.repository.FilmRepository;
import cinemax.repository.PrenotazioneRepository;
import cinemax.repository.ProiezioneRepository;
import cinemax.repository.UtenteRepository;
import cinemax.util.DateUtils;
import cinemax.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service per la gestione delle prenotazioni.
 */
public class PrenotazioneService {
    private final PrenotazioneRepository prenotazioneRepository;
    private final ProiezioneRepository proiezioneRepository;
    private final FilmRepository filmRepository;
    private final UtenteRepository utenteRepository;

    public PrenotazioneService(
            PrenotazioneRepository prenotazioneRepository,
            ProiezioneRepository proiezioneRepository,
            FilmRepository filmRepository,
            UtenteRepository utenteRepository
    ) {
        this.prenotazioneRepository = prenotazioneRepository;
        this.proiezioneRepository = proiezioneRepository;
        this.filmRepository = filmRepository;
        this.utenteRepository = utenteRepository;
    }

    /**
     * Calcola i posti disponibili per una proiezione.
     *
     * @param proiezioneId id proiezione
     * @return posti disponibili
     */
    public int calcolaPostiDisponibili(long proiezioneId) {

        int postiOccupati = 0;

        for (Prenotazione p :
                prenotazioneRepository.trovaTutte()) {

            if (p.getProiezioneId() == proiezioneId) {
                postiOccupati += p.getNumeroBiglietti();
            }
        }

        return Proiezione.CAPACITA_SALA - postiOccupati;
    }

    /**
     * Crea una nuova prenotazione.
     *
     * @param clienteId id cliente
     * @param proiezioneId id proiezione
     * @param numeroBiglietti numero biglietti
     */
    public void creaPrenotazione(
            long clienteId,
            long proiezioneId,
            int numeroBiglietti
    ) {

        if (numeroBiglietti <= 0) {
            throw new IllegalArgumentException("Numero non valido.");
        }

        if (numeroBiglietti > 10) {
            throw new IllegalArgumentException("Limite massimo 10 biglietti.");
        }

        Proiezione proiezione = proiezioneRepository.trovaPerId(proiezioneId);

        if (proiezione == null) {
            throw new IllegalArgumentException("Proiezione non trovata.");
        }

        int postiDisponibili = calcolaPostiDisponibili(proiezioneId);

        if (numeroBiglietti > postiDisponibili) {
            throw new IllegalArgumentException("Posti insufficienti.");
        }

        long id = IdGenerator.nextId(
                prenotazioneRepository.trovaTutte()
                        .stream()
                        .map(Prenotazione::getId)
                        .toList()
        );

        Prenotazione prenotazione = new Prenotazione(
                id,
                clienteId,
                proiezioneId,
                numeroBiglietti
        );

        // DEBUG
        if (id <= 0) {
            throw new IllegalStateException("ID prenotazione non valido");
        }

        prenotazioneRepository.salva(prenotazione);
    }

    /**
     * Restituisce le prenotazioni di un cliente.
     *
     * @param clienteId id cliente
     * @return lista prenotazioni
     */
    public List<Prenotazione> trovaPerCliente(
            long clienteId
    ) {
        return prenotazioneRepository
                .trovaPerCliente(clienteId);
    }

    /**
     * Elimina la prenotazione associata all'id fornito.
     *
     * @param id id prenotazione da eliminare
     */
    public void eliminaPrenotazione(long id) {
        Prenotazione prenotazione = prenotazioneRepository.trovaPerId(id);

        if (prenotazione == null) {
            throw new IllegalArgumentException("Prenotazione non trovata.");
        }

        Proiezione proiezione = proiezioneRepository.trovaPerId(prenotazione.getProiezioneId());

        if (proiezione == null) {
            throw new IllegalStateException("Proiezione associata non trovata.");
        }

        if (!DateUtils.dopoDiOggi(proiezione.getDataOra())) {
            throw new IllegalStateException("Impossibile cancellare: la proiezione è già avvenuta.");
        }

        prenotazioneRepository.elimina(id);
    }

    /**
     * Modifica la proiezione associata a una prenotazione esistente.
     * Sia la vecchia che la nuova proiezione devono essere future.
     *
     * @param prenotazioneId id prenotazione da modificare
     * @param nuovaProiezioneId id della nuova proiezione
     */
    public void modificaPrenotazione(long prenotazioneId, long nuovaProiezioneId) {
        Prenotazione prenotazione = prenotazioneRepository.trovaPerId(prenotazioneId);

        if (prenotazione == null) {
            throw new IllegalArgumentException("Prenotazione non trovata.");
        }

        Proiezione vecchiaProiezione = proiezioneRepository.trovaPerId(prenotazione.getProiezioneId());

        if (vecchiaProiezione == null) {
            throw new IllegalStateException("Proiezione originale non trovata.");
        }

        if (!DateUtils.dopoDiOggi(vecchiaProiezione.getDataOra())) {
            throw new IllegalStateException("Impossibile modificare: la proiezione originale è già avvenuta.");
        }

        Proiezione nuovaProiezione = proiezioneRepository.trovaPerId(nuovaProiezioneId);

        if (nuovaProiezione == null) {
            throw new IllegalArgumentException("Nuova proiezione non trovata.");
        }

        if (!DateUtils.dopoDiOggi(nuovaProiezione.getDataOra())) {
            throw new IllegalStateException("Impossibile modificare: la nuova proiezione è già avvenuta.");
        }

        int postiDisponibili = calcolaPostiDisponibili(nuovaProiezioneId);

        if (prenotazione.getNumeroBiglietti() > postiDisponibili) {
            throw new IllegalStateException("Posti insufficienti nella nuova proiezione.");
        }

        prenotazioneRepository.elimina(prenotazioneId);

        long nuovoId = IdGenerator.nextId(
                prenotazioneRepository.trovaTutte()
                        .stream()
                        .map(Prenotazione::getId)
                        .toList()
        );

        Prenotazione modificata = new Prenotazione(
                nuovoId,
                prenotazione.getClienteId(),
                nuovaProiezioneId,
                prenotazione.getNumeroBiglietti()
        );

        prenotazioneRepository.salva(modificata);
    }

    /**
     * Cerca prenotazioni secondo uno o più criteri combinabili.
     *
     * @param codice       id prenotazione (null = ignora)
     * @param nomeCliente  nome (anche parziale) del cliente (null = ignora)
     * @param titoloFilm   titolo (anche parziale) del film (null = ignora)
     * @param da           data/ora inizio intervallo (null = ignora)
     * @param a            data/ora fine intervallo (null = ignora)
     * @return lista di prenotazioni che soddisfano i criteri
     */
    public List<Prenotazione> cercaPrenotazioni(
            Long codice,
            String nomeCliente,
            String titoloFilm,
            LocalDateTime da,
            LocalDateTime a
    ) {
        return prenotazioneRepository.trovaTutte().stream()
                .filter(p -> {
                    if (codice == null) return true;
                    return p.getId() == codice;
                })
                .filter(p -> {
                    if (nomeCliente == null || nomeCliente.isBlank()) return true;
                    Utente cliente = utenteRepository.trovaPerId(p.getClienteId());
                    if (cliente == null) return false;
                    String nomeCompleto = (cliente.getNome() + " " + cliente.getCognome()).toLowerCase();
                    return nomeCompleto.contains(nomeCliente.toLowerCase());
                })
                .filter(p -> {
                    if (titoloFilm == null || titoloFilm.isBlank()) return true;
                    Proiezione proiezione = proiezioneRepository.trovaPerId(p.getProiezioneId());
                    if (proiezione == null) return false;
                    Film film = filmRepository.trovaPerId(proiezione.getFilmId());
                    if (film == null) return false;
                    return film.getTitolo().toLowerCase().contains(titoloFilm.toLowerCase());
                })
                .filter(p -> {
                    if (da == null) return true;
                    Proiezione proiezione = proiezioneRepository.trovaPerId(p.getProiezioneId());
                    return proiezione != null && !proiezione.getDataOra().isBefore(da);
                })
                .filter(p -> {
                    if (a == null) return true;
                    Proiezione proiezione = proiezioneRepository.trovaPerId(p.getProiezioneId());
                    return proiezione != null && !proiezione.getDataOra().isAfter(a);
                })
                .toList();
    }

    /**
     * Restituisce tutte le prenotazioni le cui proiezioni sono oggi.
     *
     * @return lista prenotazioni di oggi
     */
    public List<Prenotazione> trovaPrenotazioniOggi() {
        LocalDate oggi = LocalDate.now();

        return prenotazioneRepository.trovaTutte().stream()
                .filter(p -> {
                    Proiezione proiezione = proiezioneRepository.trovaPerId(p.getProiezioneId());
                    return proiezione != null && proiezione.getData().equals(oggi);
                })
                .toList();
    }
}