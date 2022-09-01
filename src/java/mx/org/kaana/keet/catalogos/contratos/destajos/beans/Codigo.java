package mx.org.kaana.keet.catalogos.contratos.destajos.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 01/09/2022
 *@time 07:36:23 AM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Codigo extends HashMap<String, Object> implements Comparable<Codigo>, Serializable {
 
  private static final long serialVersionUID = -4854453835524123298L;
 
  private int id;  
  private String clave;  
  private String codigo;  
  private String nombre;  
  private Long idTipo;  
  private Long idExtra;  

 public Codigo(String codigo) {
    this(0, "", codigo, null);
  }
  
  public Codigo(int id, String clave, String codigo, String nombre) {
    this(id, clave, codigo, nombre, -1L, -1L);
  }
  
  public Codigo(int id, String clave, String codigo, String nombre, Long idTipo, Long idExtra) {
    this.id     = id;
    this.clave  = clave;
    this.codigo = codigo;
    this.nombre = nombre;
    this.idTipo = idTipo;
    this.idExtra= idExtra;
    this.put("codigo", codigo);
    this.put("nombre", nombre);
  }

  public int getId() {
    return id;
  }
  
  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public Long getIdTipo() {
    return idTipo;
  }

  public void setIdTipo(Long idTipo) {
    this.idTipo = idTipo;
  }

  public Long getIdExtra() {
    return idExtra;
  }

  public void setIdExtra(Long idExtra) {
    this.idExtra = idExtra;
  }

  @Override
  public int compareTo(Codigo concepto) {
    return concepto== null || this.clave== null || concepto.clave== null? 0: this.clave.compareTo(concepto.clave);  
  }   

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash+ Objects.hashCode(this.codigo);
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
    final Codigo other = (Codigo) obj;
    if (!Objects.equals(this.codigo, other.codigo)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Concepto{ " + "id="+ id+ ", codigo="+ codigo+ ", nombre="+ nombre+ ", idTipo="+ idTipo+ ", idExtra="+ idExtra+ '}'+ super.toString();
  }
  
}
