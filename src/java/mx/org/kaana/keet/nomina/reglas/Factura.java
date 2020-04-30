package mx.org.kaana.keet.nomina.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasProveedoresDto;
import mx.org.kaana.keet.nomina.beans.Rubro;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/04/2020
 *@time 09:14:48 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Factura implements Serializable {

	private static final long serialVersionUID=237030342701981082L;
  private static final Log LOG=LogFactory.getLog(Nomina.class);
	
	private Session sesion;
	private TcKeetNominasDto nomina;
	private List<Rubro> rubros;

	public Factura(Session sesion, TcKeetNominasDto nomina) {
		this.sesion=sesion;
		this.nomina=nomina;
	}
	
	private void toDestajos(TcKeetNominasProveedoresDto proveedor) throws Exception {
	}
	
	public void process(TcKeetNominasProveedoresDto proveedor) throws Exception {
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			params.put("idProveedor", proveedor.getIdProveedor());
			List<TcKeetContratosDestajosProveedoresDto> lotes= (List<TcKeetContratosDestajosProveedoresDto>)DaoFactory.getInstance().toEntitySet(this.sesion, TcKeetContratosDestajosProveedoresDto.class, "VistaNominaDto", "subContratista", params);
			if(lotes!= null && !lotes.isEmpty()) {
				Double suma= 0D;
				for(TcKeetContratosDestajosProveedoresDto lote: lotes) {
					lote.setIdNomina(this.nomina.getIdNomina());
					suma+= lote.getCosto();
					DaoFactory.getInstance().update(this.sesion, lote);
				} // for
				proveedor.setSubtotal(Numero.toRedondearSat(suma));
				proveedor.setIva(Numero.toRedondearSat(suma* 0.16));
				proveedor.setTotal(Numero.toRedondearSat(suma* 1.16));
				this.rubros= (List<Rubro>)DaoFactory.getInstance().toEntitySet(this.sesion, Rubro.class, "VistaNominaDto", "rubros", params);
				DaoFactory.getInstance().insert(this.sesion, proveedor);
				// ALMACENAR EL DETALLE DE CALCULO DE LA NOMINA DEL PROVEEDOR
				for (Rubro rubro: this.rubros) {
					rubro.setIdNominaProveedor(proveedor.getIdNominaProveedor());
					DaoFactory.getInstance().insert(this.sesion, rubro);
				} // for		
			} // if
		} // try
		finally {
			Methods.clean(params);
		} // finally	
	}
	
}
