package mx.org.kaana.libs.correo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import static mx.org.kaana.libs.pagina.JsfUtilities.getApplication;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.recurso.TcConfiguraciones;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Envio implements Serializable {
	
  private static final Log LOG              = LogFactory.getLog(Envio.class);
	private static final long serialVersionUID= 6595458971187535281L;
	private Authenticator autenticar;

	public Envio (Authenticator autenticar) {
    this.autenticar = autenticar;
  }    
	
  public void asuntoMensaje(String de, String para) {
    envia(de, para,"", null, "asunto", "contenido", "", false, "application/pdf");
  }
  
  public  void asuntoMensaje(String de, String para, String asunto, String contenido) {
    envia(de, para,"", null, asunto, contenido, "", false, "application/pdf");
  }
  
  public void asuntoMensaje(String de, String para, String asunto, String contenido, boolean textoHTML) {
    envia(de, para,"", null, asunto, contenido, "", textoHTML, "application/pdf");
  }
  
  public void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo) {
    envia(de, para,"", anexo, "asunto", "contenido", "nombre_anexo", false, "application/pdf");
  }
  
  public  void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo, String asunto) {
    envia(de, para,"", anexo, asunto, "contenido", "nombre_anexo", false, "application/pdf");
  }
  
  public  void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo, String asunto, String contenido) {
    envia(de, para, "",anexo, asunto, contenido, "nombre_anexo", false, "application/pdf");
  }
  
  public  void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo, String asunto, String contenido, String adjunto) {
    envia(de, para, "",anexo, asunto, contenido, adjunto, false, "application/pdf");
  }
  
  public  void asuntoMensaje(String de, String para, String conCopia,ByteArrayOutputStream anexo, String asunto, String contenido, String adjunto, boolean textoHTML) {
    envia(de, para, conCopia, anexo, asunto, contenido, adjunto, textoHTML, "application/pdf");
  }
  
  public  void asuntoMensaje(String de, String para, String conCopia, ByteArrayOutputStream anexo, String asunto, String contenido, String adjunto, boolean textoHTML, String formato) {
    List<String> adjuntos = new ArrayList<>();
    adjuntos.add(adjunto);
    envia(de, para,conCopia, anexo, asunto, contenido, adjuntos, textoHTML, formato);
  }

  public  void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo, String asunto, String contenido, String adjunto, boolean textoHTML, String formato) {
    List<String> adjuntos = new ArrayList<>();
    adjuntos.add(adjunto);
    envia(de, para,"", anexo, asunto, contenido, adjuntos, textoHTML, formato);
  }
  
  public  void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo, String asunto, String contenido, List<String> adjunto, boolean textoHTML, String formato) {
    envia(de, para,"",anexo, asunto, contenido, adjunto, textoHTML, formato);
  }
	
	public void asuntoMensaje(String de, String para, ByteArrayOutputStream anexo, String asunto, String contenido, List<String> adjunto, boolean textoHTML, String formato, String[] rutasInLine) {
    envia(de, para, "", anexo, asunto, contenido, adjunto, textoHTML, formato, rutasInLine);
  }  
  
  private  void envia(String de, String para, String conCopia,ByteArrayOutputStream anexo, String asunto, String contenido, String adjunto, boolean textoHTML, String formato) {
    List<String> adjuntos = new ArrayList<>();
    adjuntos.add(adjunto);
    envia(de, para,conCopia,anexo, asunto, contenido,adjuntos, textoHTML, formato);
  }
	
	private  void envia(String de, String para,String concopia, ByteArrayOutputStream anexo, String asunto, String contenido, List<String> adjunto, boolean textoHTML, String formato) {
		envia(de, para,concopia,anexo, asunto, contenido, adjunto, textoHTML, formato, null);
	}

  private  void envia(String de, String para, String conCopia, ByteArrayOutputStream anexo, String asunto, String contenido, List<String> adjunto, boolean textoHTML, String formato, String[] rutasInLine) {
    Properties properties             = null;
    Session session                   = null;
    MimeMessage message               = null;
    InternetAddress internetaddressDe = null;
    StringTokenizer st                = null;
    StringTokenizer stCc              = null;
    MimeBodyPart mbp1                 = null;
    MimeBodyPart mbp2                 = null;
    DataSource ds                     = null;
    Multipart multipart               = null;    
    List<BodyPart> listaArchivos      = null;
    String path                       = null;
    try {
      properties = System.getProperties();
      //properties.put("mail.smtp.host", "10.1.8.102");
			//properties.put("mail.smtp.localhost", Configuracion.getInstance().getPropiedadServidor("domain.mail.server"));
      //properties.put("mail.smtp.starttls.enable", "true");
      //properties.put("mail.smtp.port", "465");			
      properties.put("mail.host", Configuracion.getInstance().getPropiedadServidor("mail.smtp.server"));
      properties.put("mail.transport.protocol", "smtp");
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.ssl.enable", "true");
      properties.put("mail.smtp.port", Configuracion.getInstance().getPropiedadServidor("mail.smtp.port"));			
      properties.put("mail.smtp.ssl.trust", Configuracion.getInstance().getPropiedadServidor("mail.smtp.server"));
      properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.1 TLSv1.2");
      LOG.info("AUTENTICACION EN EL SERVIDOR DE CORREOS");
			session= Session.getDefaultInstance(properties, null);
      session.setDebug(true);
      message = new MimeMessage(session);
      internetaddressDe = new InternetAddress(de);
      message.setFrom(internetaddressDe);
      // SI SON VARIOS CORREOS TIENEN QUE ESTAR SEPARADOS POR COMAS Y SIN ESPACIOS EN BLANCO
      st   = new StringTokenizer(para, ",");     
      stCc = new StringTokenizer(conCopia, ",");
      Address[] internetaddressPara= new InternetAddress[st.countTokens()];
      Address[] internetaddressCc  = new InternetAddress[stCc.countTokens()];      
      int contador= -1;
      while(st.hasMoreTokens()) {
        internetaddressPara[++contador]= new InternetAddress(st.nextToken());
      } // while
      contador= -1;
      while(stCc.hasMoreTokens()) {
        String value = stCc.nextToken();
        if (!Cadena.isVacio(value)) {
          internetaddressCc[++contador]= new InternetAddress(value);
        }  
      } // while
      message.addRecipients(javax.mail.Message.RecipientType.TO,internetaddressPara);
      if (internetaddressCc.length>0)
        message.addRecipients(javax.mail.Message.RecipientType.BCC,internetaddressCc);
      message.setSubject(asunto);
      if (anexo== null) {
        if (textoHTML) {           
					if(rutasInLine!= null) {
						mbp1 = new MimeBodyPart(); //MimeBodyPart
            mbp1.setContent(Cadena.toCharSet(contenido), "text/html");
						multipart = new MimeMultipart(); //Multipart
						multipart.addBodyPart(mbp1);						
						for(String item: rutasInLine) {                   
              mbp1 = new MimeBodyPart();
              ds = new FileDataSource(new File(item.split("¬")[0]));
              mbp1.setDataHandler(new DataHandler(ds));
              mbp1.setFileName(item.split("¬")[1]);
              mbp1.setDisposition(MimeBodyPart.INLINE);
              mbp1.setHeader("Content-ID","<".concat(item.split("¬")[1]).concat(">"));
              multipart.addBodyPart(mbp1);               
						}// if rutas                           
						message.setContent(multipart);   
					} //if rutasInLine
					else
						message.setContent(Cadena.toCharSet(contenido), "text/html");
				} // if
        else 
          message.setText(contenido);
      } // if
      else {
        path = getApplication().getRealPath(Constantes.PROPIEDAD_TEMPORALES);
        //Crear lista de adjuntos
        listaArchivos = new ArrayList<>();
        for(String file : adjunto) {
          BodyPart adj = new MimeBodyPart();
          LOG.info(" ruta archivo :" + path.concat(file));
          adj.setDataHandler(new DataHandler(new FileDataSource(path.concat(file))));
          adj.setFileName(file);
          LOG.error(" ruta archivo :" + path.concat(Constantes.SEPARADOR.concat(file)));
          listaArchivos.add(adj);
        } // for      
        LOG.error("contenido mensaje :" + contenido);
        mbp1= new MimeBodyPart();
        if (textoHTML) 
          mbp1.setContent(Cadena.toCharSet(contenido), "text/html");
        else 
          mbp1.setText(contenido);
        multipart = new MimeMultipart();
        multipart.addBodyPart(mbp1);
        //Agregar lista de adjuntos
        for(BodyPart bp: listaArchivos) {
          multipart.addBodyPart(bp);
        } // for        
        message.setContent(multipart);
      } // if
      Transport transport= session.getTransport("smtp");
      transport.connect(
        Configuracion.getInstance().getPropiedadServidor("mail.smtp.server"), 
        TcConfiguraciones.getInstance().getPropiedadServidor("correo.admin.user"), 
        TcConfiguraciones.getInstance().getPropiedadServidor("correo.admin.pass")
      );
      transport.sendMessage(message, message.getAllRecipients());
      transport.close();      
      // Transport.send(message);
			LOG.info("Correo enviado: "+ para);
    }
    catch (Exception e) {
        Error.mensaje(e);
    }
    finally {
      if(properties!= null)
        properties = null;
      if(session!= null)
        session = null;
      if(message != null)
        message = null;
      if(internetaddressDe != null)
        internetaddressDe = null;
      if(st != null)
        st = null;
      if(mbp1 != null)
        mbp1 = null;
      if(mbp2 != null)
        mbp2 = null;
      if(ds != null)
        ds = null;
      if(multipart!=null)
        multipart = null;      
    }
  } 
}
