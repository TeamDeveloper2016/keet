package mx.org.kaana.keet.catalogos.contratos.destajos.comun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Codigo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.mantic.catalogos.comun.MotorBusquedaCatalogos;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorTipoContacto;
import mx.org.kaana.mantic.correos.beans.Attachment;
import mx.org.kaana.mantic.correos.enums.ECorreos;
import mx.org.kaana.mantic.correos.reglas.IBaseAttachment;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.facturas.beans.Correo;
import mx.org.kaana.mantic.ventas.reglas.MotorBusqueda;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class IBaseReporteDestajos extends IBaseFilter implements Serializable {
	
	private static final Log LOG              = LogFactory.getLog(IBaseReporteDestajos.class);
	private static final long serialVersionUID= 831285430730032608L;
	protected Reporte reporte;
	protected List<Correo> correos;
	protected List<Correo> selectedCorreos;	
	protected Correo correo;	
	
	public Reporte getReporte() {
		return reporte;
	}
	
	public List<Correo> getCorreos() {
		return correos;
	}

	public void setCorreos(List<Correo> correos) {
		this.correos = correos;
	}	
	
	public List<Correo> getSelectedCorreos() {
		return selectedCorreos;
	}

	public void setSelectedCorreos(List<Correo> selectedCorreos) {
		this.selectedCorreos = selectedCorreos;
	}	

	public Correo getCorreo() {
		return correo;
	}

	public void setCorreo(Correo correo) {
		this.correo = correo;
	}
	
	public void initBase() {
		this.correos        = new ArrayList<>();
		this.selectedCorreos= new ArrayList<>();
	} // initBase
	
	public void doSendMail(String reporte) {		
		Map<String, Object> params= null;
		String emails             = null;
		List<Attachment> files    = null; 
		IBaseAttachment notificar = null;
		Attachment attachments    = null;
		StringBuilder sb          = null;
		try {
			params= new HashMap<>();
			sb= new StringBuilder("");
			if(this.selectedCorreos!= null && !this.selectedCorreos.isEmpty()) {
				for(Correo mail: this.selectedCorreos) {
					if(!Cadena.isVacio(mail.getDescripcion()))
						sb.append(mail.getDescripcion()).append(",");
				} // for
			} // if
			emails= sb.length()> 0? sb.substring(0, sb.length()- 1) : "";
			params.put("header", "...");
			params.put("footer", "...");
			params.put("empresa", JsfBase.getAutentifica().getEmpresa().getNombre());			
			params.put("personalDestajo", this.attrs.get("figuraNombreCompletoCorreo"));
			params.put("correo", ECorreos.ADMINISTRACION.getEmail());
      params.put("solucion", Configuracion.getInstance().getEmpresa("titulo"));
      params.put("url", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));
			this.doReporte(reporte, true);
			params.put("tipo", "Reporte - ".concat(this.attrs.get("tituloCorreo").toString()));			
			attachments= new Attachment(this.reporte.getNombre(), false);
			files= new ArrayList<>();
			files.add(attachments);
			files.add(new Attachment("logo", ECorreos.ADMINISTRACION.getImages().concat(Configuracion.getInstance().getEmpresa("logo")), true));
			params.put("attach", attachments.getId());
      try {
        if(!Cadena.isVacio(emails)) {
          notificar= new IBaseAttachment(ECorreos.ADMINISTRACION, ECorreos.ADMINISTRACION.getEmail(), emails, ECorreos.ADMINISTRACION.getBackup(), Configuracion.getInstance().getEmpresa("titulo").concat(" - ").concat(this.attrs.get("tituloCorreo").toString()), params, files);
          LOG.info("Enviando correo a la cuenta: " + emails);
          notificar.send();
        } // if	
      } // try
      finally {
        if(attachments.getFile().exists()) 
          LOG.info("Eliminando archivo temporal: " + attachments.getAbsolute());				  
      } // finally	
	  	LOG.info("Se envio el correo de forma exitosa");
			if(sb.length()> 0)
		    JsfBase.addMessage("Se envió el correo de forma exitosa", ETipoMensaje.INFORMACION);
			else
		    JsfBase.addMessage("No se selecciono ningún correo, por favor verifiquelo e intente de nueva cuenta", ETipoMensaje.ALERTA);
		} // try // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(files);
		} // finally
	} // doSendMail
  
	public void doSendMailResidente(String reporte) {		
		Map<String, Object> params= null;
		String[] emails           = null;
		List<Attachment> files    = null; 
		IBaseAttachment notificar = null;
		Attachment attachments    = null;
		StringBuilder sb          = null;
		try {
			params= new HashMap<>();
			sb= new StringBuilder("");
			if(this.selectedCorreos!= null && !this.selectedCorreos.isEmpty()) {
				for(Correo mail: this.selectedCorreos) {
					if(!Cadena.isVacio(mail.getDescripcion()))
						sb.append(mail.getDescripcion()).append(",");
				} // for
			} // if
			emails= new String[]{(sb.length()> 0? sb.substring(0, sb.length()- 1) : "")};		
			params.put("header", "...");
			params.put("footer", "...");
			params.put("empresa", JsfBase.getAutentifica().getEmpresa().getNombre());			
			params.put("personalDestajo", this.attrs.get("figuraNombreCompletoCorreo"));
			params.put("correo", ECorreos.RESIDENTE.getEmail());
      params.put("solucion", Configuracion.getInstance().getEmpresa("titulo"));
      params.put("url", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));
			this.doReporte(reporte, true);
			params.put("tipo", "Reporte - ".concat(this.attrs.get("tituloCorreo").toString()));			
			attachments= new Attachment(this.reporte.getNombre(), false);
			files= new ArrayList<>();
			files.add(attachments);
			files.add(new Attachment("logo", ECorreos.RESIDENTE.getImages().concat(Configuracion.getInstance().getEmpresa("logo")), true));
			params.put("attach", attachments.getId());
			for (String item: emails) {
				try {
					if(!Cadena.isVacio(item)) {
					  notificar= new IBaseAttachment(ECorreos.RESIDENTE, ECorreos.RESIDENTE.getEmail(), item, ECorreos.RESIDENTE.getBackup(), Configuracion.getInstance().getEmpresa("titulo").concat(" - ").concat(this.attrs.get("tituloCorreo").toString()), params, files);
					  LOG.info("Enviando correo a la cuenta: " + item);
					  notificar.send();
					} // if	
				} // try
				finally {
				  if(attachments.getFile().exists()) 
   	  	    LOG.info("Eliminando archivo temporal: " + attachments.getAbsolute());				  
				} // finally	
			} // for
	  	LOG.info("Se envio el correo de forma exitosa");
			if(sb.length()> 0)
		    JsfBase.addMessage("Se envió el correo de forma exitosa", ETipoMensaje.INFORMACION);
			else
		    JsfBase.addMessage("No se selecciono ningún correo, por favor verifiquelo e intente de nueva cuenta", ETipoMensaje.ALERTA);
		} // try // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(files);
		} // finally
	} // doSendMail
  
	public abstract void doReporte(String nombre, boolean sendMail) throws Exception;
	
	public boolean doVerificarReporte() {
    boolean regresar = false;
		if(this.reporte.getTotal()> 0L) {
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");	
      regresar = true;
    }
		else {
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
      regresar = false;
		} // else
    return regresar;
	} // doVerificarReporte	
	
	public void doAgregarCorreo() {		
		Transaccion transaccion= null;
		try {
			if(!Cadena.isVacio(getCorreo().getDescripcion())){				
				transaccion= new Transaccion(Long.valueOf(this.attrs.get("idFiguraCorreo").toString()), Long.valueOf(this.attrs.get("idTipoFiguraCorreo").toString()), this.correo);
				if(transaccion.ejecutar(EAccion.COMPLEMENTAR))
					JsfBase.addMessage("Se agrego el correo electronico correctamente !");
				else
					JsfBase.addMessage("Ocurrió un error al agregar el correo electronico");
			} // if
			else
				JsfBase.addMessage("Es necesario capturar un correo electronico !");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAgregarCorreo
	
	public void doLoadEstatus() {
		MotorBusquedaCatalogos motor                 = null; 
		List<PersonaTipoContacto>contactosPersona    = null;
		List<ProveedorTipoContacto>contactosProveedor= null;
		Correo correo                                = null;
		try {
			motor= new MotorBusqueda(-1L, Long.valueOf((String)this.attrs.get("idFiguraCorreo")));
			LOG.warn("Inicializando listas de correos y seleccionados");
			this.correos.clear();
			this.selectedCorreos.clear();			
			if(((Long)this.attrs.get("idTipoFiguraCorreo")).equals(1L)) {
				contactosPersona= motor.toPersonaContacto(Long.valueOf((String)this.attrs.get("idFiguraCorreo")));
				LOG.warn("Total de contactos: " + contactosPersona.size());
				for(PersonaTipoContacto contacto: contactosPersona) {
					if(contacto.getIdTipoContacto().equals(ETiposContactos.CORREO.getKey()) || contacto.getIdTipoContacto().equals(ETiposContactos.CORREO_PERSONAL.getKey())) {
						correo= new Correo(contacto.getIdPersonaTipoContacto(), contacto.getValor().toUpperCase(), contacto.getIdPreferido());
						this.getCorreos().add(correo);		
						this.getSelectedCorreos().add(correo);
					} // if
				} // for
			} // if
      else {
				contactosProveedor= motor.toProveedoresTipoContacto();
				LOG.warn("Total de contactos: " + contactosProveedor.size());
				for(ProveedorTipoContacto contacto: contactosProveedor){
					if(contacto.getIdTipoContacto().equals(ETiposContactos.CORREO.getKey())) {
						correo= new Correo(contacto.getIdProveedorTipoContacto(), contacto.getValor().toUpperCase(), contacto.getIdPreferido());
						this.getCorreos().add(correo);		
						this.getSelectedCorreos().add(correo);
					} // if
				} // for
			} // else			
			if(this.attrs.get("idDesarrollo")!= null && (Long)this.attrs.get("idDesarrollo")> 0L) {
				contactosPersona= motor.toResidenteContacto((Long)this.attrs.get("idDesarrollo"));
				LOG.warn("Total de residentes: " + contactosPersona.size());
				for(PersonaTipoContacto contacto: contactosPersona) {
					if(contacto.getIdTipoContacto().equals(ETiposContactos.CORREO.getKey()) || contacto.getIdTipoContacto().equals(ETiposContactos.CORREO_PERSONAL.getKey())) {
						correo= new Correo(contacto.getIdPersonaTipoContacto(), contacto.getValor().toUpperCase(), contacto.getIdPreferido());
						this.getCorreos().add(correo);		
						this.getSelectedCorreos().add(correo);
					} // if
				} // for
      } // if
			LOG.warn("Agregando correo por defecto");
			this.getCorreos().add(new Correo(-1L, "", 2L));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doLoadEstatus
	
	@Override
	protected void finalize() throws Throwable {
    super.finalize();
		Methods.clean(this.correos);
		Methods.clean(this.selectedCorreos);
	}	// finalize

  protected void prepare(List<Codigo> model, List<Entity> lotes) throws Exception {
    model.clear();
    try {      
      if(lotes!= null && !lotes.isEmpty()) {
        int count     = 0;
        String partida= "";
        String clave  = "";
        for (Entity codigo: lotes) {
          clave= Cadena.rellenar(String.valueOf(count), 4, '0', true);
          if(codigo.toString("codigo").trim().startsWith("#")) {
            partida   = codigo.toString("codigo");
            String pre= partida.substring(0, partida.lastIndexOf("A")+ 1);
            String pos= partida.substring(partida.lastIndexOf("A")+ 1);
            if(pos.trim().length()== 1)
              partida= pre.concat("0").concat(pos);
            clave= "";
          } // if  
          Codigo concepto= new Codigo(count, partida.concat(Constantes.SEPARADOR).concat(clave), codigo.toString("codigo"), codigo.toString("nombre"));
          int index= model.indexOf(concepto);
          if(index< 0) {
            model.add(concepto);
            count++;
          } // if  
        } // for
        Collections.sort(model);
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
}
