package com.example.Entities;
import java.time.LocalDateTime;

public class Notificacao {

	private String id;
	private String message;
	//private LocalDateTime date;


	public Notificacao() {
	}

	public Notificacao(String id, String message) {
		this.id = id;
		this.message = message;
	}


	public String getId() {return this.id;}
	public void setId(String id) {this.id = id;}
	public String getMessage() {return this.message;}
	public void setMessage(String message) {this.message = message;}


	@Override
	public String toString() {
		return "Notification{" +
				"Id=" + id +
				", Message=" + message +
				'}';
	}
}