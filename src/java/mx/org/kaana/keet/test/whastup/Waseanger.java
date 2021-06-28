package mx.org.kaana.keet.test.whastup;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.Serializable;
import mx.org.kaana.libs.wassenger.Upload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 24/06/2021
 * @time 10:15:57 AM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Waseanger implements Serializable {

  private static final long serialVersionUID = -1584863300996381130L;
  
  private static final Log LOG = LogFactory.getLog(Waseanger.class);
  private static final String IMOX_TOKEN    = "IMOX_TOKEN";

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws UnirestException {
    String token= System.getenv(IMOX_TOKEN);
    
//    HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
//    .header("Content-Type", "application/json")
//    .header("Token", token)
//    .body("{\"group\":\"5214492090586-1424358690@g.us\",\"message\":\"Los _quiero_ mil, que nunca se les *olvide*\"}")
//    .asString();
//    
//    HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
//    .header("Content-Type", "application/json")
//    .header("Token", token)
//    .body("{\"phone\":\"+5214492090586\",\"message\":\"Hello world,\n this is a sample message\"}")
//    .asString();    

//HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
//  .header("Content-Type", "application/json")
//  .header("Token", token)
//  .body("{\"phone\":\"+5214492090586\",\"enqueue\":\"never\",\"message\":\"Check out this cool link: https://www.youtube.com\"}")
//  .asString();

//HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
//  .header("Content-Type", "application/json")
//  .header("Token", token)
//  .body("{\"phone\":\"+5214492090586\",\"device\":\"60d4987e48b592aa82f09e92\",\"message\":\"This message,\n will be delivered through the specificed WhatsApp device ID\"}")
//  .asString();

//HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
//  .header("Content-Type", "application/json")
//  .header("Token", token)
//  .body("{\"phone\":\"+5214492090586\",\"message\":\"This is a caption for an image message\",\"media\":{\"file\":\"FILE ID GOES HERE\"}}")
//  .asString();

//HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/files")
//  .header("Content-Type", "application/json")
//  .header("Token", token)
//  .body("{\"url\":\"https://picsum.photos/seed/picsum/500/500\"}")
//  .asString();

    // Subir un archivo
    HttpResponse<String> file= Unirest.post("https://api.wassenger.com/v1/files")
   .header("Content-Type", "application/json")
   .header("Token", token)
   .body("{\"url\":\"https://cafu.jvmhost.net/Temporal/Pdf/CAFU_2021062410492325_orden_de_compra_detalle.pdf\"}")
   .asString();
    Upload upload= new Upload(file);
    if(upload.isOk())
      LOG.info(upload.getMedia());
    else
      LOG.error(upload.getStatusText());
  }

}
