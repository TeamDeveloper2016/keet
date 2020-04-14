package mx.org.kaana.keet.estaciones.backing;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;

@Named(value = "keetEstacionesContrato")
@ViewScoped
public class Contrato extends Filtro{
	
	@PostConstruct
  @Override
  protected void init() {
		Estaciones estaciones=null;
    try {
			estaciones=new Estaciones();
			estaciones.cleanLevels();
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.current= (TcKeetEstacionesDto)JsfBase.getFlashAttribute("estacionProcess");
				actualizarChildren(1);
			} // if
			else{
				this.current=new TcKeetEstacionesDto();
				this.current.setClave("");
				this.current.setNivel(1L);
				actualizarChildren(0, 3);
				this.current.setNivel(3L);
			} // if	
		loadCombos();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	@Override
  public void doLoad() {
    Estaciones estaciones        = null;
		String nodo                  = "";
		TcKeetContratosLotesDto lote= null;
		Value contrato = null;
    try {
			estaciones= new Estaciones();
			if(this.attrs.get("lote")!=null && ((UISelectEntity)this.attrs.get("lote")).getKey()>0L){
				lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(TcKeetContratosLotesDto.class, ((UISelectEntity)this.attrs.get("lote")).getKey());
			  nodo= estaciones.toCodeByIdContrato(lote.getIdContrato());
				this.current=new TcKeetEstacionesDto();
				this.current.setClave(estaciones.toCode(nodo.concat(lote.getOrden().toString())));
				this.current.setNivel(4L);
				actualizarChildren(1);
			} // if
			else if(this.attrs.get("contrato")!=null && ((UISelectEntity)this.attrs.get("contrato")).getKey()>0L) {
				nodo= estaciones.toCodeByIdContrato(((UISelectEntity)this.attrs.get("contrato")).getKey());
				this.current= new TcKeetEstacionesDto();
				this.current.setClave(nodo);
				this.current.setNivel(3L);
				actualizarChildren(1);
			} // else if
			else if(this.attrs.get("idEmpresa")!=null && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>0L) {
					nodo= ((UISelectEntity)this.attrs.get("idEmpresa")).getKey().toString();
					this.current= new TcKeetEstacionesDto();
					this.current.setClave(estaciones.toCode(nodo));
					this.current.setNivel(1L);
					actualizarChildren(1,2);
					this.current.setNivel(3L);
				} // else if
				else
					doInicio();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad

	protected void loadCombos(){
		try {
			loadEmpresas();
			doLoadContratos();
			doLoadLotes();
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public void doLoadLotes(){
		UISelectEntity contrato = null;
	  try {
			contrato = (UISelectEntity)this.attrs.get("contrato");
			if(contrato!= null && contrato.getKey()> 0L) 
			  this.attrs.put(Constantes.SQL_CONDICION, "id_contrato= ".concat(contrato.getKey().toString()));
			else
				this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("lotes", UIEntity.seleccione("TcKeetContratosLotesDto", "row", this.attrs, "clave"));
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadLotes
	
	public void doLoadContratos(){
		UISelectEntity empresa= null;
	  try {
			empresa = (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  this.attrs.put("sucursales", empresa.getKey());
			else
				this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("contratos", UIEntity.seleccione("VistaContratosDto", "byEmpresa", this.attrs, "clave"));
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadContratos
	
	
	
}
