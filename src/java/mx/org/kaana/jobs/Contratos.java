package mx.org.kaana.jobs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 12/09/2023
 *@time 09:14:15 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Contratos implements Job, Serializable {
  
  private static final Log LOG = LogFactory.getLog(Contratos.class);
  private static final long serialVersionUID = 5848037222682242442L;
  
  public Contratos() {
  }
  
  @Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {    
    List<Entity> contratos    = null;
    Map<String, Object> params= new HashMap<>();
    Boolean process           = Boolean.FALSE;      
    Transaccion transaccion   = null;
		try {
			if(!Configuracion.getInstance().isEtapaDesarrollo() && !Configuracion.getInstance().isEtapaCapacitacion()) {
        switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
          case "cafu":
            process= Boolean.TRUE;
            break;
          case "gylvi":
            break;
          case "triana":
            break;
        } // swtich
        LOG.error("PROCESANDO LOS CONTRATOS PARA ACTIVAR O DESACTIVAR SEGUN APLIQUE");
        if(process) {
          params.put("estatus", "1, 2, 3, 4, 5");      
          params.put("fecha", "20231001");      
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
          contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaContratosDto", "control", params);
          if(contratos!= null && !contratos.isEmpty()) {
            LOG.error("CANTIDAD DE CONTRATOS A REVISAR "+ contratos.size());
            for (Entity item: contratos) {
              if(item.toLong("dias")>= 19L) {
                StringBuilder control= new StringBuilder(this.toCheckContrato(item));
                Long idContratoEstatus= null;
                if(Objects.equals(control.length(), 0)) {
                  if(!Objects.equals(item.toLong("idContratoEstatus"), EContratosEstatus.ACTIVO.getKey()))
                    idContratoEstatus= EContratosEstatus.ACTIVO.getKey();
                } // if  
                else {
                  control.insert(0, "CONTRATO: ".concat(item.toString("nombre")).concat(", NUMERO: ").concat(item.toString("numero")).concat(" => "));
                  control.append(" => ").append(item.toLong("idContratoEstatus"));
                  LOG.error(control.toString());
                  if(Objects.equals(item.toLong("idContratoEstatus"), EContratosEstatus.ACTIVO.getKey()))
                    idContratoEstatus= EContratosEstatus.ESCANEADO.getKey();
                } // if  
                if(!Objects.equals(idContratoEstatus, null)) {
                  transaccion = new Transaccion(item.toLong("idContrato"), idContratoEstatus);
                  if(transaccion.ejecutar(EAccion.ASIGNAR))
                    LOG.error("Se actualizó de forma correcta el contrato [ "+ item.toLong("idContrato")+ " ]");
                  else
                    LOG.error("Ocurrio un error al actualizar el contrato");		              
                } // if  
              } // if  
            } // for
            LOG.error("TERMINANDO DE REVISAR LOS ESTATUS DE LOS CONTRATOS");
          } // if
        } // if
			} // if
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
      LOG.error("Ocurrio un error vefificar los contratos");
		} // catch	
    finally {
      Methods.clean(contratos);
      Methods.clean(params);
    } // finally
  }
  
	public String toCheckContrato(Long idContrato, Boolean update) throws Exception {    
    StringBuilder regresar    = new StringBuilder();
    List<Entity> contratos    = null;
    Map<String, Object> params= new HashMap<>();
    Transaccion transaccion   = null;
		try {
      params.put("estatus", "1, 2, 3, 4, 5, 6, 7, 8, 9, 10");      
      params.put("fecha", "20000101");      
      params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato= "+ idContrato);      
      contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaContratosDto", "control", params);
      if(contratos!= null && !contratos.isEmpty()) {
        for (Entity item: contratos) {
          Long idContratoEstatus= null;
          regresar.append(this.toCheckContrato(item));
          if(!Objects.equals(regresar.length(), 0)) {
            regresar.insert(0, "CONTRATO: ".concat(item.toString("nombre")).concat(", NUMERO: ").concat(item.toString("numero")).concat(" => "));
            if(Objects.equals(item.toLong("idContratoEstatus"), EContratosEstatus.ACTIVO.getKey()))
              idContratoEstatus= EContratosEstatus.ESCANEADO.getKey();
            LOG.error(regresar.toString());
          } // if  
          else {
            regresar.append("EL CONTRATO TIENE TODO EN ORDEN ...");
            if(!Objects.equals(item.toLong("idContratoEstatus"), EContratosEstatus.ACTIVO.getKey()))
              idContratoEstatus= EContratosEstatus.ACTIVO.getKey();
          } // else  
          
          if(update && !Objects.equals(idContratoEstatus, null)) {
            transaccion = new Transaccion(item.toLong("idContrato"), idContratoEstatus);
            if(transaccion.ejecutar(EAccion.ASIGNAR))
              LOG.error("Se actualizó de forma correcta el contrato [ "+ item.toLong("idContrato")+ " ]");
            else
              LOG.error("Ocurrio un error al actualizar el contrato");		              
          } // if    
        } // for
      } // if
      else
        regresar.append("EL CONTRATO TIENE TODO EN ORDEN ...");
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
      throw e;
		} // catch	
    finally {
      Methods.clean(contratos);
      Methods.clean(params);
    } // finally
    return regresar.toString();
  }
  
  private String toCheckContrato(Entity item) throws Exception {
    StringBuilder regresar    = new StringBuilder();
    Map<String, Object> params= new HashMap<>();
    double costo, total, materiales, destajos, subcontratos, porElDia, administrativos, maquinaria, indirectos, utilidad;
    long viviendas, lotes, individual;
    try {
      costo       = Objects.equals(item.toDouble("costo"), null)? 0D: item.toDouble("costo");
      // ESTE REPRESENTA CUANTOS LOTES NO TIENE UN COSTO DEFINIDO
      individual  = Objects.equals(item.toLong("individual"), null)? 0L: item.toLong("individual");
      // ESTE ES LA SUMA CADA COSTO POR LOTE
      total       = Objects.equals(item.toDouble("total"), null)? 0D: item.toDouble("total");
      materiales  = Objects.equals(item.toDouble("materiales"), null)? 0D: item.toDouble("materiales");
      destajos    = Objects.equals(item.toDouble("destajos"), null)? 0D: item.toDouble("destajos");
      subcontratos= Objects.equals(item.toDouble("subcontratados"), null)? 0D: item.toDouble("subcontratados");
      porElDia    = Objects.equals(item.toDouble("porElDia"), null)? 0D: item.toDouble("porElDia");
      administrativos= Objects.equals(item.toDouble("administrativos"), null)? 0D: item.toDouble("administrativos");
      maquinaria  = Objects.equals(item.toDouble("maquinaria"), null)? 0D: item.toDouble("maquinaria");
      indirectos  = Objects.equals(item.toDouble("indirecto"), null)? 0D: item.toDouble("indirecto");
      utilidad    = Objects.equals(item.toDouble("utilidad"), null)? 0D: item.toDouble("utilidad");

      viviendas   = Objects.equals(item.toLong("viviendas"), null)? 0L: item.toLong("viviendas");
      lotes       = Objects.equals(item.toLong("lotes"), null)? 0L: item.toLong("lotes");

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO SU COSTO DEL PRESUPUESTO
      if(Objects.equals(costo, 0D))
        regresar.append(", PRESUPUESTO: ").append(costo);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS MATERIALES
      if(Objects.equals(materiales, 0D))
        regresar.append(", MATERIALES: ").append(materiales);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS DESTAJOS
      if(Objects.equals(destajos, 0D))
        regresar.append(", DESTAJOS: ").append(destajos);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS POR EL DIA
      if(Objects.equals(porElDia, 0D))
        regresar.append(", POR EL DIA: ").append(porElDia);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS ADMINISTRATIVOS
      if(Objects.equals(administrativos, 0D))
        regresar.append(", ADMINISTRATIVOS: ").append(administrativos);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS SUBCONTRATOS
      if(Objects.equals(subcontratos, 0D))
        regresar.append(", SUBCONTRATOS: ").append(subcontratos);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS MAQUINARIA
      if(Objects.equals(maquinaria, 0D))
        regresar.append(", MAQUINARIA: ").append(maquinaria);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS INDIRECTOS
      if(Objects.equals(indirectos, 0D))
        regresar.append(", INDIRECTOS: ").append(indirectos);

      // VERIFICAR QUE EL CONTRATO TENGA CARGADO EL COSTO DE LOS UTILIDAD
      if(Objects.equals(utilidad, 0D))
        regresar.append(", UTILIDAD: ").append(utilidad);

      // VERIFICAR QUE EL COSTO DEL CONTRATO CORRESPONDA A LA SUMA DE LOS COSTOS POR LOTE
      if(!Objects.equals(costo, total))
        regresar.append(", COSTOS: contrato[ ").append(costo).append(" ] VS lotes[ ").append(total);

      // VERIFICAR QUE EL DESGLOSE DE LOS COSTOS CORRESPODA CON EL COSTO DEL CONTRATO
      if(!Objects.equals(costo, maquinaria+ destajos+ subcontratos+ porElDia+ administrativos+ maquinaria+ indirectos+ utilidad))
        regresar.append(", COSTOS: contrato[ ").append(costo).append(" ] VS lotes[ ").append(maquinaria+ destajos+ subcontratos+ porElDia+ administrativos+ maquinaria+ indirectos+ utilidad).append(" ]");

      // VERIFICAR QUE LA CANTIDAD DE VIVIENDAS CORRESPONDA A LA CANTIDAD DE LOTES
      if(!Objects.equals(viviendas, lotes))
        regresar.append(", VIVIENDAS DIFERENTES: viviendas[ ").append(viviendas).append(" ] VS lotes[ ").append(lotes).append(" ]");

      // VERIFICAR QUE TODOS LOS LOTES TENGAN UNA COORDENADA 
      if(!Objects.equals(lotes, item.toLong("latitud")) || !Objects.equals(lotes, item.toLong("longitud")))
        regresar.append(", COORDENADAS: lotes [ ").append(lotes).append(" ] con latitud [ ").append(item.toLong("latitud")).append(" ] con longitud [ ").append(item.toLong("longitud")).append(" ]");

      // VERIFICAR QUE TODOS LOS LOTES TENGAN UN COSTO ASIGNADO
      if(!Objects.equals(lotes, individual))
        regresar.append(", COSTOS: lotes [ ").append(lotes).append(" ] tiene un costo [ ").append(individual).append(" ] ");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally
    return regresar.toString();
  }  
  
  public static void main(String ... args ) throws JobExecutionException {
     Contratos contratos= new Contratos();
     contratos.execute(null);
  }
  
}
