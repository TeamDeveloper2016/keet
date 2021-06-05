package mx.org.kaana.keet.test;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.usage.Record;
import com.twilio.type.PhoneNumber;


/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 3/06/2021
 *@time 02:45:21 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Sms {

  public static final String ACCOUNT_SID= "AC227cacebc522f1bdd4960991127ad969";
  public static final String AUTH_TOKEN = "f83b146628a025cb2ebd3d0f3d35cbd7";  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

    Iterable<Record> usage = Record.reader().read();
    for (Record record : usage) {
      System.out.println(record);
    }

    // Get a number
    System.out.println("+524492090586");

    // Send a text message
    Message message = Message.creator(
        ACCOUNT_SID,
        new PhoneNumber("+524492090586"),
        new PhoneNumber("+524492090586"),
        "Hello world!"
    ).create();

    System.out.println(message.getSid());
    System.out.println(message.getBody());    
  }

}
