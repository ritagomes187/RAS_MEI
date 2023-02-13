
/* RUN WITH:
javac -cp ".;libs\*" RASBet.java
java  -cp ".;libs\*" RASBet
*/


public class RASBet {

	public static void main(String[] args) { 
		try {
			System.out.print("Opening database... ");
			RASBetDL dl = new RASBetDL("database.db");
			System.out.println("OK");


			System.out.print("Initializing database... ");
			dl.init();
			System.out.println("OK");


			System.out.print("Populating database... ");
			dl.populate();
			System.out.println("OK");


			System.out.print("Starting sync threads... ");
			dl.startSyncing();
			System.out.println("OK\n");


			// Business layer
			RASBetBL controller = new RASBetBL(dl);
			controller.run();


			dl.close();
			System.out.println("\nDatabase closed");
			System.out.println("See you soon...");



		} catch (Exception e) {

			System.out.println( e.getClass().getName() + ": " + e.getMessage() );
			e.printStackTrace();
			System.exit(0);
		}
	}
}