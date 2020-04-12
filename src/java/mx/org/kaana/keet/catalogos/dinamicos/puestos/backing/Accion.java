package mx.org.kaana.keet.catalogos.dinamicos.puestos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;

@Named(value = "keetCatalogosDinamicosPuestosAccion")
@ViewScoped
public class Accion extends mx.org.kaana.keet.catalogos.dinamicos.backing.Accion implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;  	
	
  @PostConstruct
  @Override
  protected void init() {				
		loadEmpresas();
    super.init();		
  } // init

	private void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params= new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} // loadEmpresas
	
	@Override
	public void doLoad() {		
		IBaseDto dto= null;    
		EAccion eaccion        = null;
    try {
			super.doLoad();
			dto= (IBaseDto) this.attrs.get("dto");
			eaccion= (EAccion) this.attrs.get("accion");			
			switch (eaccion) {        
        case MODIFICAR:
        case CONSULTAR:
        case COPIAR:
        case ACTIVAR:
					this.attrs.put("idEmpresa", ((List<UISelectEntity>)this.attrs.get("sucursales")).get(((List<UISelectEntity>)this.attrs.get("sucursales")).indexOf(new UISelectEntity((Long)dto.toValue("idEmpresa")))));          
          break;
      } // switch			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad		

	@Override
  public String doAceptar() {		
    String regresar= null;		
		IBaseDto dto   = null;				
    try {			
			dto= (IBaseDto) this.attrs.get("dto");
			Methods.setValue(dto, "idEmpresa", new Object[]{((UISelectEntity)this.attrs.get("idEmpresa")).getKey()});			
			regresar= super.doAceptar();			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
}
