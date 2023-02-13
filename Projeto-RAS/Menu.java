import java.util.*;

/**
 * This class implements a menu in text mode.
 *
 * @author José Creissac Campos (modified for this project)
 * @version v3.4 (20210930)
 */
public class Menu {

	public interface Handler {void execute();}
	public interface PreCondition {boolean validate();}

	private static final Scanner is = new Scanner(System.in);

	private final String title;						// Title of the menu (opcional)
	private final List<String> options;				// List of options
	private final List<PreCondition> available;		// List of pre-conditions
	private final List<Handler> handlers;			// List of handlers


	// Constructors

	/**
	 * Empty constructor for objects of the class Menu.
	 *
	 * Creates an empty menu, to which can be added options.
	 */
	public Menu() {
		this.title = "Menu";
		this.options = new ArrayList<>();
		this.available = new ArrayList<>();
		this.handlers = new ArrayList<>();
	}

	/**
	 * Constructor for objects of class Menu (with title and a List of options).
	 *
	 * Creates a menu of options without event handlers.
	 *
	 * @param title Menu title
	 * @param options List of strings with the menu options
	 */
	public Menu(String title, List<String> options) {
		this.title = title;
		this.options = new ArrayList<>(options);
		this.available = new ArrayList<>();
		this.handlers = new ArrayList<>();
		this.options.forEach(s-> {
			this.available.add(()->true);
			this.handlers.add(()->System.out.println("\nERROR: Option not implemented!"));
		});
	}

	/**
	 * Constructor for objects of class Menu (without title but with a List of options).
	 *
	 * Creates a menu of options without event handlers.
	 *
	 * @param options List of strings with the menu options
	 */
	public Menu(List<String> options) { this("Menu", options); }

	/**
	 * Constructor for objects of class Menu (with title and an array of options).
	 *
	 * Creates a menu of options without event handlers.
	 *
	 * @param title Menu title
	 * @param options String array with the menu options
	 */
	public Menu(String title, String[] options) { this(title, Arrays.asList(options)); }

	/**
	 * Constructor for objects of class Menu (without title but with an array of options).
	 *
	 * Creates a menu of options without event handlers.
	 *
	 * @param options String array with the menu options
	 */
	public Menu(String[] options) { this(Arrays.asList(options)); }



	// Instance methods

	/**
	 * Add option to menu.
	 *
	 * @param name Option name
	 * @param p Option pre-condition
	 * @param h Option handler
	 */
	public void option(String name, PreCondition p, Handler h) {
		this.options.add(name);
		this.available.add(p);
		this.handlers.add(h);
	}

	/**
	 * Run menu once.
	 */
	public void runOnce() {
		int op;
		show();
		op = readOption();
		// test pre-condition
		if (op>0 && !this.available.get(op-1).validate()) {
			System.out.println("Option unavailable!");
		} else if (op>0) {
			// execute handler
			this.handlers.get(op-1).execute();
		}
	}

	/**
	 * Run menu multiple times.
	 *
	 * Stops with option 0 (zero).
	 */
	public void run() {
		int op;
		do {
			show();
			op = readOption();
			// test pre-condition
			if (op>0 && !this.available.get(op-1).validate()) {
				System.out.println("Option unavailable! Try again.");
			} else if (op>0) {
				// execute handler
				this.handlers.get(op-1).execute();
			}
		} while (op != 0);
	}

	/**
	 * Method to register a pre-condition to a menu option.
	 *
	 * @param i option index (starts with 1)
	 * @param b pre-condition to register
	 */
	public void setPreCondition(int i, PreCondition b) {
		this.available.set(i-1,b);
	}

	/**
	 * Method to register a handler to a menu option.
	 *
	 * @param i option index  (starts with 1)
	 * @param h handler to register
	 */
	public void setHandler(int i, Handler h) {
		this.handlers.set(i-1, h);
	}



	// Auxiliary methods

	/** Print menu */
	private void show() {
		System.out.println("\n*** "+this.title+" *** ");
		for (int i=0; i<this.options.size(); i++) {
			System.out.print(i+1);
			System.out.print(" - ");
			System.out.println(this.available.get(i).validate()?this.options.get(i):"---");
		}
		System.out.println("0 - Exit");
	}

	/** Read valid option */
	private int readOption() {
		int op;

		System.out.print("> ");
		try {
			String line = is.nextLine();
			op = Integer.parseInt(line);
		}
		catch (NumberFormatException e) { // Not an int
			op = -1;
		}
		if (op<0 || op>this.options.size()) {
			System.out.println("Invalid option!!!");
			op = -1;
		}
		return op;
	}



	public static String prompt(String field) {
		System.out.print(field+": ");
		return is.nextLine();
	}

	public static Integer promptInt(String field) {
		return Integer.parseInt(prompt(field));
	}

	public static Float promptFloat(String field) {
		return Float.parseFloat(prompt(field));
	}
	
	public interface ConditionStr {boolean validate(String response);}
	public interface ConditionInt {boolean validate(Integer response);}
	public interface ConditionFlt {boolean validate(Float response);}

	public static String prompt(String field, ConditionStr c, String errorMessage) {
		System.out.print(field+": ");
		String response = is.nextLine();
		if(!c.validate(response)) {
			System.out.println(errorMessage);
			return null;
		}
		return response;
	}

	public static Integer promptInt(String field, ConditionInt c, String errorMessage) {
		Integer response = Integer.parseInt(prompt(field));
		if(!c.validate(response)) {
			System.out.println(errorMessage);
			return null;
		}
		return response;
	}

	public static Float promptFloat(String field, ConditionFlt c, String errorMessage) {
		Float response = Float.parseFloat(prompt(field));
		if(!c.validate(response)) {
			System.out.println(errorMessage);
			return null;
		}
		return response;
	}

	public static String promptUntilValid(String field, ConditionStr c, String errorMessage) {
		String response = null;
		do {response = prompt(field, c, errorMessage);} while(response==null);
		return response;
	}

	public static Integer promptIntUntilValid(String field, ConditionInt c, String errorMessage) {
		Integer response = null;
		do {response = promptInt(field, c, errorMessage);} while(response==null);
		return response;
	}

	public static Float promptFloatUntilValid(String field, ConditionFlt c, String errorMessage) {
		Float response = null;
		do {response = promptFloat(field, c, errorMessage);} while(response==null);
		return response;
	}
}
