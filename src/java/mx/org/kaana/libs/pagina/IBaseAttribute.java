package mx.org.kaana.libs.pagina;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.recurso.TcConfiguraciones;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project  (Sistema de Seguimiento y Control de Proyectos Estadisticos)
 *@date 20-feb-2014
 *@time 15:20:21
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public abstract class IBaseAttribute implements Serializable {

  private static final long serialVersionUID = -5324353121363296378L;
  private static final Log LOG = LogFactory.getLog(IBaseAttribute.class);

  protected Map<String, Object> attrs;

  public IBaseAttribute() {
    this.attrs= new HashMap<>();
  }

  public Map<String, Object> getAttrs() {
    return attrs;
  }

  public void setAttrs(Map<String, Object> attrs) {
    this.attrs = attrs;
  }

  protected abstract void init();

  protected Object toAttr(String name, Object value) {
    if(this.attrs.containsKey(name))
      return this.attrs.get(name);
    else
      return value;
  }

  protected Object toAttr(String name) {
    return toAttr(name, "");
  }

  @Override
  protected void finalize() throws Throwable {
    try {
      this.attrs.clear();
      for (Object item: this.attrs.values())
        Methods.clean(item);
      this.attrs= null;
    } // try
    finally {
      super.finalize();
    } // finally
  }

  public String doFechaEstandar(Timestamp fecha) {
		return fecha== null? "": Global.format(EFormatoDinamicos.FECHA_CORTA, fecha);
	}
	
  public String doFechaEstandar(LocalDateTime fecha) {
		return fecha== null? "": Global.format(EFormatoDinamicos.FECHA_CORTA, fecha);
	}
	
  public String doFechaEstandar(LocalDate fecha) {
		return fecha== null? "": Global.format(EFormatoDinamicos.FECHA_CORTA, fecha);
	}
	
  public String doDecimalSat(Double numero) {
		return Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, Numero.toRedondearSat(numero));
	}
	
  public String doDecimalSat(String numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, Numero.toRedondearSat(new Double(numero)));
	}
	
  public String doDecimalSat(BigDecimal numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, Numero.toRedondearSat(numero.doubleValue()));
	}
	
  public String doDecimalSat(Value numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, Numero.toRedondearSat(numero.toDouble()));
	}
	
  public String doMonedaSat(Double numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(numero));
	}
	
  public String doMonedaSat(String numero) {
		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(new Double(numero)));
	}
	
  public String doMonedaSat(BigDecimal numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(numero.doubleValue()));
	}
	
  public String doMonedaSat(Value numero) {
		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(numero.toDouble()));
	}
	
  public String doNumericoSat(Double numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.NUMERO_CON_DECIMALES, Numero.toRedondearSat(numero));
	}
	
  public String doNumericoSat(String numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.NUMERO_CON_DECIMALES, Numero.toRedondearSat(new Double(numero)));
	}
	
  public String doNumericoSat(BigDecimal numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.NUMERO_CON_DECIMALES, Numero.toRedondearSat(numero.doubleValue()));
	}
	
  public String doNumericoSat(Value numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.NUMERO_CON_DECIMALES, Numero.toRedondearSat(numero.toDouble()));
	}

  public String doNumerico(Long numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, numero);
	}

  public String doNumerico(Double numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, numero);
	}

  public String doNumerico(Value numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, numero.toLong());
	}
	
  public String doMiles(Long numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, numero);
	}

  public String doMiles(Value numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, numero.toLong());
	}

  public String doMiles(BigDecimal numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, Numero.toRedondearSat(numero.doubleValue()));
	}

  public String doMiles(Double numero) {
		return numero== null? "0": Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, numero);
	}

  public String doRegistro(Timestamp registro) {
		return registro== null? "": Global.format(EFormatoDinamicos.FECHA_HORA, registro);
	}

  public String doRegistro(LocalDateTime registro) {
		return registro== null? "": Global.format(EFormatoDinamicos.FECHA_HORA, registro);
	}

  public String doFecha(LocalDate fecha) {
		return fecha== null? "": Global.format(EFormatoDinamicos.FECHA_CORTA, fecha);
	}

  public String doFecha(Value fecha) {
		return fecha== null? "": Global.format(EFormatoDinamicos.FECHA_CORTA, fecha.toDate());
	}

	protected UISelectEntity toDefaultSucursal(List<UISelectEntity> sucursales) {
		UISelectEntity regresar= sucursales== null || sucursales.isEmpty()? new UISelectEntity(new Entity(-1L, "SELECCIONE")): sucursales.get(0);
    if(regresar.getKey()> 0L) {
			String sucursal= TcConfiguraciones.getInstance().getPropiedad("sucursal."+ JsfBase.getAutentifica().getCredenciales().getCuenta());
			if(!Cadena.isVacio(sucursal)) {
				int index= sucursales.indexOf(new UISelectEntity(sucursal));
				if(index>= 0)
					regresar= sucursales.get(index);
			} // if
      else
        regresar= new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
		} // if
		return regresar;
	}

  public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA");
  }
  
}
