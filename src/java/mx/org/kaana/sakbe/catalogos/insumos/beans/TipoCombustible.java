package mx.org.kaana.sakbe.catalogos.insumos.beans;

import java.io.Serializable;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.sakbe.db.dto.TcSakbeTiposCombustiblesDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 09/07/2022
 *@time 09:05:53 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class TipoCombustible extends TcSakbeTiposCombustiblesDto implements Serializable {
  
  private static final long serialVersionUID = 3683013042223492538L;
  private static final Log LOG = LogFactory.getLog(TipoCombustible.class);
  
  private UISelectEntity ikTipoInsumo;
  private String grupo;

  public TipoCombustible() {
    this(-1L);
  }
  
  public TipoCombustible(Long idTipoCombustible) {
    super(
      "", // String descripcion, 
      JsfBase.getIdUsuario(), // Long idUsuario, 
      -1L, // Long idTipoCombustible, 
      "", // String nombre, 
      -1L // Long idTipoInsumo      
    );
    this.ikTipoInsumo= new UISelectEntity(this.getIdTipoInsumo());
  }

  public UISelectEntity getIkTipoInsumo() {
    return ikTipoInsumo;
  }

  public void setIkTipoInsumo(UISelectEntity ikTipoInsumo) {
    this.ikTipoInsumo = ikTipoInsumo;
		if(this.ikTipoInsumo!= null)
		  this.setIdTipoInsumo(this.ikTipoInsumo.getKey());
  }

  public String getGrupo() {
    return grupo;
  }

  public void setGrupo(String grupo) {
    this.grupo = grupo;
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeTiposCombustiblesDto.class;
  }
  
  public Boolean isComplete() {
    return Boolean.FALSE;
  }
  
}
