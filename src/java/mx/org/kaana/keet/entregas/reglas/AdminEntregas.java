package mx.org.kaana.keet.entregas.reglas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetEntregasDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.entregas.beans.Material;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2023
 *@time 07:23:11 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx
 */

public class AdminEntregas implements Serializable {

  private static final long serialVersionUID = 940143926501271163L;

  private Long idDesarrollo;
  private Long idPrototipo;
  private Long idProceso;
  private Long idSubProceso;
  private TcKeetEntregasDto entrega;
  private List<Material> materiales;
  
  private UISelectEntity ikSolicita;
  private UISelectEntity ikRecibe;
  private UISelectEntity ikAlmacen;
  
  public AdminEntregas() {
    this(-1L, -1L, -1L, -1L);
  }
  
  public AdminEntregas(Long idEntrega) {
    this.init(idEntrega);
  }
  
  public AdminEntregas(Long idDesarrollo, Long idPrototipo, Long idProceso, Long idSubProceso) {
    this.idDesarrollo= idDesarrollo;
    this.idPrototipo = idPrototipo;
    this.idProceso   = idProceso;
    this.idSubProceso= idSubProceso;
    this.init();
  }

  public TcKeetEntregasDto getEntrega() {
    return entrega;
  }

  public void setEntrega(TcKeetEntregasDto entrega) {
    this.entrega = entrega;
  }

  public List<Material> getMateriales() {
    return materiales;
  }

  public void setMateriales(List<Material> materiales) {
    this.materiales = materiales;
  }

  public UISelectEntity getIkSolicita() {
    return ikSolicita;
  }

  public void setIkSolicita(UISelectEntity ikSolicita) {
    this.ikSolicita = ikSolicita;
    if(!Objects.equals(ikSolicita, null))
      this.entrega.setIdAutoriza(ikSolicita.getKey());
  }

  public UISelectEntity getIkRecibe() {
    return ikRecibe;
  }

  public void setIkRecibe(UISelectEntity ikRecibe) {
    this.ikRecibe = ikRecibe;
    if(!Objects.equals(ikRecibe, null))
      this.entrega.setIdRecibe(ikRecibe.getKey());
  }

  public UISelectEntity getIkAlmacen() {
    return ikAlmacen;
  }

  public void setIkAlmacen(UISelectEntity ikAlmacen) {
    this.ikAlmacen = ikAlmacen;
    if(!Objects.equals(ikAlmacen, null))
      this.entrega.setIdAlmacen(ikAlmacen.getKey());
  }
  
  private void init() {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idDesarrollo", this.idDesarrollo);      
      params.put("idPrototipo", this.idPrototipo);      
      params.put("idProceso", this.idProceso);      
      params.put("idSubProceso", this.idSubProceso);      
      this.materiales= (List<Material>)DaoFactory.getInstance().toEntitySet(Material.class, "VistaProcesosDto", "entrega", params);
      if(Objects.equals(this.materiales, null) || this.materiales.isEmpty()) {
        this.entrega= new TcKeetEntregasDto(
          null, // String consecutivo, 
          -1L, // Long idAutoriza, 
          2L, // Long idCompleto, 
          -1L, // Long idPaquete, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          -1L, // Long idEntrega, 
          -1L, // Long idContratoLote, 
          null, // String observaciones, 
          1L, // Long orden, 
          -1L, // Long idRecibe, 
          new Long(Fecha.getAnioActual()), // Long ejercicio      
          LocalDate.now(), // LocalDate fecha
          -1L  // Long idAlmacen
        );
        this.materiales= (List<Material>)DaoFactory.getInstance().toEntitySet(Material.class, "VistaProcesosDto", "igual", params);
      } // if  
      else 
        this.entrega= (TcKeetEntregasDto)DaoFactory.getInstance().findById(TcKeetEntregasDto.class, this.materiales.get(0).getIdEntrega());
      if(Objects.equals(this.materiales, null) || this.materiales.isEmpty()) 
        this.materiales= new ArrayList<>();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void init(Long idEntrega) {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEntrega", idEntrega);      
      this.entrega   = (TcKeetEntregasDto)DaoFactory.getInstance().findById(TcKeetEntregasDto.class, idEntrega);
      this.materiales= (List<Material>)DaoFactory.getInstance().toEntitySet(Material.class, "TcKeetEntregasDetallesDto", "igual", params);
      if(Objects.equals(this.materiales, null) || this.materiales.isEmpty()) 
        this.materiales= new ArrayList<>();
      else
        for (Material item: materiales) {
          item.setSql(ESql.SELECT);
        } // for
      this.setIkSolicita(new UISelectEntity(this.entrega.getIdAutoriza()));
      this.setIkRecibe(new UISelectEntity(this.entrega.getIdRecibe()));
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
