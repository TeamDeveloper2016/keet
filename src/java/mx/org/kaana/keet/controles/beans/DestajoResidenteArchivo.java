package mx.org.kaana.keet.controles.beans;

import java.time.LocalDateTime;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseDestajoArchivo;
import mx.org.kaana.keet.db.dto.TcKeetContratosResidentesArchivosDto;

public class DestajoResidenteArchivo extends TcKeetContratosResidentesArchivosDto implements IBaseDestajoArchivo {
	
	private static final long serialVersionUID = 7733521648482345931L;
	private Long idArchivo;	
	private Long tipo;
	private String especialidad;
	private String concepto;
	private String consecutivo;

	public DestajoResidenteArchivo(Long idArchivo, Long tipo, String especialidad, String concepto, String consecutivo, Long idContratoResidenteArchivo, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idContratoDestajoResidente, String nombre) {
		super(archivo, eliminado, ruta, tamanio, idUsuario, idTipoArchivo, idContratoDestajoResidente, observaciones, alias, idContratoResidenteArchivo, nombre);
		this.idArchivo   = idArchivo;
		this.tipo        = tipo;
		this.especialidad= especialidad;
		this.concepto    = concepto;
		this.consecutivo = consecutivo;
	} // DestajoResidenteArchivo	

	@Override
	public Long getIdComun() {
		return getIdContratoDestajoResidente();
	}

	@Override
	public void setIdComun(Long idComun) {
		setIdContratoDestajoResidente(idComun);
	}
	
	@Override
	public Long getIdArchivo() {
		return idArchivo;
	}

	@Override
	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}
	
	@Override
	public Long getTipo() {
		return tipo;
	}

	@Override
	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	@Override
	public String getEspecialidad() {
		return especialidad;
	}

	@Override
	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	@Override
	public String getConcepto() {
		return concepto;
	}

	@Override
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	@Override
	public String getConsecutivo() {
		return consecutivo;
	}

	@Override
	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}	
  
}