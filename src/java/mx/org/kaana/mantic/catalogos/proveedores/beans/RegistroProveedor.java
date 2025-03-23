package mx.org.kaana.mantic.catalogos.proveedores.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.mantic.catalogos.proveedores.reglas.MotorBusqueda;
import mx.org.kaana.mantic.catalogos.proveedores.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresPortalesDto;
import mx.org.kaana.mantic.enums.ETiposCuentas;

public class RegistroProveedor implements Serializable{
	
	private static final long serialVersionUID = 4690869520445115664L;
	private Long idProveedor;
	private TcManticProveedoresDto proveedor;
	private TcManticProveedoresPortalesDto portal;
	private List<ProveedorDomicilio> proveedoresDomicilio;
	private ProveedorDomicilio proveedorDomicilioSeleccion;
	private List<ProveedorTipoContacto> proveedoresTipoContacto;
	private ProveedorTipoContacto proveedorTipoContactoSeleccion;
	private List<ProveedorDepartamento> proveedoresDepartamentos;
	private ProveedorDepartamento proveedorDepartamentoSeleccion;
	private List<ProveedorBanca> proveedoresServicio;
	private ProveedorBanca proveedorServicioSeleccion;
	private List<ProveedorBanca> proveedoresTransferencia;
	private ProveedorBanca proveedorTransferenciaSeleccion;
	private List<IBaseDto> deleteList;
	private ContadoresListas contadores;
	private Long countIndice;
	private Domicilio domicilio;
	private Domicilio domicilioPivote;
	private List<ProveedorContactoAgente> personasTiposContacto;
	private PersonaTipoContacto personaTipoContactoSeleccion;
	private ProveedorContactoAgente personaTipoContactoPivote;
	private ProveedorContactoAgente personaTipoContacto;
	private List<ProveedorCondicionPago> proveedoresCondicionPago;
	private ProveedorCondicionPago proveedorCondicionPagoSeleccion;
	private List<ProveedorMaterial> proveedoresMateriales;
	private ProveedorMaterial proveedorMaterialSeleccion;	
	private List<ProveedorArticulo> proveedorArticulos;
	private ProveedorArticulo proveedorArticuloSeleccion;
	private List<ProveedorArticuloCliente> proveedorArticulosCliente;
	private ProveedorArticuloCliente proveedorArticuloClienteSeleccion;
	private List<ProveedorFamilia> proveedoresFamilias;
	private ProveedorFamilia proveedorFamilia;
	
	public RegistroProveedor() {
		this(-1L, new TcManticProveedoresDto(), new ProveedorDomicilio(), new ArrayList<ProveedorTipoContacto>(), new Domicilio(), new Domicilio(), new ArrayList<ProveedorContactoAgente>(), new ProveedorContactoAgente(), new ProveedorContactoAgente(), new ArrayList<ProveedorCondicionPago>(), new ArrayList<ProveedorDomicilio>(), new TcManticProveedoresPortalesDto(), new ArrayList<ProveedorBanca>(), new ArrayList<ProveedorBanca>(), new ArrayList<ProveedorMaterial>(), new ProveedorMaterial(), new ArrayList<ProveedorDepartamento>(), new ArrayList<ProveedorArticulo>(), new ProveedorArticulo(), new ArrayList<ProveedorArticuloCliente>(), new ProveedorArticuloCliente(), new ArrayList<ProveedorFamilia>(), new ProveedorFamilia());
	} // RegistroProveedor

	public RegistroProveedor(Long idProveedor) {
		this.idProveedor              = idProveedor;
		this.contadores               = new ContadoresListas();
		this.countIndice              = 0L;
		this.deleteList               = new ArrayList<>();
		this.domicilio                = new Domicilio();
		this.domicilioPivote          = new Domicilio();
		this.personaTipoContactoPivote= new ProveedorContactoAgente();
		this.personaTipoContacto      = new ProveedorContactoAgente();
		init();
	} // RegistroProveedor

	public RegistroProveedor(Long idProveedor, TcManticProveedoresDto proveedor, ProveedorDomicilio proveedorDomicilioSeleccion, List<ProveedorTipoContacto> proveedoresTipoContacto, Domicilio domicilio, Domicilio domicilioPivote, List<ProveedorContactoAgente> personasTiposContacto, ProveedorContactoAgente personaTipoContactoPivote, ProveedorContactoAgente personaTipoContacto, List<ProveedorCondicionPago> proveedoresCondicionPago, List<ProveedorDomicilio> proveedoresDomicilio, TcManticProveedoresPortalesDto portal, List<ProveedorBanca> proveedoresServicio, List<ProveedorBanca> proveedoresTransferencia, List<ProveedorMaterial> proveedoresMateriales, ProveedorMaterial proveedorMaterialSeleccion, List<ProveedorDepartamento> proveedoresDepartamentos, List<ProveedorArticulo> proveedorArticulos, ProveedorArticulo proveedorArticuloSeleccion, List<ProveedorArticuloCliente> proveedorArticulosCliente, ProveedorArticuloCliente proveedorArticuloClienteSeleccion, List<ProveedorFamilia> proveedoresFamilias, ProveedorFamilia proveedorFamilia) {
		this.idProveedor                      = idProveedor;
		this.proveedor                        = proveedor;
		this.proveedorDomicilioSeleccion      = proveedorDomicilioSeleccion;
		this.proveedoresTipoContacto          = proveedoresTipoContacto;
		this.deleteList                       = new ArrayList<>();
		this.contadores                       = new ContadoresListas(); 
		this.countIndice                      = 0L;
		this.domicilio                        = domicilio;
		this.domicilioPivote                  = domicilioPivote;
		this.personasTiposContacto            = personasTiposContacto;
		this.personaTipoContactoPivote        = personaTipoContactoPivote;
		this.personaTipoContacto              = personaTipoContacto;
		this.proveedoresCondicionPago         = proveedoresCondicionPago;
		this.proveedoresDomicilio             = proveedoresDomicilio;
		this.portal                           = portal;
		this.proveedoresServicio              = proveedoresServicio;
		this.proveedoresTransferencia         = proveedoresTransferencia;
		this.proveedoresMateriales            = proveedoresMateriales;
		this.proveedorMaterialSeleccion       = proveedorMaterialSeleccion;
		this.proveedoresDepartamentos         = proveedoresDepartamentos;
		this.proveedorArticulos               = proveedorArticulos;
		this.proveedorArticuloSeleccion       = proveedorArticuloSeleccion;
		this.proveedorArticulosCliente        = proveedorArticulosCliente;
		this.proveedorArticuloClienteSeleccion= proveedorArticuloClienteSeleccion;
		this.proveedoresFamilias              = proveedoresFamilias;
		this.proveedorFamilia                 = proveedorFamilia;
	} // RegistroProveedor	

	public Long getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Long idProveedor) {
		this.idProveedor = idProveedor;
	}

	public TcManticProveedoresDto getProveedor() {
		return proveedor;
	}

	public void setProveedor(TcManticProveedoresDto proveedor) {
		this.proveedor = proveedor;
	}

	public List<ProveedorDomicilio> getProveedoresDomicilio() {
		return proveedoresDomicilio;
	}

	public void setProveedoresDomicilio(List<ProveedorDomicilio> proveedoresDomicilio) {
		this.proveedoresDomicilio = proveedoresDomicilio;
	}

	public ProveedorDomicilio getProveedorDomicilioSeleccion() {
		return proveedorDomicilioSeleccion;
	}

	public void setProveedorDomicilioSeleccion(ProveedorDomicilio proveedorDomicilioSeleccion) {
		this.proveedorDomicilioSeleccion = proveedorDomicilioSeleccion;
	}

	public List<ProveedorTipoContacto> getProveedoresTipoContacto() {
		return proveedoresTipoContacto;
	}

	public void setProveedoresTipoContacto(List<ProveedorTipoContacto> proveedoresTipoContacto) {
		this.proveedoresTipoContacto = proveedoresTipoContacto;
	}

	public ProveedorTipoContacto getProveedorTipoContactoSeleccion() {
		return proveedorTipoContactoSeleccion;
	}

	public void setProveedorTipoContactoSeleccion(ProveedorTipoContacto proveedorTipoContactoSeleccion) {
		this.proveedorTipoContactoSeleccion = proveedorTipoContactoSeleccion;
	}

	public List<IBaseDto> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<IBaseDto> deleteList) {
		this.deleteList = deleteList;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}

	public Domicilio getDomicilioPivote() {
		return domicilioPivote;
	}

	public void setDomicilioPivote(Domicilio domicilioPivote) {
		this.domicilioPivote = domicilioPivote;
	}

	public List<ProveedorContactoAgente> getPersonasTiposContacto() {
		return personasTiposContacto;
	}

	public void setPersonasTiposContacto(List<ProveedorContactoAgente> personasTiposContacto) {
		this.personasTiposContacto = personasTiposContacto;
	}

	public PersonaTipoContacto getPersonaTipoContactoSeleccion() {
		return personaTipoContactoSeleccion;
	}

	public void setPersonaTipoContactoSeleccion(PersonaTipoContacto personaTipoContactoSeleccion) {
		this.personaTipoContactoSeleccion = personaTipoContactoSeleccion;
	}

	public ProveedorContactoAgente getPersonaTipoContactoPivote() {
		return personaTipoContactoPivote;
	}

	public void setPersonaTipoContactoPivote(ProveedorContactoAgente personaTipoContactoPivote) {
		this.personaTipoContactoPivote = personaTipoContactoPivote;
	}

	public ProveedorContactoAgente getPersonaTipoContacto() {
		return personaTipoContacto;
	}

	public void setPersonaTipoContacto(ProveedorContactoAgente personaTipoContacto) {
		this.personaTipoContacto = personaTipoContacto;
	}

	public List<ProveedorCondicionPago> getProveedoresCondicionPago() {
		return proveedoresCondicionPago;
	}

	public void setProveedoresCondicionPago(List<ProveedorCondicionPago> proveedoresCondicionPago) {
		this.proveedoresCondicionPago = proveedoresCondicionPago;
	}

	public ProveedorCondicionPago getProveedorCondicionPagoSeleccion() {
		return proveedorCondicionPagoSeleccion;
	}

	public void setProveedorCondicionPagoSeleccion(ProveedorCondicionPago proveedorCondicionPagoSeleccion) {
		this.proveedorCondicionPagoSeleccion = proveedorCondicionPagoSeleccion;
	}

	public TcManticProveedoresPortalesDto getPortal() {
		return portal;
	}

	public void setPortal(TcManticProveedoresPortalesDto portal) {
		this.portal = portal;
	}

	public List<ProveedorBanca> getProveedoresServicio() {
		return proveedoresServicio;
	}

	public void setProveedoresServicio(List<ProveedorBanca> proveedoresServicio) {
		this.proveedoresServicio = proveedoresServicio;
	}

	public ProveedorBanca getProveedorServicioSeleccion() {
		return proveedorServicioSeleccion;
	}

	public void setProveedorServicioSeleccion(ProveedorBanca proveedorServicioSeleccion) {
		this.proveedorServicioSeleccion = proveedorServicioSeleccion;
	}

	public List<ProveedorBanca> getProveedoresTransferencia() {
		return proveedoresTransferencia;
	}

	public void setProveedoresTransferencia(List<ProveedorBanca> proveedoresTransferencia) {
		this.proveedoresTransferencia = proveedoresTransferencia;
	}

	public ProveedorBanca getProveedorTransferenciaSeleccion() {
		return proveedorTransferenciaSeleccion;
	}

	public void setProveedorTransferenciaSeleccion(ProveedorBanca proveedorTransferenciaSeleccion) {
		this.proveedorTransferenciaSeleccion = proveedorTransferenciaSeleccion;
	}

	public List<ProveedorMaterial> getProveedoresMateriales() {
		return proveedoresMateriales;
	}

	public void setProveedoresMateriales(List<ProveedorMaterial> proveedoresMateriales) {
		this.proveedoresMateriales = proveedoresMateriales;
	}

	public ProveedorMaterial getProveedorMaterialSeleccion() {
		return proveedorMaterialSeleccion;
	}

	public void setProveedorMaterialSeleccion(ProveedorMaterial proveedorMaterialSeleccion) {
		this.proveedorMaterialSeleccion = proveedorMaterialSeleccion;
	}	

	public List<ProveedorDepartamento> getProveedoresDepartamentos() {
		return proveedoresDepartamentos;
	}

	public void setProveedoresDepartamentos(List<ProveedorDepartamento> proveedoresDepartamentos) {
		this.proveedoresDepartamentos = proveedoresDepartamentos;
	}

	public ProveedorDepartamento getProveedorDepartamentoSeleccion() {
		return proveedorDepartamentoSeleccion;
	}

	public void setProveedorDepartamentoSeleccion(ProveedorDepartamento proveedorDepartamentoSeleccion) {
		this.proveedorDepartamentoSeleccion = proveedorDepartamentoSeleccion;
	}	

	public List<ProveedorArticulo> getProveedorArticulos() {
		return proveedorArticulos;
	}

	public void setProveedorArticulos(List<ProveedorArticulo> proveedorArticulos) {
		this.proveedorArticulos = proveedorArticulos;
	}

	public ProveedorArticulo getProveedorArticuloSeleccion() {
		return proveedorArticuloSeleccion;
	}

	public void setProveedorArticuloSeleccion(ProveedorArticulo proveedorArticuloSeleccion) {
		this.proveedorArticuloSeleccion = proveedorArticuloSeleccion;
	}

	public List<ProveedorArticuloCliente> getProveedorArticulosCliente() {
		return proveedorArticulosCliente;
	}

	public void setProveedorArticulosCliente(List<ProveedorArticuloCliente> proveedorArticulosCliente) {
		this.proveedorArticulosCliente = proveedorArticulosCliente;
	}

	public ProveedorArticuloCliente getProveedorArticuloClienteSeleccion() {
		return proveedorArticuloClienteSeleccion;
	}

	public void setProveedorArticuloClienteSeleccion(ProveedorArticuloCliente proveedorArticuloClienteSeleccion) {
		this.proveedorArticuloClienteSeleccion = proveedorArticuloClienteSeleccion;
	}	

	public List<ProveedorFamilia> getProveedoresFamilias() {
		return proveedoresFamilias;
	}

	public void setProveedoresFamilias(List<ProveedorFamilia> proveedoresFamilias) {
		this.proveedoresFamilias = proveedoresFamilias;
	}

	public ProveedorFamilia getProveedorFamilia() {
		return proveedorFamilia;
	}

	public void setProveedorFamilia(ProveedorFamilia proveedorFamilia) {
		this.proveedorFamilia = proveedorFamilia;
	}	
	
	private void init(){
		MotorBusqueda motorBusqueda= null;
		try {
			motorBusqueda= new MotorBusqueda(this.idProveedor);
			this.proveedor= motorBusqueda.toProveedor();					
			this.portal= motorBusqueda.toPortal();
			initCollections(motorBusqueda);
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
	} // init
	
	private void initCollections(MotorBusqueda motor) throws Exception{
		int count= 0;
		try {
			this.proveedoresDomicilio= motor.toProveedoresDomicilio(true);
			for(ProveedorDomicilio proveedorDomicilio: this.proveedoresDomicilio){
				count++;
				proveedorDomicilio.setConsecutivo(Long.valueOf(count));
			} // for				
			this.proveedoresTipoContacto= motor.toProveedoresTipoContacto();			
			this.proveedoresDepartamentos= motor.toProveedoresDepartamentos();			
			this.personasTiposContacto= motor.toAgentes();
			this.proveedoresCondicionPago= motor.toCondicionesPago();
			this.proveedoresServicio= motor.toServicios();
			this.proveedoresTransferencia= motor.toTransferencias();
			this.proveedoresMateriales= motor.toMateriales();
			// this.proveedorArticulos= motor.toArticulos();
			// this.proveedorArticulosCliente= motor.toArticulosCliente();
			this.proveedoresFamilias= motor.toFamilias();
		} // try
		catch (Exception e) {
			Error.mensaje(e);			
			throw e;
		} // catch		
	} // initCollections
	
	public void doAgregarProveedorDomicilio(){
		ProveedorDomicilio proveedorDomicilio= null;
		try {								
			proveedorDomicilio= new ProveedorDomicilio(this.contadores.getTotalProveedoresDomicilios() + this.countIndice, ESql.INSERT, true);	
			setValuesProveedorDomicilio(proveedorDomicilio, false);			
			this.proveedoresDomicilio.add(proveedorDomicilio);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorDomicilio
	
	public void doEliminarProveedorDomicilio(){
		try {			
			if(this.proveedoresDomicilio.remove(this.proveedorDomicilioSeleccion)){
				if(!this.proveedorDomicilioSeleccion.getNuevo())
					addDeleteList(this.proveedorDomicilioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el domicilio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el domicilio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorDomicilio	
	
	public void doConsultarProveedorDomicilio(){
		ProveedorDomicilio pivote= null;
		try {			
			pivote= this.proveedoresDomicilio.get(this.proveedoresDomicilio.indexOf(this.proveedorDomicilioSeleccion));
			pivote.setModificar(true);
			this.domicilioPivote= new Domicilio();
			this.domicilioPivote.setIdTipoDomicilio(pivote.getIdTipoDomicilio());
			this.domicilioPivote.setPrincipal(pivote.getIdPrincipal().equals(1L));	
			if(pivote.getDomicilio() != null){
				this.domicilioPivote.setIdDomicilio(pivote.getDomicilio().getKey());
				this.domicilioPivote.setDomicilio(pivote.getDomicilio());
			} // if
			this.domicilioPivote.setIdEntidad(pivote.getIdEntidad());
			this.domicilioPivote.setIdMunicipio(pivote.getIdMunicipio());
			this.domicilioPivote.setLocalidad(pivote.getIdLocalidad());
			this.domicilioPivote.setIdLocalidad(pivote.getIdLocalidad().getKey());
			this.domicilioPivote.setCodigoPostal(pivote.getCodigoPostal());
			this.domicilioPivote.setCalle(pivote.getCalle());
			this.domicilioPivote.setNumeroExterior(pivote.getExterior());
			this.domicilioPivote.setNumeroInterior(pivote.getInterior());
			this.domicilioPivote.setAsentamiento(pivote.getColonia());
			this.domicilioPivote.setEntreCalle(pivote.getEntreCalle());
			this.domicilioPivote.setYcalle(pivote.getyCalle());
			this.domicilioPivote.setNuevoCp(pivote.getCodigoPostal()!= null && !Cadena.isVacio(pivote.getCodigoPostal()));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doConsultarProveedorDomicilio
	
	public void doActualizarProveedorDomicilio(){
		ProveedorDomicilio pivote= null;
		try {			
			pivote= this.proveedoresDomicilio.get(this.proveedoresDomicilio.indexOf(this.proveedorDomicilioSeleccion));			
			pivote.setModificar(false);
			setValuesProveedorDomicilio(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doActualizarProveedorDomicilio
	
	private void setValuesProveedorDomicilio(ProveedorDomicilio proveedorDomicilio, boolean actualizar) throws Exception{
		try {
			if(this.domicilio.getPrincipal()){
				for(ProveedorDomicilio record: this.proveedoresDomicilio)
					record.setIdPrincipal(0L);
			} // if
			proveedorDomicilio.setIdPrincipal(this.domicilio.getPrincipal() ? 1L : 2L);	
			if(this.domicilio.getDomicilio()!= null){
				proveedorDomicilio.setDomicilio(this.domicilio.getDomicilio());
				proveedorDomicilio.setIdDomicilio(this.domicilio.getDomicilio().getKey());
			} // if
			proveedorDomicilio.setIdUsuario(JsfBase.getIdUsuario());
			proveedorDomicilio.setIdTipoDomicilio(this.domicilio.getIdTipoDomicilio());
			if(!actualizar)
				proveedorDomicilio.setConsecutivo(this.proveedoresDomicilio.size() + 1L);
			proveedorDomicilio.setIdEntidad(this.domicilio.getIdEntidad());
			proveedorDomicilio.setIdMunicipio(this.domicilio.getIdMunicipio());
			proveedorDomicilio.setIdLocalidad(this.domicilio.getLocalidad());
			proveedorDomicilio.setCodigoPostal(this.domicilio.getCodigoPostal());
			proveedorDomicilio.setCalle(this.domicilio.getCalle());
			proveedorDomicilio.setExterior(this.domicilio.getNumeroExterior());
			proveedorDomicilio.setInterior(this.domicilio.getNumeroInterior());
			proveedorDomicilio.setEntreCalle(this.domicilio.getEntreCalle());
			proveedorDomicilio.setyCalle(this.domicilio.getYcalle());
			proveedorDomicilio.setColonia(this.domicilio.getAsentamiento());
			proveedorDomicilio.setNuevoCp(this.domicilio.getCodigoPostal()!= null && !Cadena.isVacio(this.domicilio.getCodigoPostal()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // setValuesProveedorDomicilio
	
	public void doAgregarProveedorCondicionPago(){
		ProveedorCondicionPago proveedorCondicionPago= null;
		try {					
			proveedorCondicionPago= new ProveedorCondicionPago(this.contadores.getTotalProveedoresPago() + this.countIndice, ESql.INSERT, true);				
			proveedorCondicionPago.setIdTipoPago(1L);
			this.proveedoresCondicionPago.add(proveedorCondicionPago);		
			// doValidaTipoPago(proveedorCondicionPago);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorTipoContacto
	
	public void doAgregarProveedorTipoContacto(){
		ProveedorTipoContacto proveedorTipoContacto= null;
		try {					
			proveedorTipoContacto= new ProveedorTipoContacto(this.contadores.getTotalProveedoresTipoContacto()+ this.countIndice, ESql.INSERT, true);				
			proveedorTipoContacto.setOrden(this.proveedoresTipoContacto.size() + 1L);
			this.proveedoresTipoContacto.add(proveedorTipoContacto);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorTipoContacto
	
	public void doAgregarProveedorDepartamento(){
		ProveedorDepartamento proveedorDepartamento= null;
		try {					
			proveedorDepartamento= new ProveedorDepartamento(this.contadores.getTotalProveedoresDepartamentos()+ this.countIndice, ESql.INSERT, true);							
			this.proveedoresDepartamentos.add(proveedorDepartamento);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorTipoContacto
	
	public void doAgregarProveedorServicio(){
		ProveedorBanca proveedorBanca= null;
		try {					
			proveedorBanca= new ProveedorBanca(this.contadores.getTotalProveedoresServicio()+ this.countIndice, ESql.INSERT, true);				
			proveedorBanca.setIdTipoCuenta(ETiposCuentas.SERVICIOS.getKey());
			this.proveedoresServicio.add(proveedorBanca);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorTipoContacto
	
	public void doAgregarProveedorTransferencia(){
		ProveedorBanca proveedorBanca= null;
		try {					
			proveedorBanca= new ProveedorBanca(this.contadores.getTotalProveedoresTransferencia()+ this.countIndice, ESql.INSERT, true);				
			proveedorBanca.setIdTipoCuenta(ETiposCuentas.TRANSFERENCIAS.getKey());
			this.proveedoresTransferencia.add(proveedorBanca);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorTipoContacto
	
	public void doAgregarAgenteContacto(){
		PersonaTipoContacto personaTipoContacto= null;
		try {
			personaTipoContacto= new PersonaTipoContacto(this.contadores.getTotalPersonasTipoContacto() + this.countIndice, ESql.INSERT, true, "");
			this.personaTipoContactoPivote.getContactos().add(personaTipoContacto);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			this.countIndice++;
		} // finally
	}
	
	public void doEliminarProveedorCondicionPago(){
		try {			
			if(this.proveedoresCondicionPago.remove(this.proveedorCondicionPagoSeleccion)){
				if(!this.proveedorCondicionPagoSeleccion.getNuevo())
					addDeleteList(this.proveedorCondicionPagoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de pago", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el tipo de pago", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorCondicionPago
	
	public void doEliminarProveedorDepartamento(){
		try {			
			if(this.proveedoresDepartamentos.remove(this.proveedorDepartamentoSeleccion)){
				if(!this.proveedorDepartamentoSeleccion.getNuevo())
					addDeleteList(this.proveedorDepartamentoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el departamento", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el departamento", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorDepartamento
	
	public void doEliminarProveedorTipoContacto(){
		try {			
			if(this.proveedoresTipoContacto.remove(this.proveedorTipoContactoSeleccion)){
				if(!this.proveedorTipoContactoSeleccion.getNuevo())
					addDeleteList(this.proveedorTipoContactoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorTipoContacto
	
	public void doEliminarProveedorServicio(){
		try {			
			if(this.proveedoresServicio.remove(this.proveedorServicioSeleccion)){
				if(!this.proveedorServicioSeleccion.getNuevo())
					addDeleteList(this.proveedorServicioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el registro de servicio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el registro de servicio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorTipoContacto
	
	public void doEliminarProveedorTransferencia(){
		try {			
			if(this.proveedoresTransferencia.remove(this.proveedorTransferenciaSeleccion)){
				if(!this.proveedorTransferenciaSeleccion.getNuevo())
					addDeleteList(this.proveedorTransferenciaSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el registro de transferencia", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el registro de transferencia", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorTipoContacto
	
	public void doEliminarAgenteContacto(){
		try {			
			if(this.personaTipoContactoPivote.getContactos().remove(this.personaTipoContactoSeleccion)){
				if(!this.personaTipoContactoSeleccion.getNuevo())
					addDeleteList(this.personaTipoContactoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorTipoContacto
	
	private void addDeleteList(IBaseDto dto) throws Exception{
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);
			//this.deleteList.add(dto);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // addDeleteList
	
	public void doAgregarAgente(){
		ProveedorContactoAgente proveedorContactoAgente= null;
		try {								
			proveedorContactoAgente= new ProveedorContactoAgente(this.contadores.getTotalProveedoresAgentes() + this.countIndice, ESql.INSERT, true);				
			proveedorContactoAgente.setConsecutivo(this.personasTiposContacto.size()+1L);
			proveedorContactoAgente.setNombres(this.personaTipoContactoPivote.getNombres());
			proveedorContactoAgente.setPaterno(this.personaTipoContactoPivote.getPaterno());
			proveedorContactoAgente.setMaterno(this.personaTipoContactoPivote.getMaterno());
			proveedorContactoAgente.setContactos(this.personaTipoContactoPivote.getContactos());
			this.personasTiposContacto.add(proveedorContactoAgente);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarClienteDomicilio
	
	public void doConsultarAgente() {
		ProveedorContactoAgente pivote= null;
		try {			
			pivote= this.personasTiposContacto.get(this.personasTiposContacto.indexOf(this.personaTipoContacto));
			pivote.setModificar(true);
			this.personaTipoContactoPivote= new ProveedorContactoAgente();
			this.personaTipoContactoPivote.setNombres(pivote.getNombres());
			this.personaTipoContactoPivote.setPaterno(pivote.getPaterno());
			this.personaTipoContactoPivote.setMaterno(pivote.getMaterno());
			this.personaTipoContactoPivote.setContactos(pivote.getContactos());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doConsultarAgente
	
	public void doEliminarAgente() {
		try {
			if(this.personasTiposContacto.remove(this.personaTipoContacto)) {
				if(!this.personaTipoContacto.getNuevo() && !Objects.equals(this.personaTipoContacto, null))
					addDeleteList(this.personaTipoContacto);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doEliminarAgente
	
	public void doActualizaAgente() {
		ProveedorContactoAgente pivote= null;
		try {			
			pivote= this.personasTiposContacto.get(this.personasTiposContacto.indexOf(this.personaTipoContacto));
			pivote.setModificar(false);
			pivote.setNombres(this.personaTipoContactoPivote.getNombres());
			pivote.setPaterno(this.personaTipoContactoPivote.getPaterno());
			pivote.setMaterno(this.personaTipoContactoPivote.getMaterno());
			pivote.setContactos(this.personaTipoContactoPivote.getContactos());			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch	
	} // doActualizaAgente
	
	public void doValidaTipoPago(ProveedorCondicionPago condicionPago) {
		int countEfectivo=0;
		try {
			if(this.proveedoresCondicionPago.size()> 1) {
				for(ProveedorCondicionPago record: this.proveedoresCondicionPago) {
					if(record.getIdTipoPago().equals(1L) || record.getIdTipoPago().equals(3L))
						countEfectivo++;
				} // for
				if(countEfectivo> 2 && (condicionPago.getIdTipoPago().equals(1L) || condicionPago.getIdTipoPago().equals(3L))) {
					this.proveedoresCondicionPago.get(this.proveedoresCondicionPago.indexOf(condicionPago)).setIdTipoPago(2L);
				} // if
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doValidaTipoPago
	
	public void doAgregarProveedorMaterial(Long idArticulo, String nombre, String propio, String unidadMedida){
		ProveedorMaterial proveedorMaterial= null;
		try {					
			proveedorMaterial= new ProveedorMaterial(this.contadores.getTotalProveedoresMateriales()+ this.countIndice, ESql.INSERT, true);							
			proveedorMaterial.setIdArticulo(idArticulo);
			proveedorMaterial.setNombre(nombre);
			proveedorMaterial.setPropio(propio);
			proveedorMaterial.setUnidadMedida(unidadMedida);
			this.proveedoresMateriales.add(proveedorMaterial);					
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorMaterial
	
	public void doEliminarProveedorMaterial(){
		try {			
			if(this.proveedoresMateriales.remove(this.proveedorMaterialSeleccion)){
				if(!this.proveedorMaterialSeleccion.getNuevo())
					addDeleteList(this.proveedorMaterialSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el material", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el material", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorMaterial
	
	public void doAgregarProveedorArticulo(){
		ProveedorArticulo proveedorArticulo= null;
		UISelectEntity pivote             = null;
		Map<String, Object> params        = null;
		try {					
			for(ProveedorArticulo record: this.proveedorArticulos){
				if(record.getSqlAccion().equals(ESql.UPDATE)){
					pivote= new UISelectEntity(record.getIdArticulo());
					pivote.put("nombre", new Value("nombre", record.getNombre()));
					record.setUiArticulo(pivote);
				} // if
				else{					
					params= new HashMap<>();
					params.put("idArticulo", record.getIdArticulo());
					pivote= new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("VistaOrdenesComprasDto", "articuloEspecifico", params));
					record.setNombre(pivote.toString("nombre"));								
					record.setUiArticulo(pivote);					
				} // if
			} // for
			proveedorArticulo= new ProveedorArticulo(this.contadores.getTotalProveedoresArticulos()+ this.countIndice, ESql.INSERT, true);
			this.proveedorArticulos.add(proveedorArticulo);					
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorArticulo
	
	public void doEliminarProveedorArticulo(){
		try {			
			if(this.proveedorArticulos.remove(this.proveedorArticuloSeleccion)){
				if(!this.proveedorArticuloSeleccion.getNuevo())
					addDeleteList(this.proveedorArticuloSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el articulo", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el articulo", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorArticulo
	
	public void doAgregarProveedorArticuloCliente(){
		ProveedorArticuloCliente proveedorArticuloCliente= null;
		UISelectEntity pivote                            = null;
		UISelectEntity pivoteCliente                     = null;
		Map<String, Object> params                       = null;
		try {					
			for(ProveedorArticuloCliente proveedorArticulo: this.proveedorArticulosCliente){
				if(proveedorArticulo.getSqlAccion().equals(ESql.UPDATE)){
					pivote= new UISelectEntity(proveedorArticulo.getIdArticulo());
					pivote.put("nombre", new Value("nombre", proveedorArticulo.getNombre()));
					proveedorArticulo.setUiArticulo(pivote);
					pivoteCliente= new UISelectEntity(proveedorArticulo.getIdCliente());
					pivoteCliente.put("cliente", new Value("cliente", proveedorArticulo.getCliente()));
					proveedorArticulo.setUiCliente(pivoteCliente);
				} // if
				else{					
					params= new HashMap<>();
					params.put("idArticulo", proveedorArticulo.getIdArticulo());
					pivote= new UISelectEntity((Entity)DaoFactory.getInstance().toEntity("VistaOrdenesComprasDto", "articuloEspecifico", params));
					proveedorArticulo.setNombre(pivote.toString("nombre"));								
					proveedorArticulo.setUiArticulo(pivote);					
				} // else
			} // for
			proveedorArticuloCliente= new ProveedorArticuloCliente(this.contadores.getTotalProveedoresArticuloCliente()+ this.countIndice, ESql.INSERT, true);
			this.proveedorArticulosCliente.add(proveedorArticuloCliente);					
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorArticulo
	
	public void doEliminarProveedorArticuloCliente(){
		try {			
			if(this.proveedorArticulosCliente.remove(this.proveedorArticuloClienteSeleccion)){
				if(!this.proveedorArticuloClienteSeleccion.getNuevo())
					addDeleteList(this.proveedorArticuloClienteSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el articulo del cliente", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el articulo del cliente", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorArticulo
	
	public void doAgregarFamilia(){
		ProveedorFamilia proveedorFamiliaPivote= null;
		try {								
			proveedorFamiliaPivote= new ProveedorFamilia(this.contadores.getTotalProveedoresFamilia()+ this.countIndice, ESql.INSERT, true);							
			this.proveedoresFamilias.add(proveedorFamiliaPivote);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarFamilia	
	
	public void doEliminarFamilia() {
		try {
			if(this.proveedoresFamilias.remove(this.proveedorFamilia)) {
				if(!this.proveedorFamilia.getNuevo())
					addDeleteList(this.proveedorFamilia);
				JsfBase.addMessage("Se eliminó correctamente la familia seleccionada", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar la familia seleccionada", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doEliminarFamilia
}
