package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDate;
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

@Entity
@Table(name = "tc_mantic_personas")
public class TcManticPersonasDto implements IBaseDto, Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  @Column(name = "ID_PERSONA")
  private Long idPersona;
  @Column(name = "ID_PERSONA_TITULO")
  private Long idPersonaTitulo;
  @Column(name = "ID_TIPO_PERSONA")
  private Long idTipoPersona;
  @Column(name = "ID_USUARIO")
  private Long idUsuario;
  @Column(name = "NOMBRES")
  private String nombres;
  @Column(name = "PATERNO")
  private String paterno;
  @Column(name = "MATERNO")
  private String materno;
  @Column(name = "CURP")
  private String curp;
  @Column(name = "RFC")
  private String rfc;
  @Column(name = "ID_TIPO_SEXO")
  private Long idTipoSexo;
  @Column(name = "CONTRASENIA")
  private String contrasenia;
  @Column(name = "OBSERVACIONES")
  private String observaciones;
  @Column(name = "CUENTA")
  private String cuenta;
  @Column(name = "ESTILO")
  private String estilo;  
  @Column(name = "REGISTRO")
  private LocalDateTime registro;
  @Column(name = "FECHA_NACIMIENTO")
  private LocalDate fechaNacimiento;  
  @Column(name = "ID_ESTADO_CIVIL")
  private Long idEstadoCivil;
	@Column (name="APODO")
  private String apodo;
  @Column(name = "id_autoriza")
  private Long idAutoriza;
  


  public TcManticPersonasDto() {
    this(new Long(-1L));
  }

  public TcManticPersonasDto(Long key) {
    this(null, null, null, null, null, 1L, null, null, null, LocalDate.now(), null, 2L);
    setKey(key);
  }

  public TcManticPersonasDto(Long idEmpleado, String nombres, String paterno, String materno, String curp, Long idTipoSexo,  String estilo, String cuenta, String contrasenia, LocalDate fechaNacimiento, Long idUsuario, Long idAutoriza) {
    this(idEmpleado, nombres, paterno, materno, curp, idTipoSexo, estilo, cuenta, contrasenia, fechaNacimiento, idUsuario, 1L, null, idAutoriza);
	}
	
  public TcManticPersonasDto(Long idEmpleado, String nombres, String paterno, String materno, String curp, Long idTipoSexo,  String estilo, String cuenta, String contrasenia, LocalDate fechaNacimiento, Long idUsuario, Long idEstadoCivil, String apodo, Long idAutoriza) {
    setIdEmpleado(idEmpleado);
    setNombres(nombres);
		setPaterno(paterno);
		setMaterno(materno);    
    setIdTipoSexo(idTipoSexo);
    setCurp(curp);
    setRegistro(LocalDateTime.now());
    setCuenta(cuenta);
    setContrasenia(contrasenia);   
    setEstilo(estilo);
		setFechaNacimiento(fechaNacimiento);
		setIdUsuario(idUsuario);	
		setIdEstadoCivil(idEstadoCivil);	
		this.apodo= apodo;
    this.idAutoriza= idAutoriza;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public String getRfc() {
    return rfc;
  }

  public void setRfc(String rfc) {
    this.rfc = rfc;
  }
  
  public void setFechaNacimiento(LocalDate fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }
  
  public String getContrasenia() {
    return contrasenia;
  }

  public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public String getEstilo() {
    return estilo;
  }

  public void setEstilo(String estilo) {
    this.estilo = estilo;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setIdEmpleado(Long idPersona) {
    this.idPersona = idPersona;
  }

  public String getNombres() {
    return nombres;
  }

  public void setNombres(String nombres) {
    this.nombres = nombres;
  }  

  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public Long getIdPersonaTitulo() {
    return idPersonaTitulo;
  }

  public void setIdPersonaTitulo(Long idPersonaTitulo) {
    this.idPersonaTitulo = idPersonaTitulo;
  }

  public Long getIdTipoPersona() {
    return idTipoPersona;
  }

  public void setIdTipoPersona(Long idTipoPersona) {
    this.idTipoPersona = idTipoPersona;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Long idUSuario) {
    this.idUsuario = idUSuario;
  }

  public String getPaterno() {
    return paterno;
  }

  public void setPaterno(String paterno) {
    this.paterno = paterno;
  }

  public String getMaterno() {
    return materno;
  }

  public void setMaterno(String materno) {
    this.materno = materno;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }
 
  public String getCurp() {
    return curp;
  }

  public void setCurp(String curp) {
    this.curp = curp;
  }

  public Long getIdTipoSexo() {
    return idTipoSexo;
  }

  public void setIdTipoSexo(Long idTipoSexo) {
    this.idTipoSexo = idTipoSexo;
  }

	public Long getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(Long idEstadoCivil) {
		this.idEstadoCivil=idEstadoCivil;
	}

  public String getApodo() {
		return apodo;
	}

	public void setApodo(String apodo) {
		this.apodo=apodo;
	}
	
  public Long getIdAutoriza() {
    return idAutoriza;
  }

  public void setIdAutoriza(Long idAutoriza) {
    this.idAutoriza = idAutoriza;
  }
  
	@Transient
  @Override
  public Long getKey() {
    return getIdPersona();
  }

  @Override
  public void setKey(Long key) {
    this.idPersona = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar = new StringBuilder();
    regresar.append("[");
    regresar.append(getIdPersona());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getNombres());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getPaterno());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getMaterno());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getRfc());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getCurp());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdTipoSexo());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getCuenta());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getContrasenia());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getEstilo());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getRegistro());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getFechaNacimiento());
    regresar.append(Constantes.SEPARADOR);    
    regresar.append(getIdEstadoCivil());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getApodo());
    regresar.append(Constantes.SEPARADOR);
    regresar.append(getIdAutoriza());
    regresar.append("]");
    return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
    regresar.put("idPersona", getIdPersona());
    regresar.put("nombres", getNombres());
    regresar.put("paterno", getPaterno());
    regresar.put("materno", getMaterno());
    regresar.put("rfc", getRfc());
    regresar.put("curp", getCurp());
    regresar.put("idTipoSexo", getIdTipoSexo());
    regresar.put("cuenta", getCuenta());
    regresar.put("contrasenia", getContrasenia());   
    regresar.put("registro", getRegistro());
    regresar.put("estilo", getEstilo());
    regresar.put("fechaNacimiento", getFechaNacimiento());    
    regresar.put("idEstadoCivil", getIdEstadoCivil());
    regresar.put("idUsuario", getIdUsuario());
		regresar.put("apodo", getApodo());
    regresar.put("idAutoriza", getIdAutoriza());
    return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdPersona(), getNombres(), getPaterno(), getMaterno(), getIdTipoSexo(), getCurp(), getCuenta(), getContrasenia(), getEstilo(), getFechaNacimiento(), getIdEstadoCivil(), getIdUsuario(), getApodo(), getIdAutoriza()
		};
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar = new StringBuilder();
    regresar.append("|");
    regresar.append("idPersona~");
    regresar.append(getIdPersona());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar = new StringBuilder();
    regresar.append(getIdPersona());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticPersonasDto.class;
  }

  @Override
  public boolean isValid() {
    return getIdPersona() != null && getIdPersona() != -1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticPersonasDto other = (TcManticPersonasDto) obj;
    if (getIdPersona() != other.idPersona && (getIdPersona() == null || !getIdPersona().equals(other.idPersona))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersona() != null ? getIdPersona().hashCode() : 0);
    return hash;
  }
  
}