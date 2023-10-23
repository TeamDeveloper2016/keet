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
@Table(name="tc_sakbe_suministros")
public class TcSakbeSuministrosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="lectura_actual")
  private Double lecturaActual;
  @Column (name="latitud")
  private String latitud;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="recibio")
  private String recibio;
  @Column (name="id_suministro_estatus")
  private Long idSuministroEstatus;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="horas")
  private Double horas;
  @Column (name="longitud")
  private String longitud;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="litros")
  private Double litros;
  @Column (name="lectura_nueva")
  private Double lecturaNueva;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_suministro")
  private Long idSuministro;
	@Column (name="id_patrocinado")
  private Long idPatrocinado;
  @Column (name="observaciones")
  private String observaciones;

  public TcSakbeSuministrosDto() {
    this(new Long(-1L));
  }

  public TcSakbeSuministrosDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, new Long(-1L), null, 2L);
    setKey(key);
  }

  public TcSakbeSuministrosDto(Double lecturaActual, String latitud, Long idDesarrollo, String recibio, Long idSuministroEstatus, Long ejercicio, Long idMaquinaria, String consecutivo, Double horas, String longitud, Long idUsuario, Double litros, Double lecturaNueva, Long orden, Long idSuministro, String observaciones, Long idPatrocinado) {
    setLecturaActual(lecturaActual);
    setLatitud(latitud);
    setIdDesarrollo(idDesarrollo);
    setRecibio(recibio);
    setIdSuministroEstatus(idSuministroEstatus);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setIdMaquinaria(idMaquinaria);
    setConsecutivo(consecutivo);
    setHoras(horas);
    setLongitud(longitud);
    setIdUsuario(idUsuario);
    setLitros(litros);
    setLecturaNueva(lecturaNueva);
    setOrden(orden);
    setIdSuministro(idSuministro);
    setObservaciones(observaciones);
    setIdPatrocinado(idPatrocinado);
  }
	
  public void setLecturaActual(Double lecturaActual) {
    this.lecturaActual = lecturaActual;
  }

  public Double getLecturaActual() {
    return lecturaActual;
  }

  public void setLatitud(String latitud) {
    this.latitud = latitud;
  }

  public String getLatitud() {
    return latitud;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setRecibio(String recibio) {
    this.recibio = recibio;
  }

  public String getRecibio() {
    return recibio;
  }

  public void setIdSuministroEstatus(Long idSuministroEstatus) {
    this.idSuministroEstatus = idSuministroEstatus;
  }

  public Long getIdSuministroEstatus() {
    return idSuministroEstatus;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setHoras(Double horas) {
    this.horas = horas;
  }

  public Double getHoras() {
    return horas;
  }

  public void setLongitud(String longitud) {
    this.longitud = longitud;
  }

  public String getLongitud() {
    return longitud;
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

  public void setLecturaNueva(Double lecturaNueva) {
    this.lecturaNueva = lecturaNueva;
  }

  public Double getLecturaNueva() {
    return lecturaNueva;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdSuministro(Long idSuministro) {
    this.idSuministro = idSuministro;
  }

  public Long getIdSuministro() {
    return idSuministro;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public Long getIdPatrocinado() {
    return idPatrocinado;
  }

  public void setIdPatrocinado(Long idPatrocinado) {
    this.idPatrocinado = idPatrocinado;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdSuministro();
  }

  @Override
  public void setKey(Long key) {
  	this.idSuministro = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getLecturaActual());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRecibio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministroEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getHoras());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLitros());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLecturaNueva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPatrocinado());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("lecturaActual", getLecturaActual());
		regresar.put("latitud", getLatitud());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("recibio", getRecibio());
		regresar.put("idSuministroEstatus", getIdSuministroEstatus());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("horas", getHoras());
		regresar.put("longitud", getLongitud());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("litros", getLitros());
		regresar.put("lecturaNueva", getLecturaNueva());
		regresar.put("orden", getOrden());
		regresar.put("idSuministro", getIdSuministro());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idPatrocinado", getIdPatrocinado());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getLecturaActual(), getLatitud(), getIdDesarrollo(), getRecibio(), getIdSuministroEstatus(), getEjercicio(), getRegistro(), getIdMaquinaria(), getConsecutivo(), getHoras(), getLongitud(), getIdUsuario(), getLitros(), getLecturaNueva(), getOrden(), getIdSuministro(), getObservaciones(), getIdPatrocinado()
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
    regresar.append("idSuministro~");
    regresar.append(getIdSuministro());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdSuministro());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeSuministrosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdSuministro()!= null && getIdSuministro()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeSuministrosDto other = (TcSakbeSuministrosDto) obj;
    if (getIdSuministro() != other.idSuministro && (getIdSuministro() == null || !getIdSuministro().equals(other.idSuministro))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdSuministro() != null ? getIdSuministro().hashCode() : 0);
    return hash;
  }

}


