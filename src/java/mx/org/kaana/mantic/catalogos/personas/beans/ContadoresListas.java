package mx.org.kaana.mantic.catalogos.personas.beans;

import java.util.Collections;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;

public class ContadoresListas {

	private static final Long INCREMENTO= 10000L;
	private Long totalPersonasDomicilios;
	private Long totalPersonasTipoContacto;	
	private Long totalPersonasBancos;		
	private Long totalPersonasBeneficiarios;		
	private Long totalPersonasAlimenticias;		

	public ContadoresListas() {
		init();
	} // ContadoresListas
	
	public ContadoresListas(Long totalPersonasDomicilios, Long totalPersonasTipoContacto, Long totalPersonasBancos, Long totalPersonasBeneficiarios, Long totalPersonasAlimenticias) {
		this.totalPersonasDomicilios   = totalPersonasDomicilios;
		this.totalPersonasTipoContacto = totalPersonasTipoContacto;
		this.totalPersonasBancos       = totalPersonasBancos;
		this.totalPersonasBeneficiarios= totalPersonasBeneficiarios;
		this.totalPersonasAlimenticias = totalPersonasAlimenticias;
	} // ContadoresListas

	public Long getTotalPersonasDomicilios() {
		return totalPersonasDomicilios;
	}

	public void setTotalPersonasDomicilios(Long totalPersonasDomicilios) {
		this.totalPersonasDomicilios = totalPersonasDomicilios;
	}

	public Long getTotalPersonasTipoContacto() {
		return totalPersonasTipoContacto;
	}

	public void setTotalPersonasTipoContacto(Long totalPersonasTipoContacto) {
		this.totalPersonasTipoContacto = totalPersonasTipoContacto;
	}	

	public Long getTotalPersonasBancos() {
		return totalPersonasBancos;
	}

	public Long getTotalPersonasBeneficiarios() {
		return totalPersonasBeneficiarios;
	}	

  public Long getTotalPersonasAlimenticias() {
    return totalPersonasAlimenticias;
  }

  public void setTotalPersonasAlimenticias(Long totalPersonasAlimenticias) {
    this.totalPersonasAlimenticias = totalPersonasAlimenticias;
  }
	
	private void init(){
		try {
			this.totalPersonasDomicilios   = this.toMaxClienteDomicilio();
			this.totalPersonasTipoContacto = this.toMaxClienteTiposContactos();			
			this.totalPersonasBancos       = this.toMaxPersonasBancos();	
			this.totalPersonasBeneficiarios= this.toMaxPersonasBeneficiarios();
			this.totalPersonasAlimenticias = this.toMaxPersonasAlimenticias();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
	} 
	
	private Long toMaxClienteDomicilio() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TrManticPersonaDomicilioDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString())+ INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} 
	
	private Long toMaxClienteTiposContactos() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TrManticPersonaTipoContactoDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString())+ INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} 
	
	private Long toMaxPersonasBancos() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TcKeetPersonasBancosDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString())+ INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} 
	
	private Long toMaxPersonasBeneficiarios() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TcKeetPersonasBeneficiariosDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString())+ INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} 
  
	private Long toMaxPersonasAlimenticias() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TcKeetPersonasPensionesDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString())+ INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} 
  
}
