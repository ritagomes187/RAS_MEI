package Entities;

public class Utilizador {

	private Integer age;
	private Integer nif;
	private String address;
	private String cellno;
	private String cc;
	private String name;
	private String language;
	private String email;
	private String username;

	private int privileges;
	private String idUser;
	private Saldo saldo;
	private Historico hist;


	public Utilizador() {
		this.privileges = -1;
	}

	public Utilizador(Utilizador other) {
		this.age = other.getAge();
		this.nif = other.getNIF();
		this.address = other.getAddress();
		this.cellno = other.getCellNo();
		this.cc = other.getCC();
		this.name = other.getName();
		this.language = other.getLanguage();
		this.email = other.getEmail();
		this.username = other.getUsername();
		this.idUser = other.getIdUser();
	}
	
	public Utilizador(String email, String name, String username, String idUser, Saldo saldo, Historico hist, int privileges) {
		this.email = email;
		this.name = name;
		this.username = username;
		this.idUser = idUser;
		this.saldo = saldo;
		this.hist = hist;
		this.privileges = privileges;
	}

	public Utilizador(Integer age, Integer nif, String address, String cellno, String cc, String name,
			String language, String email, String username) {
		this.age = age;
		this.nif = nif;
		this.address = address;
		this.cellno = cellno;
		this.cc = cc;
		this.name = name;
		this.language = language;
		this.email = email;
		this.username = username;
	}


	public Integer getAge() {return age;}
	public void setAge(Integer age) {this.age = age;}
	public Integer getNIF() {return nif;}
	public void setNIF(Integer nif) {this.nif = nif;}
	public String getAddress() {return address;}
	public void setAddress(String address) {this.address = address;}
	public String getCellNo() {return cellno;}
	public void setCellNo(String cellno) {this.cellno = cellno;}
	public String getCC() {return cc;}
	public void setCC(String cc) {this.cc = cc;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getLanguage() {return language;}
	public void setLanguage(String language) {this.language = language;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getUsername() {return username;}
	public void setUsername(String username) {this.username = username;}

	public int getPrivileges() {return privileges;}
	public void setPrivileges(Integer privileges) {this.privileges = privileges;}
	public String getIdUser() {return idUser;}
	public void setIdUser(String idUser) {this.idUser = idUser;}
	public Saldo getSaldo() {return saldo;}
	public void setSaldo(Saldo saldo) {this.saldo = saldo;}
	public Historico getHistorico() {return hist;}
	public void setHistorico(Historico hist) {this.hist = hist;}

	@Override
	public String toString() {
		return "\n" +
		"Age:      " + age +      "\n" +
		"NIF:      " + nif +      "\n" +
		"Address:  " + address +  "\n" +
		"Cell No.: " + cellno +   "\n" +
		"CC:       " + cc +       "\n" +
		"Name:     " + name +     "\n" +
		"Language: " + language + "\n" +
		"Email:    " + email +    "\n" +
		"Username: " + username
		;
	}
}
