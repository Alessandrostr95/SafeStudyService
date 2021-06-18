package it.uniroma2.is.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import it.uniroma2.is.entities.Aula;

public interface HandlerAule {
	/**
	 * Metodo che ritorna l'insieme delle aule
	 * @return {Set} Aule
	 * */
	public Set<Aula> getAule();
	
	/**
	 * Metodo che inseirisce le aule nel sistema
	 * @param {Set} aule
	 * @return {boolean}
	 * */
	public boolean insertAule(Set<Aula> aule);
	
	/**
	 * Metodo che data il giorno del quale si vogliono avere informazioni,
	 * ritorna una mappa che a ogni aula associa le deisponibilita suddivise in fasce oraria.
	 * @param {Date} giorno che rappresenta la data scelta
	 * @return {Map<Aula,Map<Integer, Integer>>} Una mappa che a ogni aula associa le deisponibilita suddivise in fasce oraria
	 * */
	public HashMap<Aula, HashMap<Integer, Integer>> getAuleDisponibiliByGiorno(Date giorno);
}
