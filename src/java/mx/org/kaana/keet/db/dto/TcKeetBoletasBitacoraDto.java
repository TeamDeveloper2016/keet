package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_keet_vales_bitacora")
public class TcKeetBoletasBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_boleta_bitacora")
  private Long idBoletaBitacora;
  @Column (name="id_boleta_estatus")
  private Long idBoletaEstatus;
  @Column (name="id_boleta")
  private Long idBoleta;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetBoletasBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetBoletasBitacoraDto(Long key) {
    this(null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetBoletasBitacoraDto(String justificacion, Long idUsuario, Long idBoletaBitacora, Long idBoletaEstatus, Long idBoleta) {
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdBoletaBitacora(idBoletaBitacora);
    setIdBoletaEstatus(idBoletaEstatus);
    setIdBoleta(idBoleta);
    setRegistro(LocalDateTime.now());
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdBoletaBitacora(Long idBoletaBitacora) {
    this.idBoletaBitacora = idBoletaBitacora;
  }

  public Long getIdBoletaBitacora() {
    return idBoletaBitacora;
  }

  public void setIdBoletaEstatus(Long idBoletaEstatus) {
    this.idBoletaEstatus = idBoletaEstatus;
  }

  public Long getIdBoletaEstatus() {
    return idBoletaEstatus;
  }

  public void setIdBoleta(Long idBoleta) {
    this.idBoleta = idBoleta;
  }

  public Long getIdBoleta() {
    return idBoleta;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdBoletaBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idBoletaBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoletaBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoletaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoleta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idBoletaBitacora", getIdBoletaBitacora());
		regresar.put("idBoletaEstatus", getIdBoletaEstatus());
		regresar.put("idBoleta", getIdBoleta());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getJustificacion(), getIdUsuario(), getIdBoletaBitacora(), getIdBoletaEstatus(), getIdBoleta(), getRegistro()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idBoletaBitacora~");
    regresar.append(getIdBoletaBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdBoletaBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetBoletasBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdBoletaBitacora()!= null && getIdBoletaBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final TcKeetBoletasBitacoraDto other = (TcKeetBoletasBitacoraDto) obj;
    if (getIdBoletaBitacora() != other.idBoletaBitacora && (getIdBoletaBitacora() == null || !getIdBoletaBitacora().equals(other.idBoletaBitacora))) 
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdBoletaBitacora() != null ? getIdBoletaBitacora().hashCode() : 0);
    return hash;
  }

}


