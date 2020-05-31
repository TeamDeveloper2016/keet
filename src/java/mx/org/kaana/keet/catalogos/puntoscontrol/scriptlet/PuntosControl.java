package mx.org.kaana.keet.catalogos.puntoscontrol.scriptlet;


import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.reportes.scriptlets.BarraProgreso;
import net.sf.jasperreports.engine.JRScriptletException;

public class PuntosControl extends BarraProgreso implements Serializable{
  

  @Override
  public void afterDetailEval() throws JRScriptletException {
    super.afterDetailEval();
    List<Entity> rubros	      = null;
    List<Entity> puntos	      = null;
		Map<String, Object> params= null;
    StringBuilder sb = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();
    StringBuilder sb3 = new StringBuilder();
		try {
			params = new HashMap<String, Object>();
			params.put("idPuntoGrupo", getFieldValue("ID_PUNTO_GRUPO"));
			params.put("idDepartamento", getFieldValue("ID_DEPARTAMENTO"));
      sb.append("\n");
      sb2.append("\n");
      sb3.append("\n");
			rubros = DaoFactory.getInstance().toEntitySet("VistaPuntosControlReporteDto", "rubros", params);
			puntos = DaoFactory.getInstance().toEntitySet("VistaPuntosControlReporteDto", "puntos", params);
      for(Entity rubro : rubros)
        sb.append("  - ".concat(rubro.toString("concepto").concat("\n")));
      
      for(Entity punto : puntos){
        sb2.append(("  * ").concat(punto.toString("descripcion").concat("\n")));
        sb3.append(String.format("%.0f", punto.toDouble("factor")).concat("\n"));
      }
      sb.append("\n");
      sb2.append("\n");
      sb3.append("\n");
			setVariableValue("RUBROS", sb.toString());
			setVariableValue("PUNTOS", sb2.toString());
			setVariableValue("FACTORES", sb3.toString());
			setVariableValue("FECHA", getFecha(7, new java.util.Date()));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
			Methods.clean(rubros);
		}//finally
  }
  
}
