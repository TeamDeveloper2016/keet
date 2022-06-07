package mx.org.kaana.sakbe.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
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
@Table(name="tc_sakbe_tickets_saldos")
public class TcSakbeTicketsSaldosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="litros")
  private Double litros;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ticket_saldo")
  private Long idTicketSaldo;
  @Column (name="id_suministro")
  private Long idSuministro;
  @Column (name="id_combustible")
  private Long idCombustible;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeTicketsSaldosDto() {
    this(new Long(-1L));
  }

  public TcSakbeTicketsSaldosDto(Long key) {
    this(null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcSakbeTicketsSaldosDto(Long idUsuario, Double litros, Long idTicketSaldo, Long idSuministro, Long idCombustible) {
    setIdUsuario(idUsuario);
    setLitros(litros);
    setIdTicketSaldo(idTicketSaldo);
    setIdSuministro(idSuministro);
    setIdCombustible(idCombustible);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setLitros(Double litros) {
    this.litros = litros;
  }

  public Double getLitros() {
    return litros;
  }

  public void setIdTicketSaldo(Long idTicketSaldo) {
    this.idTicketSaldo = idTicketSaldo;
  }

  public Long getIdTicketSaldo() {
    return idTicketSaldo;
  }

  public void setIdSuministro(Long idSuministro) {
    this.idSuministro = idSuministro;
  }

  public Long getIdSuministro() {
    return idSuministro;
  }

  public void setIdCombustible(Long idCombustible) {
    this.idCombustible = idCombustible;
  }

  public Long getIdCombustible() {
    return idCombustible;
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
  	return getIdTicketSaldo();
  }

  @Override
  public void setKey(Long key) {
  	this.idTicketSaldo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLitros());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTicketSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("litros", getLitros());
		regresar.put("idTicketSaldo", getIdTicketSaldo());
		regresar.put("idSuministro", getIdSuministro());
		regresar.put("idCombustible", getIdCombustible());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getLitros(), getIdTicketSaldo(), getIdSuministro(), getIdCombustible(), getRegistro()
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
    regresar.append("idTicketSaldo~");
    regresar.append(getIdTicketSaldo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTicketSaldo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeTicketsSaldosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTicketSaldo()!= null && getIdTicketSaldo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeTicketsSaldosDto other = (TcSakbeTicketsSaldosDto) obj;
    if (getIdTicketSaldo() != other.idTicketSaldo && (getIdTicketSaldo() == null || !getIdTicketSaldo().equals(other.idTicketSaldo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTicketSaldo() != null ? getIdTicketSaldo().hashCode() : 0);
    return hash;
  }

}


