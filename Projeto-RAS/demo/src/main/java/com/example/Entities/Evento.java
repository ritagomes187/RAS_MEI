package com.example.Entities;

public class Evento {

	private String idEvento;
	private String desporto;
	private String home_team;
	private String away_team;
	private String estado;
	private String resultadoFinal;
	private String data;
	private Float odd_home;
	private Float odd_away;
	private Float odd_draw;
	private Promocao promocao;


	public Evento(String idEvento, String desporto, String home_team, String away_team, String estado, String resultado, String data, float odd_home, float odd_away, float odd_draw) {
		this.idEvento = idEvento;
		this.desporto = desporto;
		this.home_team = home_team;
		this.away_team = away_team;
		this.estado = estado;
		this.resultadoFinal = resultado;
		this.data = data;
		this.odd_home = odd_home;
		this.odd_away = odd_away;
		this.odd_draw = odd_draw;
	}

	public Evento() {
	}

	public String getEstado() {return estado;}
	public void setEstado(String estado) {this.estado = estado;}
	public String getDesporto() {return desporto;}
	public void setDesporto(String desporto) {this.desporto = desporto;}
	public String getIdEvento() {return idEvento;}
	public void setIdEvento(String idEvento) {this.idEvento = idEvento;}
	public String getData() {return data;}
	public void setData(String data) {this.data = data;}
	public String getResultado() {return resultadoFinal;}
	public void setResultado(String resultadoFinal) {this.resultadoFinal = resultadoFinal;}
	public String getHomeTeam() { return home_team;}
	public String getAwayTeam() {return away_team;}
	public Float getHomeOdd() {return odd_home;}
	public void setHomeOdd(float odd) { this.odd_home = odd;}
	public Float getAwayOdd() {return odd_away;}
	public void setAwayOdd(float odd) { this.odd_away = odd;}
	public Float getDrawOdd() {return odd_draw;}
	public void setDrawOdd(float odd) { this.odd_draw = odd;}
	public void setPromocao(Promocao p) {this.promocao = p;}
	public Promocao getPromocao() {return this.promocao;}

	@Override
	public String toString() {
		return idEvento+"\t"+String.format("%"+(-20)+"s",home_team)+" "+(resultadoFinal!=null?resultadoFinal:(odd_home.toString()+','+odd_draw.toString()+','+odd_away.toString()))+" "+String.format("%"+(20)+"s",away_team)+"\t"+data;
	}

}
