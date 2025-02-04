package mx.org.kaana.keet.nomina.functions;

import com.eteks.parser.Function;
import com.eteks.parser.Interpreter;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 28/04/2020
 *@time 11:05:31 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Redondea implements Function, Serializable {

  private static final long serialVersionUID = 4459440093816607684L;

  @Override
	public String getName() {
		return "ROUND";
	}

  @Override
	public boolean isValidParameterCount(int parameterCount) {
		return parameterCount==1; // At least one parameter
	}

  @Override
	public Object computeFunction(Interpreter interpreter, Object[] parametersValue) {
		double value=0;
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
			int operador=value<0 ? -1: 1;
			value=operador*(Math.floor(Math.abs(value)*100+0.5001)/100.0);
		} // if
		else 
			value= 0D;
		return value;
	}

}
