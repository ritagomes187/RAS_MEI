
import java.util.*;
import java.util.concurrent.TimeUnit;

import Entities.*;

public class RASBetBL implements Runnable {

	private RASBetDL dl;
	private final static Scanner scin = new Scanner(System.in);
	private int privileges;
	Utilizador user;
	Apostador apostador;


	public RASBetBL(RASBetDL dl)
	{
		this.privileges = -1;
		this.dl = dl;
	}


	public void run()
	{
		System.out.println("Welcome to RASBet!");
		this.menuPrincipal();
		this.logout();
	}


	private void menuPrincipal()
	{
		Menu menu = new Menu( "RASBet", new String[] {
				"Register",
				"Login",
				"Logout",
		});

		/* Register */
		menu.setPreCondition(1, ()->this.privileges < 0);
		menu.setHandler     (1, this::register);

		/* Login */
		menu.setPreCondition(2, ()->this.privileges < 0);
		menu.setHandler     (2, this::login);

		/* Logout */
		menu.setPreCondition(3, ()->this.privileges >= 0);
		menu.setHandler     (3, this::logout);

		menu.run();
	}


	private void register()
	{
		// Prompt for age (fail if less than 18)
		Integer age = Menu.promptInt("Age", (obj)-> obj>=18, "You should be at least 18 to register !");
		if (age==null) return;
		
		// Prompt for NIF (fail if NIF is blocked)
		Integer nif = Menu.promptInt("NIF", (obj)-> !dl.isNIFBlocked(obj), "Register denied! This NIF is blocked.");
		if (nif==null) return;

		String currency = Menu.prompt("Currency"); //TODO: Menu


		// Ask for confirmation
		Utilizador new_user = new Utilizador(age,nif,
			Menu.prompt("Address"),
			Menu.prompt("Cellphone no."),
			Menu.prompt("CC"),
			Menu.prompt("Name"),
			Menu.prompt("Prefered language"), //TODO: Menu
			Menu.prompt("Email"),
			Menu.prompt("Username")
		);
		String password = Menu.promptUntilValid("Password", (obj)-> obj.length() >= 5, "Password should have at least 5 characters!");
		System.out.println(new_user.toString());
		if (Menu.prompt("Confirm (y/n/default=y)", (obj)-> !obj.equals("n"), "Cancelled") == null) return;



		// Insert into database
		if(dl.register(new_user, password, currency))
			System.out.println("User created successfully.");
		else
			System.out.println("\n[ERROR] User creation failed!");
	}


	private void login()
	{
		this.user = dl.login(
			Menu.prompt("Email"),
			Menu.prompt("Password")
		);

		if(user != null) {
			System.out.println("Authenticated.");

			privileges = user.getPrivileges();

			switch(privileges) {
				case 0:
					apostador = new Apostador(user);
					menuApostador();
					break;

				case 1:
					menuEspecialista();
					break;
				
				case 2:
					menuAdministrador();
					break;
				
				default:
					System.out.println("[ERROR] Privileges error! Contact an administrator.");
					return;
			}

		} else System.out.println("\n[ERROR] Wrong credentials!");
	}


	private void logout()
	{
		if(user!=null) dl.logout(user.getEmail());
		this.privileges = -1;
	}




	//=== BEGIN PUTTER ===
	private void menuApostador()
	{
		Menu menu = new Menu( "Home page", new String[] {
			"View games",
			"View balance",
			"View history",
			"Make bet",
			"Withdraw money",
			"Deposit money",
			"View notifications",
			"Edit profile",
			"Follow event",
			"Unfollow event"
		});

		/* View Games */
		menu.setHandler(1, this::menuDesportos);

		/* View balance */
		menu.setHandler(2, this::consultarSaldo);

		/* View history */
		menu.setHandler(3, this::consultarHistorico);

		/* Make bet */
		menu.setPreCondition(4, ()->dl.getUserBalance(apostador.getIdUser()).getBalance() > 0);
		menu.setHandler(4, this::menuDesportosAposta);

		/* Withdraw money */
		menu.setPreCondition(5, ()->dl.getUserBalance(apostador.getIdUser()).getBalance() > 0);
		menu.setHandler(5, this::levantarDinheiro);

		/* Deposit money */
		menu.setHandler(6, this::menuMetodosPagamento);

		/* View notifications */
		menu.setHandler(7, this::consultarNotificacoes);

		/* Edit profile */
		menu.setHandler(8, this::menuEditarPerfil);

		/* Follow event */
		menu.setHandler(9, this::seguirEvento);

		/* Unfollow event */
		menu.setHandler(10, this::deixarSeguirEvento);

		menu.run();
	}


	private void menuDesportos()
	{	
		Menu menu = new Menu( "Sports", dl.getAvailableSports());

		for(int i = 1; i <= dl.getAvailableSports().size(); i++) {
			final int idx = i-1;
			menu.setPreCondition(i, ()->true);
			menu.setHandler(i, ()->this.verJogos(dl.getAvailableSports().get(idx)));
		}

		menu.runOnce();
	}


	private void verJogos(String sport)
	{
		long start = System.nanoTime();

		List<Evento> eventos = dl.getGames(sport);

		for (int i = 0; i < eventos.size(); i++)
		    System.out.println(eventos.get(i).toString());
		
		long end = System.nanoTime();
		System.out.println("[DEBUG] Time elapsed: " + TimeUnit.NANOSECONDS.toMillis(end - start) + "ms");
	}


	private void consultarSaldo()
	{
		Saldo saldo = dl.getUserBalance(apostador.getIdUser());
		String coin_token = saldo.getMoeda().getToken();
					
		System.out.println("----------");

			System.out.println("Balance: "+saldo.getBalance()+coin_token);
			
		System.out.println("----------");
	}


	private void consultarHistorico()
	{
		Historico hist = dl.getUserHistory(apostador.getIdUser());

		System.out.println("----------");

		System.out.println("Total Wins: " + hist.getTotalWins());
		System.out.println("Total Profit: " + hist.getTotalProfit());
		System.out.println("Total Invested: " + hist.getTotalInvested());
		System.out.println();

		System.out.println("Closed Bets:");
		for(Aposta bet : hist.getClosedBets().values())
			System.out.println(bet.getIdAposta()+"\t"+bet.getEstado()+"\t"+bet.getOdd()+"\t"+bet.getQuantia());
		System.out.println();
		
		System.out.println("Open Bets:");
		for(Aposta bet : hist.getOpenBets().values())
			System.out.println(bet.getIdAposta()+"\t"+bet.getEstado()+"\t"+bet.getOdd()+"\t"+bet.getQuantia());
		System.out.println();

		System.out.println("Transactions:");
		for(Transacao t : hist.getTransactions())
			System.out.println(t.getAmmount()+"\t"+t.getDescription());
		System.out.println();
		
		System.out.println("----------");
	}


	private void menuDesportosAposta()
	{	
		List<String> sports = dl.getAvailableSports();

		Menu menu = new Menu( "Sports", sports);

		for(int i = 1; i <= sports.size(); i++) {
			final int idx = i-1;
			menu.setPreCondition(i, ()->true);
			menu.setHandler(i, ()->this.menuTipoAposta(sports.get(idx)));
		}

		menu.runOnce();
	}


	private void menuTipoAposta(String sport)
	{
		String[] bet_types = new String[] {"Simple", "Multiple"};

		Menu menu = new Menu( "Bet type", bet_types);

		for(int i = 1; i <= bet_types.length; i++) {
			final int idx = i-1;
			menu.setPreCondition(i, ()->true);
			menu.setHandler(i, ()->this.fazerAposta(sport, bet_types[idx]));
		}

		menu.runOnce();
	}
	
	
	private void fazerAposta(String sport, String type_of_bet)
	{
		boolean stay = true;
		String chosen_outcome;
		String chosen_team;
		Float chosen_odd;
		Float chosen_ammount = (float) 0;
		List<Aposta> apostas = new ArrayList<>();
		Float odd_total = (float) 1;
		Float total_ammount = (float) 0;

		List<Evento> games = dl.getOpenGames(sport);

		while(stay) {

			// Display games
			System.out.println("Number of games: " + games.size());
			if(games.size() == 0) return;
			for(Evento game : games) System.out.println(game.toString());
			
			// Select game
			Integer idx_game = Menu.promptIntUntilValid("Game Index", (obj)-> obj<games.size() && obj>=0, "Invalid index!");
			Evento game = games.get(idx_game);

			// Choose outcome
			// TODO: Verificar se pode ser empate (req. 30) && desportos individuais
			System.out.println(game.toString());
			chosen_outcome = Menu.prompt("1 / x / 2");
			if(chosen_outcome.equals("1")) {
				chosen_team = game.getHomeTeam();
				chosen_odd = game.getHomeOdd();
			} else if (chosen_outcome.equals("2")) {
				chosen_team = game.getAwayTeam();
				chosen_odd = game.getAwayOdd();
			} else {
				chosen_team = "Draw";
				chosen_odd = game.getDrawOdd();
			}
			System.out.println("Chosen outcome: " + chosen_team);
			System.out.println("Chosen odd: " + chosen_odd);


			if (type_of_bet.equals("Simple")) {

				// Choose bet ammount
				final Float curr_total_ammoun = total_ammount;
				Float saldo_atual = dl.getUserBalance(apostador.getIdUser()).getBalance();
				chosen_ammount = Menu.promptFloatUntilValid("Ammount", (obj)-> obj>0 && curr_total_ammoun+obj<=saldo_atual, "Insert a valid value!");

				total_ammount += chosen_ammount;

				apostas.add(new Aposta(chosen_odd, chosen_ammount, chosen_team, game));

			}
			else {
				odd_total *= chosen_odd;
				apostas.add(new Aposta(chosen_odd, 0, chosen_team, game));
			}

			System.out.println("Make more bets(y/n)?");
			stay = scin.nextLine().equals("y");
		}

		if (type_of_bet.equals("Multiple")) {
			
			// Choose bet ammount
			Float saldo_atual = dl.getUserBalance(apostador.getIdUser()).getBalance();
			chosen_ammount = Menu.promptFloatUntilValid("Ammount", (obj)-> obj>0 && obj<=saldo_atual, "Insert a valid value!");

		}

		String id_user = apostador.getIdUser();

		for (Aposta a : apostas) {
			String id_game = a.getEvento().getIdEvento();
			
			if (type_of_bet.equals("Simple"))
				dl.makeBet(a.getQuantia(), a.getOdd(), id_user, id_game, a.getEquipa());
			else
				dl.makeMultipleBet(chosen_ammount, a.getOdd(), id_user, id_game, a.getEquipa(), odd_total);
		}
	}


	private void levantarDinheiro()
	{
		Float saldo_atual = dl.getUserBalance(apostador.getIdUser()).getBalance();

		// Ammount
		Float montante = Menu.promptFloatUntilValid("Ammount", (obj)-> obj>0 && obj<=saldo_atual, "Insert a valid value!");

		// Apply tax
		System.out.println("Tax: " + montante * user.getSaldo().getMoeda().getImposto());

		// Ask for confirmation
		if (Menu.prompt("Confirm (y/n/default=y)", (obj)-> !obj.equals("n"), "Cancelled") == null) return;

		dl.withdrawMoney(apostador.getIdUser(), montante);
	}


	private void menuMetodosPagamento()
	{
		String[] payment_methods = new String[] {"Credit/Debit card", "Bank transfer"};

		Menu menu = new Menu( "Payment Method", payment_methods);

		for(int i = 1; i <= payment_methods.length; i++) {
			menu.setPreCondition(i, ()->true);
			menu.setHandler(i, this::depositarDinheiro);
		}

		menu.runOnce();
	}


	private void depositarDinheiro()
	{
		// Ammount
		Float montante = Menu.promptFloatUntilValid("Ammount", (obj)-> obj>0, "Insert a valid value!");

		// Apply tax
		System.out.println("Tax: " + montante * user.getSaldo().getMoeda().getImposto());

		// Ask for confirmation
		if (Menu.prompt("Confirm (y/n/default=y)", (obj)-> !obj.equals("n"), "Cancelled") == null) return;

		dl.depositMoney(apostador.getIdUser(), montante);
	}


	private void consultarNotificacoes()
	{
		List<Notificacao> notifications = dl.getUserNotifications(apostador.getIdUser());

		System.out.println("----------");

		System.out.println("Notifications:");
		int counter = 0;
		for(Notificacao n : notifications) {
			System.out.println(counter+": "+n.getMessage());
			counter++;
		}
		System.out.println();
		
		System.out.println("----------");
	}

	private void menuEditarPerfil()
	{
		Menu menu = new Menu( "Edit profile", new String[] {
			"Name",
			"Email",
			"Language",
			"Address",
			"Cellphone no."
		});

		/* Name */
		menu.setHandler(1, this::mudarNome);

		/* Email */
		menu.setHandler(2, this::mudarEmail);

		/* Language */
		menu.setHandler(3, this::mudarLinguagem);

		/* Address */
		menu.setHandler(4, this::mudarEndereco);

		/* Cell no. */
		menu.setHandler(5, this::mudarCellNo);

		menu.runOnce();
	}

	private void mudarNome()
	{
		String name = Menu.prompt("Name");
		dl.setName(user.getIdUser(), name);
	}

	private void mudarEmail()
	{
		String email = Menu.prompt("Email", (obj)->!dl.existsEmail(obj), "Email already taken");
		dl.setEmail(user.getIdUser(), email);
	}

	private void mudarLinguagem()
	{
		String language = Menu.prompt("Language"); //Menu
		dl.setLanguage(user.getIdUser(), language);
	}

	private void mudarEndereco()
	{
		//2fa
		Menu.promptUntilValid("Code", (obj)->true, "Wrong code");

		String address = Menu.prompt("Address");
		dl.setAddress(user.getIdUser(), address);
	}

	private void mudarCellNo()
	{
		//2fa
		Menu.promptUntilValid("Code", (obj)->true, "Wrong code");

		String cellno = Menu.prompt("Cellphone no.");
		dl.setCellno(user.getIdUser(), cellno);

	}

	private void seguirEvento()
	{
		String id = Menu.promptUntilValid("ID evento", dl::existsEvent, "Insert valid id!");
		dl.followEvent(user.getIdUser(), id);
	}

	private void deixarSeguirEvento()
	{
		String id = Menu.promptUntilValid("ID evento", dl::existsEvent, "Insert valid id!");
		dl.unfollowEvent(user.getIdUser(), id);
	}
	//=== END PUTTER ===




	//=== BEGIN SPECIALIST ===
	private void menuEspecialista()
	{
		Menu menu = new Menu( "Specialist", new String[] {
			"Change event odd"
		});

		/* Change event odd */
		menu.setHandler(1, this::changeEventOdd);

		menu.run();
	}


	private void changeEventOdd()
	{
		String id = Menu.promptUntilValid("Event ID", dl::existsEvent, "Insert a valid ID!");
		String outcome = Menu.prompt("Outcome");
		Float odd = Menu.promptFloat("New odd");

		dl.changeEventOdd(id, outcome, odd);
	}
	//=== END SPECIALIST ===




	//=== BEGIN ADMIN ===
	private void menuAdministrador()
	{
		Menu menu = new Menu( "Administrator", new String[] {
			"Launch promotion",
			"Create event",
			"Close event",
			"Change event state"
		});

		/* Launch promotion */
		menu.setHandler(1, this::criarPromocao);

		/* Create event */
		menu.setHandler(2, this::criarEvento);

		/* Close event */
		menu.setHandler(3, this::fecharEvento);

		/* Change event state */
		menu.setHandler(4, this::mudarEstadoEvento);

		menu.run();
	}


	private void criarPromocao()
	{
		String id = Menu.promptUntilValid("Event ID", dl::existsEvent, "Insert a valid ID!");
		Float bonus = Menu.promptFloat("Bonus");

		dl.createPromo(id, bonus);
	}


	private void criarEvento()
	{
		String sport = Menu.prompt("Sport");
		String home_team = Menu.prompt("Home Team");
		String away_team = Menu.prompt("Away Team");
		String date = Menu.prompt("Date");

		dl.createEvent(sport, home_team, away_team, date);
	}


	private void fecharEvento()
	{
		String id = Menu.promptUntilValid("Event ID", (obj)-> dl.existsEvent(obj) && !dl.getEventState(obj).equals("Closed"), "Insert a valid ID");
		String result = Menu.prompt("Result");
		String outcome = Menu.prompt("Outcome");
		dl.closeEvent(id, result, outcome);
	}

	/*private void menuOutcomesEvento(String id)
	{
		String[] outcomes = new String[] {"Home", "Draw", "Away"};

		Menu menu = new Menu( "Outcome", outcomes);

		for(int i = 1; i <= outcomes.length; i++) {
			final int idx = i-1;
			menu.setPreCondition(i, ()->true);
			menu.setHandler(i, ()->dl.closeEvent(id, outcomes[idx]));
		}

		menu.runOnce();
	}*/


	private void mudarEstadoEvento()
	{
		String id = Menu.promptUntilValid("Event ID", dl::existsEvent, "Insert a valid ID");
		menuEstadosEvento(id);
	}


	private void menuEstadosEvento(String id)
	{
		String[] event_states = new String[] {"Open", "Suspended", "Cancelled"};

		Menu menu = new Menu( "Event State", event_states);

		// Open
		menu.setPreCondition(1, ()-> dl.getEventState(id).equals("Suspended"));

		// Suspended
		menu.setPreCondition(2, ()-> dl.getEventState(id).equals("Open"));

		// Cancelled
		menu.setPreCondition(3, ()-> dl.getEventState(id).equals("Open") || dl.getEventState(id).equals("Suspended"));

		for(int i = 1; i <= event_states.length; i++) {
			final int idx = i-1;
			menu.setHandler(i, ()->dl.changeEventState(id, event_states[idx]));
		}

		menu.runOnce();
	}
	//=== END SPECIALIST ===
}
