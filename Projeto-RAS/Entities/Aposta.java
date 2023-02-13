package Entities;

import java.time.LocalDateTime;

public class Aposta {

	private String idAposta;
	private float quantia;
	private float odd;
	private String idResultado;
	private String estado;
	private LocalDateTime data;
	private String equipa;
	private Moeda moeda;
	private Evento evento;
	private Promocao promocao;


	public Aposta() {
	}

	public Aposta(Aposta a) {
		this.quantia = a.getQuantia();
		this.odd = a.getOdd();
		this.estado = a.getEstado();
		this.idResultado = a.getIdResultado();
		this.idAposta = a.getIdAposta();
		this.data = a.getData();
		this.moeda = a.getMoeda();
		this.evento = a.getEvento();
		this.promocao = a.getPromocao();
		this.equipa = a.getEquipa();
	}

	public Aposta(float quantia, float oddFixa, String estado, String idResultado, String idAposta, String equipa, LocalDateTime data, Moeda m, Evento e, Promocao p) {
		this.quantia = quantia;
		this.odd = oddFixa;
		this.estado = estado;
		this.idResultado = idResultado;
		this.idAposta = idAposta;
		this.data = data;
		this.moeda = m;
		this.evento = e;
		this.promocao = p;
		this.equipa = equipa;
	}

	public Aposta(float odd, float quantia, String equipa, Evento evento) {
		this.quantia = quantia;
		this.odd = odd;
		this.evento = evento;
		this.equipa = equipa;
	}


	public float getOdd() {return odd;}
	public void setOdd(float odd) {this.odd = odd;}
	public String getIdResultado() {return idResultado;}
	public void setIdResultado(String idResultado) {this.idResultado = idResultado;}
	public void setEstado(String estado) {this.estado = estado;}
	public String getEstado() {return this.estado;}
	public String getIdAposta() {return idAposta;}
	public void setIdAposta(String idAposta) {this.idAposta = idAposta;}
	public float getQuantia() {return quantia;}
	public void setQuantia(float quantia) {this.quantia = quantia;}
	public LocalDateTime getData() {return data;}
	public void setData(LocalDateTime data) {this.data = data;}
	public Moeda getMoeda() {return moeda;}
	public void setMoeda(Moeda moeda) {this.moeda = moeda;}
	public Evento getEvento() {return evento;}
	public void setEvento(Evento evento) {this.evento = evento;}
	public Promocao getPromocao() {return promocao;}
	public void setPromocao(Promocao promocao) {this.promocao = promocao;}
	public String getEquipa() {return equipa;}
	public void setEquipa(String equipa) {this.equipa = equipa;}


	@Override
	public String toString() {
		return "Aposta{" +
				"quantity=" + quantia +
				", odd=" + odd +
				'}';
	}

}