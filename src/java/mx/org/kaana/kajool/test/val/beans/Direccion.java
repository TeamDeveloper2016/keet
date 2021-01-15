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
 *@date 15/01/2021
 *@time 08:41:14 AM 
 *@author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */

public class Direccion extends Reactivo implements Serializable {

  private static final Log LOG = LogFactory.getLog(Direccion.class);
  private static final long serialVersionUID = 1L;

  List<Reactivo> direcciones;
  private Double suma;
  
  public Direccion() {
    this(new ArrayList<Reactivo>(), 0D);
  }
  
  public Direccion(List<Reactivo> direcciones, Double ponderador) {
    super(0D, ponderador);
    this.direcciones= new ArrayList<>();
    this.direcciones.addAll(direcciones);
    this.suma= 0D;
  }
  
  public Double getSuma() {
    return suma;
  }
  
  public void add(Reactivo reactivo) {
    this.direcciones.add(reactivo);
  }
  
  public void delete(Reactivo reactivo) {
    if(this.direcciones.contains(reactivo))
      this.direcciones.remove(reactivo);
  }
  
  public void clean() {
    this.direcciones.clear();
  }
  
  @Override
  public Double execute() {
    int count     = 0;
    Double calculo= ((int)(100/ this.direcciones.size())* 1D);
    for (Reactivo item : this.direcciones) {
      item.ponderador= (count== this.direcciones.size()-1)? 100- this.suma: calculo;
      this.valor+= item.execute(this.ponderador);
      this.suma+= item.ponderador;
      count++;
    } // for
    this.valor     = Numero.getDouble(Numero.toTruncate(this.valor, CANTIDAD_DE_DECIMALES), 0D);
    this.porcentaje= Numero.getDouble(Numero.toTruncate((this.valor* this.ponderador)/ this.suma, CANTIDAD_DE_DECIMALES), 0D);
    LOG.info("Dirección: "+ this.valor+ ", "+ this.ponderador+ ", "+ this.suma+ " >>>["+ this.porcentaje+ "]");
    return this.porcentaje;
  }  
}
