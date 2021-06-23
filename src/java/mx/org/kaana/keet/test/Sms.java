package mx.org.kaana.keet.test;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;


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

    //+19292654422 
     Message message = Message.creator( 
                new com.twilio.type.PhoneNumber("whatsapp:+5214492090586"),
                new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                "Hola como estas ? este mensaje fue enviado por mi *aplicación*")
            .create();
    System.out.println(message.getSid());
    System.out.println(message.getBody());    
  }

}
