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
import java.util.*;

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

public class TestBot
{
	
	private static Configuration config;
	private static Socket skt;
    private static BufferedReader from_exchange;
    private static PrintWriter to_exchange;
    private static int orderID;
    private static List<Integer> buyList;
	private static List<Integer> sellList;
	
    public static int fairVal(List<Integer> buyList, List<Integer> sellList){
		System.out.println("buyList[0]: " + buyList.get(0));
		System.out.println();
		System.out.println("sellList[0]: " + sellList.get(0));
		System.out.println();
		
    	return (buyList.get(0)+sellList.get(0))/2;
    }
    
	public static void trade(String message[], List<Integer> buyList, List<Integer> sellList) {
		
		int start = 0;
		int first = 0;
		String symbol = "BOND";
		int buyPrice = 998;
		int buyNum = 10;
		int sellPrice = 1002;
		
		buyList.clear();
		sellList.clear();
		
		String sellFinal = "";
		
		
		String buyFinal = "ADD " + orderID + " " + symbol + " BUY " + buyPrice + " " + buyNum;
		to_exchange.println(buyFinal);
		orderID++;
			
		if(message[0].equals("FILL")) {
			sellFinal = "ADD " + orderID + " " + symbol + " SELL " + sellPrice + " " + message[5];
			to_exchange.println(sellFinal);
			orderID++;
		}
		
		/*buyFinal = "ADD " + orderID + " " + "MS" + " BUY " + "3999" + " " + "2";
		to_exchange.println(buyFinal);
		orderID++;
		
		if(message[0].equals("FILL")) {
			sellFinal = "ADD " + orderID + " " + "MS" + " SELL " + "4001" + " " + "2";
			to_exchange.println(sellFinal);
			orderID++;
		}
		
		if(message[0].equals("BOOK") && message[1].contentEquals("VALE")) {
			for(int x = 0; x < message.length; x++) {
				if(message[x].equals("BUY")) {
					start = x + 1;
				}
			}
			
			while(!message[start].equals("SELL")){
			  first = message[start].indexOf(":");
			  buyList.add(Integer.parseInt(message[start].substring(0,first)));
			  start++;
			}
			start++;
			
			while(start < message.length){
			  first =  message[start].indexOf(":");
			  sellList.add(Integer.parseInt(message[start].substring(0,first)));
			  start++;
			}
		}
		
		if(!(buyList.isEmpty()) || !(sellList.isEmpty())) {
			if(buyPrice < fairVal(buyList, sellList)) {
				buyFinal = "ADD " + orderID + " " + "VALE" + " BUY " + buyPrice + " 1";
				to_exchange.println(buyFinal);
				orderID++;
			}
			
			if(sellPrice > fairVal(buyList, sellList)) {
					sellFinal = "ADD " + orderID + " " + "VALE" + " SELL " + sellPrice + " " + message[5];
					to_exchange.println(sellFinal);
					orderID++;
			}
		}*/	  
	}
	
    public static void main(String[] args)
    {
        /* The boolean passed to the Configuration constructor dictates whether or not the
           bot is connecting to the prod or test exchange. Be careful with this switch! */
        try
        {
        	config = new Configuration(true);
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
            buyList = new ArrayList<Integer>();
    		sellList = new ArrayList<Integer>();
            while (true) {
                String message[] = from_exchange.readLine().trim().split(" ");
                trade(message, buyList, sellList);
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
