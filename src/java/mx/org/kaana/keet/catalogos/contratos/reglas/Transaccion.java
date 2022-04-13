package mx.org.kaana.keet.catalogos.contratos.reglas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoDomicilio;
import mx.org.kaana.keet.catalogos.contratos.beans.Documento;
import mx.org.kaana.keet.catalogos.contratos.beans.Fondo;
import mx.org.kaana.keet.catalogos.contratos.beans.Garantia;
import mx.org.kaana.keet.catalogos.contratos.beans.Generador;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.beans.Presupuesto;
import mx.org.kaana.keet.catalogos.contratos.beans.RegistroContrato;
import mx.org.kaana.keet.db.dto.TcKeetContratosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosBitacoraDto;
import mx.org.kaana.keet.enums.EArchivosContratos;
import mx.org.kaana.keet.catalogos.contratos.beans.Retencion;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRetencionesDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroContrato contrato;	
  private String messageError;
	private IBaseDto dtoDelete;
	private EArchivosContratos tipoArchivo;
	private TcKeetContratosBitacoraDto bitacora;
  private List<Garantia> garantias;
  private Fondo fondo;
	
	public Transaccion(RegistroContrato contrato) {
		this(contrato, EArchivosContratos.DOCUMENTOS);
	}

	public Transaccion(RegistroContrato contrato, TcKeetContratosBitacoraDto bitacora) {
		this(contrato, EArchivosContratos.DOCUMENTOS);
		this.bitacora= bitacora;
	}
	
	public Transaccion(RegistroContrato contrato, EArchivosContratos tipoArchivo) {
		this.contrato   = contrato;	
		this.tipoArchivo= tipoArchivo;
	}

	public Transaccion(IBaseDto dtoDelete) {
		this.dtoDelete = dtoDelete;
	}	
	
	public Transaccion(List<Garantia> garantias) {
		this.garantias = garantias;
	}	
	
	public Transaccion(Fondo fondo) {
		this.fondo = fondo;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		this.messageError  = "";
		try {
			switch(accion){
				case AGREGAR:					
					siguiente= toSiguiente(sesion);
					this.contrato.getContrato().setConsecutivo(siguiente.getConsecutivo());
					this.contrato.getContrato().setOrden(siguiente.getOrden());
					this.contrato.getContrato().setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					this.contrato.getContrato().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
          if(this.contrato.getContrato().getIdTipoMedioPago()<= 0L)
            this.contrato.getContrato().setIdTipoMedioPago(null);
          if(this.contrato.getContrato().getIdBanco()<= 0L)
            this.contrato.getContrato().setIdBanco(null);
					regresar= DaoFactory.getInstance().insert(sesion, this.contrato.getContrato())>= 1L;
					for(Lote item:this.contrato.getContrato().getLotes())
						this.actualizarLote(sesion, item);
          this.registraContratoDomicilios(sesion, this.contrato.getContrato().getIdContrato());
          this.toFillRetenciones(sesion);
					break;
				case MODIFICAR:
          if(this.contrato.getContrato().getIdTipoMedioPago()<= 0L)
            this.contrato.getContrato().setIdTipoMedioPago(null);
          if(this.contrato.getContrato().getIdBanco()<= 0L)
            this.contrato.getContrato().setIdBanco(null);
					regresar= DaoFactory.getInstance().update(sesion, this.contrato.getContrato())>= 1L;
					for(Lote item:this.contrato.getContrato().getLotes())
						this.actualizarLote(sesion, item);
          this.registraContratoDomicilios(sesion, this.contrato.getContrato().getIdContrato());
          this.toFillRetenciones(sesion);
					break;				
				case ELIMINAR:
          Map<String, Object> params= new HashMap<>();
          params.put("idContrato", this.contrato.getContrato().getIdContrato());
          DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosRetencionesDto.class, params);
          Methods.clean(params);
					for(Lote item:this.contrato.getContrato().getLotes()) {
						item.setAccion(ESql.DELETE);
						this.actualizarLote(sesion, item);
					} // for
					regresar= DaoFactory.getInstance().delete(sesion, this.contrato.getContrato())>= 1L;
          break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dtoDelete)>= 1L;
					break;
				case SUBIR:					
					switch(this.tipoArchivo) {
						case DOCUMENTOS:
							for(Documento dto: this.contrato.getDocumentos()){
								if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
									toSaveFile(dto.getIdArchivo());
							} // for
							break;
						case GENERADORES:
							for(Generador dto: this.contrato.getGeneradores())
								if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
									toSaveFile(dto.getIdArchivo());
							break;
						case PRESUPUESTOS:
							for(Presupuesto dto: this.contrato.getPresupuestos()){
								if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
									toSaveFile(dto.getIdArchivo());
							} // for
							break;
					} // switch
					regresar= true;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.contrato.getContrato().setIdContratoEstatus(this.bitacora.getIdContratoEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.contrato.getContrato())>= 1L;
					} // if
					break;
				case GENERAR:
          regresar= this.toGarantias(sesion);
					break;
				case MOVIMIENTOS:
          regresar= this.toFondoGarantia(sesion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
      throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		return regresar;
	}	// ejecutar
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idProyecto", this.contrato.getContrato().getIdProyecto());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetContratosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	private void actualizarLote(Session sesion, Lote item) throws Exception {
		Entity entity = null;
		try {
			//setea el nuevo orden, debido al ordenamiento por fecha
			// orden= DaoFactory.getInstance().toField(sesion, "TcKeetContratosLotesDto", "siguiente", item.toMap(), "maxOrden");
			// item.setOrden(orden.toLong(1L));
			switch(item.getAccion()) {
				case INSERT:
          item.setIdContratoLote(-1L);
          item.setIdContratoLoteEstatus(1L);//ver estatus
					item.setIdContrato(this.contrato.getContrato().getIdContrato());
					item.setIdUsuario(JsfBase.getIdUsuario());
					entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "VistaContratosLotesDto", "getCoordenadasDesarrollo", this.contrato.getContrato().toMap());
					item.setLatitud(entity.toString("latitud"));
					item.setLongitud(entity.toString("longitud"));
					DaoFactory.getInstance().insert(sesion, item);
					//this.cargarPlanos(sesion, (List<TcKeetContratosArchivosDto>)DaoFactory.getInstance().toEntitySet(TcKeetContratosArchivosDto.class,"TcKeetPrototiposArchivosDto", "toContratos", item.toMap()));
					break;
				case UPDATE:
					DaoFactory.getInstance().update(sesion, item);
					break;
				case DELETE:
					DaoFactory.getInstance().delete(sesion, item);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch	
	} // actualizarConstructivo

	private void cargarPlanos(Session sesion, List<TcKeetContratosArchivosDto> planos) throws Exception {
		String nuevaRuta = null;
		File origen      = null;
    File destino     = null;
		InputStream in   = null;
		OutputStream out = null;		
		byte[] buf       = new byte[1024];
    int len          = 0;      
		try {
			for (TcKeetContratosArchivosDto tcKeetContratosArchivosDto : planos) {
				tcKeetContratosArchivosDto.setIdUsuario(JsfBase.getIdUsuario());
				tcKeetContratosArchivosDto.setIdContrato(this.contrato.getContrato().getIdContrato());
				nuevaRuta = tcKeetContratosArchivosDto.getAlias().replaceAll("prototipos", "contratos");
				origen      = new File(tcKeetContratosArchivosDto.getAlias());
        destino     = new File(nuevaRuta.substring(0, nuevaRuta.lastIndexOf("/")));	
			  if (!destino.exists())
				  destino.mkdirs();
				destino     = new File(nuevaRuta);
				if (!destino.exists() || DaoFactory.getInstance().findIdentically(sesion, TcKeetContratosArchivosDto.class, tcKeetContratosArchivosDto.toMap())==null){
					in = new FileInputStream(origen);
					out= new FileOutputStream(destino);
					while ((len = in.read(buf)) > 0) 
						out.write(buf, 0, len);
					in.close();
          out.close();
					tcKeetContratosArchivosDto.setAlias(nuevaRuta);
					DaoFactory.getInstance().insert(sesion, tcKeetContratosArchivosDto);
				} // if
			} // for
		} // try
		catch (Exception e) {
			throw new Exception(e);
		} // catch	
	} // cargarPlanos
  
  private boolean registraContratoDomicilios(Session sesion, Long idContrato) throws Exception {
    ESql sqlAccion    = null;
    int count         = 0;
    int countPrincipal= 0;
    boolean validate  = false;
    boolean regresar  = false;
    try {
			if(this.contrato.getContratoDomicilios().size()== 1)
				this.contrato.getContratoDomicilios().get(0).setIdPrincipal(1L);
      for (ContratoDomicilio item: this.contrato.getContratoDomicilios()) {								
				if(item.getIdPrincipal().equals(1L))
					countPrincipal++;
				if(countPrincipal== 0 && this.contrato.getContratoDomicilios().size()-1 == count)
					item.setIdPrincipal(1L);
        item.setIdContrato(idContrato);
        item.setIdUsuario(JsfBase.getIdUsuario());
				item.setIdDomicilio(this.toIdDomicilio(sesion, item));		
        sqlAccion = item.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
						item.setIdPrincipal(item.getIdPrincipal().equals(1L)? item.getIdPrincipal(): 2L);
            item.setIdContratoDomicilio(-1L);
            validate = this.registrar(sesion, item);
            break;
          case UPDATE:
            validate = this.actualizar(sesion, item);
            break;
        } // switch
        if (validate) 
          count++;
      } // for		
      regresar = count == this.contrato.getContratoDomicilios().size();
    } // try    
    finally {
      this.messageError = "Error al registrar los domicilios, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraContratoDomicilios
 
	private Long toIdDomicilio(Session sesion, ContratoDomicilio contratoDomicilio) throws Exception{		
		Long regresar         = -1L;		
		Entity entityDomicilio= this.toDomicilio(sesion, contratoDomicilio);
		if(entityDomicilio!= null)
			regresar= entityDomicilio.getKey();
		else
			regresar= this.insertDomicilio(sesion, contratoDomicilio);									
		return regresar;
	} // registrarDomicilio	
 
	private Long insertDomicilio(Session sesion, ContratoDomicilio contratoDomicilio) throws Exception {
		Long regresar= -1L;		
    try {
      TcManticDomiciliosDto domicilio= new TcManticDomiciliosDto();
      domicilio.setIdLocalidad(contratoDomicilio.getIdLocalidad().getKey());
      domicilio.setAsentamiento(contratoDomicilio.getColonia());
      domicilio.setCalle(contratoDomicilio.getCalle());
      domicilio.setCodigoPostal(contratoDomicilio.getCodigoPostal());
      domicilio.setEntreCalle(contratoDomicilio.getEntreCalle());
      domicilio.setIdUsuario(JsfBase.getIdUsuario());
      domicilio.setNumeroExterior(contratoDomicilio.getExterior());
      domicilio.setNumeroInterior(contratoDomicilio.getInterior());
      domicilio.setYcalle(contratoDomicilio.getyCalle());
      regresar= DaoFactory.getInstance().insert(sesion, domicilio);		
    } // try 
    catch(Exception e) {
      throw e;
    } // catch
		return regresar;
	} // insertDomicilio
	
	private Entity toDomicilio(Session sesion, ContratoDomicilio contratoDomicilio) throws Exception{
		Entity regresar          = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", contratoDomicilio.getIdLocalidad().getKey());
			params.put("codigoPostal", contratoDomicilio.getCodigoPostal());
			params.put("calle", contratoDomicilio.getCalle());
			params.put("numeroExterior", contratoDomicilio.getExterior());
			params.put("numeroInterior", contratoDomicilio.getInterior());
			params.put("asentamiento", contratoDomicilio.getColonia());
			params.put("entreCalle", contratoDomicilio.getEntreCalle());
			params.put("yCalle", contratoDomicilio.getyCalle());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
 
  private boolean registrar(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().insert(sesion, dto)>= 1L;
  } // registrar
  
  private boolean actualizar(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().update(sesion, dto) >= 1L;
  } // actualizar
 
  private void toFillRetenciones(Session sesion) throws Exception {
    try {   
      for (Retencion item: this.contrato.getContrato().getRetenciones()) {
        if(item.getActivo())
          switch(item.getSql()) {
            case INSERT:
              item.setIdUsuario(JsfBase.getIdUsuario());
              item.setIdContrato(this.contrato.getContrato().getIdContrato());
              DaoFactory.getInstance().insert(sesion, item);
              break;
            case UPDATE:
              item.setRegistro(LocalDateTime.now());
              DaoFactory.getInstance().update(sesion, item);
              break;
            case SELECT:
              break;
          } // switch
        else
          if(item.getKey()> 0L)
             DaoFactory.getInstance().delete(sesion, item);
      } // for
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
  }
 
  private Boolean toGarantias(Session sesion) throws Exception {
    Boolean regresar= this.garantias.size()<= 0;
    try {   
      for (Garantia item: this.garantias) {
        switch(item.getSql()) {
          case SELECT:
            regresar= Boolean.TRUE;
            break;
          case INSERT:
            regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
            break;
          case UPDATE:
            regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
          case DELETE:
            regresar= DaoFactory.getInstance().delete(sesion, item)> 0L;
            break;
        } // switch
      } // for
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toFondoGarantia(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    try { 
      this.fondo.setIdUsuario(JsfBase.getIdUsuario());
      this.fondo.setRegistro(LocalDateTime.now());
      if(this.fondo.isValid())
        regresar= DaoFactory.getInstance().update(sesion, this.fondo)> 0L;
      else
        regresar= DaoFactory.getInstance().insert(sesion, this.fondo)> 0L;
      TcKeetContratosDto item= (TcKeetContratosDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosDto.class, this.fondo.getIdContrato());      
      item.setIdContratoEstatus(EContratosEstatus.LIQUIDADO.getKey());
      DaoFactory.getInstance().update(sesion, item);
      TcKeetContratosBitacoraDto evidencia= new TcKeetContratosBitacoraDto("CONTRATO LIQUIDADO EL FONDO DE GARANTÍA", item.getIdContratoEstatus(), JsfBase.getIdUsuario(), -1L, item.getIdContrato());
      DaoFactory.getInstance().insert(sesion, evidencia);
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    return regresar;
  }
  
}