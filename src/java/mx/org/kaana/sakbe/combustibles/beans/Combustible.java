package mx.org.kaana.sakbe.combustibles.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.sakbe.db.dto.TcSakbeCombustiblesDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/06/2022
 *@time 02:15:53 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Combustible extends TcSakbeCombustiblesDto implements Serializable {
  
  private static final long serialVersionUID = 3685016040223492538L;
  private static final Log LOG = LogFactory.getLog(Combustible.class);
  
  private UISelectEntity ikEmpresa;
  private UISelectEntity ikTipoCombustible;
  private UISelectEntity ikTipoMedioPago;
  private UISelectEntity ikBanco;

  public Combustible() {
    this(-1L);
  }
  
  public Combustible(Long idCombustible) {
    super(
      JsfBase.getAutentifica().getPersona().getIdEmpresaPersona(), // idEmpresa      
      1L, // idTipoMedioPago, 
      null, // ticket, 
      null, // lugar, 
      0D, // saldo, 
      1L, // idTipoCombustible, 
      1L, // idCombustibleEstatus, 
      new Long(Fecha.getAnioActual()), // ejercicio, 
      null, // consecutivo, 
      LocalDate.now(), // fecha, 
      0D, // precioLitro, 
      JsfBase.getIdUsuario(), // idUsuario, 
      0D, // litros, 
      0L, // orden, 
      -1L, // idCombustible
      null, // observaciones
      -1L, // idBanco
      null, // referencia
      0D // total
    );    
    this.ikEmpresa= new UISelectEntity(this.getIdEmpresa());
    this.ikTipoCombustible= new UISelectEntity(this.getIdTipoCombustible());
    this.ikTipoMedioPago= new UISelectEntity(this.getIdTipoMedioPago());
    this.ikBanco= new UISelectEntity(this.getIdBanco());
  }
  
  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
  }

  public UISelectEntity getIkTipoCombustible() {
    return ikTipoCombustible;
  }

  public void setIkTipoCombustible(UISelectEntity ikTipoCombustible) {
    this.ikTipoCombustible = ikTipoCombustible;
		if(this.ikTipoCombustible!= null)
		  this.setIdTipoCombustible(this.ikTipoCombustible.getKey());
  }
  
  public UISelectEntity getIkTipoMedioPago() {
    return ikTipoMedioPago;
  }

  public void setIkTipoMedioPago(UISelectEntity ikTipoMedioPago) {
    this.ikTipoMedioPago = ikTipoMedioPago;
		if(this.ikTipoMedioPago!= null)
		  this.setIdTipoMedioPago(this.ikTipoMedioPago.getKey());
  }

  public UISelectEntity getIkBanco() {
    return ikBanco;
  }

  public void setIkBanco(UISelectEntity ikBanco) {
    this.ikBanco = ikBanco;
    if(this.ikBanco!= null)
		  this.setIdBanco(this.ikBanco.getKey());    
  }

  public Boolean isComplete() {
    return Cadena.isVacio(this.getTicket()) && Cadena.isVacio(this.getPrecioLitro()) && Cadena.isVacio(this.getLitros()) && Cadena.isVacio(this.getFecha()) && Cadena.isVacio(this.getTotal());
  }
  
}
