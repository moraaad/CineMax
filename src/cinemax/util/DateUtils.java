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
package cinemax.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility centralizzata per gestione di date e orari
 * nel progetto CineMax.
 */
public class DateUtils {

    /**
     * Formattatori utili per la gestione delle date nel contesto del progetto
     * (per CSV e Interfaccia Utente)
     */

    // Data e Ora Proiezione Film
    public static final DateTimeFormatter CSV_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    // Data di Nascita Utente
    public static final DateTimeFormatter CSV_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter UI_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final DateTimeFormatter UI_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter UI_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Converte da String a oggetto che gestisce le date e gli orari
     */
    // Usato per le proiezioni nel dataset
    public static LocalDateTime convertiDataOraPerCSV(String dataOra) {
        if (dataOra == null || dataOra.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(dataOra, CSV_DATETIME_FORMATTER);
    }

    // Usato per le date di nascita degli utenti
    public static LocalDate convertiDataPerCSV(String data) {
        if (data == null || data.isBlank()) {
            return null;
        }

        return LocalDate.parse(data, CSV_DATE_FORMATTER);
    }

    /**
     * Formatta da oggetto in formato CSV
     */
    public static String formatoCsv(LocalDateTime dataOra) {
        if (dataOra == null) return "";
        return dataOra.format(CSV_DATETIME_FORMATTER);
    }

    public static String formatoCsv(LocalDate data) {
        if (data == null) return "";
        return data.format(CSV_DATE_FORMATTER);
    }

    /**
     * Formatta per Interfaccia Utente
     */
    public static String formatoUi(LocalDateTime dataOra) {
        return dataOra.format(UI_DATETIME_FORMATTER);
    }

    public static String formatoUi(LocalDate data) {
        return data.format(UI_DATE_FORMATTER);
    }

    public static String formatoUi(LocalTime ora) {
        return ora.format(UI_TIME_FORMATTER);
    }

    /**
     * Creazione delle date
     */
    public static LocalDateTime creaDataOra(
            int giorno, int mese, int anno,
            int ora, int minuto) {

        return LocalDateTime.of(anno, mese, giorno, ora, minuto);
    }

    public static LocalDateTime unisci(LocalDate data, LocalTime ora) {
        return LocalDateTime.of(data, ora);
    }

    /**
     * Get
     */
    public static LocalDate getData(LocalDateTime dataOra) {
        return dataOra.toLocalDate();
    }

    public static LocalTime getOrario(LocalDateTime dataOra) {
        return dataOra.toLocalTime();
    }

    /**
     * Validazione delle date (prima e dopo)
     */
    public static boolean primaDiOggi(LocalDateTime dataOra) {
        return dataOra.isBefore(LocalDateTime.now());
    }

    public static boolean dopoDiOggi(LocalDateTime dataOra) {
        return dataOra.isAfter(LocalDateTime.now());
    }
}