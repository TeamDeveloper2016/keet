package mx.org.kaana.keet.catalogos.contratos.destajos.beans;

import java.time.LocalDateTime;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseDestajoArchivo;
import mx.org.kaana.keet.db.dto.TcKeetContratosContratistasArchivosDto;

public class DestajoContratistaArchivo extends TcKeetContratosContratistasArchivosDto implements IBaseDestajoArchivo{
	
	private static final long serialVersionUID = 7733521648482345931L;
	private Long idArchivo;	
	private Long tipo;
	private String especialidad;
	private String concepto;
	private String consecutivo;

	public DestajoContratistaArchivo(Long idArchivo, Long tipo, String especialidad, String concepto, String consecutivo, Long idContratoContratistaArchivo, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idContratoDestajoContratista, String nombre) {
		super(idContratoContratistaArchivo, archivo, eliminado, ruta, tamanio, idUsuario, idTipoArchivo, observaciones, alias, idContratoDestajoContratista, nombre);
		this.idArchivo   = idArchivo;
		this.tipo        = tipo;
		this.especialidad= especialidad;
		this.concepto    = concepto;
		this.consecutivo = consecutivo;
	} // DestajoContratistaArchivo	

	@Override
	public Long getIdComun() {
		return getIdContratoDestajoContratista();
	}

	@Override
	public void setIdComun(Long idComun) {
		setIdContratoDestajoContratista(idComun);
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