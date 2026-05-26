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

import java.time.LocalDate;

/**
 * Rappresenta un utente del sistema CineMax.
 */

public class Utente {

    private final long id;
    private String nome;
    private String cognome;
    private String username;
    private String password;
    private final LocalDate dataNascita;
    private String domicilio;
    private Ruolo ruolo;

    public Utente(long id,
                  String nome,
                  String cognome,
                  String username,
                  String password,
                  LocalDate dataNascita,
                  String domicilio,
                  Ruolo ruolo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataNascita = dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo;
    }

    public long getId() { return id; }

    public String getNome() { return nome; }

    public String getCognome() { return cognome; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public LocalDate getDataNascita() { return dataNascita; }

    public String getDomicilio() { return domicilio; }

    public Ruolo getRuolo() { return ruolo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente utente)) return false;
        return id == utente.id;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", username='" + username + '\'' +
                ", ruolo=" + ruolo +
                '}';
    }
}