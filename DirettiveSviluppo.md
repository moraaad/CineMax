**GUIDA ALLO SVILUPPO PER PROGETTO (CineMax)**

**1. REGOLE DEI BRANCH (Tassative)**

- È vietato fare commit diretti su main e su develop.

- Ogni feature deve avere il suo branch locale dedicato creato a partire da develop.

- **Naming Convention Branch (Seguiamola per facilitare anche la doc):** feature/nome-funzionalita (es. feature/tui-menu, feature/csv-parser).

**2. WORKFLOW OPERATIVO QUOTIDIANO**

Ogni volta che iniziate a programmare, seguite questa sequenza sul terminale per evitare conflitti:

1.  Spostatevi su develop: git checkout develop

2.  Scaricate gli aggiornamenti degli altri: git pull origin develop

3.  Tornate sul vostro branch di feature: git checkout feature/vostro-branch

4.  Portate le novità nel vostro branch: git merge develop

**Per HARMAN:** \"Entra nel terminale della tua cartella CineMax e digita:

- git fetch origin git checkout -b feature/tui-e-modelli origin/develop

- Ti troverai nel tuo branch di lavoro privato, basato su develop, con le cartelle tui e core già pronte ad aspettarti. Lavora lì dentro.\"

**Per GIOVANNI:** \"Entra nel terminale della tua cartella CineMax e digita:

- git fetch origin git checkout -b feature/data-layer origin/develop

- Ti troverai nel tuo branch di lavoro privato, basato su develop, con le cartelle data e core già pronte.

- Troverai anche il file proiezioni.csv già posizionato nella cartella /data principale.\"

**3. REGOLE DI FINE TASK (Pull Request)**

- Quando avete finito il vostro compito e il codice compila sul vostro PC, fate il push del vostro branch su GitHub.

- Aprite una **Pull Request (PR)** da vostro-branch verso develop.

- Mandate un messaggio al PROJECT MANAGER. Il codice verrà controllato e unito a develop solo dopo la verifica di compilazione. (PIPLINE)
