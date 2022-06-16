package mx.org.kaana.keet.catalogos.contratos.destajos.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 12/01/2022
 *@time 04:56:23 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Concepto extends HashMap<String, Object> implements Comparable<Concepto>, Serializable {
 
  private static final long serialVersionUID = -4854453835524121298L;
 
  private int id;  
  private String codigo;  
  private String nombre;  
  private Long idTipo;  
  private Long idExtra;  

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

  public Concepto(String codigo) {
    this(-1, codigo, null);
  }

  public Concepto(int id, String codigo, String nombre) {
    this(id, codigo, nombre, -1L, -1L);
  }

  public Concepto(int id, String codigo, String nombre, Long idTipo, Long idExtra) {
    this.id     = id;
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

  public String getCodigo() {
    return codigo;
  }

  public String getNombre() {
    return nombre;
  }

  @Override
  public int compareTo(Concepto concepto) {
    if(this.id> concepto.id) 
      return 1;  
    else 
      if(this.id< concepto.id) 
        return -1;  
      else
        return 0;  
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
    final Concepto other = (Concepto) obj;
    if (!Objects.equals(this.codigo, other.codigo)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Concepto{"+ "id="+ id+ ", codigo="+ codigo+ ", nombre="+ nombre+ ", idTipo="+ idTipo+ ", idExtra="+ idExtra+ '}'+ super.toString();
  }
  
}
