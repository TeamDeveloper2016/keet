package mx.org.kaana.keet.estimaciones.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.estimaciones.beans.Estimacion;
import mx.org.kaana.keet.estimaciones.beans.Retencion;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
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
      if(Objects.equals(idEstimacion, -1L)) {
        this.estimacion= new Estimacion();
        this.estimacion.setIkEmpresa(new UISelectEntity(-1L));
        this.estimacion.setIkDesarrollo(new UISelectEntity(-1L));
        this.estimacion.setIkCliente(new UISelectEntity(-1L));
        this.estimacion.setIkContrato(new UISelectEntity(-1L));
        this.estimacion.setIkNominaPeriodo(new UISelectEntity(-1L));
        this.estimacion.setRetenciones(new ArrayList<>());
      } // if
      else {
        this.estimacion= (Estimacion)DaoFactory.getInstance().toEntity(Estimacion.class, "TcKeetEstimacionesDto", "estimacion", params);
        if(this.estimacion!= null) {
          params.put("sucursales", this.estimacion.getIdEmpresa());      
          params.put("idDesarrollo", this.estimacion.getIdDesarrollo());      
          params.put("idCliente", this.estimacion.getIdCliente());
          params.put("idContrato", this.estimacion.getIdContrato());      
          params.put("idNominaPeriodo", this.estimacion.getIdNominaPeriodo());      
          this.estimacion.setIkEmpresa(new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("TcManticEmpresasDto", "empresas", params)));
          this.estimacion.setIkDesarrollo(new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("TcKeetDesarrollosDto", "byId", params)));
          this.estimacion.setIkCliente(new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("TcManticClientesDto", "igual", params)));
          this.estimacion.setIkContrato(new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("TcKeetContratosDto", "byId", params)));
          this.estimacion.setIkNominaPeriodo(new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("TcKeetNominasPeriodosDto", "igual", params)));
          this.estimacion.setRetenciones((List<Retencion>)DaoFactory.getInstance().toEntitySet(Retencion.class, "TcKeetEstimacionesDetallesDto", "estimacion", params));
          for (Retencion item: this.estimacion.getRetenciones()) {
            item.setSql(ESql.SELECT);
            if(Objects.equals(Configuracion.getInstance().getPropiedad("sistema.empresa.principal"), "cafu") && item.getKey()< 0L) 
              item.setIdDeduccion(2L);
          } // if  
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
      for (Retencion item: this.estimacion.getRetenciones()) {
        item.setSql(ESql.INSERT);
        if(Objects.equals(Configuracion.getInstance().getPropiedad("sistema.empresa.principal"), "cafu") && item.getKey()< 0L) 
          item.setIdDeduccion(2L);
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
