package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22-sep-2015
 *@time 9:11:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.nomina.beans.Contrato;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.keet.nomina.reglas.Almacenar;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.formato.BouncyEncryption;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_OBRA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_OBRA;

public class Eliminar implements Job, Serializable {

	private static final Log LOG              =LogFactory.getLog(Eliminar.class);
	private static final long serialVersionUID=7505746848602636876L;
  
	private List<Entity> desarrollos;
  private List<Contrato> contratos;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
    Map<String, Object> params= new HashMap<>();    
    Long idNomina             = 194L; 
		try {
      Autentifica autentifica= this.toLoadUser();
      this.toLoadContratos(idNomina, autentifica);
    } // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch	
    finally {
      Methods.clean(params);
    } // finally
	} // execute
  
  private Autentifica toLoadUser() throws Exception {
    String password     = BouncyEncryption.decrypt(Configuracion.getInstance().getPropiedad("sistema.autenticar.contrasenia"));
    Autentifica regresar= new Autentifica();
    if (regresar.tieneAccesoBD(Configuracion.getInstance().getPropiedad("sistema.autenticar.cuenta"), password, "127.0.0.1")) {
      regresar.loadSucursales();
      LOG.warn("Acceso automático con autentifica [".concat(regresar.toString()).concat("]"));
    } // if  
    return regresar;
  } // toLoadUser

	private void toLoadContratos(Long idNomina, Autentifica autentifica) throws Exception {
    List<Entity> nominas     = null;
		Map<String, Object>params= new HashMap<>();
    try {
      LOG.error("ENTRO A CALCULAR LOS COSTOS POR CONTRATO");
      params.put("idTipoNomina", 1L);
      nominas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "ultima", params, 10L);
      if(nominas!= null && nominas.size()> 0) {
        Entity nomina= nominas.get(1);
        for (Entity item: nominas) {
          if(Objects.equals(idNomina, item.toLong("idNomina")))
            nomina= item;
        } // for
        params.put("idNomina", nomina.toLong("idNomina"));
        Entity existe= (Entity)DaoFactory.getInstance().toEntity("TcKeetNominasContratosCostosDto", "existe", params);
        if(Objects.equals(existe, null) || Objects.equals(existe.toLong("total"), 0L)) {
          this.toLoadDesarrollos(nomina);
          params.put("semana", nomina.toString("semana"));
          params.put("idNomina", nomina.toLong("idNomina"));
          this.contratos= (List<Contrato>)DaoFactory.getInstance().toEntitySet(Contrato.class, "VistaCostosContratosDto", "contratos", params);
          int count= 0;
          if(this.contratos!= null) {
            for (Contrato item: this.contratos) {
              if(item.isValid()) 
                item.setSql(ESql.SELECT);
              else {
                item.setSql(ESql.INSERT);
                item.setIdNomina(nomina.toLong("idNomina"));
                count++;
              } // if  
            } // for  
            if(Objects.equals(count, this.contratos.size()))
              this.toDivideCostos();
            Almacenar almacenar= new Almacenar(autentifica, this.contratos);
            if(almacenar.ejecutar(EAccion.GENERAR)) 
              LOG.error("Se registraron los costos de mano de obra");
            else
              LOG.error("Ocurrió un error en los costos de mano de obra");
          } // if    
        } // if    
      } // if    
    } // try // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally		
	}

  private void toLoadDesarrollos(Entity nomina) throws Exception {
		Map<String, Object>params= new HashMap<>();
    try {
			params.put("idNomina", nomina.toLong("idNomina"));
			params.put("sortOrder", "order by tc_keet_nominas_desarrollos.id_desarrollo");
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          break;
        case "gylvi":
          params.put("puestosPorDia", GYLVI_PERSONAL_DIA);      
          params.put("puestosPorObra", GYLVI_PERSONAL_OBRA);      
          break;
        case "triana":
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          break;
        default:  
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          break;
      } // switch
      this.desarrollos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosContratosDto", "desarrollos", params);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
    } // finally		
	}
  
  private void toDivideCostos() throws Exception {
    Map<Long, Integer> partes= new HashMap<>();
    try {
      int count        = 0;
      Long idDesarrollo= -1L;
      for (Contrato item: this.contratos) {
        if(!Objects.equals(idDesarrollo, -1L) && !Objects.equals(idDesarrollo, item.getIdDesarrollo())) {
          partes.put(idDesarrollo, count);
          count= 0;
        } // if
        idDesarrollo= item.getIdDesarrollo();
        count++;  
      } // for
      partes.put(idDesarrollo, count);
      for (Entity item: this.desarrollos) {
        Double porDiaCosto   = item.toDouble("porDiaCosto");
        Double porObraCosto  = item.toDouble("porObraCosto");
        idDesarrollo         = item.toLong("idDesarrollo");
        Double porDia        = 0D;
        Double porObra       = 0D;
        Double porcentajeDia = 0D;
        Double porcentajeObra= 0D;
        count= 1;
        for (Contrato contrato: this.contratos) {
          if(Objects.equals(idDesarrollo, contrato.getIdDesarrollo())) {
            contrato.setPorcentajeDia(Numero.toRedondearSat(100/ partes.get(idDesarrollo)));
            contrato.setPorcentajeObra(Numero.toRedondearSat(100/ partes.get(idDesarrollo)));
            if(Objects.equals(count, partes.get(idDesarrollo))) {
              contrato.setPorDia(Numero.toRedondearSat(porDiaCosto- porDia));
              contrato.setPorcentajeDia(Numero.toRedondearSat(100- porcentajeDia));
              contrato.setPorObra(Numero.toRedondearSat(porObraCosto- porObra));
              contrato.setPorcentajeObra(Numero.toRedondearSat(100- porcentajeObra));
            } // if
            else {
              contrato.setPorDia(Numero.toRedondearSat(contrato.getPorcentajeDia()/ 100* porDiaCosto));
              porDia+= contrato.getPorDia();
              porcentajeDia+= contrato.getPorcentajeDia();
              contrato.setPorObra(Numero.toRedondearSat(contrato.getPorcentajeObra()/ 100* porObraCosto));
              porObra+= contrato.getPorObra();
              porcentajeObra+= contrato.getPorcentajeObra();
            } // else
            contrato.setTotal(Numero.toRedondearSat(contrato.getPorDia()+ contrato.getPorObra()));
            count++;
          } // if  
        } // for
      } // for
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(partes);
    } // finally
  } 
  
  public static void main(String ... args) throws JobExecutionException, Exception {
    Eliminar cierre= new Eliminar();
    cierre.execute(null);
  }
  
}

