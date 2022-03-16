package mx.org.kaana.keet.estmaciones.reglas;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.estmaciones.beans.Estimacion;
import mx.org.kaana.keet.estmaciones.beans.Retencion;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/03/2022
 *@time 03:40:16 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Estimaciones implements Serializable {

  private static final long serialVersionUID = 498607016989851853L;

  protected Estimacion estimacion;

  public Estimaciones() {
    this(-1L);
  }
  
  public Estimaciones(Long idEstimacion) {
    this.init(idEstimacion);
  }

  public Estimacion getEstimacion() {
    return estimacion;
  }

  public void setEstimacion(Estimacion estimacion) {
    this.estimacion = estimacion;
  }

  private void init(Long idEstimacion) {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEstimacion", idEstimacion);      
      if(Objects.equal(idEstimacion, -1L)) {
        this.estimacion= new Estimacion();
        this.estimacion.setIkEmpresa(new UISelectEntity(-1L));
        this.estimacion.setIkDesarrollo(new UISelectEntity(-1L));
        this.estimacion.setIkCliente(new UISelectEntity(-1L));
        this.estimacion.setIkContrato(new UISelectEntity(-1L));
        this.estimacion.setRetenciones(new ArrayList<>());
      } // if
      else {
        this.estimacion= (Estimacion)DaoFactory.getInstance().toEntity(Estimacion.class, "TcKeetEstimacionesDto", "estimacion", params);
        if(this.estimacion!= null) {
          params.put("idContrato", this.estimacion.getIdContrato());      
          this.estimacion.setIkEmpresa(new UISelectEntity(this.estimacion.getIdEmpresa()));
          this.estimacion.setIkDesarrollo(new UISelectEntity(this.estimacion.getIdDesarrollo()));
          this.estimacion.setIkCliente(new UISelectEntity(this.estimacion.getIdCliente()));
          this.estimacion.setIkContrato(new UISelectEntity(this.estimacion.getIdContrato()));
          this.estimacion.setRetenciones((List<Retencion>)DaoFactory.getInstance().toEntitySet(Retencion.class, "TcKeetEstimacionesDetallesDto", "estimacion", params));
          for (Retencion item: this.estimacion.getRetenciones()) 
            item.setSql(ESql.SELECT);
        } // if
      } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  public void toLoadRetenciones(Long idContrato) {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("idContrato", idContrato);      
      this.estimacion.setRetenciones((List<Retencion>) DaoFactory.getInstance().toEntitySet(Retencion.class, "TcKeetContratosRetencionesDto", "inicial", params));
      for (Retencion item : this.estimacion.getRetenciones()) {
        item.setSql(ESql.INSERT);
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
          
}
