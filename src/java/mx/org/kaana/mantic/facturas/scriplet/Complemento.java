package mx.org.kaana.mantic.facturas.scriplet;

import java.io.Serializable;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Letras;
import mx.org.kaana.libs.reportes.scriptlets.BarraProgreso;
import net.sf.jasperreports.engine.JRScriptletException;

public class Complemento extends BarraProgreso implements Serializable {

	private static final long serialVersionUID= 6191179382089789177L;
	
  @Override
  public void afterDetailEval() throws JRScriptletException {
    super.afterDetailEval();
    Letras letras      = null;
		try {
      letras = new Letras();
      if(!getFieldValue("TOTAL_FINAL").toString().isEmpty())
        setVariableValue("LETRAS", letras.getMoneda(getFieldValue("TOTAL_FINAL").toString(), Boolean.FALSE));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch   
  }  
	
}
