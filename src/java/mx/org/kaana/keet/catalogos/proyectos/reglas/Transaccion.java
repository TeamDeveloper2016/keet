package mx.org.kaana.keet.catalogos.proyectos.reglas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.proyectos.beans.Documento;
import mx.org.kaana.keet.catalogos.proyectos.beans.Generador;
import mx.org.kaana.keet.catalogos.proyectos.beans.Lote;
import mx.org.kaana.keet.catalogos.proyectos.beans.Presupuesto;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.keet.db.dto.TcKeetContratosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosGeneradoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPresupuestosDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosBitacoraDto;
import mx.org.kaana.keet.enums.EArchivosProyectos;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroProyecto proyecto;	
	private IBaseDto dtoDelete;
	private EArchivosProyectos tipoArchivo;
	private TcKeetProyectosBitacoraDto bitacora;
	
	public Transaccion(RegistroProyecto proyecto) {
		this(proyecto, EArchivosProyectos.DOCUMENTOS);
	}

	public Transaccion(RegistroProyecto proyecto, TcKeetProyectosBitacoraDto bitacora) {
		this(proyecto, EArchivosProyectos.DOCUMENTOS);
		this.bitacora= bitacora;
	}
	
	public Transaccion(RegistroProyecto proyecto, EArchivosProyectos tipoArchivo) {
		this.proyecto   = proyecto;	
		this.tipoArchivo= tipoArchivo;
	}

	public Transaccion(IBaseDto dtoDelete) {
		this.dtoDelete = dtoDelete;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {
			switch(accion){
				case AGREGAR:					
					siguiente= toSiguiente(sesion);
					this.proyecto.getProyecto().setConsecutivo(siguiente.getConsecutivo());
					this.proyecto.getProyecto().setOrden(siguiente.getOrden());
					this.proyecto.getProyecto().setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					this.proyecto.getProyecto().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
					this.proyecto.getProyecto().setIdProyectoEstatus(1L);
					regresar= DaoFactory.getInstance().insert(sesion, this.proyecto.getProyecto())>= 1L;
					for(Lote item:this.proyecto.getProyecto().getLotes())
						actualizarLote(sesion, item);
					//crearContrato(sesion);
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.proyecto.getProyecto())>= 1L;
					for(Lote item:this.proyecto.getProyecto().getLotes())
						actualizarLote(sesion, item);
					break;				
				case ELIMINAR:
					for(Lote item:this.proyecto.getProyecto().getLotes()){
						item.setAccion(ESql.DELETE);
						actualizarLote(sesion, item);
					} // for
					regresar= DaoFactory.getInstance().delete(sesion, this.proyecto.getProyecto())>= 1L;
					break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dtoDelete)>= 1L;
					break;
				case SUBIR:					
					switch(this.tipoArchivo) {
						case DOCUMENTOS:
							for(Documento dto: this.proyecto.getDocumentos()){
								if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
									toSaveFile(dto.getIdArchivo());
							} // for
							break;
						case GENERADORES:
							for(Generador dto: this.proyecto.getGeneradores())
								if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
									toSaveFile(dto.getIdArchivo());
							break;
						case PRESUPUESTOS:
							for(Presupuesto dto: this.proyecto.getPresupuestos()){
								if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
									toSaveFile(dto.getIdArchivo());
							} // for
							break;
					} // switch
					regresar= true;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.proyecto.getProyecto().setIdProyectoEstatus(this.bitacora.getIdProyectoEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.proyecto.getProyecto())>= 1L;
						if(this.bitacora.getIdProyectoEstatus().equals(7L))
							this.crearContrato(sesion);
					} // if
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		return regresar;
	}	// ejecutar
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idCliente", this.proyecto.getProyecto().getIdCliente());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetProyectosDto", "siguiente", params, "siguiente");
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
		Value orden= null;
		try {
			switch(item.getAccion()){
				case INSERT:
          item.setIdProyectoLote(-1L);
					item.setIdProyecto(this.proyecto.getProyecto().getIdProyecto());
					item.setIdUsuario(JsfBase.getIdUsuario());
					orden= DaoFactory.getInstance().toField("TcKeetProyectosLotesDto", "getOrden", item.toMap(), "maxOrden");
					item.setOrden(orden.toLong(1L));
					DaoFactory.getInstance().insert(sesion, item);
					cargarPlanos(sesion, (List<TcKeetProyectosArchivosDto>)DaoFactory.getInstance().toEntitySet(TcKeetProyectosArchivosDto.class,"TcKeetPrototiposArchivosDto", "toProyectos", item.toMap()));
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

	private void cargarPlanos(Session sesion, List<TcKeetProyectosArchivosDto> planos) throws Exception {
		String nuevaRuta = null;
		File origen      = null;
    File destino     = null;
		InputStream in   = null;
		OutputStream out = null;		
		byte[] buf       = new byte[1024];
    int len          = 0;      
		try {
			for (TcKeetProyectosArchivosDto tcKeetProyectosArchivosDto : planos) {
				tcKeetProyectosArchivosDto.setIdUsuario(JsfBase.getIdUsuario());
				tcKeetProyectosArchivosDto.setIdProyecto(this.proyecto.getProyecto().getIdProyecto());
				nuevaRuta = tcKeetProyectosArchivosDto.getAlias().replaceAll("prototipos", "proyectos");
				origen      = new File(tcKeetProyectosArchivosDto.getAlias());
        destino     = new File(nuevaRuta.substring(0, nuevaRuta.lastIndexOf("/")));	
			  if (!destino.exists())
				  destino.mkdirs();
				destino     = new File(nuevaRuta);
				if (!destino.exists() || DaoFactory.getInstance().findIdentically(sesion, TcKeetProyectosArchivosDto.class, tcKeetProyectosArchivosDto.toMap())==null){
					in = new FileInputStream(origen);
					out= new FileOutputStream(destino);
					while ((len = in.read(buf)) > 0) 
						out.write(buf, 0, len);
					in.close();
          out.close();
					tcKeetProyectosArchivosDto.setAlias(nuevaRuta);
					DaoFactory.getInstance().insert(sesion, tcKeetProyectosArchivosDto);
				} // if
			} // for
		} // try
		catch (Exception e) {
			throw new Exception(e);
		} // catch	
	} // cargarPlanos

	private void crearContrato(Session sesion) throws Exception {
		TcKeetContratosDto contratosDto    = null;
		try{
			contratosDto= (TcKeetContratosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDto.class,"TcKeetProyectosDto", "byId", this.proyecto.getProyecto().toMap());
			contratosDto.setIdUsuario(JsfBase.getIdUsuario());
			contratosDto.setIdContratoEstatus(1L);//cambiar estatus por el inicial
			DaoFactory.getInstance().insert(sesion, contratosDto);
		  for(TcKeetContratosLotesDto lote: (List<TcKeetContratosLotesDto>) DaoFactory.getInstance().toEntitySet(TcKeetContratosLotesDto.class,"TcKeetProyectosLotesDto", "byProyecto", contratosDto.toMap())){
				lote.setIdContrato(contratosDto.getIdContrato());
				lote.setIdUsuario(JsfBase.getIdUsuario());
				lote.setIdContratoLoteEstatus(1L);
				DaoFactory.getInstance().insert(sesion, lote);
			} // for
		  this.cargarDocumentos(sesion, DaoFactory.getInstance().toEntitySet(TcKeetContratosArchivosDto.class,"TcKeetProyectosArchivosDto", "toContratos", contratosDto.toMap()));
		  this.cargarDocumentos(sesion, DaoFactory.getInstance().toEntitySet(TcKeetContratosPresupuestosDto.class,"TcKeetProyectosPrespuestosDto", "toContratos", contratosDto.toMap()));
		  this.cargarDocumentos(sesion, DaoFactory.getInstance().toEntitySet(TcKeetContratosGeneradoresDto.class,"TcKeetProyectosGeneradoresDto", "toContratos", contratosDto.toMap()));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		
  }
	
	private void cargarDocumentos(Session sesion, List<IBaseDto> documentos) throws Exception {
		File origen      = null;
    File destino     = null;
		InputStream in   = null;
		OutputStream out = null;		
		byte[] buf       = new byte[1024];
    int len          = 0;      
		try {
			for (IBaseDto item : documentos) {
				origen      = new File(item.toValue("alias").toString().replaceAll("contrato", "proyecto"));
        destino     = new File(item.toValue("alias").toString().substring(0, item.toValue("alias").toString().lastIndexOf("/")));	
			  if (!destino.exists())
				  destino.mkdirs();
				destino     = new File(item.toValue("alias").toString());
				if (!destino.exists() || DaoFactory.getInstance().findIdentically(sesion, item.getClass(), item.toMap())==null){
					in = new FileInputStream(origen);
					out= new FileOutputStream(destino);
					while ((len = in.read(buf)) > 0) 
						out.write(buf, 0, len);
					in.close();
          out.close();
					DaoFactory.getInstance().insert(sesion, item);
				} // if
			} // for
		} // try
		catch (Exception e) {
			throw new Exception(e);
		} // catch	
	} // cargarPlanos
	
	
}