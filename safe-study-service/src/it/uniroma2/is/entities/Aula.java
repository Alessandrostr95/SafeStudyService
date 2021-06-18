package it.uniroma2.is.entities;

public class Aula {
	private String id;
	private int piano;
	private String edificio;
	private int capienza;
	private boolean aria_condizionata;
	
	/**
	 * Costruttore di classe
	 * @param {String} id
	 * @param {String} edificio
	 * @param {Integer} piano
	 * @param {Integer} numeroPosti
	 * @param {Boolean} ariaCondizionata
	 * @return {Aula} aula
	 * */
	public Aula(String nomeAula, String edificio, int piano, int numeroPosti, boolean ariaCondizionata) {
		super();
		this.id = nomeAula;
		this.piano = piano;
		this.edificio = edificio;
		this.capienza = numeroPosti;
		this.aria_condizionata = ariaCondizionata;
	}
	
	public String getNomeAula() {
		return id;
	}
	public int getPiano() {
		return piano;
	}
	public String getEdificio() {
		return edificio;
	}	
	public int getNumeroPosti() {
		return capienza;
	}
	@Deprecated
	public boolean checkAriaCondizionata() {
		return aria_condizionata;
	}
	public boolean getAriaCondizionata() {
		return aria_condizionata;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Aula [nomeAula=" + id
				+ ", piano=" + piano
				+ ", edificio=" + edificio
				+ ", numeroPosti=" + capienza
				+ ", ariaCondizionata=" + aria_condizionata + "]";
	}
	
}
