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
package cinemax.service;

import cinemax.model.Film;
import cinemax.model.Proiezione;
import cinemax.repository.FilmRepository;
import cinemax.repository.PrenotazioneRepository;
import cinemax.repository.ProiezioneRepository;
import cinemax.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service per la gestione delle proiezioni.
 */
public class ProiezioneService {

    private final ProiezioneRepository proiezioneRepository;
    private final FilmRepository filmRepository;
    private final PrenotazioneRepository prenotazioneRepository;

    public ProiezioneService(FilmRepository filmRepository,
                             ProiezioneRepository proiezioneRepository,
                             PrenotazioneRepository prenotazioneRepository
    ) {
        this.filmRepository = filmRepository;
        this.proiezioneRepository = proiezioneRepository;
        this.prenotazioneRepository = prenotazioneRepository;
    }

    /**
     * Aggiunge una nuova proiezione.
     *
     * @param filmId id film
     * @param dataOra data e ora
     * @param costoBiglietto costo biglietto
     */
    public void aggiungiProiezione(
            long filmId,
            LocalDateTime dataOra,
            double costoBiglietto
    ) {

        Film film = filmRepository.trovaPerId(filmId);

        if (film == null) {
            throw new IllegalArgumentException("Film non trovato.");
        }

        long id = IdGenerator.nextId(
                proiezioneRepository.trovaTutte()
                        .stream()
                        .map(Proiezione::getId)
                        .toList()
        );

        Proiezione proiezione = new Proiezione(
                id,
                filmId,
                dataOra,
                costoBiglietto
        );

        LocalDateTime fineNuovaProiezione = dataOra.plusMinutes(film.getDurataMinuti());

        boolean sovrapposta = proiezioneRepository.trovaTutte().stream()
                .anyMatch(p -> {

                    Film filmEsistente =
                            filmRepository.trovaPerId(p.getFilmId());

                    LocalDateTime inizioEsistente =
                            p.getDataOra();

                    LocalDateTime fineEsistente =
                            inizioEsistente.plusMinutes(
                                    filmEsistente.getDurataMinuti()
                            );

                    return dataOra.isBefore(fineEsistente)
                            && fineNuovaProiezione.isAfter(inizioEsistente);
                });

        if (sovrapposta) {
            throw new IllegalStateException(
                    "La proiezione si sovrappone ad un'altra."
            );
        }

        proiezioneRepository.salva(proiezione);
    }

    /**
     * Restituisce tutte le proiezioni.
     *
     * @return lista proiezioni
     */
    public List<Proiezione> trovaTutte() { return proiezioneRepository.trovaTutte(); }

    /**
     * Cerca una proiezione tramite ID.
     *
     * @param id id proiezione
     * @return proiezione trovata oppure null
     */
    public Proiezione trovaPerId(long id) { return proiezioneRepository.trovaPerId(id); }

    /**
     * Cerca proiezioni in un intervallo di date.
     *
     * @param inizio data iniziale
     * @param fine data finale
     * @return lista proiezioni
     */
    public List<Proiezione> cercaPerData(LocalDateTime inizio, LocalDateTime fine) {
        List<Proiezione> risultato = new ArrayList<>();

        for (Proiezione proiezione
                : proiezioneRepository.trovaTutte()) {

            if (!proiezione.getDataOra().isBefore(inizio)
                    && !proiezione.getDataOra().isAfter(fine)) {

                risultato.add(proiezione);
            }
        }

        return risultato;
    }

    /**
     * Cerca proiezioni in un determinato intervallo.
     *
     * @param titolo titolo film
     * @param genere genere film
     * @param da data da dove iniziare la ricerca
     * @param a data a dove finire la ricerca
     * @param prezzoMin prezzo minimo del film
     * @param prezzoMax prezzo massimo del film
     * @return lista filtrata di proiezioni trovate
     */
    public List<Proiezione> cercaProiezione(
            String titolo,
            String genere,
            LocalDateTime da,
            LocalDateTime a,
            Double prezzoMin,
            Double prezzoMax
    ) {

        return proiezioneRepository.trovaTutte().stream()
                .filter(p -> {

                    if (titolo == null || titolo.isBlank()) return true;

                    Film film = filmRepository.trovaPerId(p.getFilmId());
                    return film != null &&
                            film.getTitolo().toLowerCase().contains(titolo.toLowerCase());
                })
                .filter(p -> {

                    if (genere == null || genere.isBlank()) return true;

                    Film film = filmRepository.trovaPerId(p.getFilmId());
                    return film != null &&
                            film.getGenere().equalsIgnoreCase(genere);
                })
                .filter(p -> da == null || !p.getDataOra().isBefore(da))
                .filter(p -> a == null || !p.getDataOra().isAfter(a))
                .filter(p -> prezzoMin == null || p.getCostoBiglietto() >= prezzoMin)
                .filter(p -> prezzoMax == null || p.getCostoBiglietto() <= prezzoMax)
                .toList();
    }

    /**
     * Modifica una proiezione.
     *
     * @param aggiornata proiezione modificata
     */
    public void modificaProiezione(Proiezione aggiornata) {
        Proiezione esistente = proiezioneRepository.trovaPerId(aggiornata.getId());

        if (esistente == null) {
            throw new IllegalArgumentException("Proiezione non trovata");
        }

        boolean haPrenotazioni = prenotazioneRepository.trovaTutte().stream()
                .anyMatch(pr -> pr.getProiezioneId() == aggiornata.getId());

        if (haPrenotazioni) {
            throw new IllegalStateException("Impossibile modificare: ci sono prenotazioni per questa proiezione.");
        }

        List<Proiezione> nuove = proiezioneRepository.trovaTutte().stream()
                .map(p -> p.getId() == aggiornata.getId() ? aggiornata : p)
                .collect(Collectors.toList());

        proiezioneRepository.aggiorna(nuove);
    }

    /**
     * Elimina una proiezione.
     *
     * @param id id proiezione
     * @return true se eliminata
     */
    public boolean eliminaProiezione(long id) {
        Proiezione p = proiezioneRepository.trovaPerId(id);

        if (p == null) {
            throw new IllegalArgumentException("Proiezione non trovata");
        }

        boolean haPrenotazioni = prenotazioneRepository.trovaTutte().stream()
                .anyMatch(pr -> pr.getProiezioneId() == id);

        if (haPrenotazioni) {
            throw new IllegalStateException("Impossibile eliminare: prenotazioni presenti");
        }

        return proiezioneRepository.elimina(id);
    }
}