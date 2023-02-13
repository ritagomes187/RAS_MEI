package com.example.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Historico {

	private int totalWins;
	private float totalProfit;
	private float totalInvested;
	private HashMap<String, Aposta> openBets;
	private HashMap<String, Aposta> closedBets;
	private List<Transacao> transactions;


	public Historico() {
		this.totalWins = 0;
		this.totalProfit = 0;
		this.totalInvested = 0;
		this.openBets = new HashMap<>();
		this.closedBets = new HashMap<>();
		this.transactions = new ArrayList<>();
	}

	public Historico(Historico h) {
		this.openBets = h.getOpenBets();
		this.closedBets = h.getClosedBets();
		this.transactions = h.getTransactions();
		this.totalWins = h.getTotalWins();
		this.totalProfit = h.getTotalProfit();
		this.totalInvested = h.getTotalInvested();
	}

	public Historico(HashMap<String, Aposta> openBets, HashMap<String, Aposta> closedBets, List<Transacao> transactions, float totalInvested, float totalProfit, int totalWins) {
		this.openBets = openBets;
		this.closedBets = closedBets;
		this.transactions = transactions;
		this.totalWins = totalWins;
		this.totalProfit = totalProfit;
		this.totalInvested = totalInvested;
	}


	public int getTotalWins() {return this.totalWins;}
	public void setTotalWins(int totalWins) {this.totalWins = totalWins;}
	public float getTotalProfit() {return this.totalProfit;}
	public void setTotalProfit(float totalProfit) {this.totalProfit = totalProfit;}
	public float getTotalInvested() {return this.totalInvested;}
	public void setTotalInvested(float totalInvested) {this.totalInvested = totalInvested;}
	public HashMap<String, Aposta> getOpenBets() {return this.openBets;}
	public void setOpenBets(HashMap<String, Aposta> openBets) {this.openBets = openBets;}
	public HashMap<String, Aposta> getClosedBets() {return this.closedBets;}
	public void setClosedBets(HashMap<String, Aposta> closedBets) {this.closedBets = closedBets;}
	public List<Transacao> getTransactions() {return this.transactions;}
	public void setTransactions(List<Transacao> transactions) {this.transactions = transactions;}
	public void novaAposta(Aposta aposta) {this.openBets.put(aposta.getIdAposta(), aposta);}

	
	@Override
	public String toString() {
		return "History{" +
				"Total Wins='" + totalWins + '\'' +
				", Total Invested='" + totalInvested + '\'' +
				", Total Profit='" + totalProfit + '\'' +
				", closed Bets='" + closedBets + '\'' +
				", open Bets='" + openBets + '\'' +
				", transactions='" + transactions +
				'}';
	}

}

