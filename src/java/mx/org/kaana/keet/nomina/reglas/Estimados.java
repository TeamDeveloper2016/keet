package mx.org.kaana.keet.nomina.reglas;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.keet.nomina.beans.Criterio;
import mx.org.kaana.libs.formato.Numero;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/06/2022
 *@time 07:42:00 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Estimados extends Egresos implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(Estimados.class);
  private static final long serialVersionUID = -3364636967222678893L;

  public Estimados() {
    this(-1L);  
  }

  public Estimados(Long idContrato) {
    super(-1L, idContrato);
    this.idXml= "estimados";
  }

  @Override
  protected void toAddConceptoNoPagado() throws Exception {
    try {
      this.addCell(this.posicionColumna, this.posicionFila, "CONCEPTOS:");
      this.addCellColor(this.posicionColumna+ 1, this.posicionFila++, "LOS IMPORTES EN FONDO NARANJA NO ESTAN PAGADOS", jxl.format.Colour.ORANGE);
      this.posicionFila++;    
    } // try
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  @Override
  protected void isConceptoPagado(int columna, int fila, Criterio criterio) throws Exception {
    String costo= Numero.formatear(Numero.MILES_SAT_DECIMALES, criterio.getCosto());
    if(Objects.equals(criterio.getActual(), 1L))
      this.addCellColor(columna, fila, costo, jxl.format.Colour.ORANGE);
    else
      this.addCellCosto(columna, fila, costo);
  }
  
  public static void main(String ... args) throws Exception {
    Estimados corte= new Estimados(5L);
    LOG.info(corte.local());
  }
  
}
