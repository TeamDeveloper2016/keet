package mx.org.kaana.kajool.test.val.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.libs.formato.Numero;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company INEGI
 *@project IKTAN (Sistema de seguimiento y control de proyectos)
 *@date 14/01/2021
 *@time 09:44:15 PM 
 *@author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */

public class Reactivo implements Serializable {
  private static final Log LOG = LogFactory.getLog(Reactivo.class);

  private static final long serialVersionUID    = 2765904937224722784L;
  public static final int CANTIDAD_DE_DECIMALES= 4;
  public static final int VALOR_MAXIMO_REACTIVO= 5;
  
  protected Double ponderador;
  protected Double valor;
  protected Double porcentaje;

  public Reactivo(Double valor) {
    this(valor, 0D);
  }
  
  public Reactivo(Double valor, Double ponderador) {
    this.ponderador = ponderador;
    this.valor = valor;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }
  
  public Double execute(Double suma) {
    this.porcentaje= Numero.getDouble(Numero.toTruncate((this.valor* this.ponderador)/suma, CANTIDAD_DE_DECIMALES), 0D);
    LOG.info("Reactivo: "+ this.valor+ ", "+ this.ponderador+ ", "+ VALOR_MAXIMO_REACTIVO+ " ["+ this.porcentaje+ "]");
    return this.porcentaje;
  }
  
  public Double execute() {
    return this.execute(VALOR_MAXIMO_REACTIVO* 1D);
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 41 * hash + Objects.hashCode(this.ponderador);
    hash = 41 * hash + Objects.hashCode(this.valor);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Reactivo other = (Reactivo) obj;
    return true;
  }
  
}
