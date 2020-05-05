package mx.org.kaana.keet.catalogos.contratos.reglas;

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
import mx.org.kaana.keet.catalogos.contratos.beans.Documento;
import mx.org.kaana.keet.catalogos.contratos.beans.Generador;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.beans.Presupuesto;
import mx.org.kaana.keet.catalogos.contratos.beans.RegistroContrato;
import mx.org.kaana.keet.db.dto.TcKeetContratosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosBitacoraDto;
import mx.org.kaana.keet.enums.EArchivosContratos;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Collections;

public class Transaccion extends IBaseTnx {

	private RegistroContrato contrato;	
	private IBaseDto dtoDelete;
	private EArchivosContratos tipoArchivo;
	private TcKeetContratosBitacoraDto bitacora;
	
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
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {
			switch(accion){
				case AGREGAR:					
					siguiente= toSiguiente(sesion);
					this.contrato.getContrato().setConsecutivo(siguiente.getConsecutivo());
					this.contrato.getContrato().setOrden(siguiente.getOrden());
					this.contrato.getContrato().setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					regresar= DaoFactory.getInstance().insert(sesion, this.contrato.getContrato())>= 1L;
					Collections.sort(this.contrato.getContrato().getLotes());
					for(Lote item:this.contrato.getContrato().getLotes())
						actualizarLote(sesion, item);
					break;
				case MODIFICAR:
					siguiente= toSiguiente(sesion);
					regresar= DaoFactory.getInstance().update(sesion, this.contrato.getContrato())>= 1L;
					for(Lote item:this.contrato.getContrato().getLotes()){
						actualizarLote(sesion, item);
					} // for
					break;				
				case ELIMINAR:
					for(Lote item:this.contrato.getContrato().getLotes()){
						item.setAccion(ESql.DELETE);
						actualizarLote(sesion, item);
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
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
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
		Value orden= null;
		try {
			switch(item.getAccion()){
				case INSERT:
          item.setIdContratoLote(-1L);
					item.setIdContrato(this.contrato.getContrato().getIdContrato());
					item.setIdUsuario(JsfBase.getIdUsuario());
					if(item.getOrden()== null){ //verfica si no tiene un orden, debido al ordenamiento
						orden= DaoFactory.getInstance().toField("TcKeetContratosArchivosDto", "getOrden", item.toMap(), "maxOrden	");
						item.setOrden(orden.toLong(1L));
					} // if
					DaoFactory.getInstance().insert(sesion, item);
					cargarPlanos(sesion, (List<TcKeetContratosArchivosDto>)DaoFactory.getInstance().toEntitySet(TcKeetContratosArchivosDto.class,"TcKeetPrototiposArchivosDto", "toContratos", item.toMap()));
					break;
				case UPDATE:
					if(item.getOrden()== null){ //verfica si no tiene un orden, debido al ordenamiento 
						orden= DaoFactory.getInstance().toField("TcKeetContratosArchivosDto", "getOrden", item.toMap(), "maxOrden	");
						item.setOrden(orden.toLong(1L));
					} // if
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
}