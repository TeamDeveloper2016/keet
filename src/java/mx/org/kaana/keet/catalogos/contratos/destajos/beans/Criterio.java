package mx.org.kaana.keet.catalogos.contratos.destajos.beans;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Objects;
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

  private long dias(LocalDate uno, LocalDate dos) {
    final DayOfWeek startW= uno.getDayOfWeek();
    final DayOfWeek endW  = dos.getDayOfWeek();
    final long days = DAYS.between(uno, dos);
    final long daysWithoutWeekends= days- 2* ((days+ startW.getValue())/ 7);
    //adjust for starting and ending on a Sunday:
    return daysWithoutWeekends + (startW == DayOfWeek.SUNDAY ? 1 : 0) + (endW == DayOfWeek.SUNDAY ? 1 : 0);
  }
  
  private void toCalulate() {
    if(Objects.equals(this.idEstacionEstatus, 4L)) {
      this.semaforo= "circulo-magenta.png";
      this.titulo  = "NO APLICA POR ESTAR CANCELADO";
    } // if
    else {
      if(Objects.equals(this.idEstacionEstatus, 1L)) {
        if(this.inicio.isEqual(this.hoy) || this.inicio.isAfter(this.hoy)) {
          long dias= this.dias(this.hoy, this.inicio);
          if(dias>= 5) 
            this.semaforo= "circulo-cafe.png";
          else 
            this.semaforo= "circulo-gris.png";
          this.titulo= "FALTAN "+ dias+ " DIAS PARA INICIAR\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus;
        } // if
        else {
          long dias    = this.dias(this.inicio, this.hoy);
          this.semaforo= "circulo-rojo.png";
          this.titulo  = "ESTA RETRASADO CON "+ dias+ " DIAS\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus;
        } // else
      } // if
      else {
        if(this.entrega!= null) {
          if(Objects.equals(this.idEstacionEstatus, 3L)) {
            if((this.entrega.isEqual(this.inicio) || this.entrega.isAfter(this.inicio)) && (this.entrega.isEqual(this.termino) || this.entrega.isBefore(this.termino))) {
              this.semaforo= "circulo-verde.png";
              this.titulo  = "SE TERMINO EN TIEMPO\rPAGADO: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.entrega);
            } // if
            else {
              if(this.inicio.isEqual(this.entrega) || this.inicio.isAfter(this.entrega)) {
                long dias    = this.dias(this.entrega, this.inicio);
                this.semaforo= "circulo-azul.png";
                this.titulo  = "SE TERMINO CON "+ dias+ " DIAS ANTES\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\rPAGADO: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.entrega);
              } // if
              else {
                long dias    = this.dias(this.termino, this.entrega);
                this.semaforo= "circulo-rojo.png";
                this.titulo  = "SE TERMINO CON "+ dias+ " DIAS DESPUES\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\rPAGADO: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.entrega);
              } // else  
            } // else  
          } // if
          else {
            if(this.entrega.isEqual(this.inicio) || this.entrega.isBefore(this.inicio)) {
              long dias= this.dias(this.entrega, this.inicio);
              this.semaforo= "circulo-naranja.png";
              this.titulo  = "ESTA EN TIEMPO, "+ dias+ " DIAS PARA TERMINAR\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\rPAGADO: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.entrega);
            } // if
            else {
              if(this.entrega.isAfter(this.termino)) {
                long dias= this.dias(this.termino, this.entrega);
                this.semaforo= "circulo-rojo.png";
                this.titulo  = "ESTA RETRASADO CON "+ dias+ " DIAS PARA TERMINAR\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\rPAGADO: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.entrega);
              } // if
              else {
                if(this.hoy.isBefore(this.termino)) {
                  long dias= this.dias(this.hoy, this.termino);
                  if(dias<= 2) {
                    this.semaforo= "circulo-amarillo.png";
                  } // if
                  else {
                    this.semaforo= "circulo-naranja.png";
                  } // else
                    this.titulo  = "ESTA A "+ dias+ " DIAS PARA TERMINAR\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\rHOY ES: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.hoy);
                } // if
                else {
                  long dias= this.dias(this.termino, this.hoy);
                  this.semaforo= "circulo-rojo.png";
                  this.titulo  = "ESTA RETRASADO CON "+ dias+ " DIAS PARA TERMINAR\r("+ Fecha.formatear(Fecha.FECHA_CORTA, this.inicio)+ " a "+ Fecha.formatear(Fecha.FECHA_CORTA, this.termino)+ ") "+ this.estatus+ "\rHOY ES: "+ Fecha.formatear(Fecha.FECHA_CORTA, this.hoy);
                } // else
              } // else
            } // else  
          } // else
        } // if
        else {
          this.semaforo= "circulo-turquesa.png";
          this.titulo  = "REVISAR PORQUE ESTA VACIO LA FECHA DE ENTREGA";
        } // else  
      } // else
    } // else
  }

  public String getSemaforo() {
    return semaforo;
  }
  
  public String getTitulo() {
    return titulo;
  }
  
  @Override
  public String toString() {
    return "Criterio{" + "lote=" + lote + ", inicio=" + inicio + ", termino=" + termino + ", entrega=" + entrega + ", hoy=" + hoy + ", idEstatusEstacion=" + idEstacionEstatus + '}';
  }

}
