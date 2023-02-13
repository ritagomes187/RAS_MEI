package Entities;

public class Saldo {

	private Moeda moeda;
	private Float valor;


	public Saldo() {
		this.moeda = new Moeda();
		this.valor = (float) 0;
	}

	public Saldo(Moeda moeda, Float valor) {
		this.moeda = moeda;
		this.valor = valor;
	}

	public Float getBalance() {return valor;}
	public void setBalance(Float valor) {this.valor = valor;}
	public Moeda getMoeda() {return moeda;}
	public void setMoeda(Moeda moeda) {this.moeda = moeda;}

	@Override
	public String toString() {return moeda.getNome()+": "+valor+moeda.getToken();}
}
