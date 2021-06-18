package it.uniroma2.is.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.uniroma2.is.entities.Aula;
import it.uniroma2.is.entities.Prenotazione;

public class HandlerPrenotazioniDB implements HandlerPrenotazioni {
	private Connection connection;

	public HandlerPrenotazioniDB(String user, String password, String database) {
		final String URL = "jdbc:mysql://localhost:3306/" + database;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = (Connection) DriverManager.getConnection(URL, user, password);
			System.out.println("Connected to database ProgettoIs.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("Connection to database failed in "+this.getClass().getSimpleName());
		}
	}

	@Override
	public boolean checkDisponibilita(Aula aula, Date datetime) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String insertPrenotazione(Prenotazione prenotazione) throws SQLException {
		Statement statement = this.connection.createStatement();
		String id_prenotazione = prenotazione.getId();
		String query = String.format(
				"INSERT INTO prenotazioni VALUES('%s', '%s', '%s', '%s')",
				id_prenotazione,
				prenotazione.getAula(),
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(prenotazione.getAccessoInAula()),
				prenotazione.getEmailStudente()
				);
		statement.executeUpdate(query);
		statement.close();
		return id_prenotazione;
	}

	@Override
	public void closeConnection() {
		try {
			this.connection.close();
			System.out.println("Close DB...");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
