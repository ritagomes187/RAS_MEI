
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Entities.Moeda;
import Entities.Notificacao;
import Entities.Aposta;
import Entities.Evento;
import Entities.Historico;
import Entities.Saldo;
import Entities.Transacao;
import Entities.Utilizador;

public class RASBetDL {

	private Connection c;
	private Thread syncThread;
	private boolean sync;


	public RASBetDL(String database_path) { try {
			
		c = DriverManager.getConnection("jdbc:sqlite:"+database_path);
		c.setAutoCommit(true);

		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void init() { try {

		Statement s = c.createStatement();

		// Create Blocked NIFs
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS BlockedNIFs (" +

				// FIELDS:
				"nif  			INT  			NOT NULL  	," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (nif)" +

			") WITHOUT ROWID;"
		);

		// Create Coin
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Coin (" +

				// FIELDS:
				"name  		VARCHAR(128)  	NOT NULL  				," +
				"token  	VARCHAR(5)  	NOT NULL  				," +
				"tax  		FLOAT  			NOT NULL  	DEFAULT 0  	," +

				// CONSTRAINTS:
				"CHECK (tax >= 0)," +

				// KEYS:
				"PRIMARY KEY (name)" +

			") WITHOUT ROWID;"
		);


		// Create User
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS User (" +

				// FIELDS:
				"id  				VARCHAR(16)  	NOT NULL  				," +

				//"birthdate  		DATETIME  		NULL  					," +
				"age  				INT  			NOT NULL  				," +
				"nif  	        	INT  			NOT NULL  				," +
				"address  			VARCHAR(256)  	NOT NULL  				," +
				"cellno  			VARCHAR(16)  	NOT NULL  				," +
				"cc  				VARCHAR(16)  	NOT NULL  				," +
				"name  				VARCHAR(256)  	NOT NULL  				," +
				"language  			VARCHAR(32)  	NOT NULL  				," +
				"email  			VARCHAR(128)  	NOT NULL  				," +
				"username  			VARCHAR(32)  	NOT NULL  				," +
				"password  			BLOB  			NOT NULL  				," +

				"logged_in  		TINYINT  		NOT NULL  	DEFAULT 0  	," +
				"privileges  		INT  			NOT NULL  	DEFAULT 0  	," +

				"wallet_coin  		VARCHAR(16)  	NOT NULL  				," +
				"wallet_bal  		FLOAT  			NOT NULL  	DEFAULT 0  	," +
				
				"total_wins  		INT  			NOT NULL  	DEFAULT 0  	," +
				"total_profit  		FLOAT  			NOT NULL  	DEFAULT 0  	," +
				"total_invested  	FLOAT  			NOT NULL  	DEFAULT 0  	," +

				// CONSTRAINTS:
				"UNIQUE (email)," +
				"CHECK (logged_in IN (0,1))," +
				"CHECK (wallet_bal >= 0)," +
				"CHECK (total_wins >= 0)," +
				"CHECK (total_profit >= 0)," +
				"CHECK (total_invested >= 0)," +

				// KEYS:
				"PRIMARY KEY (id)," +
				"FOREIGN KEY (wallet_coin) REFERENCES Coin(name)" +

			") WITHOUT ROWID;"
		);


		// Create Event
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Event (" +

				// FIELDS:
				"id  			VARCHAR(32)  	NOT NULL  						," +
				"sport  		VARCHAR(16)  	NOT NULL  						," +
				"state  		VARCHAR(16)  	NOT NULL  	DEFAULT 'Open'  	," +
				"result  		VARCHAR(32)  	NULL  							," + // 1-8, 0-0, 3-2, ...
				"outcome_team  	VARCHAR(32)  	NULL  							," + // Individuo / Equipa vencedor(a)
				"date  			VARCHAR(64)  	NOT NULL  						," +
				//TODO:
				//adicionar id da promoção

				// CONSTRAINTS:
				"CHECK (state in ('Open','Closed','Suspended','Canceled'))," +

				// KEYS:
				"PRIMARY KEY (id)" +
				"FOREIGN KEY (outcome_team) REFERENCES Outcome(team)" +

			") WITHOUT ROWID;"
		);


		// Create Outcome
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Outcome (" +

				// FIELDS:
				"team  		VARCHAR(32)  	NOT NULL," +
				"tag  		VARCHAR(32)  	NOT NULL," +
				"odd  		FLOAT  			NOT NULL," +
				"id_event  	VARCHAR(32)  	NOT NULL," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (team,id_event)," +
				"FOREIGN KEY (id_event) REFERENCES Event(id)" +

			") WITHOUT ROWID;"
		);


		// Create Bet
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Bet (" +

				// FIELDS:
				"id  			VARCHAR(16)  	NULL  							," +
				"ammount  		FLOAT  			NOT NULL  						," +
				"odd  			FLOAT  			NOT NULL  						," +
				"result  		VARCHAR(16)  	NULL  							," +
				"state  		VARCHAR(16)  	NOT NULL  	DEFAULT 'Open'  	," +
				"date  			DATETIME  		NOT NULL  						," +
				"id_coin  		VARCHAR(16)  	NOT NULL  						," +
				"id_event  		VARCHAR(16)  	NOT NULL  						," +
				"id_sale  		VARCHAR(16)  	NULL  							," +
				"id_user  		VARCHAR(16)  	NOT NULL  						," +
				"chosen_team  	VARCHAR(40)  	NOT NULL  						," +
				"id_multiple  	VARCHAR(16)  	NULL  							," +

				// CONSTRAINTS:
				"CHECK (odd >= 0)," +
				"CHECK (ammount > 0)," +
				"CHECK (state in ('Open','Closed','Suspended','Canceled'))," +

				// KEYS:
				"PRIMARY KEY (id)," +
				"FOREIGN KEY (id_coin) REFERENCES Coin(name)," +
				"FOREIGN KEY (id_event) REFERENCES Event(id)," +
				"FOREIGN KEY (id_sale) REFERENCES Sale(id)," +
				"FOREIGN KEY (id_multiple) REFERENCES MultipleBet(id)"+

			") WITHOUT ROWID;"
		);


		// Create Sale
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Sale (" +

				// FIELDS:
				"id  			VARCHAR(16)  	NOT NULL  	," +
				"description  	VARCHAR(1024)  	NULL  		," +
				"bonus  		FLOAT  			NOT NULL  	," +
				"id_event  		VARCHAR(16)  	NOT NULL  	," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (id)," +
				"FOREIGN KEY (id_event) REFERENCES Event(id)" +

			") WITHOUT ROWID;"
		);


		// Create MutipleBet
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS MultipleBet (" +

				// FIELDS:
				"id  			VARCHAR(16)  	NOT NULL  	," +
				"ammount  		FLOAT  			NOT NULL  	," +
				"odd  			FLOAT  			NOT NULL  	," +
				"id_user  		VARCHAR(16)  	NOT NULL  	," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (id)," +
				"FOREIGN KEY (id_user) REFERENCES User(id)" +

			") WITHOUT ROWID;"
		);


		// Create Notification
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Notification (" +

				// FIELDS:
				"id  			VARCHAR(16)  	NOT NULL  	," +
				"message	  	VARCHAR(1024)  	NOT NULL  	," +
				"id_user  		VARCHAR(16)  	NOT NULL  	," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (id)," +
				"FOREIGN KEY (id_user) REFERENCES User(id)" +

			") WITHOUT ROWID;"
		);


		// Create UserTransaction
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS UserTransaction (" +

				// FIELDS:
				"id  			VARCHAR(16)  	NOT NULL  	," +
				"ammount  		FLOAT  			NOT NULL  	," +
				"description  	VARCHAR(1024)  	NULL  		," +
				"id_user  		VARCHAR(16)  	NOT NULL  	," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (id)," +
				"FOREIGN KEY (id_user) REFERENCES User(id)" +

			") WITHOUT ROWID;"
		);


		// Create Following
		s.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Following (" +

				// FIELDS:
				"id_user  		VARCHAR(16)  	NOT NULL  	," +
				"id_event  		VARCHAR(32)  	NOT NULL  	," +

				// CONSTRAINTS:

				// KEYS:
				"PRIMARY KEY (id_user,id_event)," +
				"FOREIGN KEY (id_user) REFERENCES User(id)," +
				"FOREIGN KEY (id_event) REFERENCES Event(id)" +

			") WITHOUT ROWID;"
		);


		s.close();
		
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void populate() { try {

		Statement s = c.createStatement();

		s.executeUpdate("INSERT OR IGNORE INTO Coin(name,token) VALUES ('Euro','€');");

		s.executeUpdate("INSERT OR IGNORE INTO Coin(name,token,tax) VALUES ('Dollar','$',0.05);");

		s.executeUpdate("INSERT OR IGNORE INTO User(id,age,nif,address,cellno,cc,name,language,email,username,password,privileges,wallet_coin,wallet_bal) " +
						"VALUES ('_specialist_1',99,456123789,'_specialist_1_null','_specialist_1_null','_specialist_1_null','RASBet Specialist #1','English','specialist@rasbet.com','specialist#1','12345',1,'Euro',999999);");

		s.executeUpdate("INSERT OR IGNORE INTO User(id,age,nif,address,cellno,cc,name,language,email,username,password,privileges,wallet_coin,wallet_bal) " +
						"VALUES ('_admin_1',99,789456123,'_admin_1_null','_admin_1_null','_admin_1_null','RASBet Admin #1','English','admin@rasbet.com','admin#1','12345',2,'Euro',999999);");

		s.close();
		
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void addCoin(Moeda coin) { try {

		Statement s = c.createStatement();

		s.executeUpdate(
			"INSERT INTO Coin (name,token,tax) VALUES (" +
				"'" + coin.getNome() + "'," +
				"'" + coin.getToken() + "'," +
				"'" + coin.getImposto() + "'" +
			");"
		);

		s.close();
		
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public boolean addUser(Utilizador user, String password, String currency) { try {

		Statement s = c.createStatement();

		s.executeUpdate(
			"INSERT INTO User (id,age,nif,address,cellno,cc,name,language,email,username,password,privileges,wallet_coin) VALUES (" +
				"'" + (user.getIdUser()!=null?user.getIdUser():idGenerator(32)) + "'," +
				"'" + user.getAge() + "'," +
				"'" + user.getNIF() + "'," +
				"'" + user.getAddress() + "'," +
				"'" + user.getCellNo() + "'," +
				"'" + user.getCC() + "'," +
				"'" + user.getName() + "'," +
				"'" + user.getLanguage() + "'," +
				"'" + user.getEmail() + "'," +
				"'" + user.getUsername() + "'," +
				"'" + password + "'," +
				"'" + user.getPrivileges() + "'," +
				"'" + currency + "'" +
			");"
		);

		s.close();
		return true;
		
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public void startSyncing() {
		this.sync = true;
		this.syncThread = new Thread(() -> {
			
			// This is a mess, mas basicamente tenta conectar à API, e espera 50s, independentemente se conseguiu ou não
			while(sync) {
				try {
					this.syncAPIs();
					Thread.sleep(50000); //50s
				} catch (IOException e) {
					System.out.println("\n[ERROR] Failed to sync threads!\n");
					try {
						Thread.sleep(50000); //50s
					} catch (InterruptedException whatever) {}
				} catch (InterruptedException whatever) {}
			}
		});
		
		this.syncThread.start();
	}


	public void syncAPIs() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder(
			URI.create("http://ucras.di.uminho.pt/v1/games"))
			//.header("accept", "application/json")
			.header("Content-Type", "text/plain")
			//.timeout(Duration.ofSeconds(3))
			.build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());


		String jsonString = "{'games':" + response.body().toString() + "}";
		JSONObject obj = new JSONObject(jsonString);
		JSONArray games = obj.getJSONArray("games");

		try {
			Statement s = c.createStatement();
			for(int i = 0; i < games.length(); i++) {
				JSONObject game = games.getJSONObject(i);

				String id = game.getString("id");
				String home_team = game.getString("homeTeam");
				String away_team = game.getString("awayTeam");
				String time = game.getString("commenceTime");
				Boolean completed = game.getBoolean("completed");
				String scores = null; if(completed) scores = game.getString("scores");
				Float odd_home = (float) 0;
				int count_home = 0;
				Float odd_away = (float) 0;
				int count_away = 0;
				Float odd_tie = (float) 0;
				int count_tie = 0;
				JSONArray bookmakers = game.getJSONArray("bookmakers");
				for (int j=0; j<bookmakers.length(); j++) {
					JSONObject bookmaker = bookmakers.getJSONObject(j);
					JSONArray markets = bookmaker.getJSONArray("markets");
					for(int k=0; k<markets.length(); k++) {
						JSONObject market = markets.getJSONObject(k);
						JSONArray outcomes = market.getJSONArray("outcomes");
						for(int w=0; w<outcomes.length(); w++) {
							JSONObject outcome = outcomes.getJSONObject(w);
							if (outcome.getString("name").equals(home_team)) {
								odd_home += outcome.getFloat("price");
								count_home++;
							}
							if (outcome.getString("name").equals(away_team)) {
								odd_away += outcome.getFloat("price");
								count_away++;
							}
							else {
								odd_tie += outcome.getFloat("price");
								count_tie++;
							}						
						}
					}
				}
				Float final_odd_home = odd_home/count_home;
				Float final_odd_away = odd_away/count_away;
				Float final_odd_tie = odd_tie/count_tie;

				ResultSet rs = s.executeQuery("SELECT * FROM Outcome WHERE team=='"+home_team+"' AND id_event=='"+id+"';");
				if(rs.next()) if(rs.getFloat("odd") != final_odd_home) {
					rs = s.executeQuery("SELECT id_user FROM Following WHERE id_event=='"+id+"';");
					while(rs.next())
						createNotification(
							rs.getString("id_user"),
							"Odd for team "+home_team+" on event "+id+" has changed to "+final_odd_home);
				}

				ResultSet rs1 = s.executeQuery("SELECT * FROM Outcome WHERE team=='"+away_team+"' AND id_event=='"+id+"';");
				if(rs1.next()) if(rs1.getFloat("odd") != final_odd_away) {
					rs1 = s.executeQuery("SELECT id_user FROM Following WHERE id_event=='"+id+"';");
					while(rs1.next())
						createNotification(
							rs1.getString("id_user"),
							"Odd for team "+away_team+" on event "+id+" has changed to "+final_odd_away);
				}

				ResultSet rs2 = s.executeQuery("SELECT * FROM Outcome WHERE team=='Draw' AND id_event=='"+id+"';");
				if(rs2.next()) if(rs2.getFloat("odd") != final_odd_tie) {
					rs2 = s.executeQuery("SELECT id_user FROM Following WHERE id_event=='"+id+"';");
					while(rs2.next())
						createNotification(
							rs2.getString("id_user"),
							"Odd for Draw on event "+id+" has changed to "+final_odd_tie);
				}

				s.executeUpdate(
					"INSERT INTO Outcome(team,odd,id_event,tag) " +
					"VALUES ('"+home_team+"',"+final_odd_home+",'"+id+"','home') " +
					"ON CONFLICT (team,id_event) DO UPDATE SET odd=excluded.odd;"
				);
				s.executeUpdate(
					"INSERT INTO Outcome(team,odd,id_event,tag) " +
					"VALUES ('"+away_team+"',"+final_odd_away+",'"+id+"','away') " +
					"ON CONFLICT (team,id_event) DO UPDATE SET odd=excluded.odd;"
				);
				s.executeUpdate(
					"INSERT INTO Outcome(team,odd,id_event,tag) " +
					"VALUES ('Draw',"+final_odd_tie+",'"+id+"','draw') " +
					"ON CONFLICT (team,id_event) DO UPDATE SET odd=excluded.odd;"
				);


				if(!completed) {
					s.executeUpdate(
						"INSERT INTO Event(id,date,sport) " +
						"VALUES ('"+id+"','"+time+"','Football') " +
						"ON CONFLICT (id) DO UPDATE SET date=excluded.date;"
					);
				} else {
					String old_state = "Open";
					String outcome = "null";

					ResultSet rs3 = s.executeQuery("SELECT * FROM Event WHERE id=='"+id+"';");
					if(rs3.next()) {old_state = rs.getString("state");}

					Integer home_score = Character.getNumericValue(scores.charAt(0));
					Integer away_score = Character.getNumericValue(scores.charAt(2));
					if(home_score == away_score) outcome = "Draw";
					if(home_score >  away_score) outcome = home_team;
					if(home_score <  away_score) outcome = away_team;

					s.executeUpdate(
						"INSERT INTO Event(id,date,sport,state,result,outcome_team) " +
						"VALUES ('"+id+"','"+time+"','Football','Closed','"+scores+"','"+outcome+"') " +
						"ON CONFLICT (id) DO UPDATE SET date=excluded.date, state=excluded.state, result=excluded.result, outcome_team=excluded.outcome_team;"
					);

					if(!old_state.equals("Closed")) this.closeEvent(id, scores, outcome);
				}
			}
			s.close();

		} catch (SQLException e) {System.out.println(e.getMessage());}
	}


	public void close() throws SQLException {
		this.sync = false;
		syncThread.interrupt();
		c.close();
	}


	public boolean register(Utilizador new_user, String password, String currency) {

		// Check constraints
		if(existsUser(new_user.getEmail()) || isNIFBlocked(new_user.getNIF())) return false;

		// Find suitable id for User
		String id;
		int max_tries = 10;
		do { id = idGenerator(16); } while(existsUser(id) && (--max_tries>0));
		if(max_tries <= 0) return false;
		new_user.setIdUser(id);
		new_user.setPrivileges(0);

		// Create & Add user
		return addUser(new_user, password, currency);
	}


	public Utilizador login(String email, String password) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM User WHERE email=='"+email+"';" );

		// Check credentials
		if(!rs.next()) return null; //no user with that email
		if(!password.equals(rs.getString("password"))) return null; //wrong password

		// Retrieve user info
		Utilizador user = new Utilizador(
			rs.getInt("age"),
			rs.getInt("nif"),
			rs.getString("address"),
			rs.getString("cellno"),
			rs.getString("cc"),
			rs.getString("name"),
			rs.getString("language"),
			rs.getString("email"),
			rs.getString("username")
		);
		user.setIdUser(rs.getString("id"));
		user.setPrivileges(rs.getInt("privileges"));
		user.setSaldo(getUserBalance(email));
		user.setHistorico(getUserHistory(email));
		rs.close();
		
		// Update logged_in
		s.executeUpdate("UPDATE User SET logged_in=1 WHERE email=='"+email+"';");

		s.close();
		return user;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


	public void logout(String email) { try {
		Statement s = c.createStatement();

		// Update logged_in
		s.executeUpdate("UPDATE User SET logged_in=0 WHERE email=='"+email+"';");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public boolean existsUser(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM User WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsUsername(String username) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery("SELECT * FROM User WHERE username=='"+username+"';");
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsEmail(String email) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery("SELECT * FROM User WHERE email=='"+email+"';");
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsBet(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM Bet WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsSale(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM Sale WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsCoin(String name) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM Coin WHERE name=='"+name+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsNotification(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM Notification WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsTransaction(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM UserTransaction WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean existsEvent(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM Event WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public String getEventState(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT state FROM Event WHERE id=='"+id+"';" );
		rs.next();
		String result = rs.getString("state");
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


	public boolean existsMultipleBet(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM MultipleBet WHERE id=='"+id+"';" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}


	public boolean isNIFBlocked(Integer nif) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM BlockedNIFs WHERE nif=="+nif.toString()+";" );
		boolean result = rs.next();
		rs.close();

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}

	}


	public Saldo getUserBalance(String id) { try {
		Statement s = c.createStatement();

		ResultSet rs1 = s.executeQuery("SELECT wallet_coin,wallet_bal FROM User WHERE id=='"+id+"';");
		String coin_name = rs1.getString("wallet_coin");
		Float ammount = rs1.getFloat("wallet_bal");
		rs1.close();

		ResultSet rs2 = s.executeQuery( "SELECT * FROM Coin WHERE name=='"+coin_name+"';" );
		String token = rs2.getString("token");
		Float tax = rs2.getFloat("tax");
		rs2.close();
		
		Moeda coin = new Moeda(coin_name, token, tax);
		Saldo result = new Saldo(coin, ammount);

		s.close();
		return result;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


	public void withdrawMoney(String id, Float ammount) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET wallet_bal=wallet_bal-"+ammount+" WHERE id=='"+id+"';");
		createTransaction(id, -ammount, "User withdraw.");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void depositMoney(String id, Float ammount) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET wallet_bal=wallet_bal+"+ammount+" WHERE id=='"+id+"';");
		createTransaction(id, ammount, "User deposit.");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	/*private String getUserID(String email) { try {
		Statement s = c.createStatement();

		ResultSet rs = s.executeQuery( "SELECT * FROM User WHERE email=='"+email+"';" );
		String id = rs.getString("id");
		rs.close();

		s.close();
		return id;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}*/


	public Historico getUserHistory(String id) { try {
			Statement s = c.createStatement();

			ResultSet rs1 = s.executeQuery("SELECT * FROM User WHERE id=='"+id+"';");
			Integer total_wins = rs1.getInt("total_wins");
			Float total_profit = rs1.getFloat("total_profit");
			Float total_invested = rs1.getFloat("total_invested");
			rs1.close();


			HashMap<String,Aposta> open_bets = new HashMap<>();
			HashMap<String,Aposta> closed_bets = new HashMap<>();
			ResultSet rs2 = s.executeQuery("SELECT * FROM Bet WHERE id_user=='"+id+"';");
			while (rs2.next()) {
				Aposta bet = new Aposta(
					Float.parseFloat(rs2.getString("ammount")),
					Float.parseFloat(rs2.getString("odd")),
					rs2.getString("state"),
					rs2.getString("result"),
					rs2.getString("id"),
					rs2.getString("chosen_team"),
					null, null, null, null);

				if(bet.getEstado().equals("Closed"))
					closed_bets.put(bet.getIdAposta(), bet);
				else if(bet.getEstado().equals("Open") || bet.getEstado().equals("Suspended"))
					open_bets.put(bet.getIdAposta(), bet);
			}
			rs2.close();


			List<Transacao> transactions = new ArrayList<>();
			ResultSet rs3 = s.executeQuery("SELECT * FROM UserTransaction WHERE id_user=='"+id+"';");
			while (rs3.next()) {
				Transacao t;
				if(rs3.getString("description") == null)
					t = new Transacao(rs3.getString("id"), rs3.getFloat("ammount"));
				else
					t = new Transacao(rs3.getString("id"), rs3.getFloat("ammount"), rs3.getString("description"));
				transactions.add(t);
			}
			rs3.close();

			Historico result = new Historico(open_bets, closed_bets, transactions, total_invested, total_profit, total_wins);

			s.close();
			return result;

		} catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


	public List<Notificacao> getUserNotifications(String id) { try {
		Statement s = c.createStatement();

		List<Notificacao> result = new ArrayList<>();
		ResultSet rs = s.executeQuery("SELECT * FROM Notification WHERE id_user=='"+id+"';");
		while (rs.next()) {
			Notificacao n = new Notificacao(rs.getString("id"), rs.getString("message"));
			result.add(n);
		}
		rs.close();

		s.close();
		return result;

		} catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}


	public void makeBet(Float chosen_ammount, Float chosen_odd, String id_user, String id_game, String chosen_team) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET wallet_bal=wallet_bal-"+chosen_ammount+" WHERE id=='"+id_user+"';");
		createTransaction(id_user, -chosen_ammount, "Bet on game "+id_game);

		s.executeUpdate("UPDATE User SET total_invested=total_invested+"+chosen_ammount+" WHERE id=='"+id_user+"';");

		String id;
		do { id = idGenerator(16); } while(existsBet(id));

		s.executeUpdate("INSERT INTO Bet(ammount,odd,date,id_coin,id_event,id_user,id,chosen_team) VALUES ("+chosen_ammount+","+chosen_odd+",'now','Euro','"+id_game+"','"+id_user+"','"+id+"','"+chosen_team+"');");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void makeMultipleBet(Float chosen_ammount, Float chosen_odd, String id_user, String id_game, String chosen_team, Float total_odd) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET wallet_bal=wallet_bal-"+chosen_ammount+" WHERE id=='"+id_user+"';");
		createTransaction(id_user, -chosen_ammount, "Multiple Bet on game "+id_game);

		s.executeUpdate("UPDATE User SET total_invested=total_invested+"+chosen_ammount+" WHERE id=='"+id_user+"';");

		String id;
		do { id = idGenerator(16); } while(existsBet(id));

		String id_mult;
		do { id_mult = idGenerator(16); } while(existsMultipleBet(id_mult));

		s.executeUpdate("INSERT INTO MultipleBet(id,ammount,odd,id_user) VALUES('"+id_mult+"',"+chosen_ammount+","+total_odd+",'"+id_user+"');");

		s.executeUpdate("INSERT INTO Bet(ammount,odd,date,id_coin,id_event,id_user,id,chosen_team,id_multiple) VALUES ("+chosen_ammount+","+chosen_odd+",'now','Euro','"+id_game+"','"+id_user+"','"+id+"','"+chosen_team+"','"+id_mult+"');");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public List<String> getAvailableSports() {
		List<String> sports = new ArrayList<>();
		
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT DISTINCT sport FROM Event;");
			while(rs.next())
				sports.add(rs.getString("sport"));
			rs.close();
			s.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return sports;
	}


	public List<Evento> getOpenGames(String sport) {
		List<Evento> games = new ArrayList<>();

		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM Event WHERE sport=='"+sport+"' AND state=='Open';");
			while(rs.next()) {
				String id = rs.getString("id");
				String state = rs.getString("state");
				String result = rs.getString("result");
				String date = rs.getString("date");
				
				String home_team, away_team;
				Float odd_home, odd_away, odd_draw;

				Statement s2 = c.createStatement();
				ResultSet rs2 = s2.executeQuery("SELECT * FROM Outcome WHERE id_event=='"+id+"' AND tag=='draw';");
				rs2.next();
				odd_draw = rs2.getFloat("odd");
				rs2.close();

				ResultSet rs3 = s2.executeQuery("SELECT * FROM Outcome WHERE id_event=='"+id+"' AND tag=='home';");
				rs3.next();
				home_team = rs3.getString("team");
				odd_home = rs3.getFloat("odd");
				rs3.close();

				ResultSet rs4 = s2.executeQuery("SELECT * FROM Outcome WHERE id_event=='"+id+"' AND tag=='away';");
				rs4.next();
				away_team = rs4.getString("team");
				odd_away = rs4.getFloat("odd");
				rs4.close();
				s2.close();
				
				games.add(new Evento(id,sport,home_team,away_team,state,result,date,odd_home,odd_away,odd_draw));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return games;
	}


	public List<Evento> getGames(String sport) {
		List<Evento> games = new ArrayList<>();

		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM Event WHERE sport=='"+sport+"';");
			while(rs.next()) {
				String id = rs.getString("id");
				String state = rs.getString("state");
				String result = rs.getString("result");
				String date = rs.getString("date");
				
				String home_team, away_team;
				Float odd_home, odd_away, odd_draw;

				Statement s2 = c.createStatement();
				ResultSet rs2 = s2.executeQuery("SELECT * FROM Outcome WHERE id_event=='"+id+"' AND tag=='draw';");
				rs2.next();
				odd_draw = rs2.getFloat("odd");
				rs2.close();

				ResultSet rs3 = s2.executeQuery("SELECT * FROM Outcome WHERE id_event=='"+id+"' AND tag=='home';");
				rs3.next();
				home_team = rs3.getString("team");
				odd_home = rs3.getFloat("odd");
				rs3.close();

				ResultSet rs4 = s2.executeQuery("SELECT * FROM Outcome WHERE id_event=='"+id+"' AND tag=='away';");
				rs4.next();
				away_team = rs4.getString("team");
				odd_away = rs4.getFloat("odd");
				rs4.close();
				s2.close();
				
				games.add(new Evento(id,sport,home_team,away_team,state,result,date,odd_home,odd_away,odd_draw));
			}
			rs.close();
			s.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return games;
	}


	public void setName(String id, String name) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET name='"+name+"' WHERE id=='"+id+"';");

		s.close();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void setEmail(String id, String email) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET email='"+email+"' WHERE id=='"+id+"';");

		s.close();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void setLanguage(String id, String lang) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET language='"+lang+"' WHERE id=='"+id+"';");

		s.close();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void setAddress(String id, String addr) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET address='"+addr+"' WHERE id=='"+id+"';");

		s.close();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void setCellno(String id, String cellno) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE User SET cellno='"+cellno+"' WHERE id=='"+id+"';");

		s.close();
		
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public void changeEventOdd(String id, String outcome, Float odd) { try {
		Statement s = c.createStatement();
			
		s.executeUpdate("UPDATE Outcome SET odd="+odd+" WHERE id_event=='"+id+"' AND team=='"+outcome+"';");

		ResultSet rs = s.executeQuery("SELECT id_user FROM Following WHERE id_event=='"+id+"';");
		while(rs.next())
			createNotification(
				rs.getString("id_user"),
				"Odd for "+outcome+" on event "+id+" has changed to "+odd);

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void changeBetState(String id_bet, String state) { try {
		Statement s = c.createStatement();
		
		s.executeUpdate("UPDATE Bet SET state='"+state+"' WHERE id=='"+id_bet+"';");

		if(state.equals("Closed")) {
			ResultSet rs = s.executeQuery("SELECT id_user,id_event,id_sale,chosen_team,ammount,odd FROM Bet WHERE id=='"+id_bet+"';");
			String id_user = rs.getString("id_user");
			String id_event = rs.getString("id_event");
			String id_sale = rs.getString("id_sale");
			String chosen_team = rs.getString("chosen_team");
			Float ammount = rs.getFloat("ammount");
			Float odd = rs.getFloat("odd");

			ResultSet rs2 = s.executeQuery("SELECT outcome_team FROM Event WHERE id=='"+id_event+"';");
			String event_result = rs2.getString("outcome_team");

			if(event_result.equals(chosen_team))
			{
				ResultSet rs3 = s.executeQuery("SELECT bonus FROM Sale WHERE id=='"+id_sale+"';");
				Float bonus = rs3.getFloat("bonus");

				s.executeUpdate("UPDATE User SET total_wins=total_wins+1 WHERE id=='"+id_user+"';");
				s.executeUpdate("UPDATE User SET total_profit=total_profit+"+(ammount*(odd-1)+bonus)+" WHERE id=='"+id_user+"';");
				s.executeUpdate("UPDATE User SET wallet_bal=wallet_bal+"+(ammount*odd+bonus)+" WHERE id=='"+id_user+"';");
				createTransaction(id_user, ammount*odd+bonus, "Profit from bet "+id_bet);
			}
		}

		if(state.equals("Cancelled")) {
			ResultSet rs4 = s.executeQuery("SELECT id_user,ammount FROM Bet WHERE id=='"+id_bet+"';");
			String id_user = rs4.getString("id_user");
			Float ammount = rs4.getFloat("ammout");

			s.executeUpdate("UPDATE User SET total_invested=total_invested-"+ammount+" WHERE id=='"+id_user+"';");
			s.executeUpdate("UPDATE User SET wallet_bal=wallet_bal+"+(ammount)+" WHERE id=='"+id_user+"';");
			createTransaction(id_user, ammount, "Refund from bet "+id_bet);
		}

		ResultSet rs5 = s.executeQuery("SELECT id_user FROM Bet WHERE id=='"+id_bet+"';");
		String id_user = rs5.getString("id_user");
		createNotification(id_user, "State of bet "+id_bet+" set to "+state);

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void createPromo(String id_evento, Float bonus) { try {
		Statement s = c.createStatement();
		
		String id;

		// Find suitable id for sale
		do { id = idGenerator(16); } while(existsSale(id));

		s.executeUpdate("INSERT INTO Sale (id, bonus) VALUES ('"+id+ "', "+bonus+");");

		// TODO: adicionar também no Evento

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void createNotification(String id_user, String message) { try {
		Statement s = c.createStatement();
		
		String id;

		// Find suitable id for notification
		do { id = idGenerator(16); } while(existsNotification(id));

		s.executeUpdate("INSERT INTO Notification (id, message, id_user) VALUES ('"+id+ "', '"+message+"', '"+id_user+"');");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void createTransaction(String id_user, Float ammount, String description) { try {
		Statement s = c.createStatement();
		
		String id;

		// Find suitable id for UserTransaction
		do { id = idGenerator(16); } while(existsTransaction(id));

		s.executeUpdate("INSERT INTO UserTransaction (id, ammount, description, id_user) VALUES ('"+id+ "', "+ammount+", '"+description+"', '"+id_user+"');");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void createTransaction(String id_user, Float ammount) { try {
		Statement s = c.createStatement();
		
		String id;

		// Find suitable id for UserTransaction
		do { id = idGenerator(16); } while(existsTransaction(id));

		s.executeUpdate("INSERT INTO UserTransaction (id, ammount, id_user) VALUES ('"+id+ "', "+ammount+", '"+id_user+"');");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void createEvent(String sport, String home_team, String away_team, String date) { try {
		Statement s = c.createStatement();

		String id;
		// Find suitable id for event
		do { id = idGenerator(16); } while(existsEvent(id));

		s.executeUpdate(
			"INSERT INTO Event(id,sport,state,date) " +
			"VALUES ('"+id+"','"+sport+"','Suspended','"+date+"');"
		);

		s.executeUpdate(
			"INSERT INTO Outcome(team,odd,id_event,tag) " +
			"VALUES ('"+home_team+"',1,'"+id+"','home') " +
			"ON CONFLICT (team,id_event) DO UPDATE SET odd=excluded.odd;"
		);
		s.executeUpdate(
			"INSERT INTO Outcome(team,odd,id_event,tag) " +
			"VALUES ('"+away_team+"',1,'"+id+"','away') " +
			"ON CONFLICT (team,id_event) DO UPDATE SET odd=excluded.odd;"
		);
		s.executeUpdate(
			"INSERT INTO Outcome(team,odd,id_event,tag) " +
			"VALUES ('Draw',1,'"+id+"','draw') " +
			"ON CONFLICT (team,id_event) DO UPDATE SET odd=excluded.odd;"
		);

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void closeEvent(String id, String result, String outcome) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE Event SET state='Closed' WHERE id=='"+id+"';");
		s.executeUpdate("UPDATE Event SET result='"+result+"' WHERE id=='"+id+"';");
		s.executeUpdate("UPDATE Event SET outcome_team='"+outcome+"' WHERE id=='"+id+"';");

		// TODO: Multiple Bet
		ResultSet rs = s.executeQuery("SELECT id FROM Bet WHERE id_event=='"+id+"';");
		while(rs.next()) {
			changeBetState(rs.getString("id"), "Closed");
		}

		s.close();  

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void changeEventState(String id, String state) { try {
		Statement s = c.createStatement();

		s.executeUpdate("UPDATE Event SET state='"+state+"' WHERE id=='"+id+"';");

		ResultSet rs = s.executeQuery("SELECT id FROM Bet WHERE id_event=='"+id+"';");
		while(rs.next()) {
			changeBetState(rs.getString("id"), state);
		}

		s.close();  

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void followEvent(String id_user, String id_event) { try {
		Statement s = c.createStatement();

		s.executeUpdate("INSERT INTO Following (id_user,id_event) VALUES ('"+id_user+"', '"+id_event+"');");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	public void unfollowEvent(String id_user, String id_event) { try {
		Statement s = c.createStatement();

		s.executeUpdate("DELETE FROM Following WHERE id_user=='"+id_user+"' AND id_event=='"+id_event+"';");

		s.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
	}


	private String idGenerator(int n) {
		String alphaNumeric = "0123456789" + "abcdefghijklmnopqrstuvxyz";

		StringBuilder sb = new StringBuilder(n);
		
		for (int i = 0; i < n; i++) {
			int index = (int)(alphaNumeric.length() * Math.random());
			sb.append(alphaNumeric.charAt(index));
		}

		return sb.toString();
	}
}