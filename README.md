# CineMax - Sistema di gestione di un cinema

CineMax è un'applicazione Java per la gestione di un cinema monosala, sviluppata nell'ambito del **Laboratorio Interdisciplinare A** del Corso di Laurea in Informatica presso l'Università degli Studi dell'Insubria.

Il progetto permette di gestire il palinsesto cinematografico, consultare le proiezioni e amministrare le prenotazioni attraverso un'interfaccia testuale (TUI).

## Funzionalità

### Ricerca e consultazione

* Ricerca delle proiezioni in base al titolo del film, anche parziale.
* Ricerca per genere, data e costo del biglietto.
* Combinazione di più criteri di ricerca.
* Visualizzazione dei dettagli dei film e delle proiezioni.
* Consultazione dei posti disponibili in sala.

### Gestione clienti

* Registrazione di nuovi clienti.
* Autenticazione e accesso al proprio account.
* Creazione di prenotazioni.
* Visualizzazione delle proprie prenotazioni.
* Modifica e cancellazione delle prenotazioni, secondo le regole previste dall'applicazione.

### Gestione del palinsesto

* Inserimento di film e nuove proiezioni.
* Modifica della data e dell'ora delle proiezioni.
* Eliminazione delle proiezioni.
* Controlli sui conflitti di orario e sulle prenotazioni esistenti.

### Funzionalità per i bigliettai

* Consultazione delle prenotazioni.
* Ricerca per codice prenotazione, cliente, titolo del film e data.
* Visualizzazione dei dettagli e dei costi delle prenotazioni.

## Tecnologie utilizzate

* **Linguaggio:** Java
* **Versione:** JDK 21
* **Ambiente di sviluppo:** IntelliJ IDEA
* **Persistenza dei dati:** file CSV
* **Interfaccia utente:** terminale (TUI)

## Requisiti

Per eseguire l'applicazione è necessario disporre di:

* Java 21 o una versione compatibile del JDK.
* Un terminale.
* Il file eseguibile `CineMax.jar` e i file di dati richiesti dall'applicazione.

È possibile scaricare il JDK dal sito ufficiale di [Oracle](https://www.oracle.com/java/technologies/downloads/#java21).

## Installazione ed esecuzione

1. Clonare il repository oppure scaricare il progetto:

   ```bash
   git clone https://github.com/moraaad/CineMax.git
   ```

2. Aprire la cartella del progetto.

3. Verificare che il file `CineMax.jar`, presente nella cartella `bin`, venga spostato nella cartella `root` e che la directory `data` contenga i file CSV necessari.

4. Aprire un terminale nella cartella radice del progetto ed eseguire:

   ```bash
   java -jar bin/CineMax.jar
   ```

Se il comando `java` non viene riconosciuto, verificare che il JDK sia installato correttamente e che Java sia disponibile nel `PATH`.

## Accesso agli account

Il progetto prevede ruoli distinti: cliente, proiezionista e bigliettaio.

Per effettuare prove con gli account speciali già predisposti:

1. Aprire il file `data/utenti.csv`.
2. Individuare lo username dell'utente con il ruolo desiderato.
3. Inserire lo username nella schermata di login.
4. Per gli account di test predisposti nel dataset, utilizzare la password indicata nelle istruzioni del progetto: `a`.

La password riportata è una credenziale di test, non una password consigliata per un utilizzo reale.

## Struttura del progetto

```text
CineMax/
├── bin/        # Applicazione eseguibile (.jar)
├── data/       # File CSV con i dati dell'applicazione
├── doc/        # Documentazione del progetto
├── lib/        # Eventuali librerie esterne
├── src/        # Codice sorgente Java
├── autori.txt  # Informazioni sugli autori
└── README.md   # Documentazione introduttiva
```

La struttura riportata è quella prevista per la consegna del progetto; la presenza effettiva di ogni file o directory dipende dalla versione del repository.

## Dati dell'applicazione

I dati vengono memorizzati localmente in file CSV, senza necessità di un database esterno.

I file contengono le informazioni relative a film, proiezioni, utenti e prenotazioni. Le modifiche effettuate tramite l'applicazione vengono salvate secondo le modalità implementate nel progetto.

## Autori

Progetto realizzato da:

* [**Giovanni Trupia**](https://github.com/giova2217)
* **Harman Mahhay**
* [**Morad Ait Laarabi**](https://github.com/moraaad)
* **Ayman Maatouch**

**Università degli Studi dell'Insubria**
Corso di Laurea in Informatica - Laboratorio Interdisciplinare A, a.a. 2025/2026.
