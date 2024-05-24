package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 20/03/2021
 * @time 12:27:03 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "kajoolAuditorias")
@ViewScoped
public class Auditorias extends Contratos implements Serializable {

  private static final long serialVersionUID= 5323749709626263801L;
  private static final Log LOG              = LogFactory.getLog(Auditorias.class);
  
  private UISelectEntity idColonia;
  private List<UISelectEntity> colonias;
  private UISelectEntity idManzana;
  private List<UISelectEntity> manzanas;
  private UISelectEntity idLote;
  private List<UISelectEntity> lotes;

  public UISelectEntity getIdColonia() {
    return idColonia;
  }

  public void setIdColonia(UISelectEntity idColonia) {
    this.idColonia = idColonia;
  }

  public List<UISelectEntity> getColonias() {
    return colonias;
  }

  public UISelectEntity getIdManzana() {
    return idManzana;
  }

  public void setIdManzana(UISelectEntity idManzana) {
    this.idManzana = idManzana;
  }

  public List<UISelectEntity> getManzanas() {
    return manzanas;
  }

  public UISelectEntity getIdLote() {
    return idLote;
  }

  public void setIdLote(UISelectEntity idLote) {
    this.idLote = idLote;
  }

  public List<UISelectEntity> getLotes() {
    return lotes;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {      
      this.attrs.put("pathPivote", "/".concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/images/"));
      this.attrs.put("idEstado", -1L);
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  @Override
  public void doLoad() {
    try {
      this.toLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad

  protected void toLoadDesarrollos() {
    Map<String, Object> params= new HashMap<>();
    try {      
      if(JsfBase.isResidente())
        params.put(Constantes.SQL_CONDICION, " and tc_keet_desarrollos.id_desarrollo in ("+ this.toLoadDesarrollosResidentes()+ ")");     
      else
        params.put(Constantes.SQL_CONDICION, " and tc_keet_desarrollos.id_desarrollo is not null");      
      this.colonias = UIEntity.build("VistaSeguimientoDto", "nombresDesarrollos", params);
      this.idColonia= UIBackingUtilities.toFirstKeySelectEntity(this.colonias);
      this.doLoadManzanas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void doLoadManzanas() {
    Map<String, Object> params= new HashMap<>();
    try {      
      int index= this.colonias.indexOf(this.idColonia);
      if(index>= 0)
        this.idColonia= this.colonias.get(index);
      params.put("idDesarrollo", this.idColonia.getKey());
      this.manzanas = UIEntity.build("VistaSeguimientoDto", "cuadras", params);
      this.idManzana= UIBackingUtilities.toFirstKeySelectEntity(this.manzanas);
      this.doLoadLotes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadLotes() {
    Map<String, Object> params= new HashMap<>();
    try {      
      int index= this.manzanas.indexOf(this.idManzana);
      if(index>= 0)
        this.idManzana= this.manzanas.get(index);
      params.put("idDesarrollo", this.idColonia.getKey());      
      params.put("manzana", this.idManzana.toString("manzana"));      
      this.lotes = UIEntity.build("VistaSeguimientoDto", "casas", params);
      this.idLote= UIBackingUtilities.toFirstKeySelectEntity(this.lotes);
      this.doLoadConceptos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }  

  public void doLoadConceptos() {
    try {      
      int index= this.lotes.indexOf(this.idLote);
      if(index>= 0)
        this.idLote= this.lotes.get(index);
      this.doLoadDetalle(this.idColonia.getKey(), this.idManzana.toString("manzana"), this.idLote.toString("lote"), (Long)this.attrs.get("idEstado"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  
  
}
