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
 *@time 09:45:01 PM 
 *@author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */

public class Factor extends Reactivo implements Serializable {

  private static final Log LOG = LogFactory.getLog(Factor.class);
  private static final long serialVersionUID = 3554149372212302871L;
  
  private List<Reactivo> reactivos;
  private Double suma;

  public Factor() {
    this(new ArrayList<Reactivo>(), 0D);
  }
  
  public Factor(List<Reactivo> reactivos, Double ponderador) {
    super(0D, ponderador);
    this.reactivos= new ArrayList<>();
    this.reactivos.addAll(reactivos);
    this.suma= 0D;
  }

  public Double getSuma() {
    return suma;
  }
  
  public void add(Reactivo reactivo) {
    this.reactivos.add(reactivo);
  }
  
  public void delete(Reactivo reactivo) {
    if(this.reactivos.contains(reactivo))
      this.reactivos.remove(reactivo);
  }
  
  public void clean() {
    this.reactivos.clear();
  }
  
  @Override
  public Double execute() {
    for (Reactivo item: this.reactivos) {
      this.valor+= item.execute();
      this.suma+= item.ponderador;
    } // for
    this.valor     = Numero.getDouble(Numero.toTruncate(this.valor, CANTIDAD_DE_DECIMALES), 0D);
    this.porcentaje= Numero.getDouble(Numero.toTruncate((this.valor* this.ponderador)/ this.suma, CANTIDAD_DE_DECIMALES), 0D);
    LOG.info("Factor: "+ this.valor+ ", "+ this.ponderador+ ", "+ this.suma+ " >["+ this.porcentaje+ "]");
    return this.porcentaje;
  }
  
}
