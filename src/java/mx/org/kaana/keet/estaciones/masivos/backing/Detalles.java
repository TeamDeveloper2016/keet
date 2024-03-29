package mx.org.kaana.keet.estaciones.masivos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 17/12/2018
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "keetEstacionesMasivosDetalles")
@ViewScoped
public class Detalles extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428311L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idMasivaArchivo", JsfBase.getFlashAttribute("idMasivaArchivo"));
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("regreso", JsfBase.getFlashAttribute("regreso")== null? "filtro": JsfBase.getFlashAttribute("regreso"));
		  this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns= null;
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("archivo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("tuplas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));
      this.attrs.put("sortOrder", "order by tc_mantic_masivas_detalles.registro desc");
      this.lazyModel = new FormatCustomLazy("VistaCargasMasivasDto", this.attrs, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad
	
	public String doRegresar() {
		JsfBase.setFlashAttribute("idMasivaArchivo", this.attrs.get("idMasivaArchivo"));
		JsfBase.setFlashAttribute("regreso", this.attrs.get("retorno"));
		return ((String)this.attrs.get("regreso")).concat(Constantes.REDIRECIONAR);
	}
	
}
