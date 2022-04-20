package mx.org.kaana.libs.reportes.scriptlets;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22/09/2015
 *@time 08:36:21 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.reflection.Methods;
import net.sf.jasperreports.engine.JRScriptletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Vencido extends BarraProgreso implements Serializable {

  private static final Log LOG              = LogFactory.getLog(Vencido.class);
	private static final long serialVersionUID= 3047108539345686661L;

  private BigDecimal vencido;

  public BigDecimal getVencido() {
    return vencido;
  }
  
  @Override
  public void beforePageInit() throws JRScriptletException {
    super.beforePageInit(); 
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idContrato", this.checkField("ID_CONTRATO", -1L));      
      Value value= (Value)DaoFactory.getInstance().toField("VistaEstimacionesDto", "vencido", params, "vencido");
      if(value== null || value.getData()== null)
        this.vencido= new BigDecimal(0D);
      else
        this.vencido= (BigDecimal)value.getData();
      // this.variableMapWrapper.put("VENCIDO", this.vencido);
      // this.setVariableValue("VENCIDO", this.vencido);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
	@Override
  public void beforeDetailEval() throws JRScriptletException {
    super.beforeDetailEval();
  }

}
