package mx.org.kaana.sakbe.catalogos.tipos.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.sakbe.db.dto.TcSakbeMaquinariasDto;
import mx.org.kaana.sakbe.db.dto.TcSakbeTiposMaquinariasDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/06/2022
 *@time 06:45:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class TipoMaquinaria extends TcSakbeTiposMaquinariasDto implements Serializable {
  
  private static final long serialVersionUID = 3685013042223492538L;
  private static final Log LOG = LogFactory.getLog(TipoMaquinaria.class);
  
  private UISelectEntity ikMaquinariaGrupo;

  public TipoMaquinaria() {
    this(-1L);
  }
  
  public TipoMaquinaria(Long idTipoMaquinaria) {
    super(
      "", // String descripcion, 
      "", // String clave, 
      -1L, // Long idMaquinariaGrupo, 
      idTipoMaquinaria, // Long idTipoMaquinaria, 
      "" // String nombre
    );
    this.ikMaquinariaGrupo= new UISelectEntity(this.getIdMaquinariaGrupo());
  }

  public UISelectEntity getIkMaquinariaGrupo() {
    return ikMaquinariaGrupo;
  }

  public void setIkMaquinariaGrupo(UISelectEntity ikMaquinariaGrupo) {
    this.ikMaquinariaGrupo = ikMaquinariaGrupo;
		if(this.ikMaquinariaGrupo!= null)
		  this.setIdMaquinariaGrupo(this.ikMaquinariaGrupo.getKey());
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeTiposMaquinariasDto.class;
  }
  
  public Boolean isComplete() {
    return Boolean.FALSE;
  }
  
}
