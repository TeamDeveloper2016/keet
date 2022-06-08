package mx.org.kaana.sakbe.suministros.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.db.dto.TcSakbeSuministrosDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 6/06/2022
 *@time 02:15:53 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Suministro extends TcSakbeSuministrosDto implements Serializable {
  
  private static final long serialVersionUID = 3685016042323492538L;
  private static final Log LOG = LogFactory.getLog(Suministro.class);
  
  private UISelectEntity ikDesarrollo;
  private UISelectEntity ikMaquinaria;
  private Long idTipoCombustible;
  private Double litrox;

  public Suministro() {
    this(-1L);
  }
  
  public Suministro(Long idCombustible) {
    super(
      0D, // Double lecturaActual, 
      "", // String latitud, 
      -1L, // Long idDesarrollo, 
      "", // String recibio, 
      1L, // Long idSuministroEstatus, 
      new Long(Fecha.getAnioActual()), // Long ejercicio, 
      -1L, // Long idMaquinaria, 
      null, // String consecutivo, 
      0D, // Double horas, 
      "", // String longitud, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      0D, // Double litros, 
      0D, // Double lecturaNueva, 
      1L, // Long orden, 
      -1L, // Long idSuministro, 
      null // String observaciones
    );
    this.ikDesarrollo= new UISelectEntity(this.getIdDesarrollo());
    this.ikMaquinaria= new UISelectEntity(this.getIdMaquinaria());
    this.litrox= this.getLitros();
    this.idTipoCombustible= 1L;
  }

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
		  this.setIdDesarrollo(this.ikDesarrollo.getKey());
  }

  public UISelectEntity getIkMaquinaria() {
    return ikMaquinaria;
  }

  public void setIkMaquinaria(UISelectEntity ikMaquinaria) {
    this.ikMaquinaria = ikMaquinaria;
		if(this.ikMaquinaria!= null)
		  this.setIdMaquinaria(this.ikMaquinaria.getKey());
  }

  public Double getLitrox() {
    return litrox;
  }

  public void setLitrox(Double litrox) {
    this.litrox = litrox;
  }

  public Long getIdTipoCombustible() {
    return idTipoCombustible;
  }

  public void setIdTipoCombustible(Long idTipoCombustible) {
    this.idTipoCombustible = idTipoCombustible;
  }
  
  public Boolean isComplete() {
    return Boolean.FALSE;
  }
  
}
