package mx.org.kaana.sakbe.suministros.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "sakeSuministrosLector")
@ViewScoped
public class Lector extends IBaseFilter implements Serializable {  

	private static final long serialVersionUID = 5461370708574838211L;

  @PostConstruct
  @Override
  protected void init() {		    
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());							
      this.attrs.put("opcionResidente", JsfBase.getFlashAttribute("opcionResidente"));
      this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idDesarrollo"));
      this.attrs.put("idTipoCombustible", JsfBase.getFlashAttribute("idTipoCombustible"));
      this.attrs.put("idSuministro", JsfBase.getFlashAttribute("idSuministro"));
      this.attrs.put("porcentaje", JsfBase.getFlashAttribute("porcentaje"));
			this.attrs.put("seguimiento", JsfBase.getFlashAttribute("seguimiento"));
			this.attrs.put("regresar", JsfBase.getFlashAttribute("regresar"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "accion": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("desarrollo", JsfBase.getFlashAttribute("desarrollo"));
      
			this.attrs.put("codigoQR", "");    						
			this.attrs.put("habilitar", Boolean.FALSE);
			this.attrs.put("existe", Boolean.FALSE);
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init		
	
  @Override
  public void doLoad() {		
		String[] codigos          = null;
		Entity maquinaria         = null;
		Map<String, Object> params= new HashMap<>();
    try {   						      		
      this.attrs.put("maquinaria", null);
      this.attrs.put("ultimo", null);
			this.attrs.put("habilitar", Boolean.FALSE);
		  this.attrs.put("existe", Boolean.FALSE);
		  this.attrs.put("clave", this.attrs.get("codigoQR"));
			codigos= ((String)this.attrs.get("codigoQR")).split("[|]");
			if(codigos!= null && codigos.length> 1) {
        String clave= codigos[1].replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", "");
  		  this.attrs.put("existe", clave);
				params.put("clave", clave);
				maquinaria= (Entity) DaoFactory.getInstance().toEntity("TcSakbeMaquinariasDto", "clave", params);
				this.attrs.put("existe", maquinaria== null);
				if(maquinaria!= null && !maquinaria.isEmpty()) {
					this.attrs.put("habilitar", 
            Objects.equals(maquinaria.toLong("idDesarrollo"), (Long)this.attrs.get("idDesarrollo")) &&
            Objects.equals(maquinaria.toLong("idTipoCombustible"), (Long)this.attrs.get("idTipoCombustible")) 
          );
          if(!Objects.equals(maquinaria.toLong("idDesarrollo"), (Long)this.attrs.get("idDesarrollo")))
            JsfBase.addMessage("Precación", "Esta maquinaria esta asignada al desarrollo: "+ maquinaria.toString("desarrollo"));
          else
            if(!Objects.equals(maquinaria.toLong("idTipoCombustible"), (Long)this.attrs.get("idTipoCombustible")))
              JsfBase.addMessage("Precación", "Esta maquinaria usa combustible: "+ maquinaria.toString("combustible"));
          this.attrs.put("maquinaria", maquinaria);
          this.attrs.put("ultimo", this.toUltimaVez(clave));
				} // if
        else
    		  this.attrs.put("existe", Boolean.TRUE);
			} // if
      else 
  		  this.attrs.put("existe", Boolean.TRUE);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad			
	
	public String doAceptar() {
    String regresar= null;    				
    try {						
  		regresar= this.doCancelar();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doAceptar
	
	public String doCancelar() {
		JsfBase.setFlashAttribute("accion", this.attrs.get("accion"));		
    JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
    JsfBase.setFlashAttribute("ikTipoCombustible", this.attrs.get("idTipoCombustible"));
    JsfBase.setFlashAttribute("idSuministro", this.attrs.get("idSuministro"));
    JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));			
    JsfBase.setFlashAttribute("seguimiento", this.attrs.get("seguimiento"));
    JsfBase.setFlashAttribute("porcentaje", this.attrs.get("porcentaje"));
    JsfBase.setFlashAttribute("retorno", this.attrs.get("regresar"));
    JsfBase.setFlashAttribute("ikMaquinaria", this.attrs.get("maquinaria")== null? -1L: ((Entity)this.attrs.get("maquinaria")).toLong("idMaquinaria"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar						
  
  private String toUltimaVez(String clave) {
    String regresar= "Litros suministrados la última vez: <b>SIN REGISTRO</b>";
    List<Columna> columns     = null;    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("clave", clave);      
      params.put("idSuministro", this.attrs.get("idSuministro"));      
      columns = new ArrayList<>();
      columns.add(new Columna("litros", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.DIA_FECHA_HORA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      Entity entity = (Entity)DaoFactory.getInstance().toEntity("VistaSuministrosDto", "ultima", params);
      if(entity!= null) {
        UIBackingUtilities.toFormatEntity(entity, columns);
        regresar= "Litros sumnistrados la última vez: <b class='janal-color-blue'>".concat(entity.toString("litros")).concat(" ").concat(entity.toString("combustible")).concat("</b> | ").concat(entity.toString("registro")).concat("</b>").concat(" | ").concat(entity.toString("recibio"));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
    return regresar;
  }
  
}