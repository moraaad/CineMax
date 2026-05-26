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
package cinemax.model;

import cinemax.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Rappresenta una proiezione cinematografica.
 */
public class Proiezione {

    public static final int CAPACITA_SALA = 200;

    private final long id;
    private final long filmId;

    private LocalDateTime dataOra;
    private double costoBiglietto;

    public Proiezione(long id,
                      long filmId,
                      LocalDateTime dataOra,
                      double costoBiglietto) {

        this.id = id;
        this.filmId = filmId;
        this.dataOra = dataOra;
        this.costoBiglietto = costoBiglietto;
    }

    public long getId() { return id; }

    public long getFilmId() { return filmId; }

    public LocalDateTime getDataOra() { return dataOra; }

    public double getCostoBiglietto() { return costoBiglietto; }

    public LocalDate getData() { return DateUtils.getData(dataOra); }

    public LocalTime getOrario() { return DateUtils.getOrario(dataOra); }

    public String getDataFormattata() { return DateUtils.formatoUi(dataOra.toLocalDate()); }

    public String getOrarioFormattato() { return DateUtils.formatoUi(dataOra.toLocalTime()); }

    public String getDataOraFormattata() { return DateUtils.formatoUi(dataOra); }

    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    public void setCostoBiglietto(double costoBiglietto) { this.costoBiglietto = costoBiglietto; }

    @Override
    public String toString() {
        return "Proiezione{" +
                "id=" + id +
                ", filmId=" + filmId +
                ", dataOra=" + getDataOraFormattata() +
                ", costo=" + costoBiglietto +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proiezione proiezione)) return false;
        return id == proiezione.id;
    }
}