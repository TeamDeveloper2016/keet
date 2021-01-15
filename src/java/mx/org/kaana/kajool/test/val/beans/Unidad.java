package mx.org.kaana.kajool.test.val.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static mx.org.kaana.kajool.test.val.beans.Reactivo.CANTIDAD_DE_DECIMALES;
import mx.org.kaana.libs.formato.Numero;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company INEGI
 *@project IKTAN (Sistema de seguimiento y control de proyectos)
 *@date 14/01/2021
 *@time 10:34:05 PM 
 *@author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */

public class Unidad extends Reactivo implements Serializable {

  private static final Log LOG = LogFactory.getLog(Unidad.class);
  private static final long serialVersionUID = 229643919523145705L;
  
  private List<Evaluacion> evaluaciones;
  private Double suma;

  public Unidad() {
    this(new ArrayList<Evaluacion>());
  }
  
  public Unidad(List<Evaluacion> evaluaciones) {
    super(0D, 100D);
    this.evaluaciones= new ArrayList<>();
    this.evaluaciones.addAll(evaluaciones);
    this.suma= 0D;
  }

  public Double getSuma() {
    return suma;
  }
  
  public void add(Evaluacion evaluacion) {
    this.evaluaciones.add(evaluacion);
  }
  
  public void delete(Evaluacion evaluacion) {
    if(this.evaluaciones.contains(evaluacion))
      this.evaluaciones.remove(evaluacion);
  }
  
  public void clean() {
    this.evaluaciones.clear();
  }
  
  @Override
  public Double execute() {
    for (Evaluacion item : this.evaluaciones) {
      this.valor+= item.execute();
      this.suma+= item.ponderador;
    } // for
    this.valor     = Numero.getDouble(Numero.toTruncate(this.valor, CANTIDAD_DE_DECIMALES), 0D);
    this.porcentaje= Numero.getDouble(Numero.toTruncate((this.valor* this.ponderador)/ this.suma, CANTIDAD_DE_DECIMALES), 0D);
    LOG.info("Unidad: "+ this.valor+ ", "+ this.ponderador+ ", "+ this.suma+ " >>>["+ this.porcentaje+ "]");
    return this.porcentaje;
  }
    

}
