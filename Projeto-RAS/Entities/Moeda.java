package Entities;

public class Moeda{
	private String nome;
	private String token;
	private float imposto;


	public Moeda() {
		this.nome = "Euro";
		this.token = "€";
		this.imposto = (float) 0.0005;
	}

	public Moeda(String nome, String token, float imposto) {
		this.nome = nome;
		this.token = token;
		this.imposto = imposto;
	}


	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}
	public String getToken() {return token;}
	public void setToken(String token) {this.token = token;}
	public float getImposto() {return imposto;}
	public void setImposto(float imposto) {this.imposto = imposto;}

	@Override
	public String toString() {return "Coin{"+ nome +" (" + token + "), Tax=" + imposto + "}";}
}