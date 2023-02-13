package com.example.Entities;

import java.util.*;

public class Apostador extends Utilizador {

	// personal details
	private Date birthDate;

	// statistics
	private Saldo saldo;
	private Historico historico;


	public Apostador() {
		super();
		this.setPrivileges(0);
	}

	public Apostador(Utilizador a) {
		super(a);
		this.setPrivileges(0);
	}

	public Apostador(Apostador a) {
		super(a);
		this.setPrivileges(0);
		this.birthDate = a.getBirthDate();
		this.saldo = a.getSaldo();
		this.historico = a.getHistorico();
	}

	public Apostador(Date birthDate, String email, String name, String username, String idUser, Saldo saldo, Historico hist) {
		super(email, name, username, idUser, saldo, hist, 0);
		this.birthDate = birthDate;
		this.saldo = new Saldo();
		this.historico = new Historico();
	}


	public Date getBirthDate() {return birthDate;}
	public void setBirthDate(Date birthDate) {this.birthDate = birthDate;}
	public Saldo getSaldo() {return this.saldo;}
	public Historico getHistorico() {return this.historico;}
	public String getName() {return super.getName();}
	public void setName(String name) {super.setName(name);}
	public String getEmail() {return super.getEmail();}
	public void setEmail(String email) {super.setEmail(email);}
	public String getUsername() {return super.getUsername();}
	public void setUsername(String username) {super.setUsername(username);}
	public String getIdUser() {return super.getIdUser();}
	public void setIdUser(String idUser) {super.setIdUser(idUser);}


	@Override
	public String toString() {
		return "Apostador{" +
				super.toString() +
				", birthDate=" + birthDate +
				this.saldo.toString() +
				this.historico.toString() +
				"}";
	}

}