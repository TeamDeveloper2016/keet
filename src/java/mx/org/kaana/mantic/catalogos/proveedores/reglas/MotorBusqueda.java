package mx.org.kaana.mantic.catalogos.proveedores.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.MotorBusquedaCatalogos;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorArticulo;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorArticuloCliente;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorBanca;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorCondicionPago;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorContactoAgente;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorDepartamento;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorDomicilio;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorFamilia;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorMaterial;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorTipoContacto;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresPortalesDto;
import mx.org.kaana.mantic.enums.ETiposCuentas;

public class MotorBusqueda extends MotorBusquedaCatalogos implements Serializable{
	
	private static final long serialVersionUID = 5085305397727758226L;
	private Long idProveedor;
	
	public MotorBusqueda(Long idProveedor) {
		super(idProveedor);
		this.idProveedor= idProveedor;		
	} // MotoBusqueda
	
	public TcManticProveedoresDto toProveedor() throws Exception{
		TcManticProveedoresDto regresar= null;
		try {
			regresar= (TcManticProveedoresDto) DaoFactory.getInstance().findById(TcManticProveedoresDto.class, this.idProveedor);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toProveedor
	
	public List<ProveedorDomicilio> toProveedoresDomicilio() throws Exception{
		return toProveedoresDomicilio(false);
	} // toProveedoresDomicilio
	
	public List<ProveedorDomicilio> toProveedoresDomicilio(boolean update) throws Exception{
		List<ProveedorDomicilio> regresar= null;
		TcManticDomiciliosDto domicilio  = null;
		Map<String, Object>params        = null;
		Entity entityDomicilio           = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor);
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorDomicilio.class, "TrManticProveedorDomicilioDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			for(ProveedorDomicilio proveedorDomicilio: regresar){
				if(proveedorDomicilio.getIdDomicilio()!= null)
					proveedorDomicilio.setIdLocalidad(toLocalidad(proveedorDomicilio.getIdDomicilio()));
				if(proveedorDomicilio.getIdLocalidad()!= null)
					proveedorDomicilio.setIdMunicipio(toMunicipio(proveedorDomicilio.getIdLocalidad().getKey()));
				if(proveedorDomicilio.getIdMunicipio()!= null)
					proveedorDomicilio.setIdEntidad(toEntidad(proveedorDomicilio.getIdMunicipio().getKey()));
				if(update){
					if(proveedorDomicilio.getIdDomicilio()!= null)
						domicilio= toDomicilio(proveedorDomicilio.getIdDomicilio());
					if(domicilio!= null){
						proveedorDomicilio.setCalle(domicilio.getCalle());
						proveedorDomicilio.setCodigoPostal(domicilio.getCodigoPostal());
						proveedorDomicilio.setColonia(domicilio.getAsentamiento());
						proveedorDomicilio.setEntreCalle(domicilio.getEntreCalle());
						proveedorDomicilio.setyCalle(domicilio.getYcalle());
						proveedorDomicilio.setExterior(domicilio.getNumeroExterior());
						proveedorDomicilio.setInterior(domicilio.getNumeroInterior());
					} // if
					if(proveedorDomicilio.getIdDomicilio()!= null){
						entityDomicilio= new Entity(proveedorDomicilio.getIdDomicilio());
						entityDomicilio.put("idEntidad", new Value("idEntidad", proveedorDomicilio.getIdEntidad().getKey()));
						entityDomicilio.put("idMunicipio", new Value("idMunicipio", proveedorDomicilio.getIdMunicipio().getKey()));
						entityDomicilio.put("idLocalidad", new Value("idLocalidad", proveedorDomicilio.getIdLocalidad().getKey()));
						proveedorDomicilio.setDomicilio(entityDomicilio);
					} // if
				} // if				
			} // for
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedoresDomicilio	
	
	public List<ProveedorDepartamento> toProveedoresDepartamentos() throws Exception {
		List<ProveedorDepartamento> regresar= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor);
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorDepartamento.class, "TcKeetProveedoresDepartamentosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedoresDepartamentos

public List<ProveedorTipoContacto> toAllProveedoresTipoContacto() throws Exception {
		List<ProveedorTipoContacto> regresar= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put("idProveedor", this.idProveedor);
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorTipoContacto.class, "TrManticProveedorTipoContactoDto", "contacto", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toAllProveedoresTipoContacto  

	public List<ProveedorContactoAgente> toAgentes() throws Exception{
		List<ProveedorContactoAgente> regresar= null;
		Map<String, Object>params             = null;
		TcManticPersonasDto persona           = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor);
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorContactoAgente.class, "TrManticProveedorAgenteDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			if(!regresar.isEmpty()){
				for(ProveedorContactoAgente contacto: regresar){
					contacto.setContactos(toPersonaContacto(contacto.getIdAgente()));
					persona= toPersona(contacto.getIdAgente());
					contacto.setNombres(persona.getNombres());
					contacto.setPaterno(persona.getPaterno());
					contacto.setMaterno(persona.getMaterno());
				} // for
			} // if
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally		
		return regresar;
	} // toRepresentantes
	
	public List<ProveedorCondicionPago> toCondicionesPago() throws Exception{
		List<ProveedorCondicionPago> regresar= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor);
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorCondicionPago.class, "TrManticProveedorPagoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toCondicionesPago
	
	public TcManticProveedoresPortalesDto toPortal() throws Exception {
		TcManticProveedoresPortalesDto regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor);
			regresar= (TcManticProveedoresPortalesDto) DaoFactory.getInstance().toEntity(TcManticProveedoresPortalesDto.class, "TcManticProveedoresPortalesDto", params);
			if(regresar== null)
				regresar= new TcManticProveedoresPortalesDto();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPortal
	
	public List<ProveedorBanca> toServicios() throws Exception{
		return toProveedorBanca(ETiposCuentas.SERVICIOS);
	} // toServicios
	
	public List<ProveedorBanca> toTransferencias() throws Exception{
		return toProveedorBanca(ETiposCuentas.TRANSFERENCIAS);
	} // toTransferencias
	
	private List<ProveedorBanca> toProveedorBanca(ETiposCuentas tipoCuenta) throws Exception{
		List<ProveedorBanca> regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, " id_proveedor=" + this.idProveedor + " and id_tipo_cuenta=" + tipoCuenta.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorBanca.class, "TcManticProveedoresBancosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedorbanca
	
	public List<ProveedorMaterial> toMateriales() throws Exception{
		List<ProveedorMaterial> regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idProveedor", this.idProveedor);
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());  		
			params.put("codigo", "");
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorMaterial.class, "VistaProveedoresBancosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedorbanca
	
	public List<ProveedorFamilia> toFamilias() throws Exception{
		List<ProveedorFamilia> regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idProveedor);			
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorFamilia.class, "TcKeetProveedoresFamiliasDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedorbanca
	
	public List<ProveedorArticulo> toArticulos() throws Exception{
		List<ProveedorArticulo> regresar= null;
		Map<String, Object>params       = null;
		UISelectEntity pivote           = null;
		try {
			params= new HashMap<>();
			params.put("idProveedor", this.idProveedor);			
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorArticulo.class, "VistaProveedoresArticulosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			if(!regresar.isEmpty()){
				for(ProveedorArticulo proveedorArticulo: regresar){
					pivote= new UISelectEntity(proveedorArticulo.getIdArticulo());
					pivote.put("nombre", new Value("nombre", proveedorArticulo.getNombre()));
					proveedorArticulo.setUiArticulo(pivote);
				} // for
			} // if
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedorbanca		
	
	public List<ProveedorArticuloCliente> toArticulosCliente() throws Exception{
		List<ProveedorArticuloCliente> regresar= null;
		Map<String, Object>params              = null;
		UISelectEntity pivote                  = null;
		UISelectEntity pivoteCliente           = null;
		try {
			params= new HashMap<>();
			params.put("idProveedor", this.idProveedor);			
			regresar= DaoFactory.getInstance().toEntitySet(ProveedorArticuloCliente.class, "VistaProveedorArticuloCliente", "row", params, Constantes.SQL_TODOS_REGISTROS);
			if(!regresar.isEmpty()){
				for(ProveedorArticuloCliente proveedorArticulo: regresar){
					pivote= new UISelectEntity(proveedorArticulo.getIdArticulo());
					pivote.put("nombre", new Value("nombre", proveedorArticulo.getNombre()));
					proveedorArticulo.setUiArticulo(pivote);
					pivoteCliente= new UISelectEntity(proveedorArticulo.getIdCliente());
					pivoteCliente.put("cliente", new Value("cliente", proveedorArticulo.getCliente()));
					proveedorArticulo.setUiCliente(pivoteCliente);
				} // for
			} // if
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedorbanca		
}
