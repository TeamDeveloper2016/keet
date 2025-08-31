package mx.org.kaana.mantic.correos.enums;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.recurso.TcConfiguraciones;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/03/2019
 *@time 01:08:38 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum ECorreos { 
  
  FACTURACION   ("/mx/org/kaana/mantic/correos/templates/facturacion.html", "/resources/janal/img/sistema/", "correo.ventas.user", "correo.ventas.pass", "Facturas"), 
	COTIZACIONES  ("/mx/org/kaana/mantic/correos/templates/cotizacion.html", "/resources/janal/img/sistema/", "correo.ventas.user", "correo.ventas.pass", "Ventas"),
	ORDENES_COMPRA("/mx/org/kaana/mantic/correos/templates/ordenes.html", "/resources/janal/img/sistema/", "correo.compras.user", "correo.compras.pass", "Compras"),
	CUENTAS       ("/mx/org/kaana/mantic/correos/templates/cuentas.html", "/resources/janal/img/sistema/", "correo.ventas.user", "correo.ventas.pass", "Ventas"),
	ORDENES_CANCEL("/mx/org/kaana/mantic/correos/templates/cancelar.html", "/resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Compras"),
	//cafu
	VENTAS        ("/mx/org/kaana/keet/correos/templates/ventas.html", "/resources/janal/img/sistema/", "correo.ventas.user", "correo.ventas.pass", "Ventas"),
	COMPRAS       ("/mx/org/kaana/keet/correos/templates/compras.html", "/resources/janal/img/sistema/", "correo.compras.user", "correo.compras.pass", "Compras"),
	ADMINISTRACION("/mx/org/kaana/keet/correos/templates/administracion.html", "/resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Administración"),
	RESIDENTE     ("/mx/org/kaana/keet/correos/templates/residente.html", "/resources/janal/img/sistema/", "correo.ventas.user", "correo.admin.pass", "Administración"),
  DESTAJOS      ("/mx/org/kaana/keet/correos/templates/destajos.html", "/resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Administración"),
  PAGOS         ("/mx/org/kaana/keet/correos/templates/pagos.html", "/resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "Administración");
	
  private static final Log LOG = LogFactory.getLog(ECorreos.class);

	private String template;
	private String images;
	private String user;
	private String password;
	private String alias;
  private static Map<String, String> empresas;
  
  static {
    empresas= new HashMap<>();
    empresas.put("cafu.facturacion.email", "administracion@cafu.com.mx");  
    empresas.put("cafu.facturacion.backup", "");  
    empresas.put("cafu.cotizaciones.email", "ventas@cafu.com.mx");  
    empresas.put("cafu.cotizaciones.backup", "compras2@cafuconstrucciones.com");  
    empresas.put("cafu.ordenes_compra.email", "compras@cafu.com.mx");  
    empresas.put("cafu.ordenes_compra.backup", "compras2@cafuconstrucciones.com");  
    empresas.put("cafu.ordenes_cancel.email", "compras@cafu.com.mx");  
    empresas.put("cafu.ordenes_cancel.backup", "compras2@cafuconstrucciones.com");  
    empresas.put("cafu.cuentas.email", "ventas@cafu.com.mx");  
    empresas.put("cafu.cuentas.backup", "");  
    empresas.put("cafu.ventas.email", "ventas@cafu.com.mx");  
    empresas.put("cafu.ventas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("cafu.compras.email", "compras@cafu.com.mx");  
    empresas.put("cafu.compras.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("cafu.administracion.email", "administracion@cafu.com.mx");  
    empresas.put("cafu.administracion.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("cafu.residentes.email", "administracion@cafu.com.mx");  
    empresas.put("cafu.residentes.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("cafu.destajos.email", "administracion@cafu.com.mx");  
    empresas.put("cafu.destajos.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("cafu.pagos.email", "administracion@cafu.com.mx");  
    empresas.put("cafu.pagos.backup", "imox.soluciones.web@gmail.com");  
    
    empresas.put("gylvi.facturacion.email", "facturas@gylvi.com");  
    empresas.put("gylvi.facturacion.backup", "");  
    empresas.put("gylvi.cotizaciones.email", "ventas@gylvi.com");  
    empresas.put("gylvi.cotizaciones.backup", "compras@gylvi.com");  
    empresas.put("gylvi.ordenes_compra.email", "compras@gylvi.com");  
    empresas.put("gylvi.ordenes_compra.backup", "compras@gylvi.com");  
    empresas.put("gylvi.ordenes_cancel.email", "compras@gylvi.com");  
    empresas.put("gylvi.ordenes_cancel.backup", "compras@gylvi.com");  
    empresas.put("gylvi.cuentas.email", "ventas@gylvi.com");  
    empresas.put("gylvi.cuentas.backup", "");  
    empresas.put("gylvi.ventas.email", "ventas@gylvi.com");  
    empresas.put("gylvi.ventas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("gylvi.compras.email", "compras@gylvi.com");  
    empresas.put("gylvi.compras.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("gylvi.administracion.email", "administracion@gylvi.com");  
    empresas.put("gylvi.administracion.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("gylvi.residentes.email", "administracion@gylvi.com");  
    empresas.put("gylvi.residentes.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("gylvi.destajos.email", "administracion@gylvi.com");  
    empresas.put("gylvi.destajos.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("gylvi.pagos.email", "administracion@gylvi.com");  
    empresas.put("gylvi.pagos.backup", "imox.soluciones.web@gmail.com");     
    
    empresas.put("triana.facturacion.email", "facturas@trianamx.com");  
    empresas.put("triana.facturacion.backup", "");  
    empresas.put("triana.cotizaciones.email", "ventas@trianamx.com");  
    empresas.put("triana.cotizaciones.backup", "compras2@trianamx.com");  
    empresas.put("triana.ordenes_compra.email", "compras@trianamx.com");  
    empresas.put("triana.ordenes_compra.backup", "compras2@trianamx.com");  
    empresas.put("triana.ordenes_cancel.email", "compras@trianamx.com");  
    empresas.put("triana.ordenes_cancel.backup", "compras2@trianamx.com");  
    empresas.put("triana.cuentas.email", "ventas@trianamx.com");  
    empresas.put("triana.cuentas.backup", "");  
    empresas.put("triana.ventas.email", "ventas@trianamx.com");  
    empresas.put("triana.ventas.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("triana.compras.email", "compras@trianamx.com");  
    empresas.put("triana.compras.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("triana.administracion.email", "administracion@trianamx.com");  
    empresas.put("triana.administracion.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("triana.residentes.email", "administracion@trianamx.com");  
    empresas.put("triana.residentes.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("triana.destajos.email", "administracion@trianamx.com");  
    empresas.put("triana.destajos.backup", "imox.soluciones.web@gmail.com");  
    empresas.put("triana.pagos.email", "administracion@trianamx.com");  
    empresas.put("triana.pagos.backup", "imox.soluciones.web@gmail.com");     
  }

	private ECorreos(String template, String images, String user, String password, String alias) {
		this.template= template;
		this.images  = images;
		this.user    = user;
		this.password= password;
		this.alias   = alias;
    // AQUI SE AGREGA LA CUENTA DE CORREO DE jimenez76@yahoo.com QUE ESTA REGISTRADA EN LA BASE DE DATOS 
		// this.backup  = Cadena.isVacio(backup)? TcConfiguraciones.getInstance().getPropiedad(Constantes.EMAILS_@BACKUP_SYSTEM): backup.concat(",").concat(TcConfiguraciones.getInstance().getPropiedad(Constantes.EMAILS_BACKUP_SYSTEM));
	}

	public String getTemplate() {
		return template;
	}

	public String getImages() {
		return images;
	}	

	public String getUser() {
		return TcConfiguraciones.getInstance().getPropiedadServidor(this.user);
	}

	public String getPassword() {
		return TcConfiguraciones.getInstance().getPropiedadServidor(this.password);
	}

	public String getEmail() {
    String token   = Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase().concat(".").concat(this.name().toLowerCase()).concat(".").concat("email");
    String regresar= "";
    if(empresas.containsKey(token))
      regresar= empresas.get(token);
		return regresar;
	}

	public String getAlias() {
		return alias.concat(" ").concat(Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toUpperCase());
	}

  public String getBackup() {
    String token   = Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toLowerCase().concat(".").concat(this.name().toLowerCase()).concat(".").concat("backup");
    String regresar= "";
    if(empresas.containsKey(token))
      regresar= empresas.get(token);
		return regresar;
  }
  
}