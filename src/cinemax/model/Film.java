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
 * Rappresenta un film.
 */
public class Film {

    private final long id;
    private String titolo;
    private String genere;
    private String regista;
    private int anno;
    private int durataMinuti;
    private int etaMinima;

    public Film(long id, String titolo,
                String genere,
                String regista,
                int anno,
                int durataMinuti,
                int etaMinima) {

        this.id = id;
        this.titolo = titolo;
        this.genere = genere;
        this.regista = regista;
        this.anno = anno;
        this.durataMinuti = durataMinuti;
        this.etaMinima = etaMinima;
    }

    public long getId() { return id; }

    public String getTitolo() { return titolo; }

    public String getGenere() { return genere; }

    public String getRegista() { return regista; }

    public int getAnno() { return anno; }

    public int getDurataMinuti() { return durataMinuti; }

    public int getEtaMinima() { return etaMinima; }

    public void setTitolo(String titolo) { this.titolo = titolo; }

    public void setGenere(String genere) { this.genere = genere; }

    public void setRegista(String regista) { this.regista = regista; }

    public void setAnno(int anno) { this.anno = anno; }

    public void setDurataMinuti(int durataMinuti) { this.durataMinuti = durataMinuti; }

    public void setEtaMinima(int etaMinima) { this.etaMinima = etaMinima; }

    @Override
    public String toString() { return titolo + " (" + anno + ")"; }
}