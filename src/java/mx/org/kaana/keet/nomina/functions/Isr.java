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
  private LocalDate fecha;
  
  public Isr(LocalDate fecha) {
    Map<String, Object> params= new HashMap<>();
    try {
      this.fecha= fecha;
      params.put("fecha", Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fecha)); 
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
		return parameterCount== 3; // At least one parameter
	}

  @Override
	public Object computeFunction(Interpreter interpreter, Object[] parametersValue) {
    double value    = 0D;
    double dias     = 0D;
    double factorMes= 0D;
    double excedente= 0D;
    double tasa     = 0D;
    double aSubsidio= 0D;
    double subsidio = 0D;
    double isr      = 0D;
    if(Objects.equals(parametersValue.length, 3)) {
      value    = this.toValue(parametersValue[0]);
      dias     = this.toValue(parametersValue[1]);
      factorMes= this.toValue(parametersValue[2]);
      if (value!=0) {
        // AQUI VAL EL CALCULO DEL ISR
        for (TcKeetIsrSemanalesDto item: this.limites) {
          if((value>= item.getInferior() && value<= item.getSuperior()) || Objects.equals(item.getSuperior(), 9999999.99D)) {
            excedente= value- item.getInferior(); // excedente
            tasa     = excedente* (item.getPorcentaje()/ 100); // impuesto sobre excedente
            aSubsidio= tasa+ item.getCuotaFija(); // impuesto antes del subsidio
            value    = (aSubsidio/ factorMes)* dias; // isr del periodo
            subsidio = (subsidio/ this.fecha.lengthOfMonth())* dias; // subsidio del periodo
            isr      = aSubsidio- subsidio; // isr mensual
            value    = value> subsidio? value- subsidio: 0D; // isr a retener
            break;
          } // if
        } // for
      } // if
      else 
        value= 0D;
    } // if
    else
      throw new IllegalArgumentException("arguments is not correct");    
		return value;
	}

  private Double toValue(Object value) {
    Double regresar= 0D;
		if (value instanceof Double)
			regresar=(Double)value;
		else 
			if (value instanceof Long)
			  regresar= ((Long)value).doubleValue();
		  else 
  			if (value instanceof Integer)
	  		  regresar= ((Integer)value).doubleValue();
		    else 
    			if (value instanceof BigDecimal)
	    		  regresar= ((BigDecimal)value).doubleValue();
		      else 
			      throw new IllegalArgumentException(String.valueOf(value)+" not a number");
    return regresar;
  }
  
}
