package it.uniroma2.is.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import it.uniroma2.is.entities.Aula;

public class HandlerAuleDB implements HandlerAule {

	private Connection connection;
	private String USER;
	private String PASSWD;
	private String DB;
	private String URL;
	/* private String URL = "jdbc:mysql://localhost:3306/progettois"; */
	/* per mariadb invece "jdbc:mariadb://localhost:3306/progettois"; */

	/**
	 * Costruttore di classe
	 */
	public HandlerAuleDB(String user, String password, String database) {
		this.USER = user;
		this.PASSWD = password;
		this.DB = database;
		//this.URL = "jdbc:mariadb://localhost:3306/" + this.DB;
		this.URL = "jdbc:mysql://localhost:3306/" + this.DB;
		this.connection = this.getConnection();
	}

	/**
	 * Metodo che ritorna una connessione col DB SafeStudyService L'oggetto
	 * HandlerAule chiamante dovrebbe gi� avere un campo Connessione istanziato, nel
	 * caso non fosse cos�, il metodo crea una nuova connessione e la restituisce.
	 */
	public Connection getConnection() {
		if (this.connection != null) {
			return this.connection;
		} else {
			/*
			 * Non so se lasciarlo o meno. In teoria � deprecato ma funziona. Se non
			 * funziona decommenta questa istruzione Class.forName("com.mysql.jdbc.Driver");
			 * oppure per mariaDB Class.forName("org.mariadb.jdbc.Driver");
			 */
			Connection conn = null;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				//Class.forName("org.mariadb.jdbc.Driver");
				conn = (Connection) DriverManager.getConnection(URL, USER, PASSWD);
				System.out.println("Connected to database ProgettoIs.");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				System.err.println("Connection to database ProgettoIs failed!");
			}
			return conn;
		}
	}

	/**
	 * Metodo che chiude la connessione col DB se presente.
	 */
	public void closeConnection() {
		try {
			this.connection.close();
			System.out.println("Connection to database ProgettoIs closed.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Metodo che ritorna l'insieme delle aule
	 * 
	 * @return {Set} Aule
	 */
	public Set<Aula> getAule() {

		HashSet<Aula> insiemeAule = new HashSet<Aula>();

		try {
			Statement stmt = this.getConnection().createStatement();
			ResultSet infoAule = stmt.executeQuery("Select * from aule;");

			while (infoAule.next()) {
				insiemeAule.add(
						new Aula(infoAule.getString("id"), infoAule.getString("edificio"), infoAule.getInt("piano"),
								infoAule.getInt("capienza"), infoAule.getBoolean("aria_condizionata")));
			}

			infoAule.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return insiemeAule;
	}

	/**
	 * Metodo che date un insieme di aule, le inserisce nel DB
	 * 
	 * @param {Set} aule
	 * @return {boolean}
	 */
	public boolean insertAule(Set<Aula> aule) {
		String query = "INSERT INTO aule VALUES";
		try {
			Statement statement = this.connection.createStatement();

			for (Aula a : aule) {
				statement.executeUpdate(query + "('" + a.getNomeAula() + "', '" + a.getEdificio() + "', " + a.getPiano()
						+ ", " + a.getNumeroPosti() + ", " + a.checkAriaCondizionata() + ")");
			}

			statement.close();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Metodo che effettua una query di test sul DB
	 */
	public void queryTest() {
		try {
			String query = "select * from aule limit 10";
			Statement statement = this.connection.createStatement();
			ResultSet res = statement.executeQuery(query);

			while (res.next()) {
				System.out.println(res.getString("id"));
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
		}
	}

	/**
	 * Metodo che data il giorno del quale si vogliono avere informazioni,
	 * ritorna una mappa che a ogni aula associa le deisponibilita suddivise in fasce oraria.
	 * @param {Date} giorno che rappresenta la data scelta
	 * @return {Map<Aula,Map<Integer, Integer>>} Una mappa che a ogni aula associa le deisponibilita suddivise in fasce oraria
	 * */
	@SuppressWarnings("deprecation")
	@Override
	public HashMap<Aula, HashMap<Integer, Integer>> getAuleDisponibiliByGiorno(Date giorno) {
		// TODO Auto-generated method stub
		int gg = giorno.getDate();
		int mm = giorno.getMonth()+1;
		String queryMichele = "SELECT id, capienza - IFNULL(prenotati, 0) as posti_disponibili, aule_ore.ora "+
				"FROM ( SELECT * FROM aule, range_table ) AS aule_ore " + 
			    "LEFT JOIN ( "+
				"SELECT aulaid, hour(giornoOra) as ora, count(*) as prenotati from prenotazioni " +
			    "WHERE  DAY(giornoOra)="+gg+" AND MONTH(giornoOra)="+mm+" group by aulaid, hour(giornoOra)) "+
				"as prenotazioniAuleOra ON aule_ore.id=prenotazioniAuleOra.aulaid AND aule_ore.ora=prenotazioniAuleOra.ora " +
				"WHERE capienza - IFNULL(prenotati,0) > 0 ORDER BY aule_ore.id, ora;";

		Set<Aula> aule = this.getAule();
		HashMap<String, HashMap<Integer, Integer>> disponibilita = new HashMap<String, HashMap<Integer, Integer>>();

		for (Aula a : aule) {
			disponibilita.put(a.getNomeAula(), new HashMap<Integer, Integer>());
		}

		try {

			Statement stmt = this.connection.createStatement();
			ResultSet infoDisp = stmt.executeQuery(queryMichele);

			String id;
			int posti, ora;

			while (infoDisp.next()) {
				id = infoDisp.getString("id");
				posti = infoDisp.getInt("posti_disponibili");
				ora = infoDisp.getInt("ora");
				disponibilita.get(id).put(ora, posti);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		HashMap<Aula, HashMap<Integer, Integer>> result = new HashMap<Aula, HashMap<Integer, Integer>>();
		for (Aula a : aule) {
			result.put(a, disponibilita.get(a.getNomeAula()));
		}
		return result;
	}
	
	/*
	public static void main(String[] args) {
		HandlerAule ha = new HandlerAuleDB("IS", "password", "progettois");
		System.out.println(ha.getAuleDisponibiliByGiorno(
				new Date(2021, 7, 14)
				));
		// ha.queryTest(); System.out.println(ha.getAule());
	}
	*/

}
