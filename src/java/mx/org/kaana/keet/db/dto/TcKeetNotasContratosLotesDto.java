package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
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
@Table(name="tc_keet_notas_contratos_lotes")
public class TcKeetNotasContratosLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_familia")
  private Long idFamilia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_nota_contrato_lote")
  private Long idNotaContratoLote;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNotasContratosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetNotasContratosLotesDto(Long key) {
    this(null, null, null, null, null);
    setKey(key);
  }

  public TcKeetNotasContratosLotesDto(Long idFamilia, Long idUsuario, Long idNotaContratoLote, Long idContratoLote, Long idNotaEntrada) {
    setIdFamilia(idFamilia);
    setIdUsuario(idUsuario);
    setIdNotaContratoLote(idNotaContratoLote);
    setIdContratoLote(idContratoLote);
    setIdNotaEntrada(idNotaEntrada);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdNotaContratoLote(Long idNotaContratoLote) {
    this.idNotaContratoLote = idNotaContratoLote;
  }

  public Long getIdNotaContratoLote() {
    return idNotaContratoLote;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
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
  	return getIdNotaContratoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaContratoLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idFamilia", getIdFamilia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idNotaContratoLote", getIdNotaContratoLote());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdFamilia(), getIdUsuario(), getIdNotaContratoLote(), getIdContratoLote(), getIdNotaEntrada(), getRegistro()
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
    regresar.append("idNotasContratosLotes~");
    regresar.append(getIdNotaContratoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaContratoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotasContratosLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaContratoLote()!= null && getIdNotaContratoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNotasContratosLotesDto other = (TcKeetNotasContratosLotesDto) obj;
    if (getIdNotaContratoLote() != other.idNotaContratoLote && (getIdNotaContratoLote() == null || !getIdNotaContratoLote().equals(other.idNotaContratoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaContratoLote() != null ? getIdNotaContratoLote().hashCode() : 0);
    return hash;
  }

}


