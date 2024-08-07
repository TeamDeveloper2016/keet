package mx.org.kaana.mantic.catalogos.empresas.cuentas.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

public class TreeCuenta implements Serializable, IBaseDto {

	private static final long serialVersionUID=4222652089397624991L;

	private Long idKey;
	private String consecutivo;
	private Double importe;
	private Double saldo;
	private Double pago;
	private Long idEmpresa;
	private Long idEmpresaEstatus;
	private String persona;	
	private LocalDateTime registro;	
	private String proveedor;	
	private String almacen;				
	private String clave;				
	private String nombre;				
	private Long dias;
	private boolean ultimoNivel;  
	private Long idProveedor;
	private Long totalArchivos;
	private Long idEmpresaArchivo;

	public TreeCuenta() {
		this(-1L);
	}
	
	public TreeCuenta(Long idKey) {
		this(idKey, false);
	}	
	
	public TreeCuenta(Long idKey, boolean ultimoNivel) {
		this(idKey, null, null, null, null, null, null, null, LocalDateTime.now(), null, null, null, null, null, ultimoNivel, null, 0L, -1L);
	}	

	public TreeCuenta(Long idKey, String consecutivo, Double importe, Double saldo, Double pago, Long idEmpresa, Long idEmpresaEstatus, String persona, LocalDateTime registro, String proveedor, String almacen, String clave, String nombre, Long dias, boolean ultimoNivel, Long idProveedor, Long totalArchivos, Long idEmpresaArchivo) {
		this.idKey           = idKey;
		this.consecutivo     = consecutivo;
		this.importe         = importe;
		this.saldo           = saldo;
		this.pago            = pago;
		this.idEmpresa       = idEmpresa;
		this.idEmpresaEstatus= idEmpresaEstatus;
		this.persona         = persona;
		this.registro        = registro;
		this.proveedor       = proveedor;
		this.almacen         = almacen;
		this.clave           = clave;
		this.nombre          = nombre;
		this.dias            = dias;
		this.ultimoNivel     = ultimoNivel;
		this.idProveedor     = idProveedor;
		this.totalArchivos   = totalArchivos;
		this.idEmpresaArchivo= idEmpresaArchivo;
	}

	public Long getIdKey() {
		return idKey;
	}

	public void setIdKey(Long idKey) {
		this.idKey = idKey;
	}

	public String getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Double getPago() {
		return pago;
	}

	public void setPago(Double pago) {
		this.pago = pago;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Long getIdEmpresaEstatus() {
		return idEmpresaEstatus;
	}

	public void setIdEmpresaEstatus(Long idEmpresaEstatus) {
		this.idEmpresaEstatus = idEmpresaEstatus;
	}

	public String getPersona() {
		return persona;
	}

	public void setPersona(String persona) {
		this.persona = persona;
	}

	public LocalDateTime getRegistro() {
		return registro;
	}

	public void setRegistro(LocalDateTime registro) {
		this.registro = registro;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getAlmacen() {
		return almacen;
	}

	public void setAlmacen(String almacen) {
		this.almacen = almacen;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getDias() {
		return dias;
	}

	public void setDias(Long dias) {
		this.dias = dias;
	}

	public boolean isUltimoNivel() {
		return ultimoNivel;
	}

	public void setUltimoNivel(boolean ultimoNivel) {
		this.ultimoNivel = ultimoNivel;
	}	

	public Long getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(Long idProveedor) {
		this.idProveedor = idProveedor;
	}	

	public Long getTotalArchivos() {
		return totalArchivos;
	}

	public void setTotalArchivos(Long totalArchivos) {
		this.totalArchivos = totalArchivos;
	}	

	public Long getIdEmpresaArchivo() {
		return idEmpresaArchivo;
	}

	public void setIdEmpresaArchivo(Long idEmpresaArchivo) {
		this.idEmpresaArchivo = idEmpresaArchivo;
	}	
	
	@Override
	public Long getKey() {
		return this.idKey;
	}

	@Override
	public void setKey(Long key) {
		this.idKey= key;
	}

	@Override
	public Map<String, Object> toMap() {
		return null;
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public boolean isValid() {
		return getKey()!= null && !getKey().equals(-1L);
	}

	@Override
	public Object toValue(String name) {
		return null;
	}

	@Override
	public String toAllKeys() {
		return null;
	}

	@Override
	public String toKeys() {
		return null;
	}

	@Override
	public Class toHbmClass() {
		return null;
	}

	@Override
	public String toString() {
		return "Mindmap {"
					+ "idKey="+idKey+", "
					+ "consecutivo="+consecutivo+", "
					+ "importe="+importe+", "
					+ "saldo="+saldo+", "
					+ "pago="+pago+", "
					+ "idEmpresa="+idEmpresa+", "
					+ "idEmpresaEstatus="+idEmpresaEstatus+", "					
					+ "persona="+persona+", "
					+ "registro="+registro+", "
					+ "proveedor="+proveedor+", "										
					+ "almacen="+almacen+", "
					+ "clave="+clave+", "
					+ "nombre="+nombre+", "
					+ "dias="+dias+", "					
					+ "idProveedor="+idProveedor+", "					
					+ "totalArchivos="+totalArchivos+", "					
					+ "idEmpresaArchivo="+idEmpresaArchivo+", "					
					+ "ultimoNivel="+ultimoNivel+'}';
	} // toString
}