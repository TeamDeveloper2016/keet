package mx.org.kaana.sakbe.suministros.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "sakbeSuministrosVisor")
@ViewScoped 
public class Visor extends Filtro implements Serializable {

  private static final long serialVersionUID= 8793267741599428338L;
  
  public String doUltimaVez(Entity row) {
    String regresar= "Litros suministrados la última vez: <b>SIN REGISTRO</b>";
    List<Columna> columns     = null;    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("clave", row.toString("clave"));      
      params.put("idSuministro", row.toString("idSuministro"));      
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
  
  @Override
  protected void reset() {
    UIBackingUtilities.resetDataGrid();
  }

  @Override
  public String toPagina() {
		return "/Paginas/Sakbe/Suministros/visor";		
  } // toPagina()
  
}
