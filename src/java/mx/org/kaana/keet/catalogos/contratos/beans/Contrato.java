package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;

public class Contrato extends TcKeetContratosDto implements Serializable{

	private static final long serialVersionUID = -1210601775101099137L;
	private String claveDesarrollo;
	private String desarrollo;		
	private ESql sqlAccion;
	private Boolean nuevo;

	public Contrato() {
		this(-1L);
	}

	public Contrato(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public Contrato(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public Contrato(Long key, ESql sqlAccion, Boolean nuevo) {		
		this(key, "", "", sqlAccion, nuevo);		
	}

	public Contrato(Long key, String claveDesarrollo, String desarrollo, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.claveDesarrollo= claveDesarrollo;
		this.desarrollo     = desarrollo;				
		this.sqlAccion      = sqlAccion;
		this.nuevo          = nuevo;
	}
	
	public ESql getSqlAccion() {
		return sqlAccion;
	}

	public void setSqlAccion(ESql sqlAccion) {
		this.sqlAccion = sqlAccion;
	}	

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
	}	

	public String getClaveDesarrollo() {
		return claveDesarrollo;
	}

	public void setClaveDesarrollo(String claveDesarrollo) {
		this.claveDesarrollo = claveDesarrollo;
	}

	public String getDesarrollo() {
		return desarrollo;
	}

	public void setDesarrollo(String desarrollo) {
		this.desarrollo = desarrollo;
	}		
}