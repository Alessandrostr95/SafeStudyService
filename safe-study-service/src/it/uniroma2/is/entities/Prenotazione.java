package it.uniroma2.is.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Prenotazione {

	@Override
	public String toString() {
		return "Prenotazione [id=" + id + ", accessoInAula=" + accessoInAula + ", aula=" + this.aulaid
				+ ", dataPrenotazione=" + dataPrenotazione + ", emailStudente=" + emailStudente + "]";
	}

	private String id;
	private Date accessoInAula;
	private String aulaid;
	private Date dataPrenotazione;
	private String emailStudente;

	private Prenotazione(String id, Date accessoInAula, String aulaid, Date dataPrenotazione, String emailStudente) {
		super();
		this.id = id;
		this.accessoInAula = (Date) accessoInAula.clone();
		this.aulaid = aulaid;
		this.dataPrenotazione = (Date) dataPrenotazione.clone();
		this.emailStudente = emailStudente;
	}

	public String getId() {
		return id;
	}

	public Date getAccessoInAula() {
		return (Date) accessoInAula.clone();
	}

	public String getAula() {
		return this.aulaid;
	}

	public Date getDataPrenotazione() {
		return (Date) dataPrenotazione.clone();
	}

	public String getEmailStudente() {
		return emailStudente;
	}

	/**
	 * 
	 * @param accessoInAula
	 * @param aula
	 * @param dataPrenotazione
	 * @param emailStudente
	 * @return
	 */
	public static String randomString(int len) {
		String randStr = "";
		String charSet = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
		for (int i = 0; i < len; i++) {
			randStr = randStr + charSet.charAt((int) (Math.random() * (charSet.length() - 1)));
		}
		return randStr;
	}

	/**
	 * La classe builder consente di fare una BuildChain per costruire una
	 * prenotazione ben formata. Esempio di BuildChain:
	 * 
	 * Prenotazione p = (new Prenotazione.Builder()) .inDate("12-06-2021 14:30",
	 * "dd-MM-yyyy HH:mm") .inAula(aula) .idPrenotazione("2GKASDB3er98ksad7")
	 * .prenotatoIl("12-06-2021 14:30", "dd-MM-yyyy HH:mm")
	 * .forStudent("hdkhsk@gmail.com") .build();
	 */
	public static class Builder {
		private String id;
		private Date accessoInAula;
		private String aulaid;
		private Date dataPrenotazione;
		private String emailStudente;

		public Builder() {
			super();
		}

		/**
		 * Data in cui l'alunno puo' usufruire dell'aula
		 * 
		 * @param {String} Date stringa che rappresenta la data desiderata (coerente con
		 *                 la formattazione) e.g. "12-06-2021 14:30"
		 * @param {String} Format il formato che ha la string che rappresenta la data
		 *                 e.g. "dd-MM-yyyy HH:mm"
		 */
		public Builder inDate(String date, String format) throws ParseException {
			this.accessoInAula = new SimpleDateFormat(format).parse(date);
			return this;
		}
		
		/**
		 * Data in cui l'alunno puo' usufruire dell'aula
		 * 
		 * @param {Date} Data in cui l'alunno puo' usufruire dell'aula
		 */
		public Builder inDate(Date data) {
			this.accessoInAula = data;
			return this;
		}

		/**
		 * Data in cui e' avvenuta la prenotazione
		 * 
		 * @param {String} Date stringa che rappresenta la data desiderata (coerente con
		 *                 la formattazione) e.g. "12-06-2021 14:30"
		 * @param {String} Format il formato che ha la string che rappresenta la data
		 *                 e.g. "dd-MM-yyyy HH:mm"
		 */
		public Builder prenotatoIl(String date, String format) throws ParseException {
			this.dataPrenotazione = new SimpleDateFormat(format).parse(date);
			return this;
		}
		
		/**
		 * Data in cui e' avvenuta la prenotazione
		 * 
		 * @param {Date} Data in cui avviene la prenotazione
		 */
		public Builder prenotatoIl(Date data) {
			this.dataPrenotazione = data;
			return this;
		}

		/**
		 * Aula prenotata
		 * 
		 * @param {Aula} aula
		 */
		public Builder inAula(String aula) {
			this.aulaid = aula;
			return this;
		}

		/**
		 * @param {String} email con cui lo studente ha prenotato
		 */
		public Builder forStudent(String email) {
			this.emailStudente = email;
			return this;
		}

		/**
		 * Funzione che "assembla" tutti i dati registrati dalla BuildChain e costruisce
		 * una prenotazione ben formata
		 * 
		 * @return {Prenotazione} prenotazione
		 */
		public Prenotazione build() {
			return new Prenotazione(Prenotazione.randomString(10), this.accessoInAula, this.aulaid, this.dataPrenotazione,
					this.emailStudente);
		}
	}

}
