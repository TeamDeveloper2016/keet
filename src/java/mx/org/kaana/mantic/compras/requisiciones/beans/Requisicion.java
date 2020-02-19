package mx.org.kaana.mantic.compras.requisiciones.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;

public class Requisicion extends TcManticRequisicionesDto implements Serializable{

	private static final long serialVersionUID = -4646811548830622174L;	
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
		this.pedido = pedido;
		this.entrega= entrega;
	}

	public LocalDate getPedido() {
		return pedido;
	}

	public void setPedido(LocalDate pedido) {
		this.pedido = pedido;
		if(this.pedido!= null)
			setFechaPedido(pedido);
	}

	public LocalDate getEntrega() {
		return entrega;
	}

	public void setEntrega(LocalDate entrega) {
		this.entrega = entrega;
		if(this.entrega!= null)
			setFechaEntregada(entrega);
	}	
	
}