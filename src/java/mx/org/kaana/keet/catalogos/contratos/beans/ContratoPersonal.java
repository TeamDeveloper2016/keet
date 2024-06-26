package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosPersonalDto;

public class ContratoPersonal extends TcKeetContratosPersonalDto implements Serializable{

	private static final long serialVersionUID = -1210601775101099137L;
	private String nombres;
	private String paterno;
	private String materno;
	private String puesto;
	private String departamento;
	private String curp;
	private String rfc;
	private Long idActivo;		
	private String nss;		
	private Long idNomina;		
	private Long idSeguro;		
	private ESql sqlAccion;
	private Boolean nuevo;
	private String clave;
	private String claveDesarrollo;
	private String nombreDesarrollo;
	private Long idPersona;
	private String descripcion;

	public ContratoPersonal() {
		this(-1L);
	}

	public ContratoPersonal(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ContratoPersonal(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ContratoPersonal(Long key, ESql sqlAccion, Boolean nuevo) {		
		this(key, "", "", "", "", "", "", "", -1L, sqlAccion, nuevo, "", "", "", -1L, "", 1L, 2L, null);		
	}

	public ContratoPersonal(Long key, String nombres, String paterno, String materno, String puesto, String departamento, String curp, String rfc, Long idActivo, ESql sqlAccion, Boolean nuevo, String clave, String claveDesarrollo, String nombreDesarrollo, Long idPersona, String descripcion, Long idNomina, Long idSeguro, String nss) {
		super(key);
		this.nombres         = nombres;
		this.paterno         = paterno;
		this.materno         = materno;
		this.puesto          = puesto;
		this.departamento    = departamento;
		this.curp            = curp;
		this.rfc             = rfc;
		this.idActivo        = idActivo;
		this.sqlAccion       = sqlAccion;
		this.nuevo           = nuevo;
		this.clave           = clave;
		this.claveDesarrollo = claveDesarrollo;
		this.nombreDesarrollo= nombreDesarrollo;
		this.idPersona       = idPersona;
		this.descripcion     = descripcion;
		this.idNomina        = idNomina;
		this.idSeguro        = idSeguro;
		this.nss             = nss;
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

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getPaterno() {
		return paterno;
	}

	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	public String getMaterno() {
		return materno;
	}

	public void setMaterno(String materno) {
		this.materno = materno;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getCurp() {
		return curp;
	}

	public void setCurp(String curp) {
		this.curp = curp;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public Long getIdActivo() {
		return idActivo;
	}

	public void setIdActivo(Long idActivo) {
		this.idActivo = idActivo;
	}	

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getClaveDesarrollo() {
		return claveDesarrollo;
	}

	public void setClaveDesarrollo(String claveDesarrollo) {
		this.claveDesarrollo = claveDesarrollo;
	}

	public String getNombreDesarrollo() {
		return nombreDesarrollo;
	}

	public void setNombreDesarrollo(String nombreDesarrollo) {
		this.nombreDesarrollo = nombreDesarrollo;
	}	

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}	

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getNss() {
		return nss;
	}

	public void setNss(String nss) {
		this.nss=nss;
	}

	public Long getIdNomina() {
		return idNomina;
	}

	public void setIdNomina(Long idNomina) {
		this.idNomina=idNomina;
	}

  public Long getIdSeguro() {
    return idSeguro;
  }

  public void setIdSeguro(Long idSeguro) {
    this.idSeguro = idSeguro;
  }
  
}