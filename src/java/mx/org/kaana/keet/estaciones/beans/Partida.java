package mx.org.kaana.keet.estaciones.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDiasDto;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/01/2022
 *@time 03:00:50 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Partida extends TcKeetPrototiposDiasDto implements Serializable {

  private static final long serialVersionUID = -1695511821974843099L;
  public static final String[] NAMES_OF_WEEKS= {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
  private ESql accion;
  private Long idEstacion;
  private Long idEstacionEstatus;
  private String clave;
  private String lote;
  private String codigo;
  private String nombre;
  private String anterior;
  private LocalDate inicio;
  private LocalDate termino;
  private String diaInicio;
  private String diaTermino;
  private Double costo;
  private Double anticipo;
  private Double costoAnterior;
  private Double anticipoAnterior;
  private String prototipo;
  private UISelectEntity idRubro;

  public Partida() {
    this(-1L);
  }

  public Partida(Long key) {
    super(key);
    this.accion           = ESql.SELECT;
    this.idEstacion       = -1L;
    this.idEstacionEstatus= -1L;
    this.lote             = "";
    this.inicio           = LocalDate.now();
    this.termino          = LocalDate.now();
    this.costo            = 0D;
    this.anticipo         = 0D;
    this.costoAnterior    = 0D;
    this.anticipoAnterior = 0D;
    this.prototipo        = "";
    this.idRubro          = new UISelectEntity(-1L);
  }

  public Long getIdEstacion() {
    return idEstacion;
  }

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
  }

  public void setIdEstacionEstatus(Long idEstacionEstatus) {
    this.idEstacionEstatus = idEstacionEstatus;
  }

  public Boolean isCostoEditable() {
    return this.idEstacionEstatus!= null && Objects.equals(this.idEstacionEstatus, 1L);
  }

  public String getLote() {
    return lote;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }
          
  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public ESql getAccion() {
    return accion;
  }

  public void setAccion(ESql accion) {
    this.accion = accion;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getAnterior() {
    return anterior;
  }

  public void setAnterior(String anterior) {
    this.anterior = anterior;
  }

  public Boolean isDiasDiferente() {
    return !Objects.equals(this.getDias(), this.anterior);
  }
  
  public LocalDate getInicio() {
    return inicio;
  }

  public String getInicio$() {
    return Global.format(EFormatoDinamicos.FECHA_CORTA, inicio);
  }
  
  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
    if(inicio!= null)
      this.diaInicio= NAMES_OF_WEEKS[inicio.getDayOfWeek().ordinal()];
  }

  public String getTermino$() {
    return Global.format(EFormatoDinamicos.FECHA_CORTA, termino);
  }
  
  public LocalDate getTermino() {
    return termino;
  }

  public void setTermino(LocalDate termino) {
    this.termino = termino;
    if(termino!= null)
      this.diaTermino= NAMES_OF_WEEKS[termino.getDayOfWeek().ordinal()];
  }

  public String getDiaInicio() {
    return diaInicio;
  }

  public String getDiaTermino() {
    return diaTermino;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getAnticipo() {
    return anticipo;
  }

  public void setAnticipo(Double anticipo) {
    this.anticipo = anticipo;
  }

  public Double getCostoAnterior() {
    return costoAnterior;
  }

  public void setCostoAnterior(Double costoAnterior) {
    this.costoAnterior = costoAnterior;
  }

  public Double getAnticipoAnterior() {
    return anticipoAnterior;
  }

  public void setAnticipoAnterior(Double anticipoAnterior) {
    this.anticipoAnterior = anticipoAnterior;
  }
  
  public Boolean isCostoDiferente() {
    return !Objects.equals(this.costo, this.costoAnterior) || !Objects.equals(this.anticipo, this.anticipoAnterior);
  }

  public String getPrototipo() {
    return prototipo;
  }

  public void setPrototipo(String prototipo) {
    this.prototipo = prototipo;
  }

  public UISelectEntity getIdRubro() {
    return idRubro;
  }

  public void setIdRubro(UISelectEntity idRubro) {
    this.idRubro = idRubro;
  }
  
  @Override
  public Class toHbmClass() {
    return TcKeetPrototiposDiasDto.class;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 19 * hash + Objects.hashCode(this.codigo);
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
    final Partida other = (Partida) obj;
    if (!Objects.equals(this.codigo, other.codigo)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Partida{" + "accion=" + accion + ", idEstacion=" + idEstacion + ", codigo=" + codigo + ", nombre=" + nombre + ", anterior=" + anterior + ", inicio=" + inicio + ", termino=" + termino + '}';
  }
  
}
