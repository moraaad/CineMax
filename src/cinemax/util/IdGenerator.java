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

import java.util.List;

/**
 * Utility per la generazione di ID incrementali
 * basati sull'ultimo ID presente nel file CSV.
 */
public class IdGenerator {
    /**
     * Genera il prossimo ID disponibile.
     *
     * @param ids lista degli ID esistenti
     * @return nuovo ID incrementale
     */
    public static long nextId(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            return 1;
        }

        return ids.getLast() + 1;
    }
}