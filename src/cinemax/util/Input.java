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
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Classe utility per la gestione dell'input da terminale.
 *
 * Tutte le operazioni di parsing delle date
 * vengono delegate a DateUtils.
 */
public class Input {
    private static final Scanner in = new Scanner(System.in);


    public static String leggiStringa(String messaggio) {
        String input;

        do {
            System.out.print(messaggio);
            input = in.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Errore: input vuoto.");
            }

        } while (input.isEmpty());

        return input;
    }

    public static int leggiIntero(String messaggio) {
        while (true) {
            try {
                System.out.print(messaggio);
                return Integer.parseInt(in.nextLine());

            } catch (NumberFormatException e) {
                System.err.println("Errore: inserire un numero intero valido.");
            }
        }
    }

    public static int leggiIntero(String messaggio, int min, int max) {
        int valore;

        do {
            valore = leggiIntero(messaggio);

            if (valore < min || valore > max) {
                System.out.println("Errore: valore fuori intervallo.");
            }

        } while (valore < min || valore > max);

        return valore;
    }

    public static double leggiDouble(String messaggio) {
        while (true) {
            try {
                System.out.print(messaggio);
                return Double.parseDouble(in.nextLine());

            } catch (NumberFormatException e) {
                System.err.println("Errore: inserire un numero valido.");
            }
        }
    }

    public static boolean leggiBoolean(String messaggio) {
        while (true) {
            System.out.print(messaggio + " (s/n): ");
            String risposta = in.nextLine().trim().toLowerCase();

            if (risposta.equals("s")) return true;
            if (risposta.equals("n")) return false;

            System.out.println("Errore: inserire 's' oppure 'n'.");
        }
    }

    public static LocalDate leggiData(String messaggio) {
        while (true) {
            try {
                System.out.print(messaggio + " (gg/MM/yyyy): ");
                String input = in.nextLine().trim();

                return LocalDate.parse(input, DateUtils.UI_DATE_FORMATTER);

            } catch (DateTimeParseException e) {
                System.err.println("Errore: formato data non valido.");
            }
        }
    }

    public static LocalDateTime leggiDataOra(String messaggio) {
        while (true) {
            try {
                System.out.print(messaggio + " (gg/MM/yyyy HH:mm): ");
                String input = in.nextLine().trim();

                return LocalDateTime.parse(input, DateUtils.UI_DATETIME_FORMATTER);

            } catch (DateTimeParseException e) {
                System.err.println("Errore: formato data/ora non valido.");
            }
        }
    }

    // Pausa
    public static void attendiInvio() {
        System.out.println("\nPremi INVIO per continuare...");
        in.nextLine();
    }
}