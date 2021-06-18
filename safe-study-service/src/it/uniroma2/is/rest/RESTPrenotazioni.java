package it.uniroma2.is.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.uniroma2.is.entities.Prenotazione;
import it.uniroma2.is.handler.HandlerPrenotazioni;
import it.uniroma2.is.handler.HandlerPrenotazioniDB;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class RESTPrenotazioni
 */
public class RESTPrenotazioni extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HandlerPrenotazioni handler;
	private static final String mailCodiceConfermaFileName = "/tmp/emailCodiceConferma";  //modificare
	private static final String mailConfermaPrenotazioneFileName = "/tmp/emailPrenotazione"; //modificare
	//private FileWriter mailCodiceConfermaFileWriter;
	//private FileWriter mailConfermaPrenotazioneFileWriter;

	/**
	 * Default constructor.
	 */
	public RESTPrenotazioni() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy() {
		super.destroy();
		this.handler.closeConnection();
		/*
		try {
			this.mailCodiceConfermaFileWriter.close();
			this.mailConfermaPrenotazioneFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	@Override
	public void init() throws ServletException {
		super.init();
		String user = getServletContext().getInitParameter("user");
		String password = getServletContext().getInitParameter("password");
		String database = getServletContext().getInitParameter("database");
		this.handler = new HandlerPrenotazioniDB(user, password, database);
			/*
		try {
			String path = getServletContext().getRealPath("WEB-INF/../");
			this.mailCodiceConfermaFileWriter = new FileWriter(path+mailCodiceConfermaFileName, true);
			this.mailConfermaPrenotazioneFileWriter = new FileWriter(path+mailConfermaPrenotazioneFileName, true);
			this.mailCodiceConfermaFileWriter = new FileWriter(mailCodiceConfermaFileName, true);
			this.mailConfermaPrenotazioneFileWriter = new FileWriter(mailConfermaPrenotazioneFileName, true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ServletException("Impossibile aprire i file di richiesta mail");
			// bisogna gestire il caso in cui non riesce ad aprire il file
		}
			*/

	}

	/**
	 * Parametri della get - aulaid = Nome dell'aula da prenotare - giornoOra =
	 * giorno e ora di prenotazione espresso in formato yyyy-MM-dd-HH - email =
	 * email a cui inviare il codice di conferma in caso di disponibilita
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Prendo i parametri della richiesta
		String aulaid = request.getParameter("aulaid");
		String giornoOra = request.getParameter("giornoOra");
		String email = request.getParameter("email");

		// Setto i COOKIE
		Cookie aulaCookie = new Cookie("aulaid", aulaid);
		Cookie giornoOraCookie = new Cookie("giornoOra", giornoOra);
		Cookie emailCookie = new Cookie("email", email);

		// Tempo di vita dei cookie: 5 minuti
		final int expireTime = 5 * 60;
		aulaCookie.setMaxAge(expireTime);
		giornoOraCookie.setMaxAge(expireTime);
		emailCookie.setMaxAge(expireTime);

		// Aggiungo i COOKIES alla risposta
		response.addCookie(aulaCookie);
		response.addCookie(giornoOraCookie);
		response.addCookie(emailCookie);

		response.setHeader("Access-Control-Allow-Origin", "*");

		if (this.handler.checkDisponibilita(null, null)) {

			// Generazione del codice di controllo
			final int codeLength = 5;
			String code = "";
			
			for (int i = 0; i < codeLength; i++)
				code += (int) Math.floor(Math.random() * 10);
			
			HttpSession session = request.getSession();
			session.setAttribute("codiceConferma", code);

			// Questo codice va inviato per mail
			System.out.println("Codice conferma: " + code);
			
			// Richiedo al server mail di inviare l'email col codice di conferma
			// se non riesce ad inviarla manda un errore al client
			if (requestSendMail(request.getParameter("email"), code)) {
				RequestDispatcher view = request.getRequestDispatcher("form_conferma/form_conferma.html");
				view.forward(request, response);
			}
			else {
				response.reset(); // dovrebbe resettare i coockie
				response.sendError(503, "Ops! Ci sono dei problemi col server mail, riprova pi� tardi");
			}
			
		} else {
			response.reset();
			response.sendError(401, "Mi dispiace, impossibile prenotare l'aula desiderata.");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Prendo i cookie
		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			response.sendError(401, "Il tempo utile per eseguire la prenotazione è scaduto!");
			return;
		}

		// Prendo i dati di sessione
		HttpSession session = request.getSession();
		String codiceConfermaUtente = request.getParameter("codice-conferma");
		String codiceConferma = (String) session.getAttribute("codiceConferma");

		// Controllo che il codice di conferma sia corretto
		if (!codiceConferma.equals(codiceConfermaUtente)) {
			response.sendError(401, "Il codice di conferma inserito � errato!");
			return;
		}

		session.removeAttribute("codiceConferma");
		Map<String, Cookie> cookieMap = new HashMap();
		PrintWriter out = response.getWriter();

		// Costruisco una mappa con chiavi il nome del cookie e valore il cookie stesso
		for (Cookie cookie : cookies) {
			cookieMap.put(cookie.getName(), cookie);
			System.out.println(cookie.getName() + " " + cookie.getValue());
		}

		try {

			// Genero una nuova prenotazione
			Prenotazione p = (new Prenotazione.Builder())
					.inDate(cookieMap.get("giornoOra").getValue(), "yyyy-MM-dd-HH")
					.inAula(cookieMap.get("aulaid").getValue())
					.prenotatoIl(new Date())
					.forStudent(cookieMap.get("email").getValue())
					.build();
			// Questo ID andrà messo nell'email
			out.println(p.getId());

			try {
				// Inserisco la prenotazione nel database
				handler.insertPrenotazione(p);
			} catch (SQLException e) {
				e.printStackTrace();
				response.sendError(500, e.getMessage());
			}

			// faccio una richiesta al server mail di invio conferma
			requestSendMail(p.getEmailStudente(), p.getId(), p.getAula(), p.getAccessoInAula());
			
			// rispondo con una pagina di conferma
			RequestDispatcher view = request.getRequestDispatcher("conferma_prenotazione/conferma.html");
			view.forward(request, response);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo che richiede al server mail di inviare l'email col codice di conferma
	 * @param {String} email studente
	 * @param {String} codice di conferma
	 * */
	private boolean requestSendMail(String email, String codiceConferma) {

		String req = "'"+email+"' '"+codiceConferma+"'\n";
		try {
			//this.mailCodiceConfermaFileWriter.append(req);
			//this.mailCodiceConfermaFileWriter.write(req);
			FileWriter fw = new FileWriter(mailCodiceConfermaFileName, true);
			fw.append(req);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Metodo che richiede al server mail di inviare l'email di avvenuta prenotazione
	 * @param {String} emailStudente
	 * @param {String} idPrenotazione
	 * @param {String} aula
	 * @param {Date} accessoInAula
	 * */
	private boolean requestSendMail(String emailStudente, String idPrenotazione, String aula, Date accessoInAula) {
		
		String req = "'"+emailStudente+"' '"+idPrenotazione+"' '"+aula+"' '"+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(accessoInAula)+"'\n";

		try {
			//this.mailConfermaPrenotazioneFileWriter.write(req);
			FileWriter fw = new FileWriter(mailConfermaPrenotazioneFileName, true);
			fw.append(req);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}