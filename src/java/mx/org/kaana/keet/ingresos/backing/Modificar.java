package mx.org.kaana.keet.ingresos.backing;

import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.ingresos.beans.Ingreso;
import mx.org.kaana.keet.ingresos.beans.Retencion;
import mx.org.kaana.keet.ingresos.enums.EClaveCatalogo;
import static mx.org.kaana.keet.ingresos.enums.EClaveCatalogo.COMPROBANTES;
import static mx.org.kaana.keet.ingresos.enums.EClaveCatalogo.MEDIOS_PAGO;
import static mx.org.kaana.keet.ingresos.enums.EClaveCatalogo.SERIES;
import static mx.org.kaana.keet.ingresos.enums.EClaveCatalogo.TIPOS_PAGOS;
import static mx.org.kaana.keet.ingresos.enums.EClaveCatalogo.USOS_CFDI;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.keet.ingresos.reglas.Transaccion;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Variables;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticClientesDeudasDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetIngresosModificar")
@ViewScoped
public class Modificar extends Accion implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Modificar.class);
  private static final long serialVersionUID= 327393488565639362L;

  @Override
  public String doAceptar() { 
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      transaccion = new Transaccion(this.ingreso, this.comprobante, this.articulos, this.getXml(), this.getPdf());
      if (transaccion.ejecutar(this.accion)) {
        regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
        JsfBase.addMessage("Se modificó".concat(" la factura"), ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar la factura !", ETipoMensaje.ERROR);      			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    JsfBase.setFlashAttribute("idVenta", this.ingreso.getIdVenta());
    return regresar;
  } // doAccion
  
}