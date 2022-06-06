package mx.org.kaana.sakbe.combustibles.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.sakbe.db.dto.TcSakbeCombustiblesArchivosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 06/06/2022
 *@time 01:30:33 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Evidencia extends TcSakbeCombustiblesArchivosDto implements Serializable {

  private static final long serialVersionUID = -5760164295451267815L;
  
  private Long id;
  private Long idArchivo;
  private String usuario;
  private String tipo;
  private ESql sql;

  public Evidencia() {
    this(-1L);
  }

  public Evidencia(Long key) {
    this(key, null, null, null, null, null, null, null, null, null, null, null, null, null);
  }

  public Evidencia(Long idCombustibleArchivo, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String alias, String nombre, Long idCombustible, String observaciones, String usuario, Long idArchivo, String tipo) {
    super(archivo, eliminado, ruta, tamanio, idUsuario, idTipoArchivo, alias, idCombustibleArchivo, nombre, idCombustible);
    this.id= (new Random().nextLong())* -1L;
    this.usuario= usuario;
    this.idArchivo= idArchivo;
    this.tipo= tipo;
    this.sql= ESql.INSERT;
  }
 
  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public String getUsuario() {
    return usuario;
  }

  public void setUsuario(String usuario) {
    this.usuario = usuario;
  }

  public Long getIdArchivo() {
    return idArchivo;
  }

  public void setIdArchivo(Long idArchivo) {
    this.idArchivo = idArchivo;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + Objects.hashCode(this.id);
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
    final Evidencia other = (Evidencia) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }
  
}
