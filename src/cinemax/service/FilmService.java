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
import cinemax.repository.FilmRepository;
import cinemax.util.IdGenerator;

import java.util.List;

/**
 * Service per la gestione della logica dei film.
 */
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    /**
     * Crea un nuovo film.
     */
    public Film creaFilm(String titolo,
                         String genere,
                         String regista,
                         int anno,
                         int durataMinuti,
                         int etaMinima) {

        long id = IdGenerator.nextId(
                filmRepository.trovaTutti()
                        .stream()
                        .map(Film::getId)
                        .toList()
        );

        Film film = new Film(
                id,
                titolo,
                genere,
                regista,
                anno,
                durataMinuti,
                etaMinima
        );

        filmRepository.salva(film);

        return film;
    }

    /**
     * Restituisce tutti i film.
     */
    public List<Film> trovaTutti() {
        return filmRepository.trovaTutti();
    }

    /**
     * Cerca film per ID.
     */
    public Film trovaPerId(long id) {
        return filmRepository.trovaPerId(id);
    }

    /**
     * Cerca film per titolo.
     */
    public Film trovaPerTitolo(String titolo) {
        return filmRepository.trovaPerTitolo(titolo);
    }
}