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
 *@time 09:48:08 PM 
 *@author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */

public class Evaluacion extends Reactivo implements Serializable {

  private static final Log LOG = LogFactory.getLog(Evaluacion.class);
  private static final long serialVersionUID = -985697830722225066L;

  private List<Factor> factores;
  private Double suma;

  public Evaluacion() {
    this(new ArrayList<Factor>(), 0D);
  }
  
  public Evaluacion(List<Factor> factores, Double ponderador) {
    super(0D, ponderador);
    this.factores= new ArrayList<>();
    this.factores.addAll(factores);
    this.suma= 0D;
  }

  public Double getSuma() {
    return suma;
  }
  
  public void add(Factor factor) {
    this.factores.add(factor);
  }
  
  public void delete(Factor factor) {
    if(this.factores.contains(factor))
      this.factores.remove(factor);
  }
  
  public void clean() {
    this.factores.clear();
  }
  
  @Override
  public Double execute() {
    for (Factor item : this.factores) {
      this.valor+= item.execute();
      this.suma+= item.ponderador;
    } // for
    this.valor     = Numero.getDouble(Numero.toTruncate(this.valor, CANTIDAD_DE_DECIMALES), 0D);
    this.porcentaje= Numero.getDouble(Numero.toTruncate((this.valor* this.ponderador)/ this.suma, CANTIDAD_DE_DECIMALES), 0D);
    LOG.info("Evaluación: "+ this.valor+ ", "+ this.ponderador+ ", "+ this.suma+ " >>["+ this.porcentaje+ "]");
    return this.porcentaje;
  }
  
}
