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
package cinemax.repository;

import cinemax.model.Film;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository per la gestione dei film.
 * Gestisce lettura/scrittura su file CSV.
 */
public class FilmRepository {

    private final CsvManager csvManager;

    public FilmRepository(String percorsoFile) {
        this.csvManager = new CsvManager(percorsoFile);
    }

    /**
     * Restituisce tutti i film presenti nel CSV.
     *
     * @return lista di Film
     */
    public List<Film> trovaTutti() {

        List<String[]> righe = csvManager.leggiTutto();
        List<Film> filmList = new ArrayList<>();

        // Salta header
        for (int i = 1; i < righe.size(); i++) {

            String[] c = righe.get(i);

            long id = Long.parseLong(c[0]);
            String titolo = c[1];
            String genere = c[2];
            String regista = c[3];
            int anno = Integer.parseInt(c[4]);
            int durataMinuti = Integer.parseInt(c[5]);
            int etaMinima = Integer.parseInt(c[6]);

            Film film = new Film(
                    id,
                    titolo,
                    genere,
                    regista,
                    anno,
                    durataMinuti,
                    etaMinima
            );

            filmList.add(film);
        }

        return filmList;
    }

    /**
     * Cerca un film per ID.
     */
    public Film trovaPerId(long id) {

        for (Film f : trovaTutti()) {
            if (f.getId() == id) {
                return f;
            }
        }

        return null;
    }

    /**
     * Cerca un film per ID.
     */
    public Film trovaPerTitolo(String titolo) {
        if(titolo == null || titolo.isBlank()) {
            return null;
        }

        for (Film f : trovaTutti()) {
            if (f.getTitolo().equalsIgnoreCase(titolo)) {
                return f;
            }
        }

        return null;
    }

    /**
     * Salva un nuovo film nel CSV.
     */
    public void salva(Film film) {

        String[] riga = {
                String.valueOf(film.getId()),
                film.getTitolo(),
                film.getGenere(),
                film.getRegista(),
                String.valueOf(film.getAnno()),
                String.valueOf(film.getDurataMinuti()),
                String.valueOf(film.getEtaMinima())
        };

        csvManager.aggiungiRiga(riga);
    }

    /**
     * Aggiorna completamente il file CSV.
     */
    public void aggiorna(List<Film> filmList) {

        List<String[]> righe = new ArrayList<>();

        // header
        righe.add(new String[]{
                "id",
                "titolo",
                "genere",
                "regista",
                "anno",
                "durataMinuti",
                "etaMinima"
        });

        for (Film f : filmList) {

            righe.add(new String[]{
                    String.valueOf(f.getId()),
                    f.getTitolo(),
                    f.getGenere(),
                    f.getRegista(),
                    String.valueOf(f.getAnno()),
                    String.valueOf(f.getDurataMinuti()),
                    String.valueOf(f.getEtaMinima())
            });
        }

        csvManager.scriviTutto(righe);
    }
}