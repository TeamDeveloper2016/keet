package mx.org.kaana.mantic.compras.requisiciones.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;

public class Requisicion extends TcManticRequisicionesDto implements Serializable{

	private static final long serialVersionUID = -4646811548830622174L;	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikContrato;
	private LocalDate pedido;
	private LocalDate entrega;

	public Requisicion() {
		this(LocalDate.now(), LocalDate.now());
	}

	public Requisicion(Long key) {
		this(key, LocalDate.now(), LocalDate.now());
	}

	public Requisicion(LocalDate pedido, LocalDate entrega) {
		this(-1L, LocalDate.now(), LocalDate.now());
	}
	
	public Requisicion(Long key, LocalDate pedido, LocalDate entrega) {
		super(key);
    this.setIkEmpresa(new UISelectEntity(-1L));
    this.setIkDesarrollo(new UISelectEntity(-1L));
    this.setIkContrato(new UISelectEntity(-1L));
		this.pedido = pedido;
		this.entrega= entrega;
	}

  public UISelectEntity getIkEmpresa() {
    return ikEmpresa;
  }

  public void setIkEmpresa(UISelectEntity ikEmpresa) {
    this.ikEmpresa = ikEmpresa;
    if(!Objects.equals(ikEmpresa, null))
      this.setIdEmpresa(ikEmpresa.getKey());
  }

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
    if(!Objects.equals(ikDesarrollo, null))
      this.setIdDesarrollo(ikDesarrollo.getKey());
  }

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
    if(!Objects.equals(ikContrato, null))
      this.setIdContrato(ikContrato.getKey());
  }

	public LocalDate getPedido() {
		return pedido;
	}

	public void setPedido(LocalDate pedido) {
		this.pedido = pedido;
		if(this.pedido!= null)
			this.setFechaPedido(pedido);
	}

	public LocalDate getEntrega() {
		return entrega;
	}

	public void setEntrega(LocalDate entrega) {
		this.entrega = entrega;
		if(this.entrega!= null)
			this.setFechaEntregada(entrega);
	}	
	
}