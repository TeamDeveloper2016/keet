package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 13/08/2018
 * @time 10:31:19 AM 
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "kajoolAccesoAlerta")
@ViewScoped
public class Alerta extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID=-8923064340050821460L;

	@Override
	protected void init() {
	}

	public String getCheckCaja() {
		return "";
	}
	
}
