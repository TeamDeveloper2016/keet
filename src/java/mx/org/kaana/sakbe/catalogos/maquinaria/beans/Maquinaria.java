package mx.org.kaana.sakbe.catalogos.maquinaria.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.sakbe.db.dto.TcSakbeMaquinariasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/06/2022
 *@time 06:45:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Maquinaria extends TcSakbeMaquinariasDto implements Serializable {
  
  private static final long serialVersionUID = 3685013042323492538L;
  private static final Log LOG = LogFactory.getLog(Maquinaria.class);
  
  private UISelectEntity ikEmpresa;
  private UISelectEntity ikDesarrollo;
  private UISelectEntity ikMaquinariaGrupo;
  private UISelectEntity ikTipoMaquinaria;
  private UISelectEntity ikTipoCombustible;
  private Long idDesarrollo;
  private Long idMaquinariaGrupo;

  public Maquinaria() {
    this(-1L);
  }
  
  public Maquinaria(Long idCombustible) {
    super(
      "", // String motor, 
      "", // String proEnTarjeta, 
      "", // String color, 
      "", // String pedimento, 
      1L, // Long idTipoMaquinaria, 
      LocalDate.now(), // LocalDate fechaFactura, 
      "", // String nombre, 
      1L, // Long idTipoCombustible, 
      "", // String poliza, 
      "", // String comprado, 
      "", // String marca, 
      0D, // Double total, 
      "", // String factura, 
      "", // String proReal, 
      0D, // Double iva, 
      0D, // Double litros, 
      "", // String proEnFactura, 
      "", // String constancia, 
      "", // String tarjeta, 
      1L, // Long idMaquinariaEstatus, 
      "", // String placa, 
      2L, // Long idOriginal, 
      "TRIANA-01", // String clave, 
      new Long(Fecha.getAnioActual()), // Long modelo, 
      "", // String tipo, 
      -1L, // Long idMaquinaria, 
      2L, // Long idConstancia, 
      "", // String facturado, 
      0D, // Double rendimiento, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      0D, // Double subtotal, 
      "", // String entidad, 
      "", // String serie, 
      "", // String observaciones, 
      -1L, // Long idEmpresa    
      "", // String ultimaTarjeta 
      "" // String responsable
    );
    this.ikEmpresa= new UISelectEntity(this.getIdEmpresa());
    this.ikDesarrollo= new UISelectEntity(-1L);
    this.ikTipoMaquinaria= new UISelectEntity(this.getIdTipoMaquinaria());
    this.ikTipoCombustible= new UISelectEntity(this.getIdTipoCombustible());
    this.idDesarrollo= -1L;
    this.idMaquinariaGrupo= -1L;
  }

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
  }

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
  }

  public UISelectEntity getIkMaquinariaGrupo() {
    return ikMaquinariaGrupo;
  }

  public void setIkMaquinariaGrupo(UISelectEntity ikMaquinariaGrupo) {
    this.ikMaquinariaGrupo = ikMaquinariaGrupo;
		if(this.ikMaquinariaGrupo!= null)
		  this.setIdMaquinariaGrupo(this.ikMaquinariaGrupo.getKey());
  }

  public UISelectEntity getIkTipoMaquinaria() {
    return ikTipoMaquinaria;
  }

  public void setIkTipoMaquinaria(UISelectEntity ikTipoMaquinaria) {
    this.ikTipoMaquinaria = ikTipoMaquinaria;
		if(this.ikTipoMaquinaria!= null)
		  this.setIdTipoMaquinaria(this.ikTipoMaquinaria.getKey());
  }

  public UISelectEntity getIkTipoCombustible() {
    return ikTipoCombustible;
  }

  public void setIkTipoCombustible(UISelectEntity ikTipoCombustible) {
    this.ikTipoCombustible = ikTipoCombustible;
		if(this.ikTipoCombustible!= null)
		  this.setIdTipoCombustible(this.ikTipoCombustible.getKey());
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdMaquinariaGrupo() {
    return idMaquinariaGrupo;
  }

  public void setIdMaquinariaGrupo(Long idMaquinariaGrupo) {
    this.idMaquinariaGrupo = idMaquinariaGrupo;
  }
  
  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasDto.class;
  }
  
  public Boolean isComplete() {
    return Boolean.FALSE;
  }
  
}
