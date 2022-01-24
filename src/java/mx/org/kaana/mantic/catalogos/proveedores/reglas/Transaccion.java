package mx.org.kaana.mantic.catalogos.proveedores.reglas;

import com.google.common.base.Objects;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetArticulosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetMorososDto;
import mx.org.kaana.keet.db.dto.TcKeetProveedoresDepartamentosDto;
import mx.org.kaana.keet.db.dto.TcKeetProveedoresFamiliasDto;
import mx.org.kaana.keet.db.dto.TcKeetProveedoresMaterialesDto;
import mx.org.kaana.keet.db.dto.TrKeetArticuloProveedorClienteDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
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
import mx.org.kaana.mantic.catalogos.proveedores.beans.RegistroProveedor;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresBancosDto;
import mx.org.kaana.mantic.db.dto.TcManticProveedoresDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticPersonaTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorPagoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorAgenteDto;
import mx.org.kaana.mantic.enums.ETipoPago;
import mx.org.kaana.mantic.enums.ETipoPersona;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  private static final String ESTILO= "sentinel";
	private IBaseDto dto;
	private RegistroProveedor registroProveedor;
	private String messageError;
	
	public Transaccion(IBaseDto dto) {
		this.dto= dto;
	}
	
  public Transaccion(RegistroProveedor registroProveedor) {
    this.registroProveedor= registroProveedor;
  }

  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    boolean regresar = false;
    try {			
      switch (accion) {
        case AGREGAR:
          regresar = this.procesarProveedor(sesion);
          break;
        case MODIFICAR:
          regresar = this.actualizarProveedor(sesion);
          break;
        case ELIMINAR:
          regresar = this.eliminarProveedor(sesion);
          break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dto)>= 1L;
					break;
      } // switch
      if (!regresar) {
        throw new Exception(this.messageError);
      } // if
    } // try
    catch (Exception e) {
      throw new Exception(this.messageError.concat("<br/>")+ e);
    } // catch		
    return regresar;
  } // ejecutar
	
	private boolean procesarProveedor(Session sesion) throws Exception {
    boolean regresar = false;
    Long idProveedor = -1L;
    try {
      this.messageError = "Error al registrar el articulo";
      if (eliminarRegistros(sesion)) {
        this.registroProveedor.getProveedor().setIdUsuario(JsfBase.getIdUsuario());        
        idProveedor = DaoFactory.getInstance().insert(sesion, this.registroProveedor.getProveedor());
        if (registraProveedoresDomicilios(sesion, idProveedor)) {
          if (registraProveedoresAgentes(sesion, idProveedor)) {
						if (registraProveedoresMateriales(sesion, idProveedor)) {
              if(registraProveedoresTipoContacto(sesion, idProveedor)){
                if(registraProveedoresDepartamentos(sesion, idProveedor)){
                  if(registraProveedoresServicios(sesion, idProveedor)){
                    if(registraProveedoresTransferencia(sesion, idProveedor)){
                      if(registraProveedoresFamilias(sesion, idProveedor)){
                        if(registraProveedoresFormaPago(sesion, idProveedor)){
                          this.registroProveedor.getPortal().setIdProveedor(idProveedor);
                          if(this.registroProveedor.getPortal()!= null && !Cadena.isVacio(this.registroProveedor.getPortal().getPagina()) && !Cadena.isVacio(this.registroProveedor.getPortal().getCuenta()) && !Cadena.isVacio(this.registroProveedor.getPortal().getContrasenia())) {
                            regresar= DaoFactory.getInstance().insert(sesion, this.registroProveedor.getPortal())>= 1L;					
                            this.toInsertMoroso(sesion);
                          } // if  
                          else
                            regresar= true;
                        } // if
                      } // if
										} // if
									} // if
								} // if
							} // if
						} // if
          } // if
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    return regresar;
  } // procesarProveedor

  private boolean actualizarProveedor(Session sesion) throws Exception {
    boolean regresar = false;
    Long idProveedor = -1L;
    try {
      idProveedor = this.registroProveedor.getIdProveedor();
      if (registraProveedoresDomicilios(sesion, idProveedor)) {
        if (registraProveedoresAgentes(sesion, idProveedor)) {
					if (registraProveedoresMateriales(sesion, idProveedor)) {
            if (registraProveedoresTipoContacto(sesion, idProveedor)) {
              if (registraProveedoresDepartamentos(sesion, idProveedor)) {
                if(registraProveedoresFormaPago(sesion, idProveedor)){
                  if(registraProveedoresServicios(sesion, idProveedor)){
                    if(registraProveedoresTransferencia(sesion, idProveedor)){
                      if(registraProveedoresFamilias(sesion, idProveedor)){
                        if(this.registroProveedor.getPortal().isValid()){
                          if(DaoFactory.getInstance().update(sesion, this.registroProveedor.getPortal())>= 0L){
                            regresar = DaoFactory.getInstance().update(sesion, this.registroProveedor.getProveedor()) >= 1L;
                          } // if
                        } // if
                        else{										
                          regresar = DaoFactory.getInstance().update(sesion, this.registroProveedor.getProveedor()) >= 1L;
                          if(regresar && this.registroProveedor.getPortal()!= null && !Cadena.isVacio(this.registroProveedor.getPortal().getPagina())  && !Cadena.isVacio(this.registroProveedor.getPortal().getCuenta()) && !Cadena.isVacio(this.registroProveedor.getPortal().getContrasenia())){
                            this.registroProveedor.getPortal().setIdProveedor(idProveedor);
                            regresar= DaoFactory.getInstance().insert(sesion, this.registroProveedor.getPortal())>= 1L;
                          } // if
                        } // else
                      } // if
                    } // if
                  } // if
                } // if
              } // if
            } // if
          } // if
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    return regresar;
  } // actualizarProveedor

  private boolean eliminarProveedor(Session sesion) throws Exception {
    boolean regresar = false;
    Map<String, Object> params = null;
    try {
      params = new HashMap<>();
      params.put("idProveedor", this.dto.getKey());
      if (DaoFactory.getInstance().deleteAll(sesion, TrManticProveedorDomicilioDto.class, params) > -1L) {
				if (DaoFactory.getInstance().deleteAll(sesion, TcKeetProveedoresMaterialesDto.class, params) > -1L) {
					if (DaoFactory.getInstance().deleteAll(sesion, TcKeetArticulosProveedoresDto.class, params) > -1L) {
						if (DaoFactory.getInstance().deleteAll(sesion, TrKeetArticuloProveedorClienteDto.class, params) > -1L) {
							if (DaoFactory.getInstance().deleteAll(sesion, TrManticProveedorAgenteDto.class, params) > -1L) {
								if (DaoFactory.getInstance().deleteAll(sesion, TrManticProveedorTipoContactoDto.class, params) > -1L) {
									if(DaoFactory.getInstance().deleteAll(sesion, TrManticProveedorPagoDto.class, params)> -1L){
										if(DaoFactory.getInstance().deleteAll(sesion, TcKeetProveedoresFamiliasDto.class, params)> -1L)
											regresar = DaoFactory.getInstance().delete(sesion, TcManticProveedoresDto.class, this.dto.getKey()) >= 1L;
									} // if
								} // if
							} // if
						} // if
					} // if
        } // if
      } // if
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } // eliminarProveedor

  private boolean registraProveedoresDomicilios(Session sesion, Long idProveedor) throws Exception {
    TrManticProveedorDomicilioDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    int countPrincipal = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
			if(this.registroProveedor.getProveedoresDomicilio().size()== 1)
					this.registroProveedor.getProveedoresDomicilio().get(0).setIdPrincipal(1L);
      for (ProveedorDomicilio proveedorDomicilio : this.registroProveedor.getProveedoresDomicilio()) {								
				if(proveedorDomicilio.getIdPrincipal().equals(1L))
					countPrincipal++;
				if(countPrincipal== 0 && this.registroProveedor.getProveedoresDomicilio().size()-1 == count)
					proveedorDomicilio.setIdPrincipal(1L);
        proveedorDomicilio.setIdProveedor(idProveedor);
        proveedorDomicilio.setIdUsuario(JsfBase.getIdUsuario());
				proveedorDomicilio.setIdDomicilio(toIdDomicilio(sesion, proveedorDomicilio));		
        dto = (TrManticProveedorDomicilioDto) proveedorDomicilio;
        sqlAccion = proveedorDomicilio.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdProveedorDomicilio(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroProveedor.getProveedoresDomicilio().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los domicilios, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresDomicilios

  private boolean registraProveedoresArticulos(Session sesion, Long idProveedor) throws Exception {
    TcKeetArticulosProveedoresDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {			
      for (ProveedorArticulo proveedorArticulo : this.registroProveedor.getProveedorArticulos()) {								
        proveedorArticulo.setIdProveedor(idProveedor);
        proveedorArticulo.setIdUsuario(JsfBase.getIdUsuario());        
        proveedorArticulo.setActualizado(LocalDateTime.now());        
        dto = (TcKeetArticulosProveedoresDto) proveedorArticulo;
        sqlAccion = proveedorArticulo.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdArticuloProveedor(-1L);
            validate= registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) 
          count++;        
      } // for		
      regresar = count == this.registroProveedor.getProveedorArticulos().size();
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los articulos, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresArticulos
	
	private boolean registraProveedoresArticulosClientes(Session sesion, Long idProveedor) throws Exception {
    TrKeetArticuloProveedorClienteDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {			
      for (ProveedorArticuloCliente proveedorArticuloCliente : this.registroProveedor.getProveedorArticulosCliente()) {								
        proveedorArticuloCliente.setIdProveedor(idProveedor);
        proveedorArticuloCliente.setIdUsuario(JsfBase.getIdUsuario());        
				proveedorArticuloCliente.setActualizado(LocalDateTime.now());        
				if(proveedorArticuloCliente.getPrecioAnterior()== null || !proveedorArticuloCliente.getPrecioAnterior().equals(proveedorArticuloCliente.getPrecioConvenio()))
					proveedorArticuloCliente.setPrecioAnterior(proveedorArticuloCliente.getPrecioConvenio());
        dto = (TrKeetArticuloProveedorClienteDto) proveedorArticuloCliente;
        sqlAccion = proveedorArticuloCliente.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdArticuloProveedorCliente(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) 
          count++;        
      } // for		
      regresar = count == this.registroProveedor.getProveedorArticulosCliente().size();
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los articulos del cliente, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresArticulosClientes
	
  private boolean registraProveedoresMateriales(Session sesion, Long idProveedor) throws Exception {
    TcKeetProveedoresMaterialesDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {			
      for (ProveedorMaterial proveedorMaterial : this.registroProveedor.getProveedoresMateriales()) {								
        proveedorMaterial.setIdProveedor(idProveedor);
        proveedorMaterial.setIdUsuario(JsfBase.getIdUsuario());        
        dto = (TcKeetProveedoresMaterialesDto) proveedorMaterial;
        sqlAccion = proveedorMaterial.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdProveedorMaterial(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) 
          count++;        
      } // for		
      regresar = count == this.registroProveedor.getProveedoresMateriales().size();
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los materiales, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresMateriales  
	
  private boolean registraProveedoresAgentes(Session sesion, Long idProveedor) throws Exception {
    TrManticProveedorAgenteDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    int countPrincipal = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
			if(this.registroProveedor.getPersonasTiposContacto().size()== 1)
					this.registroProveedor.getPersonasTiposContacto().get(0).setIdPrincipal(1L);
      for (ProveedorContactoAgente proveedorRepresentante : this.registroProveedor.getPersonasTiposContacto()) {				
				if(proveedorRepresentante.getIdPrincipal().equals(1L))
					countPrincipal++;
				if(countPrincipal== 0 && this.registroProveedor.getPersonasTiposContacto().size()-1 == count)
					proveedorRepresentante.setIdPrincipal(1L);
        proveedorRepresentante.setIdProveedor(idProveedor);
        proveedorRepresentante.setIdUsuario(JsfBase.getIdUsuario());
        proveedorRepresentante.setIdAgente(addAgente(sesion, proveedorRepresentante));
        dto = (TrManticProveedorAgenteDto) proveedorRepresentante;
        sqlAccion = proveedorRepresentante.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdProveedorAgente(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroProveedor.getPersonasTiposContacto().size();
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los agentes, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresAgentes
	
	private Long addAgente(Session sesion, ProveedorContactoAgente proveedorAgente) throws Exception{
		Long regresar= -1L;
		TcManticPersonasDto representante= null;
		try {
			representante= new TcManticPersonasDto();
			representante.setNombres(proveedorAgente.getNombres());
			representante.setPaterno(proveedorAgente.getPaterno());
			representante.setMaterno(proveedorAgente.getMaterno());
			representante.setIdTipoPersona(ETipoPersona.REPRESENTANTE_LEGAL.getIdTipoPersona());	
			representante.setIdTipoSexo(1L);
			representante.setEstilo(ESTILO);
			representante.setIdPersonaTitulo(1L);		
			regresar= DaoFactory.getInstance().insert(sesion, representante);
			if(regresar > -1L)
				registraPersonasTipoContacto(sesion, regresar, proveedorAgente.getContactos());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			this.messageError = "Error al registrar el agente, verifique que no haya duplicados";
		} // finally
		return regresar;
	} // addRepresentante

	private boolean registraPersonasTipoContacto(Session sesion, Long idPersona, List<PersonaTipoContacto> tiposContactos) throws Exception {
    TrManticPersonaTipoContactoDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (PersonaTipoContacto personaTipoContacto : tiposContactos) {
				if(personaTipoContacto.getValor()!= null && !Cadena.isVacio(personaTipoContacto.getValor())){
					personaTipoContacto.setIdPersona(idPersona);
					personaTipoContacto.setIdUsuario(JsfBase.getIdUsuario());
					personaTipoContacto.setOrden(count+1L);
					dto = (TrManticPersonaTipoContactoDto) personaTipoContacto;
					sqlAccion = personaTipoContacto.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdPersonaTipoContacto(-1L);
							validate = registrar(sesion, dto);
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
				} // if
				else
					validate= true;
        if (validate) 
          count++;        
      } // for		
      regresar = count == tiposContactos.size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los tipos de contacto, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraPersonasTipoContacto
	
  private boolean registraProveedoresTipoContacto(Session sesion, Long idProveedor) throws Exception {
    TrManticProveedorTipoContactoDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    int orden = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (ProveedorTipoContacto proveedorTipoContacto : this.registroProveedor.getProveedoresTipoContacto()) {
				if(proveedorTipoContacto.getValor()!= null && !Cadena.isVacio(proveedorTipoContacto.getValor())){
					proveedorTipoContacto.setOrden(orden + 1L);
					proveedorTipoContacto.setIdProveedor(idProveedor);
					proveedorTipoContacto.setIdUsuario(JsfBase.getIdUsuario());
					dto = (TrManticProveedorTipoContactoDto) proveedorTipoContacto;
					sqlAccion = proveedorTipoContacto.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdProveedorTipoContacto(-1L);
							validate = this.registrar(sesion, dto);
              // VERIFICAR SI YA FUE NOTIFICADO PARA RECIBIR MENSAJES POR WHATSUP
              if(Objects.equal(dto.getIdPreferido(), 1L) && (Objects.equal(dto.getIdTipoContacto(), 6L) || Objects.equal(dto.getIdTipoContacto(), 7L) || Objects.equal(dto.getIdTipoContacto(), 8L))) {
                Cafu cafu= new Cafu(this.registroProveedor.getProveedor().getRazonSocial(), dto.getValor());
                cafu.doSendMessage(sesion);
              } // if
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
					orden++;
				} // if
				else
					validate= true;
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroProveedor.getProveedoresTipoContacto().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los tipos de contacto, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresTipoContacto
	
  private boolean registraProveedoresDepartamentos(Session sesion, Long idProveedor) throws Exception {
    TcKeetProveedoresDepartamentosDto dto = null;
    ESql sqlAccion  = null;    
    boolean validate= false;
    boolean regresar= false;
		int count       = 0;    
    try {
      for (ProveedorDepartamento proveedorDepartamento : this.registroProveedor.getProveedoresDepartamentos()) {								
				proveedorDepartamento.setIdProveedor(idProveedor);
				proveedorDepartamento.setIdUsuario(JsfBase.getIdUsuario());
				dto = (TcKeetProveedoresDepartamentosDto) proveedorDepartamento;
				sqlAccion = proveedorDepartamento.getSqlAccion();
				switch (sqlAccion) {
					case INSERT:
						dto.setIdProveedorDepartamento(-1L);
						validate = registrar(sesion, dto);
						break;
					case UPDATE:
						validate = actualizar(sesion, dto);
						break;
				} // switch		
				if (validate) 
					count++;        
      } // for		
      regresar = count == this.registroProveedor.getProveedoresDepartamentos().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los departamentos, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresDepartamentos
	
  private boolean registraProveedoresServicios(Session sesion, Long idProveedor) throws Exception {
    TcManticProveedoresBancosDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (ProveedorBanca proveedorBanca : this.registroProveedor.getProveedoresServicio()) {
				if(proveedorBanca.getConvenioCuenta()!= null && !Cadena.isVacio(proveedorBanca.getConvenioCuenta())){
					proveedorBanca.setIdProveedor(idProveedor);
					proveedorBanca.setIdUsuario(JsfBase.getIdUsuario());
					dto = (TcManticProveedoresBancosDto) proveedorBanca;
					sqlAccion = proveedorBanca.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdProveedorBanca(-1L);
							validate = registrar(sesion, dto);
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
				} // if
				else
					validate= true;
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroProveedor.getProveedoresServicio().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar el servicio de banco, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresServicios
	
  private boolean registraProveedoresTransferencia(Session sesion, Long idProveedor) throws Exception {
    TcManticProveedoresBancosDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (ProveedorBanca proveedorBanca : this.registroProveedor.getProveedoresTransferencia()) {
				if(proveedorBanca.getConvenioCuenta()!= null && !Cadena.isVacio(proveedorBanca.getConvenioCuenta())){
					proveedorBanca.setIdProveedor(idProveedor);
					proveedorBanca.setIdUsuario(JsfBase.getIdUsuario());
					dto = (TcManticProveedoresBancosDto) proveedorBanca;
					sqlAccion = proveedorBanca.getSqlAccion();
					switch (sqlAccion) {
						case INSERT:
							dto.setIdProveedorBanca(-1L);
							validate = registrar(sesion, dto);
							break;
						case UPDATE:
							validate = actualizar(sesion, dto);
							break;
					} // switch
				} // if
				else
					validate= true;
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.registroProveedor.getProveedoresTransferencia().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar el servicio de banco, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresTransferencia
	
  private boolean registraProveedoresFormaPago(Session sesion, Long idProveedor) throws Exception {
    TrManticProveedorPagoDto dto= null;
    ESql sqlAccion              = null;
    int count                   = 0;
    boolean validate            = false;
    boolean regresar            = true;
    try {
			if(!this.registroProveedor.getProveedoresCondicionPago().isEmpty()){
				for (ProveedorCondicionPago proveedorCondicionPago : this.registroProveedor.getProveedoresCondicionPago()) {
					if(proveedorCondicionPago.getDescuento()!= null && !Cadena.isVacio(proveedorCondicionPago.getDescuento())){
						proveedorCondicionPago.setIdProveedor(idProveedor);
						proveedorCondicionPago.setIdUsuario(JsfBase.getIdUsuario());
						if(proveedorCondicionPago.getClave()== null || Cadena.isVacio(proveedorCondicionPago.getClave()))
							proveedorCondicionPago.setClave(ETipoPago.fromIdTipoPago(proveedorCondicionPago.getIdTipoPago()).name().concat("-")+ proveedorCondicionPago.getPlazo());
						dto = (TrManticProveedorPagoDto) proveedorCondicionPago;
						sqlAccion = proveedorCondicionPago.getSqlAccion();
						switch (sqlAccion) {
							case INSERT:
								dto.setIdProveedorPago(-1L);
								validate = registrar(sesion, dto);
								break;
							case UPDATE:
								validate = actualizar(sesion, dto);
								break;
						} // switch
					} // if
					else
						validate= true;
					if (validate) {
						count++;
					}
				} // for		
				regresar = count == this.registroProveedor.getProveedoresCondicionPago().size();
			} // if
			else {
				dto= new TrManticProveedorPagoDto();
				dto.setIdProveedor(idProveedor);
				dto.setIdTipoPago(ETipoPago.EFECTIVO.getIdTipoPago());
				dto.setClave(ETipoPago.EFECTIVO.name());
				dto.setDescuento("0.00");
				dto.setPlazo(1L);
				dto.setObservaciones("REGISTRO TIPO DE PAGO POR DEFECTO");
				dto.setIdUsuario(JsfBase.getIdUsuario());
				regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;
			} // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los tipos de pago, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresFormaPago

  private boolean eliminarRegistros(Session sesion) throws Exception {
    boolean regresar = true;
    int count = 0;
    try {
      for (IBaseDto dto : this.registroProveedor.getDeleteList()) {
        if (DaoFactory.getInstance().delete(sesion, dto) >= 1L) 
          count++;
      } // for
      regresar = count == this.registroProveedor.getDeleteList().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al eliminar registros";
    } // finally
    return regresar;
  } // eliminarRegistros

  private boolean registrar(Session sesion, IBaseDto dto) throws Exception {
    return registrarSentencia(sesion, dto) >= 1L;
  } // registrar
  
	private Long registrarSentencia(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().insert(sesion, dto);
  } // registrar

  private boolean actualizar(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().update(sesion, dto) >= 1L;
  } // actualizar
	
	private Long toIdDomicilio(Session sesion, ProveedorDomicilio proveedorDomicilio) throws Exception{		
		Entity entityDomicilio= null;
		Long regresar= -1L;
		try {
			entityDomicilio= toDomicilio(sesion, proveedorDomicilio);
			if(entityDomicilio!= null)
				regresar= entityDomicilio.getKey();
			else
				regresar= insertDomicilio(sesion, proveedorDomicilio);					
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarDomicilio	
	
	private Long insertDomicilio(Session sesion, ProveedorDomicilio proveedorDomicilio) throws Exception{
		TcManticDomiciliosDto domicilio= null;
		Long regresar= -1L;
		try {
			domicilio= new TcManticDomiciliosDto();
			domicilio.setIdLocalidad(proveedorDomicilio.getIdLocalidad().getKey());
			domicilio.setAsentamiento(proveedorDomicilio.getColonia());
			domicilio.setCalle(proveedorDomicilio.getCalle());
			domicilio.setCodigoPostal(proveedorDomicilio.getCodigoPostal());
			domicilio.setEntreCalle(proveedorDomicilio.getEntreCalle());
			domicilio.setIdUsuario(JsfBase.getIdUsuario());
			domicilio.setNumeroExterior(proveedorDomicilio.getExterior());
			domicilio.setNumeroInterior(proveedorDomicilio.getInterior());
			domicilio.setYcalle(proveedorDomicilio.getyCalle());
			regresar= DaoFactory.getInstance().insert(sesion, domicilio);
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // insertDomicilio
	
	private Entity toDomicilio(Session sesion, ProveedorDomicilio proveedorDomicilio) throws Exception{
		Entity regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", proveedorDomicilio.getIdLocalidad().getKey());
			params.put("codigoPostal", proveedorDomicilio.getCodigoPostal());
			params.put("calle", proveedorDomicilio.getCalle());
			params.put("numeroExterior", proveedorDomicilio.getExterior());
			params.put("numeroInterior", proveedorDomicilio.getInterior());
			params.put("asentamiento", proveedorDomicilio.getColonia());
			params.put("entreCalle", proveedorDomicilio.getEntreCalle());
			params.put("yCalle", proveedorDomicilio.getyCalle());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
	
	private boolean registraProveedoresFamilias(Session sesion, Long idProveedor) throws Exception {
    TcKeetProveedoresFamiliasDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (ProveedorFamilia proveedorFamilia : this.registroProveedor.getProveedoresFamilias()) {				
				proveedorFamilia.setIdProveedor(idProveedor);
				proveedorFamilia.setIdUsuario(JsfBase.getIdUsuario());
				dto = (TcKeetProveedoresFamiliasDto) proveedorFamilia;
				sqlAccion = proveedorFamilia.getSqlAccion();
				switch (sqlAccion) {
					case INSERT:
						dto.setIdProveedorFamilia(-1L);
						validate = registrar(sesion, dto);
						break;
					case UPDATE:
						validate = actualizar(sesion, dto);
						break;
				} // switch								
        if (validate) 
          count++;        
      } // for		
      regresar = count == this.registroProveedor.getProveedoresFamilias().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar la familia de articulos, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraProveedoresFamilias
  
  private void toInsertMoroso(Session sesion) throws Exception {
    try {      
      TcKeetMorososDto moroso= new TcKeetMorososDto(
        this.registroProveedor.getIdProveedor(), // Long idProveedor, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        -1L, // Long idMoroso, 
        null, // String observaciones, 
        0D, // Double saldo, 
        50000D, // Double limite, 
        50000D // Double disponible
      );
      DaoFactory.getInstance().insert(sesion, moroso);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
}
