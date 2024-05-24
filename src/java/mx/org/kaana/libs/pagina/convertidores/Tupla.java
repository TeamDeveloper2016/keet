package mx.org.kaana.libs.pagina.convertidores;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date Apr 26, 2012
 * @time 12:50:07 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@FacesConverter(value="janal.convertidor.Tupla")
public class Tupla implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    Entity regresar= new Entity(-1L);
    if(value!= null)
      try {
        regresar= new Entity(Numero.getLong(value, -1L));
      } // try
      catch(Exception e) {
        Error.mensaje(e);
        throw new ConverterException(e);
      } // catch
    return regresar;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    String regresar= null;
    if(value!= null)
      try {
        regresar= value.toString();
      } // try
      catch(Exception e) {
        Error.mensaje(e);
        throw new ConverterException(e);
      } // catch
    return regresar;
  }
	
}
