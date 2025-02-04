package mx.org.kaana.keet.nomina.functions;

import com.eteks.parser.Function;
import com.eteks.parser.Interpreter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.db.dto.TcKeetIsrSemanalesDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 28/04/2020
 *@time 11:05:31 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Isr implements Function, Serializable {

  private static final long serialVersionUID = -4326034551337192914L;

  private List<TcKeetIsrSemanalesDto> limites;
  
  public Isr(LocalDate fecha) {
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("fecha", Fecha.formatear(Fecha.FECHA_ESTANDAR, fecha)); 
      this.limites= (List<TcKeetIsrSemanalesDto>)DaoFactory.getInstance().findViewCriteria(TcKeetIsrSemanalesDto.class, params, "limites");
      if(Objects.equals(this.limites, null) || this.limites.isEmpty())
        throw new RuntimeException("NO EXISTEN LOS REGISTROS DE TABLAS DE ISR SEMANALES ["+ Fecha.formatear(Fecha.FECHA_ESTANDAR, fecha)+ " ]");
    } // try
    catch(Exception e) {
      Error.mensaje(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }

  @Override
	public String getName() {
		return "ISR";
	}

  @Override
	public boolean isValidParameterCount(int parameterCount) {
		return parameterCount==1; // At least one parameter
	}

  @Override
	public Object computeFunction(Interpreter interpreter, Object[] parametersValue) {
		double value    = 0D;
    double excedente= 0D;
    double tasa     = 0D;
		if (parametersValue[0] instanceof Double)
			value=(Double)parametersValue[0];
		else 
			if (parametersValue[0] instanceof Long)
			  value= ((Long)parametersValue[0]).doubleValue();
		  else 
  			if (parametersValue[0] instanceof Integer)
	  		  value= ((Integer)parametersValue[0]).doubleValue();
		    else 
    			if (parametersValue[0] instanceof BigDecimal)
	    		  value= ((BigDecimal)parametersValue[0]).doubleValue();
		      else 
			      throw new IllegalArgumentException(String.valueOf(parametersValue[0])+" not a number");
		if (value!=0) {
			// AQUI VAL EL CALCULO DEL ISR
      for (TcKeetIsrSemanalesDto item: this.limites) {
        if((value>= item.getInferior() && value<= item.getSuperior()) || Objects.equals(item.getSuperior(), 9999999.99D)) {
          excedente= value- item.getInferior();
          tasa     = excedente* (item.getPorcentaje()/ 100);
          value    = tasa+ item.getCuotaFija();
          break;
        } // if
      } // for
		} // if
		else 
			value= 0D;
		return value;
	}

}
