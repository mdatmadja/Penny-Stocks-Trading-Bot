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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

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

public class Bot2
{
	
    private static Configuration config;
	private static Socket skt;
    private static BufferedReader from_exchange;
    private static PrintWriter to_exchange;
    private static int orderID;
    private static List<String> market;
    private static List<Integer> fair, buyPrice, buyNum, sellPrice, sellNum;
	
    private static void trade(){
        if(market.contains("BOOK")){
            switch (market.get(1)){
                case "VALE":
                    buyPrice.set(0, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(0, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(0, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

                case "VALBZ":
                    buyPrice.set(1, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(1, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(1, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

                case "XLF":
                    buyPrice.set(2, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(2, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(2, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

                case "GS":
                    buyPrice.set(3, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(3, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(3, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

                case "MS":
                    buyPrice.set(4, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(4, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(4, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

                case "WFC":
                    buyPrice.set(5, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(5, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(5, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

		case "BOND":
                    buyPrice.set(6, Integer.parseInt(market.get(market.indexOf("BUY")+1).substring(0,market.get(market.indexOf("BUY")+1).indexOf(":"))));
                    sellPrice.set(6, Integer.parseInt(market.get(market.indexOf("SELL")+1).substring(0,market.get(market.indexOf("SELL")+1).indexOf(":"))));
                    fair.set(6, buyPrice.get((buyPrice.size()-1) + sellPrice.get(sellPrice.size()-1))/2);
                break;

                default:
                    break;
            }
        }
        
        if(fair.get(0) > 0 && fair.get(1) > 0){
            if(sellPrice.get(0)*sellNum.get(0)+10 < buyPrice.get(1)*sellNum.get(0)){
                to_exchange.println("ADD " + orderID++ + " VALE " + " BUY " + sellPrice.get(0) + " " + sellNum.get(0));
                to_exchange.println("CONVERT " + orderID++ + " VALE " + " SELL "  + sellNum.get(0));
                to_exchange.println("ADD " + orderID++ + " VALBZ " + " SELL " + buyPrice.get(1) + " " + sellNum.get(0));
            }else if(sellPrice.get(1)*sellNum.get(1)+10 < buyPrice.get(0)*sellNum.get(1)){
                to_exchange.println("ADD " + orderID++ + " VALBZ " + " BUY " + sellPrice.get(1) + " " + sellNum.get(1));
                to_exchange.println("CONVERT " + orderID++ + " VALBZ " + " BUY "  + sellNum.get(1));
                to_exchange.println("ADD " + orderID++ + " VALE " + " SELL " + buyPrice.get(0) + " " + sellNum.get(1));
            }
        }
    }
	
    public static void main(String[] args)
    {
          /* The boolean passed to the Configuration constructor dictates whether or not the
            bot is connecting to the prod or test exchange. Be careful with this switch! */
          try{
        	config = new Configuration(true);
        	skt = new Socket(config.exchange_name(), config.port());
            from_exchange = new BufferedReader(new InputStreamReader(skt.getInputStream()));
            to_exchange = new PrintWriter(skt.getOutputStream(), true);
            orderID = 1;
            
	    for(int i = 0; i < 7; i++){
                fair.add(-1);
                buyPrice.add(-1);
                sellPrice.add(-1);
                buyNum.add(-1);
                sellNum.add(-1);
            }
            /*
              A common mistake people make is to to_exchange.println() > 1
              time for every from_exchange.readLine() response.
              Since many write messages generate marketdata, this will cause an
              exponential explosion in pending messages. Please, don't do that!
            */
            to_exchange.println(("HELLO " + config.team_name).toUpperCase());
            
            
            String reply = from_exchange.readLine().trim();
            System.err.printf("The exchange replied: %s\n", reply);
            int x = 0;
            while (true) {
                market = Arrays.asList(from_exchange.readLine().trim().split(" "));
                trade();
                System.out.println(fair);
                if (market.get(0).equals("CLOSE")) {
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
