package mx.org.kaana.keet.catalogos.diasfestivos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.diasfestivos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetDiasFestivosDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetCatalogosDiasFestivosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private TcKeetDiasFestivosDto dto;

	public TcKeetDiasFestivosDto getDto() {
		return dto;
	}

	public void setDto(TcKeetDiasFestivosDto dto) {
		this.dto = dto;
	}	
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idDia", JsfBase.getFlashAttribute("idDia"));			      
      loadCombos();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private void loadCombos(){
		List<Columna> columns     = null;
    Map<String, Object> params= null;		
    try {
			columns= new ArrayList<>();			
			params = new HashMap<>();
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(((List<UISelectEntity>)this.attrs.get("empresas"))));      			
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // loadCombos

  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.dto= new TcKeetDiasFestivosDto();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
        case SUBIR:					
					this.dto= (TcKeetDiasFestivosDto) DaoFactory.getInstance().findById(TcKeetDiasFestivosDto.class, (Long)this.attrs.get("idDia"));
          this.attrs.put("idEmpresa", new UISelectEntity(this.dto.getIdEmpresa()));
					this.attrs.put("oficial", this.dto.getIdOficial().equals(1L));
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");
      this.dto.setIdUsuario(JsfBase.getIdUsuario());
			this.dto.setIdEmpresa(((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
			this.dto.setIdOficial(Boolean.valueOf(this.attrs.get("oficial").toString()) ? 1L : 2L);
			this.dto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));
			transaccion= new Transaccion(this.dto);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idDia", this.dto.getIdDiaFestivo());
				regresar =  "filtro".concat(Constantes.REDIRECIONAR);
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el día festivo de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el día festivo.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idDia", this.dto.getIdDiaFestivo());
    return "filtro".concat(Constantes.REDIRECIONAR);
  } // doAccion		
}