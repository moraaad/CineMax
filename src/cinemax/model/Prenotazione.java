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

/**
 * Prenotazione di posti per una proiezione.
 */

public class Prenotazione {

    private final long id;
    private final long clienteId;
    private final long proiezioneId;
    private final int numeroBiglietti;

    public Prenotazione(long id,
                        long clienteId,
                        long proiezioneId,
                        int numeroBiglietti) {
        this.id = id;
        this.clienteId = clienteId;
        this.proiezioneId = proiezioneId;
        this.numeroBiglietti = numeroBiglietti;
    }

    public long getId() { return id; }

    public long getClienteId() { return clienteId; }

    public long getProiezioneId() { return proiezioneId; }

    public int getNumeroBiglietti() { return numeroBiglietti; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prenotazione prenotazione)) return false;
        return id == prenotazione.id;
    }
}