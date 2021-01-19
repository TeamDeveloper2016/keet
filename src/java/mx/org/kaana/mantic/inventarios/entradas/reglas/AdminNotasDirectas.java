package mx.org.kaana.mantic.inventarios.entradas.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.operation.Select;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEmpleado;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntradaDirecta;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaProyecto;
import mx.org.kaana.mantic.inventarios.entradas.enums.EGastos;

/**
 *@company INEGI
 *@project IKTAN (Sistema de seguimiento y control de proyectos)
 *@date 18/01/2021
 *@time 07:36:13 PM 
 *@author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */

public final class AdminNotasDirectas extends AdminNotas implements Serializable {

  private static final long serialVersionUID = 4338079778576309872L;

  public AdminNotasDirectas(NotaEntradaDirecta orden) throws Exception {
    this(orden, EOrdenes.DIRECTA);
  }

  public AdminNotasDirectas(NotaEntradaDirecta orden, EOrdenes tipoOrden) throws Exception {
    super(orden, tipoOrden);
    if(this.orden.isValid())
      ((NotaEntradaDirecta)this.orden).setIkEmpresa(new UISelectEntity(new Entity(this.orden.getIdEmpresa())));
    else
      ((NotaEntradaDirecta)this.orden).setIkEmpresa(new UISelectEntity(-1L));
    this.toLoadProyectosEmpleados();
  }

  private void toLoadProyectosEmpleados() {
    Map<String, Object> params= null;
    try {      
      Double suma= 0D;
      params = new HashMap<>();      
      params.put("idNotaEntrada", this.orden.getIdNotaEntrada());      
      List<NotaProyecto> proyectos= (List<NotaProyecto>)DaoFactory.getInstance().toEntitySet(NotaProyecto.class, "VistaIngresosDto", "proyecto", params);
      for (NotaProyecto item: proyectos) {
        item.setIkDesarrollo(new UISelectEntity(item.getIdDesarrollo()));
        item.setIkCliente(new UISelectEntity(item.getIdCliente()));
        item.setIkContrato(new UISelectEntity(item.getIdContrato()));
        ((NotaEntradaDirecta)this.orden).getProyectos().add(new Select(item));
        suma+= item.getImporte();
      } // for
      ((NotaEntradaDirecta)this.orden).toAdd(EGastos.DIRECTOS, suma);
      suma= 0D;
      List<NotaEmpleado> empleados= (List<NotaEmpleado>)DaoFactory.getInstance().toEntitySet(NotaEmpleado.class, "VistaIngresosDto", "empleado", params);
      for (NotaEmpleado item: empleados) {
        item.setIkDesarrollo(new UISelectEntity(item.getIdDesarrollo()));
        item.setIkCliente(new UISelectEntity(item.getIdCliente()));
        item.setIkContrato(new UISelectEntity(item.getIdContrato()));
        item.setIkEmpresaPersona(new UISelectEntity(item.getIdEmpresaPersona()));
        ((NotaEntradaDirecta)this.orden).getEmpleados().add(new Select(item));
        suma+= item.getImporte();
      } // for
      ((NotaEntradaDirecta)this.orden).toAdd(EGastos.MANO_DE_OBRA, suma);
      ((NotaEntradaDirecta)this.orden).setDeuda(((NotaEntradaDirecta)this.orden).getOriginal());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
}
