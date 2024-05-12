package mx.org.kaana.mantic.compras.requisiciones.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class TicketRequisicion extends TcManticRequisicionesDto implements Serializable {

	private static final long serialVersionUID = 1445893582080951078L;

	public TicketRequisicion() {
		this(-1L);
	}

	public TicketRequisicion(Long key) {
		this(-1L, "", -1L, 1L, 1L, -1L);
	}
	
	public TicketRequisicion(Long idUsuario, String observaciones, Long idEmpresa, Long idRequisicionEstatus, Long orden, Long ejercicio) {
		super(0D, -1L, -1L, "0", idRequisicionEstatus, -1L, -1L, ejercicio, "", LocalDate.now(), 0D,  idUsuario, 0D, 0D, observaciones, idEmpresa, LocalDate.now(), orden, null, null, 2L);
	}

	@Override
	public Class toHbmClass() {
		return TcManticRequisicionesDto.class;
	}
  
}
