package mx.org.kaana.mantic.facturas.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticFicticiasDto;

public class FacturaFicticia extends TcManticFicticiasDto implements Serializable {

	private static final long serialVersionUID = -4493162062949623588L;	
	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;

	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
	}

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
		  this.setIdDesarrollo(this.ikDesarrollo.getKey());
  }

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente=ikCliente;
		if(this.ikCliente!= null)
		  this.setIdCliente(this.ikCliente.getKey());
	}

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
		  this.setIdContrato(this.ikContrato.getKey());
  }
  
	public FacturaFicticia() {
		this(-1L);
	}

	public FacturaFicticia(Long key) {
		this(0D, null, -1L, key, "0", 0D, 0D, 1L, 1D, -1L, -1L, -1L, -1L, "0", null, Long.valueOf(Fecha.getAnioActual()), "", -1L, 0D, 2L, 2L, 0D, "", -1L, LocalDate.now(), "");
	}
	
	public FacturaFicticia(Double descuentos, Long idFactura, Long idTipoPago, Long idFicticia, String extras, Double global, Double total, Long idFicticiaEstatus, Double tipoDeCambio, Long orden, Long idTipoMedioPago, Long idCliente, Long idClienteDomicilio, String descuento, Long idBanco, Long ejercicio, String consecutivo, Long idUsuario, Double impuestos, Long idUsoCfdi, Long idSinIva, Double subTotal, String observaciones, Long idEmpresa, LocalDate dia, String referencia) {		
		super(descuentos, idTipoPago, idFicticia, extras, global, total, idFicticiaEstatus, tipoDeCambio, orden, idTipoMedioPago, idCliente, idClienteDomicilio, descuento, idBanco, ejercicio, consecutivo, idUsuario, impuestos, idUsoCfdi, idSinIva, subTotal, observaciones, idEmpresa, dia, referencia, idFactura);		
    this.setCandado(2L);			
    this.setIdAutorizar(2L);			
    this.setIdCredito(1L);			
    this.setIdFacturar(1L);			
    this.setIdManual(2L);			
    this.setCobro(LocalDateTime.now());			
    this.setIdSincronizado(2L);			
	}	
	
	@Override
	public Class toHbmClass() {
		return TcManticFicticiasDto.class;
	}			
  
}
