package mx.org.kaana.keet.catalogos.contratos.contratistas.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesContratistasDto;

public class ContratistaLote extends TcKeetContratosLotesContratistasDto implements Serializable{

	private static final long serialVersionUID = -1210601775101099137L;
	private Long idKey;
	private String nombres;
	private String paterno;
	private String materno;
	private String puesto;
	private String departamento;
	private String curp;
	private String rfc;
	private Long idActivo;		
	private Long idNomina;		
	private String nss;		
	private ESql sqlAccion;
	private Boolean nuevo;
	private String clave;
	private String claveDesarrollo;
	private String nombreDesarrollo;
	private Long idPersona;
	private Long tipo;

	public ContratistaLote() {
		this(-1L);
	}

	public ContratistaLote(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ContratistaLote(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ContratistaLote(Long key, ESql sqlAccion, Boolean nuevo) {		
		this(key, "", "", "", "", "", "", "", -1L, sqlAccion, nuevo, "", "", "", -1L, 2L, null, null, -1L);		
	}

	public ContratistaLote(Long key, String nombres, String paterno, String materno, String puesto, String departamento, String curp, String rfc, Long idActivo, ESql sqlAccion, Boolean nuevo, String clave, String claveDesarrollo, String nombreDesarrollo, Long idPersona, Long idNomina, String nss, Long tipo, Long idKey) {
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
		this.idNomina        = idNomina;
		this.nss             = nss;
		this.tipo            = tipo;
		this.idKey           = idKey;
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

	public Long getIdNomina() {
		return idNomina;
	}

	public void setIdNomina(Long idNomina) {
		this.idNomina=idNomina;
	}

	public String getNss() {
		return nss;
	}

	public void setNss(String nss) {
		this.nss = nss;
	}

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}	

	public Long getIdKey() {
		return idKey;
	}

	public void setIdKey(Long idKey) {
		this.idKey = idKey;
	}	
}