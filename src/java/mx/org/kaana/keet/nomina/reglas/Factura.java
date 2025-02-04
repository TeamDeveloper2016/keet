package mx.org.kaana.keet.nomina.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasProveedoresDto;
import mx.org.kaana.keet.nomina.beans.Rubro;
import mx.org.kaana.libs.Constantes;
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
	
  public void setSesion(Session sesion) {
    this.sesion = sesion;
  }
  
	public void process(TcKeetNominasProveedoresDto proveedor) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idNomina", this.nomina.getIdNomina());
			params.put("idProveedor", proveedor.getIdProveedor());
			List<TcKeetContratosDestajosProveedoresDto> lotes= (List<TcKeetContratosDestajosProveedoresDto>)DaoFactory.getInstance().toEntitySet(this.sesion, TcKeetContratosDestajosProveedoresDto.class, "VistaNominaDto", "subContratista", params);
			if(lotes!= null && !lotes.isEmpty()) {
				Double suma    = 0D;
				Double anticipo= 0D;
				for(TcKeetContratosDestajosProveedoresDto lote: lotes) {
					lote.setIdNomina(this.nomina.getIdNomina());
					suma+= lote.getCosto();
					anticipo+= lote.getAnticipo();
					DaoFactory.getInstance().update(this.sesion, lote);
          params.put("idContratoDestajoProveedor", lote.getIdContratoDestajoProveedor());
    			DaoFactory.getInstance().updateAll(this.sesion, TcKeetContratosPuntosProveedoresDto.class, params, "marcar");
				} // for
				proveedor.setDestajo(Numero.toRedondearSat(suma));
				proveedor.setAnticipo(Numero.toRedondearSat(anticipo));
        double importe= Numero.toRedondearSat(proveedor.getDestajo()- proveedor.getAnticipo());
				proveedor.setFondoGarantia(Numero.toRedondearSat(importe* Numero.toRedondearSat(proveedor.getPorcentajeFondo()/ 100)));
				proveedor.setSubtotal(Numero.toRedondearSat(importe- proveedor.getFondoGarantia()));
				proveedor.setIva(Numero.toRedondearSat(proveedor.getSubtotal()* Constantes.PORCENTAJE_IVA));
				proveedor.setTotal(Numero.toRedondearSat(proveedor.getSubtotal()* (1+ Constantes.PORCENTAJE_IVA)));
				this.rubros= (List<Rubro>)DaoFactory.getInstance().toEntitySet(this.sesion, Rubro.class, "VistaNominaDto", "rubros", params);
				DaoFactory.getInstance().insert(this.sesion, proveedor);
				// ALMACENAR EL DETALLE DE CALCULO DE LA NOMINA DEL PROVEEDOR
        for (Rubro rubro: this.rubros) {
          try {
            rubro.setIdNominaProveedor(proveedor.getIdNominaProveedor());
            importe= Numero.toRedondearSat(rubro.getDestajo()- rubro.getAnticipo());
            rubro.setPorcentajeFondo(proveedor.getPorcentajeFondo());
            rubro.setFondoGarantia(Numero.toRedondearSat(importe* Numero.toRedondearSat(proveedor.getPorcentajeFondo()/ 100)));
            rubro.setSubtotal(Numero.toRedondearSat(importe- rubro.getFondoGarantia()));
            rubro.setIva(Numero.toRedondearSat(rubro.getSubtotal()* Constantes.PORCENTAJE_IVA));
            rubro.setTotal(Numero.toRedondearSat(rubro.getSubtotal()* (1+ Constantes.PORCENTAJE_IVA)));
            DaoFactory.getInstance().insert(this.sesion, rubro);
          } // try
          catch(Exception e) {
            LOG.error("FALLO: "+ rubro+ "["+ e+ "]");
          } // catch
        } // for		
			} // if
		} // try
		finally {
			Methods.clean(params);
		} // finally	
	}
	
}
