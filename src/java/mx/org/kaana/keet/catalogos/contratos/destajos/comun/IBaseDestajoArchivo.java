package mx.org.kaana.keet.catalogos.contratos.destajos.comun;

public interface IBaseDestajoArchivo {

	public Long getIdArchivo();
	public void setIdArchivo(Long idArchivo);
	public Long getIdComun();
	public void setIdComun(Long idComun);
	public Long getTipo();
	public void setTipo(Long tipo);
	public String getEspecialidad();
	public void setEspecialidad(String especialidad);
	public String getConcepto();
	public void setConcepto(String concepto);	
	public String getConsecutivo();
	public void setConsecutivo(String consecutivo);
	
}
