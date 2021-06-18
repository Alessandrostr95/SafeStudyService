package it.uniroma2.is.handler;

import java.sql.SQLException;
import java.util.Date;

import it.uniroma2.is.entities.Aula;
import it.uniroma2.is.entities.Prenotazione;

public interface HandlerPrenotazioni {
	
	boolean checkDisponibilita(Aula aula, Date datetime);
	
	String insertPrenotazione(Prenotazione prenotazione) throws SQLException;
	
	void closeConnection();
}
