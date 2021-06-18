package it.uniroma2.is.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma2.is.entities.Aula;
import it.uniroma2.is.handler.HandlerAuleDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HandlerAule
 */
/* @WebServlet("/aule") */
public class RESTAule extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private HandlerAuleDB handlerAule;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		String user = getServletContext().getInitParameter("user");
		System.out.println(user);
		String password = getServletContext().getInitParameter("password");
		String database = getServletContext().getInitParameter("database");
		this.handlerAule = new HandlerAuleDB(user, password, database);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		this.handlerAule.closeConnection();
	}

	/**
	 * Default constructor.
	 */
	public RESTAule() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Restituisce un array di aule con le relative disponibilita' in base
	 * al giorno richiesto nella GET.
	 * Il metodo richiede come parametro 
	 * 	- data=[yyyy-MM-dd]
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setStatus(200);
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		Date data = null;
		try {
			data = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("data"));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			data = new Date();
		}
		
		JSONArray risposta = new JSONArray();
		HashMap<Aula, HashMap<Integer, Integer>> disponibilita;
		disponibilita = this.handlerAule.getAuleDisponibiliByGiorno(data);
		try {
			for (Aula a : disponibilita.keySet()) {
				JSONObject jsonAula = new JSONObject(a);
				JSONObject jsonDisp = new JSONObject(disponibilita.get(a));
				JSONObject element = new JSONObject();

				element.put("aula", jsonAula);
				element.put("availabilities", jsonDisp);
				
				risposta.put(element);
			}
			//System.out.println(risposta);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();
		out.println(risposta.toString());
	}

	/**
	 * Metodo che dato un body che comprende un jason array di aule, le inserisce
	 * nella raccolta delle aule studio disponibili.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Get body from request
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines()
				.collect(Collectors.joining("\n"));
		try {

			// Parse body string in json array
			JSONArray jsonArray = new JSONArray(jsonBody);

			Set<Aula> aule = new HashSet<Aula>();
			int numJsonAule = jsonArray.length();

			// For each json object, put into set aule
			for (int i = 0; i < numJsonAule; i++) {

				JSONObject jsonAula = (JSONObject) jsonArray.get(i);
				Aula aula = new Aula(jsonAula.getString("id"), jsonAula.getString("edificio"), jsonAula.getInt("piano"),
						jsonAula.getInt("capienza"), jsonAula.getBoolean("aria_condizionata"));
				aule.add(aula);
			}

			// System.out.println(aule);
			this.handlerAule.insertAule(aule);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
