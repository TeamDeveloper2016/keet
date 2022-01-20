package mx.org.kaana.keet.catalogos.contratos.destajos.beans;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import mx.org.kaana.libs.formato.Fecha;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 12/01/2022
 * @time 07:45:29 PM 
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Criterio implements Serializable {

  private static final long serialVersionUID = 1851743938707869405L;

  private String lote;
  private LocalDate inicio;
  private LocalDate termino;
  private LocalDate entrega;
  private LocalDate hoy;
  private Long idEstacionEstatus;
  private String estatus;
  private Long idNomina;
  private String semana;
  private Long actual;
  private String semaforo;
  private String titulo;

  public Criterio(String lote, LocalDate inicio, LocalDate termino, LocalDate entrega, Long idEstacionEstatus, String estatus) {
    this.lote   = lote; 
    this.inicio = inicio;
    this.termino= termino;
    this.entrega= entrega;
    this.hoy    = LocalDate.now();
    this.idEstacionEstatus= idEstacionEstatus;
    this.estatus= estatus;
    this.toCalulate();
  }
  
  public Criterio(String lote, LocalDate inicio, LocalDate termino, LocalDate entrega, Long idEstacionEstatus, String estatus, Long idNomina, String semana, Long actual) {
    this.lote   = lote; 
    this.inicio = inicio;
    this.termino= termino;
    this.entrega= entrega;
    this.hoy    = LocalDate.now();
    this.idEstacionEstatus= idEstacionEstatus;
    this.estatus= estatus;
    this.idNomina= idNomina;
    this.semana = semana;
    this.actual = actual;
    this.toControl();
  }

  public String getLote() {
    return lote;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
  }

  public LocalDate getTermino() {
    return termino;
  }

  public void setTermino(LocalDate termino) {
    this.termino = termino;
  }

  public LocalDate getEntrega() {
    return entrega;
  }

  public void setEntrega(LocalDate entrega) {
    this.entrega = entrega;
  }

  public LocalDate getHoy() {
    return hoy;
  }

  public void setHoy(LocalDate hoy) {
    this.hoy = hoy;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
  }

  public String getEstatus() {
    return estatus;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public String getSemana() {
    return semana;
  }

  public void setSemana(String semana) {
    this.semana = semana;
  }

  public Long getActual() {
    return actual;
  }

  public void setActual(Long actual) {
    this.actual = actual;
  }

  private long dias(LocalDate uno, LocalDate dos) {
    final DayOfWeek startW= uno.getDayOfWeek();
    final DayOfWeek endW  = dos.getDayOfWeek();
    final long days = DAYS.between(uno, dos);
    final long daysWithoutWeekends= days- 2* ((days+ startW.getValue())/ 7);
    //adjust for starting and ending on a Sunday:
    return daysWithoutWeekends+ (startW== DayOfWeek.SUNDAY? 1: 0)+ (endW== DayOfWeek.SUNDAY? 1: 0);
  }
  
  private String toPeriodo(String text, LocalDate fecha) {
    return "\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\r "+ text+ ": "+ Fecha.formatear(Fecha.FECHA_CORTA, fecha);
  }
  
  private void toCalulate() {
    switch(this.idEstacionEstatus.intValue()) {
      case 1: // SIN INICIAR
        if(this.hoy.isBefore(this.hoy)) {
          long dias= this.dias(this.hoy, this.inicio);
          if(dias<= 2) 
            this.semaforo= "circulo-amarillo.png";
          else 
            this.semaforo= "circulo-gris.png";
          this.titulo= "FALTAN "+ dias+ " DIAS PARA INICIAR".concat(this.toPeriodo("HOY ES", this.hoy));
        } // if
        else 
          if(this.hoy.isAfter(this.termino)) {
            long dias    = DAYS.between(this.inicio, this.hoy);
            this.semaforo= "circulo-rojo.png";
            this.titulo  = "ESTA RETRASADO CON "+ dias+ " DIAS".concat(this.toPeriodo("HOY ES", this.hoy));
          } // if
          else {
            long dias    = DAYS.between(this.inicio, this.hoy);
            this.semaforo= "circulo-rojo.png";
            this.titulo  = "DEBIO INICIAR HACE "+ dias+ " DIAS".concat(this.toPeriodo("HOY ES", this.hoy));
          } // else
        break;
      case 2: // EN PROCESO
        if((this.hoy.isEqual(this.inicio) || this.hoy.isAfter(this.inicio)) && (this.hoy.isEqual(this.termino) || this.hoy.isBefore(this.termino))) {
          long dias= this.dias(this.hoy, this.termino);
          this.semaforo= "circulo-naranja.png";
          this.titulo= "ESTA A "+ dias+ " DIAS PARA TERMINAR".concat(this.toPeriodo("HOY ES", this.hoy));
        } // if
        else 
          if(this.hoy.isBefore(this.inicio)) {
            long dias    = DAYS.between(this.hoy, this.inicio);
            this.semaforo= "circulo-azul.png";
            this.titulo  = "INICIO ANTES, CON "+ dias+ " DIAS".concat(this.toPeriodo("HOY ES", this.hoy));
          } // if
          else {
            long dias    = DAYS.between(this.inicio, this.hoy);
            this.semaforo= "circulo-rojo.png";
            this.titulo  = "ESTA RETRASADO, CON "+ dias+ " DIAS".concat(this.toPeriodo("HOY ES", this.hoy));
          } // else            
        break;
      case 3: // TERMINADO
        if(this.entrega!= null) {        
          if((this.entrega.isEqual(this.inicio) || this.entrega.isAfter(this.inicio)) && (this.entrega.isEqual(this.termino) || this.entrega.isBefore(this.termino))) {
            this.semaforo= "circulo-verde.png";
            this.titulo  = "SE TERMINO EN TIEMPO".concat(this.toPeriodo("PAGADO", this.entrega));
          } // if
          else 
            if(this.entrega.isBefore(this.inicio)) {
              long dias    = DAYS.between(this.entrega, this.inicio);
              this.semaforo= "circulo-azul.png";
              this.titulo  = "SE TERMINO CON "+ dias+ " DIAS ANTES".concat(this.toPeriodo("PAGADO", this.entrega));
            } // if
            else {
              long dias    = DAYS.between(this.termino, this.entrega);
              this.semaforo= "circulo-cafe.png";
              this.titulo  = "SE TERMINO CON "+ dias+ " DIAS DESPUES".concat(this.toPeriodo("PAGADO", this.entrega));
            } // else  
        } // if
        else {
          this.semaforo= "circulo-turquesa.png";
          this.titulo  = "REVISAR PORQUE ESTA VACIO LA FECHA DE ENTREGA".concat(this.toPeriodo("HOY ES", this.hoy));
        } // else
        break;
      case 4: // CANCELADO
        this.semaforo= "circulo-magenta.png";
        this.titulo  = "NO APLICA POR ESTAR CANCELADO".concat(this.toPeriodo("HOY ES", this.hoy));
        break;
    } // switch
  }

  private void toControl() {
    switch(this.actual.intValue()) {
      case 1: // NOMINA ACTUAL
        this.semaforo= "circulo-azul.png";
        this.titulo  = this.semana;
        break;
      case 2: // NOMINA NO ACTUAL
        this.semaforo= "circulo-verde.png";
        this.titulo  = "*";
        break;
      case 3: // COCEPTO NO PAGADO
        this.semaforo= "";
        this.titulo  = "";
        break;
      case 4: // ES UNA ESTACION
        this.semaforo= "";
        this.titulo  = "";
        break;
    } // switch
  }   
  
  public String getSemaforo() {
    return semaforo;
  }
  
  public String getTitulo() {
    return titulo;
  }

  @Override
  public String toString() {
    return "Criterio{" + "lote=" + lote + ", inicio=" + inicio + ", termino=" + termino + ", entrega=" + entrega + ", hoy=" + hoy + ", idEstacionEstatus=" + idEstacionEstatus + ", estatus=" + estatus + ", idNomina=" + idNomina + ", semana=" + semana + ", actual=" + actual + ", semaforo=" + semaforo + ", titulo=" + titulo + '}';
  }

}
