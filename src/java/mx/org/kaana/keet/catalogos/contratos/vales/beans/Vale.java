package mx.org.kaana.keet.catalogos.contratos.vales.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;

public class Vale implements Serializable{

	private static final long serialVersionUID= 5917707650360083012L;	
	private Long idFigura;
	private Long tipoFigura;
	private List<Articulo> articulos;
	private List<MaterialVale> materiales;
	private List<DetalleVale> detalle;
	private List<DetalleVale> padres;
	private Long idTipoVale;
	private String justificacion;
	private Long idAlmacen;
	private String nombreFigura;
	private Long idDesarrollo;

	public Vale() {
		this(-1L, -1L, new ArrayList<>(), -1L, "", -1L, new ArrayList<>(), new ArrayList<>(), "", new ArrayList<>(), -1L);
	}
	
	public Vale(Long idFigura, Long tipoFigura, List<MaterialVale> materiales, Long idTipoVale, String justificacion,Long idAlmacen, List<DetalleVale> detalle, List<DetalleVale> padres, String nombreFigura, List<Articulo> articulos, Long idDesarrollo) {
		this.idFigura     = idFigura;
		this.tipoFigura   = tipoFigura;
		this.materiales   = materiales;
		this.idTipoVale   = idTipoVale;
		this.justificacion= justificacion;
		this.idAlmacen    = idAlmacen;
		this.detalle      = detalle;
		this.nombreFigura = nombreFigura;						
		this.articulos    = articulos;
		this.idDesarrollo = idDesarrollo;
	}

	public Long getIdFigura() {
		return idFigura;
	}

	public void setIdFigura(Long idFigura) {
		this.idFigura = idFigura;
	}

	public Long getTipoFigura() {
		return tipoFigura;
	}

	public void setTipoFigura(Long tipoFigura) {
		this.tipoFigura = tipoFigura;
	}

	public List<MaterialVale> getMateriales() {
		return materiales;
	}

	public void setMateriales(List<MaterialVale> materiales) {
		this.materiales = materiales;
	}

	public Long getIdTipoVale() {
		return idTipoVale;
	}

	public void setIdTipoVale(Long idTipoVale) {
		this.idTipoVale = idTipoVale;
	}

	public String getJustificacion() {
		return justificacion;
	}

	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}

	public Long getIdAlmacen() {
		return idAlmacen;
	}

	public void setIdAlmacen(Long idAlmacen) {
		this.idAlmacen = idAlmacen;
	}	

	public List<DetalleVale> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<DetalleVale> detalle) {
		this.detalle = detalle;
	}	

	public String getNombreFigura() {
		return nombreFigura;
	}

	public void setNombreFigura(String nombreFigura) {
		this.nombreFigura = nombreFigura;		
	}	

	public List<DetalleVale> getPadres() {
		return padres;
	}

	public void setPadres(List<DetalleVale> padres) {
		this.padres = padres;
	}	

	public List<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(List<Articulo> articulos) {
		this.articulos = articulos;
	}	

	public Long getIdDesarrollo() {
		return idDesarrollo;
	}

	public void setIdDesarrollo(Long idDesarrollo) {
		this.idDesarrollo = idDesarrollo;
	}	
}