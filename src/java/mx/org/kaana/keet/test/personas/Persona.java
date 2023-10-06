package mx.org.kaana.keet.test.personas;

import java.io.Serializable;
import java.util.Objects;

public class Persona implements Serializable {

  private static final long serialVersionUID = -2882588108268660419L;
  
  private Long idEmpresaPersona;
  private Long idDesarrollo;

  public Persona() {
    this(-1L, -1L);
  }

  public Persona(Long idEmpresaPersona, Long idDesarrollo) {
    this.idEmpresaPersona = idEmpresaPersona;
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 43 * hash + Objects.hashCode(this.idEmpresaPersona);
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
    final Persona other = (Persona) obj;
    if (!Objects.equals(this.idEmpresaPersona, other.idEmpresaPersona)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Persona{" + "idEmpresaPersona=" + idEmpresaPersona + ", idDesarrollo=" + idDesarrollo + '}';
  }
  

}
