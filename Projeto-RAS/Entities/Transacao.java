package Entities;
import java.time.LocalDateTime;

public class Transacao {

	private String id;
	private Float ammount;
	private String description;
	//private LocalDateTime date;


	public Transacao() {
	}

	public Transacao(String id, Float ammount) {
		this.id = id;
		this.ammount = ammount;

	}

	public Transacao(String id, Float ammount, String description) {
		this.id = id;
		this.ammount = ammount;
		this.description = description;

	}


	public String getId() {return this.id;}
	public void setId(String id) {this.id = id;}
	public Float getAmmount() {return this.ammount;}
	public void setAmmount(Float ammount) {this.ammount = ammount;}
	public String getDescription() {return this.description;}
	public void setDescription(String description) {this.description = description;}


	@Override
	public String toString() {
		return "Transaction{" +
				"Id=" + id +
				", Ammount=" + ammount +
				", Description=" + description +
				'}';
	}
}