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

// Ruolo dell'utente
public enum Ruolo {
    CLIENTE,
    BIGLIETTAIO,
    PROIEZIONISTA;

    /**
     * Converte i ruoli degli utenti nel file utenti.csv scritti in minuscolo, in maiuscolo
     */
    public static Ruolo formatoCsv(String value) {
        return Ruolo.valueOf(value.trim().toUpperCase());
    }
}
