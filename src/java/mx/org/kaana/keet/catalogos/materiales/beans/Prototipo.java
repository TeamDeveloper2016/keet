package mx.org.kaana.keet.catalogos.materiales.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 2/03/2023
 *@time 08:56:40 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Prototipo implements Serializable {

  private static final long serialVersionUID = -4159741716059462698L;

  private Long idPrototipo;
  private String nombre;

  public Prototipo(String nombre) {
    this(null, nombre);
  }

  public Prototipo(Long idPrototipo, String nombre) {
    this.idPrototipo = idPrototipo;
    this.nombre = nombre;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + Objects.hashCode(this.nombre);
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
    final Prototipo other = (Prototipo) obj;
    if (!Objects.equals(this.nombre, other.nombre)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Prototipo{" + "idPrototipo=" + idPrototipo + ", nombre=" + nombre + '}';
  }
  
}
