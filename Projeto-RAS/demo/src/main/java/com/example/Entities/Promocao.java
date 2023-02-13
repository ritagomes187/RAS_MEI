package com.example.Entities;

import java.time.LocalDate;

public class Promocao {

	private String id; 
	private float bonus; 
	/*LocalDate startDate;
	LocalDate endDate;*/


	public Promocao(String id, float b) {
		this.id = id;
		this.bonus = b;
		/*this.endDate = endDate;
		this.startDate = startDate;*/
	}

	public Promocao(float bonus) {
		this.bonus = bonus;
	}

	public Promocao() {
	}

	public Promocao(Promocao p) {
		this.id = p.getID();
		this.bonus = p.getBonus();
		/*this.endDate = p.getEndDate();
		this.startDate = p.getStartDate();*/
	}


	public String getID() {return this.id;}
	public void setID(String m) {this.id = m;}
	public float getBonus() {return this.bonus;}
	public void setBonus(float b) {this.bonus = b;}
	/*public LocalDate getEndDate(){ return endDate;}
	public void SetEndDate(LocalDate endDate){ this.endDate = endDate;}
	public LocalDate getStartDate(){ return startDate;}
	public void setStartDate(LocalDate startDate){ this.startDate = startDate;}*/
}
