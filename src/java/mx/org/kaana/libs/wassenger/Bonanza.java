package mx.org.kaana.libs.wassenger;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import mx.org.kaana.libs.formato.Error;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/06/2021
 *@time 09:55:07 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Bonanza implements Serializable {

  private static final long serialVersionUID = -6510759858245467836L;
  private static final Log LOG = LogFactory.getLog(Bonanza.class);
  private static final String IMOX_TOKEN    = "IMOX_TOKEN";
  private static final String BODY_MESSAGE  = "\"phone\":\"+521{celular}\",\"message\":\"Hola _{nombre}_,\\n{saludo}, somos *Ferreteria Bonanza*, si deseas mantener una comunicación con nosotros por este medio, favor de aceptar el mensaje respondiendo con un *hola* en esta conversación.\\n\\nFerreteria Bonanza siempre tu aliado !\"";
  private static final String BODY_DESTAJO  = "\"phone\":\"+521{celular}\",\"message\":\"Hola _{nombre}_,\\n{saludo}, te hacemos llegar la factura con folio {folio} del día *{fecha}*, favor de hacer clic en el siguiente enlace: https://ferreteriabonanza.com/Temporal/Pdf/{reporte}\\nPara cualquier duda o aclaración por favor no dude en contactarno a la siguiente cuenta de correo *ventas@ferreteriabonanza.com*.\\n\\n*Ferreteria Bonanza* siempre tu aliado!\"";
  private static final int LENGTH_CELL_PHONE= 10;

  private String token;
  private String nombre;
  private String celular;
  private String reporte;
  private String folio;
  private String fecha;

  public Bonanza(String nombre, String celular) {
    this(nombre, celular, "", "", "");
  }
  
  public Bonanza(String nombre, String celular, String reporte, String folio, String fecha) {
    this.nombre = Cadena.nombrePersona(nombre);
    this.celular= this.clean(celular);
    this.reporte= reporte;
    this.folio = folio;
    this.fecha= fecha;
    this.token  = System.getenv(IMOX_TOKEN);
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getCelular() {
    return celular;
  }

  public void setCelular(String celular) {
    this.celular = celular;
  }

  public String getReporte() {
    return reporte;
  }

  public void setReporte(String reporte) {
    this.reporte = reporte;
  }

  @Override
  public String toString() {
    return "Bonanza{" + "nombre=" + nombre + ", celular=" + celular + ", reporte=" + reporte + ", folio=" + folio + ", fecha=" + fecha + '}';
  }

  private String clean(String number) {
    StringBuilder regresar= new StringBuilder();
    if(number!= null) 
      for (int x= 0; x< number.length(); x++) {
        if(number.charAt(x)>= '0' && number.charAt(x)<= '9') 
          regresar.append(number.charAt(x));
      } // for
    return regresar.toString();
  }
  
  public void doSendMessage() {
    this.doSendMessage(null);
  }
  
  public void doSendMessage(Session sesion) {
    if(Objects.equals(this.celular.length(), LENGTH_CELL_PHONE)) {
      Message message= null;
      Value value    = null; 
      Map<String, Object> params = new HashMap<>();        
      try {
        params.put("nombre", this.nombre);
        params.put("celular", this.celular);
        params.put("saludo", this.toSaludo());
        params.put("idTipoMensaje", ETypeMessage.BIENVENIDA.getId());
        if(sesion!= null)
          value= (Value)DaoFactory.getInstance().toField(sesion, "TcManticMensajesDto", "existe", params, "idKey");
        else
          value= (Value)DaoFactory.getInstance().toField("TcManticMensajesDto", "existe", params, "idKey");
        if(value== null) {
          HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
          .header("Content-Type", "application/json")
          .header("Token", this.token)
          .body("{"+ Cadena.replaceParams(BODY_MESSAGE, params, true)+ "}")
          .asString();
          if(Objects.equals(response.getStatus(), 201)) {
            LOG.warn("Enviado: "+ response.getBody());
            Gson gson= new Gson();
            message= gson.fromJson(response.getBody(), Message.class);
            if(message!= null) 
              message.init();
            else
              message= new Message();
          } // if  
          else {
            LOG.error("No se puedo enviar el mensaje por whatsup al celular ["+ this.celular+ "] "+ response.getStatusText()+ "\n"+ response.getBody());
            message= new Message();
          } // else
          message.setTelefono(this.celular);
          message.setIdSendStatus(new Long(response.getStatus()));
          message.setSendStatus(response.getStatusText());
          message.setIdTipoMensaje(ETypeMessage.BIENVENIDA.getId());
          message.setIdUsuario(JsfBase.getIdUsuario());
          if(sesion!= null) 
            DaoFactory.getInstance().insert(sesion, message);  
          else
            DaoFactory.getInstance().insert(message);
        } // if  
      } // try
      catch(Exception e) {
        Error.mensaje(e);
      } // catch
      finally {
        Methods.clean(params);
      } // finally
    } // if
    else 
      LOG.error("No se puedo enviar el mensaje por whatsup al celular ["+ this.celular+ "]");
  }
  
  public void doSendDestajo() {
    this.doSendDestajo(null);
  }
  
  public void doSendDestajo(Session sesion) {
    if(Objects.equals(this.celular.length(), LENGTH_CELL_PHONE)) {
      Message message= null;
      Map<String, Object> params = new HashMap<>();        
      try {
        params.put("nombre", this.nombre);
        params.put("celular", this.celular);
        params.put("reporte", this.reporte);
        params.put("folio", this.folio);
        params.put("fecha", this.fecha);
        params.put("saludo", this.toSaludo());
        HttpResponse<String> response = Unirest.post("https://api.wassenger.com/v1/messages")
        .header("Content-Type", "application/json")
        .header("Token", this.token)
        .body("{"+ Cadena.replaceParams(BODY_DESTAJO, params, true)+ "}")
        .asString();
        // LOG.warn(response);
        if(Objects.equals(response.getStatus(), 201)) {
          LOG.warn("Enviado: "+ response.getBody());
          Gson gson= new Gson();
          message= gson.fromJson(response.getBody(), Message.class);
          if(message!= null) 
            message.init();
          else
            message= new Message();
        } // if  
        else {
          LOG.error("No se puedo enviar el mensaje por whatsup al celular ["+ this.celular+ "] "+ response.getStatusText()+ "\n"+ response.getBody());
        } // else
        message.setTelefono(this.celular);
        message.setIdSendStatus(new Long(response.getStatus()));
        message.setSendStatus(response.getStatusText());
        message.setIdTipoMensaje(ETypeMessage.CONTRATISTA.getId());
        message.setIdUsuario(JsfBase.getIdUsuario());
        if(sesion!= null)
          DaoFactory.getInstance().insert(sesion, message);
        else
          DaoFactory.getInstance().insert(message);
        //{"id":"60d607099faa17d7ac70f63d","waId":"3EB06320796368264337","phone":"+5214492090586","wid":"5214492090586@c.us","status":"queued","deliveryStatus":"queued","createdAt":"2021-06-25T16:40:41.225Z","deliverAt":"2021-06-25T16:40:41.206Z","message":"Hola _Alejandro jiménez garcía_,\nBuenos días, te hacemos llegar el reporte de los destajos de la nómina *2021-20* del *15/06/2021 al 30/06/2021*, hacer clic en el siguiente enlace: https://cafu.jvmhost.net/Temporal/Pdf/CAFU_2021062410492325_orden_de_compra_detalle.pdf\nSi tienes alguna duda, favor de reportarlo de inmediato a tu residente; tienes *24 hrs* para descargar el reporte.\nCAFU Construcciones S.A.","priority":"normal","retentionPolicy":"plan_defaults","retry":{"count":0},"webhookStatus":"pending","media":{"format":"native"},"device":"60d4987e48b592aa82f09e92"}
      } // try
      catch(Exception e) {
        Error.mensaje(e);
      } // catch
      finally {
        Methods.clean(params);
      } // finally
    } // if
    else 
      LOG.error("No se puedo enviar el mensaje por whatsup al celular ["+ this.celular+ "]");
  }

  public void doSendDemo() {
    Map<String, Object> params = new HashMap<>();        
    try {
      params.put("nombre", this.nombre);
      params.put("celular", this.celular);
      params.put("reporte", this.reporte);
      params.put("folio", this.folio);
      params.put("fecha", this.fecha);
      params.put("saludo", this.toSaludo());
      LOG.info("{"+ Cadena.replaceParams(BODY_DESTAJO, params, true)+ "}");
      LOG.info("{"+ Cadena.replaceParams(BODY_MESSAGE, params, true)+ "}");
    } // try
    catch(Exception e) {
      Error.mensaje(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private String toSaludo() {
    String regresar= null;
    Calendar calendar= Calendar.getInstance();
    int hour= calendar.get(Calendar.HOUR_OF_DAY);
    if(hour>= 5 && hour< 12)
      regresar= "Buenos días";
    else
      if(hour>= 12 && hour< 19)
        regresar= "Buenas tardes";
      else 
        regresar= "Buenas noches";
    return regresar;
  }
        
  public static void main(String ... args) {
    String nombres[]  = {"Alejandro Jiménez García", "Daniel Davalos Gutierrez", "José Antonio Davalos"};
    String celulares[]= {"4492090586", "4499500308", "4491133049"};
    Bonanza message= new Bonanza("Alejandro Jiménez García", "449-209-05-86", "AHE150901DV6-90300.pdf", "2021027665", "25/06/2021");
    //message.doSendDemo();
    for (int x= 0; x < nombres.length; x++) {
      message.setNombre(nombres[x]);
      message.setCelular(celulares[x]);
      // message.doSendDemo();
      message.doSendMessage();
      message.doSendDestajo();
    } // for
  }  
  
}
