package mx.org.kaana.libs.pagina.convertidores;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import mx.org.kaana.libs.formato.Fecha;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date Apr 26, 2012
 * @time 12:50:07 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@FacesConverter("janal.convertidor.Calendario")
public class Calendario implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    Object date            = null;
    boolean conversionError= false;
    if (value == null || (value.trim().length() == 0)) {
      conversionError = true;
    } // if
    else {
      try {
        if(!value.contains(":"))
          date = Instant.ofEpochMilli(Fecha.getFechaHora(value).getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDate();
        else
          // suponiendo que me llega solo la hora HH:MM:SS concatenamos anio default de time 01/01/1970
          if(!value.contains("/"))
            date = Instant.ofEpochMilli(Fecha.getFechaHora("01/01/1970 ".concat(value)).getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalTime();
					else	
            date= Instant.ofEpochMilli(Fecha.getFechaHora("01/01/1970 ".concat(value)).getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
      } // try
      catch(Exception e) {
        conversionError = true;
      } // catch
    } // else
    if(conversionError) {
      FacesMessage msg = new FacesMessage("La fecha no se pudo convertir.",  "El formato de la fecha es invalido.["+ value+ "]");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
    } // if
    return date;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    String regresar = "El formato de la fecha es invalida";
    if(value instanceof LocalDate) {
      regresar = Fecha.formatear(Fecha.FECHA_CORTA, (LocalDate)value);
    } // if
		else
      if(value instanceof LocalTime) {
        regresar = Fecha.formatear(Fecha.HORA_CORTA, (LocalTime)value);
      } // if
      else
				if (value instanceof LocalDateTime) {
          regresar = Fecha.formatear(Fecha.FECHA_HORA, (LocalDateTime)value);
        } // else-if
    return regresar;
  }
	
}
