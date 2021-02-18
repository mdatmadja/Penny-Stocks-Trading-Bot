/* HOW TO RUN
   1) Configure things in the Configuration class
   2) Compile: javac Bot.java
   3) Run in loop: while true; do java Bot; sleep 1; done
*/
import java.lang.*;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;

class Configuration {
    String exchange_name;
    int    exchange_port;
    /* 0 = prod-like
       1 = slow
       2 = empty
    */
    final Integer test_exchange_kind = 0;
    /* replace REPLACEME with your team name! */
    final String  team_name          = "CASTILLIONCOOP";

    Configuration(Boolean test_mode) {
        if(!test_mode) {
            exchange_port = 20000;
            exchange_name = "production";
        } else {
            exchange_port = 20000 + test_exchange_kind;
            exchange_name = "test-exch-" + this.team_name;
        }
    }

    String  exchange_name() { return exchange_name; }
    Integer port()          { return exchange_port; }
}

public class Bot
{
	
	private static Configuration config;
	private static Socket skt;
    private static BufferedReader from_exchange;
    private static PrintWriter to_exchange;
    private static int orderID;
	
	public static void trade(String message[]) {
		
		int i;
		int j;
		String symbol = "BOND";
		int buyPrice = 999;
		int buyNum = 10;
		int sellPrice = 1001;
		
		String buyFinal = "ADD " + orderID + " " + symbol + " BUY " + buyPrice + " " + buyNum;
		to_exchange.println(buyFinal);
		orderID++;
		
		if(message[0].equals("FILL")) {
			String sellFinal = "ADD " + orderID + " " + symbol + " SELL " + sellPrice + " " + message[5];
			to_exchange.println(sellFinal);
			orderID++;
		}
		
		
		//We're clowns :)
		/*for(int x = 0; x < message.length; x++) {
			if(message[x].equals("BOND")) {
				//String stock = inputData.substring(inputData.indexOf("BOOK")+5,inputData.indexOf("BUY")-1);

				  while(!message[x].equals("SELL")) {
					  x++;
					  if(x == message.length - 1) {
						  
						  i = x;
						  j = message[i].indexOf(":");
						  buyPrice = Integer.parseInt(message[i].substring(0,j));
						  buyNum = Integer.parseInt(message[i].substring(j+1, message[i].length() ));
						  sellPrice = buyPrice + 1;

						  String buyFinal = "ADD " + orderID + " " + symbol + " BUY " + buyPrice + " " + buyNum;
						  to_exchange.println(buyFinal);
						  orderID++;
						  
					  }else {
						  i = x - 1;
						  j = message[i].indexOf(":");
						  buyPrice = Integer.parseInt(message[i].substring(0,j));
						  buyNum = Integer.parseInt(message[i].substring(j+1, message[i].length() ));
						  sellPrice = buyPrice + 1;


						  String buyFinal = "ADD " + orderID + " " + symbol + " BUY " + buyPrice + " " + buyNum;
						  to_exchange.println(buyFinal);
						  orderID++;
						  
						  String sellFinal = "ADD " + orderID + " " + symbol + " SELL " + sellPrice + " " + buyNum;
						  to_exchange.println(sellFinal);
						  orderID++;
					  }
				  }
			}
		}*/
		
			  
	}
	
    public static void main(String[] args)
    {
        /* The boolean passed to the Configuration constructor dictates whether or not the
           bot is connecting to the prod or test exchange. Be careful with this switch! */
        try
        {
        	config = new Configuration(false);
        	skt = new Socket(config.exchange_name(), config.port());
            from_exchange = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            to_exchange = new PrintWriter(skt.getOutputStream(), true);
            orderID = 1;
	    /*
              A common mistake people make is to to_exchange.println() > 1
              time for every from_exchange.readLine() response.
              Since many write messages generate marketdata, this will cause an
              exponential explosion in pending messages. Please, don't do that!
            */
            to_exchange.println(("HELLO " + config.team_name).toUpperCase());
            
            String reply = from_exchange.readLine().trim();
            System.err.printf("The exchange replied: %s\n", reply);
            while (true) {
                String message[] = from_exchange.readLine().trim().split(" ");
                trade(message);
                if (message[0].equals("CLOSE")) {
                    System.out.println("The round has ended");
                    break;
                }
                
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
    }
}
