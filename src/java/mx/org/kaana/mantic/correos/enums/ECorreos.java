package mx.org.kaana.mantic.correos.enums;

import mx.org.kaana.libs.recurso.TcConfiguraciones;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 23/03/2019
 *@time 01:08:38 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum ECorreos {
	
  FACTURACION   ("/mx/org/kaana/mantic/correos/templates/facturacion.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "facturas@cafuconstrucciones.com", "Facturas CAFU", ""), 
	COTIZACIONES  ("/mx/org/kaana/mantic/correos/templates/cotizacion.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "ventas@cafuconstrucciones.com", "Ventas CAFU", "compras2@cafuconstrucciones.com"),
	ORDENES_COMPRA("/mx/org/kaana/mantic/correos/templates/ordenes.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "compras@cafuconstrucciones.com", "Compras CAFU.", "compras2@cafuconstrucciones.com"),
	CUENTAS       ("/mx/org/kaana/mantic/correos/templates/cuentas.html", "resources/janal/img/sistema/", "correo.admin.user", "correo.admin.pass", "ventas@cafuconstrucciones.com", "Ventas CAFU", ""),
	//cafu
	VENTAS        ("/mx/org/kaana/keet/correos/templates/ventas.html", "resources/janal/img/sistema/", "correo.ventas.user.produccion", "correo.ventas.pass.produccion", "ventas@cafu.jvmhost.net", "Ventas CAFU.", ""),
	COMPRAS       ("/mx/org/kaana/keet/correos/templates/compras.html", "resources/janal/img/sistema/", "correo.compras.user.produccion", "correo.compras.pass.produccion", "compras@cafu.jvmhost.net", "Compras CAFU.", ""),
	ADMINISTRACION("/mx/org/kaana/keet/correos/templates/administracion.html", "resources/janal/img/sistema/", "correo.admin.user.produccion", "correo.admin.pass.produccion", "administracion@cafu.jvmhost.net", "Administración CAFU.", ""),
	RESIDENTE     ("/mx/org/kaana/keet/correos/templates/residente.html", "resources/janal/img/sistema/", "correo.admin.user.produccion", "correo.admin.pass.produccion", "administracion@cafu.jvmhost.net", "Administración CAFU.", ""),
  DESTAJOS      ("/mx/org/kaana/keet/correos/templates/destajos.html", "resources/janal/img/sistema/", "correo.admin.user.produccion", "correo.admin.pass.produccion", "administracion@cafuconstrucciones.com", "Administración CAFU.", "carlos.calderon@cafuconstrucciones.com, jjose.fuentes@cafuconstrucciones.com, jimenez76@yahoo.com");
	 
	private String template;
	private String images;
	private String user;
	private String password;
	private String email;
	private String alias;
  private String backup;

	private ECorreos(String template, String images, String user, String password, String email, String alias, String backup) {
		this.template= template;
		this.images  = images;
		this.user    = user;
		this.password= password;
		this.email   = email;
		this.alias   = alias;
    // AQUI SE AGREGA LA CUENTA DE CORREO DE jimenez76@yahoo.com QUE ESTA REGISTRADA EN LA BASE DE DATOS 
		// this.backup  = Cadena.isVacio(backup)? TcConfiguraciones.getInstance().getPropiedad(Constantes.EMAILS_@BACKUP_SYSTEM): backup.concat(",").concat(TcConfiguraciones.getInstance().getPropiedad(Constantes.EMAILS_BACKUP_SYSTEM));
		this.backup  = backup;
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
		return email;
	}

	public String getAlias() {
		return alias;
	}

  public String getBackup() {
    return backup;
  }
  
}