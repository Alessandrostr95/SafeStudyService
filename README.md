# Safe Study Service Tor Vergata
![](https://upload.wikimedia.org/wikipedia/commons/1/1d/Logo-Universita-Roma-Tor-Vergata.png)

![](https://github.com/Alessandrostr95/SafeStudyService/tree/main/safe-study-service) ![](https://img.shields.io/github/forks/pandao/editor.md.svg) ![](https://img.shields.io/github/tag/pandao/editor.md.svg) ![](https://img.shields.io/github/release/pandao/editor.md.svg) ![](https://img.shields.io/github/issues/pandao/editor.md.svg) ![](https://img.shields.io/bower/v/editor.md.svg)

------------

**Indice**

[TOCM]


##Introduzione
In questo anno così particolare, per colpa della pandemia globale, sono cambiate molte cose nel quotidiano modo di vivere. Nel contesto universitario, studenti e docenti hanno dovuto compiere sacrifici ed enormi adattamenti pur di continuare al meglio l’attività didattica.
Da studenti possiamo dire che uno dei sacrifici più grandi è stato quello di rinunciare a un confronto diretto, faccia a faccia, con compagni di studio e docenti. Questo sacrificio non è roba da poco, in quanto il confronto tra le menti stimola molto la curiosità, la voglia di imparare nuove cose ed infiamma la passione per le materie.
Nonostante informatici, più a nostro agio nell’interfacciarci con macchine piuttosto che con
persone, siamo pur sempre uomini, e quindi, animali sociali :tw-1f61c:.

Adesso che la situazione pandemica è in apparente miglioramento, si iniziano lentamente a
riprendere le varie attività. Alcuni corsi si svolgono in modalità mista, si stanno programmando esami da svolgere in presenza ed è di nuovo consentito l’accesso alle strutture universitarie, con le dovute precauzioni.
Proprio su quest’ultimo punto ci siamo voluti soffermare. Nonostante alcune aule siano state adibite allo studio, abbiamo notato che:
1. Non c’è un sistema che cerchi di monitorare la densità di persone presenti.
2. Con una corretta organizzazione si potrebbero aprire più aule, garantendo l’accesso a più studenti, il tutto rispettando i vincoli di sicurezza vigenti.

### Esigenza
Dopo il periodo di quarantena, ora che stanno riaprendo le strutture, in quanto studenti sentiamo la necessità di un sistema che ci permetta di ritornare in tranquillità a studiare in università. Abbiamo notato che sono disponibili poche aule studio aperte, e che ci vuole del tempo per trovarne una non troppo affollata.

Non essendoci un sistema preposto alla prenotazione delle aule studio, il metodo per verificare la disponibilità di un'aula è quello tradizionale:
1. Apri la porta dell’aula
2. “Scusate, c’è lezione?” - “No”
3. Scegliere il posto a sedere

Nonostante la robustezza di questo metodo, scambiare informazioni a parole è antiquato per la frenesia e apatia della modernità, dove l’unico modo per scambiare informazioni deve essere digitale/virtuale.


Abbiamo anche notato che ci sono parecchie aule libere che non vengono usate, e che si potrebbero sfruttarle al meglio adibendole per gli studenti.

Chiaramente sorge il problema che più aule sono aperte, più è difficile controllare la densità di gente presente, per evitare conseguenti assembramenti :tw-1f637:.

Perciò sarebbe utile un sistema che semplifichi e automatizzi questa ricerca di aule, e che nello stesso momento possa aiutare l’università a gestire la densità di studenti, cercando di garantire al meglio l’incolumità di tutti.

### Obiettivi
Si vuole realizzare un servizio universitario che consenta agli studenti di poter prenotare l'accesso alle aule studio predisposte dalla **Macroarea di Scienze MM.FF.NN.**, cercando inoltre di semplificare all'università il problema dell'organizzazione delle entrate degli studenti a fronte della situazione pandemica attuale.

È possibile guardare a questo servizio come a uno strumento utile per tenere sotto controllo al meglio la densità di persone presenti negli edifici universitari, minimizzando la possibilità che si creino affollamenti nelle aule.

La prenotazione delle aule **NON** è un requisito necessario per accedervi, in quanto si necessiterebbe di un terzo attore (umano o dispositivo elettronico) che controlli, studente per studente, chi è effettivamente in possesso o meno di una prenotazione valida; questo per ogni aula ad ogni ora. Perciò ci si affida alla *collaborazione* e al *buon senso* dello studente che, per correttezza verso il prossimo, prenoti un'aula prima di farne uso.

Il servizio dovrà quindi offrire:
1. Un'interfaccia web **SEMPLICE** e **INTUITIVA** agli studenti interessati ad usufruire delle aule al di fuori dell'orario di lezione.
2. Un sistema di prenotazione semplice e veloce, che non comporti fasi troppo articolate (registrazioni, login, ecc ...).
3. Un sistema di gestione delle disponibilità delle aule.


------------


##Sequence Diagram

### Procedure di Scelta e Prenotazione

```seq
User->Client: 1: scleta giorno
Client->Server: 1.1: getAule(giorno)
Server-->Client: 1.2: disponibilità
Client-->User: 1.3: tabella disponibilità

User->Client: 2: scleta prenotazione
Client->Server: 2.1: prenota(aula, giorno-ora, email)
Server->Server: 2.1.1: checkDisponibilità(aula, ora)
Server->Server Mail: 2.1.2: sendMail(email, codiceConferma)
Server Mail-->User: 2.1.2.1: codiceConferma

User->Client: 3: inserisce codiceConferma
Client->Server: 3.1: confirm(codiceConferma)
Server->Server: 3.1.1: checkcodiceConferma(codiceConferma)

Server->Server Mail: 3.1.2: sendMail(prenotazione)
Server-->Client: 3.1.3: prenotazione OK

Server Mail-->User: 3.1.2.1: email conferma
```

### Procedure di Annullamento Prenotazione

```seq
User->Server: 1: segue link annulamento
Server->Server: 1.1: checkConsistenza(IdPrenotazione)
Server-->User: 1.2: chiede conferma

User->Server: 2: conferma
Server->Server: 2.1: cancellaPrenotazione(IdPrenotazione)
Server-->User: 2.2: notifica
```
