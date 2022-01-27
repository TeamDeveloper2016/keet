package mx.org.kaana.keet.prestamos.proveedor.backing;

import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposLotesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.prestamos.proveedor.beans.RegistroAnticipo;
import mx.org.kaana.keet.prestamos.proveedor.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.cuentas.beans.Cuenta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value = "keetPrestamosProveedorAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final Log LOG = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
  
	private RegistroAnticipo prestamo;
  private FormatLazyModel conceptos;
  private List<Entity> seleccionados;
  
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idAnticipo", JsfBase.getFlashAttribute("idAnticipo"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("disponible", 0);
      this.attrs.put("antiguedad", 0);
      this.attrs.put("limite", 0);
      this.attrs.put("calculo", 500D);
      this.attrs.put("fecha", Fecha.formatear(Fecha.FECHA_CORTA, LocalDate.now()));      
      this.attrs.put("error", Boolean.FALSE);
      this.attrs.put("idProveedor", -1L);
      this.attrs.put("idDepartamento", "-1");
      this.seleccionados= new ArrayList<>();
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			this.doLoad();
      this.loadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	public RegistroAnticipo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(RegistroAnticipo prestamo) {
		this.prestamo = prestamo;
	}	

  public FormatLazyModel getConceptos() {
    return conceptos;
  }

  public List<Entity> getSeleccionados() {
    return seleccionados;
  }

  public void setSeleccionados(List<Entity> seleccionados) {
    this.seleccionados = seleccionados;
  }
  
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.prestamo= new RegistroAnticipo();
          this.prestamo.getPrestamo().setIdAfectaNomina(2L);
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.prestamo= new RegistroAnticipo((Long)this.attrs.get("idAnticipo"));
          this.doLoadDisponible();
          break;
      } // switch
      this.doCalculo();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
  
	private void loadCatalogos() {
		List<Columna>columns      = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put("idEmpresa", this.prestamo.getIkEmpresa().getKey());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR))
  				this.prestamo.setIkEmpresa(empresas.get(0));
			  else 
				  this.prestamo.setIkEmpresa(empresas.get(empresas.indexOf(this.prestamo.getIkEmpresa())));
			} // if	
      this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally{
			Methods.clean(columns);
		} // finally
	} // loadCatalogos
	
	public void doLoadDesarrollos() {
		List<Columna> columns           = null;
    Map<String, Object> params      = null;		
		List<UISelectEntity> desarrollos= null;
    try {
			params= new HashMap<>();					
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + this.prestamo.getIkEmpresa().getKey());
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
  		this.attrs.put("desarrollos", desarrollos);			
			if(!desarrollos.isEmpty()) {
				if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR)) 
          this.prestamo.setIkDesarrollo(desarrollos.get(0));
        else
				  this.prestamo.setIkDesarrollo(desarrollos.get(desarrollos.indexOf(this.prestamo.getIkDesarrollo())));
			} // if
      else {
				this.attrs.put("desarrollos", new ArrayList<>());
				this.prestamo.setIkDesarrollo(new UISelectEntity(-1L));
			} // else
			this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
  
	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.prestamo.getIkDesarrollo());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR))
          this.prestamo.setIkContrato(contratos.get(0));
        else  
          this.prestamo.setIkContrato(contratos.get(contratos.indexOf(this.prestamo.getIkContrato())));
		} // try // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
  
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
      if((Boolean)this.attrs.get("error")) 
        JsfBase.addMessage("Los importes de los pagos semanales no coincide con el anticipo", ETipoMensaje.ERROR);
      else 
        if((Boolean)this.attrs.get("ceros")) 
          JsfBase.addMessage("El importe de un pago semanal es cero, favor de corregir !", ETipoMensaje.ERROR);
        else {
          eaccion= (EAccion) this.attrs.get("accion");      
          this.toLoadLotes();
          transaccion= new Transaccion(this.prestamo);
          if (transaccion.ejecutar(eaccion)) {
            JsfBase.setFlashAttribute("idAnticipoProcess", this.prestamo.getPrestamo().getIdAnticipo());
            regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
            JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el anticipo de forma correcta."), ETipoMensaje.INFORMACION);
          } // if
          else 
            JsfBase.addMessage("Ocurrió un error al registrar el proyecto", ETipoMensaje.ERROR);      			
        } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
	
  public void doLoadDisponible() {  
		Entity entity             = null;
    List<Columna> columns     = null;				
    Map<String, Object> params= new HashMap<>();
    try {			
			params.put("idMoroso", this.prestamo.getPrestamo().getIkDeudor().getKey());
			entity= (Entity)DaoFactory.getInstance().toEntity("VistaMorososDto", "byIdDeudor", params);
			this.attrs.put("disponible", Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.getDouble(entity.toString("disponible"))));	
			this.attrs.put("limite", Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.getDouble(entity.toString("limite"))));	
			this.attrs.put("fecha", Fecha.formatear(Fecha.FECHA_CORTA, entity.toDate("ingreso")));		
			this.attrs.put("antiguedad", DAYS.between(entity.toDate("ingreso"), LocalDate.now()));	
			this.attrs.put("dias", Fecha.toFormatSecondsToHour(DAYS.between(entity.toDate("ingreso"), LocalDate.now())* 86400));	
      this.attrs.put("idProveedor", entity.toLong("idProveedor"));
      this.attrs.put("idDepartamento", entity.toString("idDepartamento"));
      this.getPrestamo().getPrestamo().setIkDeudor(new UISelectEntity(entity));
			UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-igual({\"cuanto\": "+ entity.toString("disponible") + "})', mascara: 'libre'});");
      this.doLoadConceptos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
  } // doLoadDisponible
  
  public void doLoadConceptos() {  
    List<Columna> columns     = null;				
    Map<String, Object> params= new HashMap<>();
    UISelectEntity contrato   = new UISelectEntity(-1L);
    try {			
      this.seleccionados= new ArrayList<>();
      // if(Objects.equals((EAccion)this.attrs.get("accion"), EAccion.AGREGAR)) {
        params.put("idDepartamento", this.attrs.get("idDepartamento"));
        if((Long)this.attrs.get("idProveedor")!= -1L && this.prestamo.getPrestamo().getIdContrato()!= -1L) {
          List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
          int index= contratos.indexOf(this.prestamo.getIkContrato());
          if(index>= 0)
            contrato= contratos.get(index);
          StringBuilder sb= new StringBuilder(Cadena.rellenar(contrato.get("idEmpresa").toString(), 3, '0', true)).
  			  append(contrato.toString("ejercicio")).
          append(Cadena.rellenar(contrato.toString("orden"), 3, '0', true));
          params.put("clave", sb.toString());
          params.put("estatus", EEstacionesEstatus.INICIAR.getKey() + "," + EEstacionesEstatus.EN_PROCESO.getKey() + "," + EEstacionesEstatus.TERMINADO.getKey());			
          columns= new ArrayList<>();      
          columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
          columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
          columns.add(new Columna("anticipo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
          this.conceptos= new FormatLazyModel("VistaCapturaDestajosDto", "anticipos", params, columns);                
          List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", "anticipos", params, Constantes.SQL_TODOS_REGISTROS);
          if(items!= null && !items.isEmpty()) {
            double anticipo= 0D;
            for (Entity item: items) 
              anticipo+= item.toDouble("total");
            this.prestamo.getPrestamo().setImporte(anticipo);
            this.attrs.put("anticipo", Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(anticipo)));
            this.doCalculo();
          } // if  
        } // if  
      // } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
  } // doLoadDisponible

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idAnticipoProcess", this.prestamo.getPrestamo().getIdAnticipo());
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
	public List<UISelectEntity> doCompleteDeudor(String deudor) {
 		List<Columna> campos      = null;
		UISelectEntity empresa    = null;
    Map<String, Object> params= new HashMap<>();
    try {
			campos= new ArrayList<>();
			campos.add(new Columna("deudor", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
			empresa = this.attrs.get("idEmpresa")==null? null:(UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  params.put("sucursales", empresa.getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("deudor", deudor.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*"));
      this.attrs.put("deudores", UIEntity.build("VistaMorososDto", "complete", params));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(campos);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("deudores");
	}	// doCompleteCliente

  public void doCalculo() {
    if(this.getPrestamo().getPrestamo().getSemanas()<= 0)
      this.getPrestamo().getPrestamo().setSemanas(1L);
    if(this.getPrestamo().getPrestamo().getImporte()<= 0)
      this.getPrestamo().getPrestamo().setImporte(1D);
    double calculo= Numero.toRedondear(this.getPrestamo().getPrestamo().getImporte()/ this.getPrestamo().getPrestamo().getSemanas());
    this.attrs.put("calculo", calculo); 
    if(Objects.equals((EAccion) this.attrs.get("accion"), EAccion.AGREGAR)) {
      this.getPrestamo().getPagos().clear();
      for (int x= 0; x< this.getPrestamo().getPrestamo().getSemanas().intValue(); x++) {
        this.getPrestamo().getPagos().add(calculo);
      } // for
      this.checkCalculos();
    } // if  
  }
 
  public void checkCalculos() {
    double suma= 0D;
    this.attrs.put("ceros", Boolean.FALSE);  
    for (Double pago: this.getPrestamo().getPagos()) {
      suma+= pago;      
      if(pago<= 0D)
        this.attrs.put("ceros", Boolean.TRUE);  
    } // if  
    this.attrs.put("diferencia", (this.getPrestamo().getPrestamo().getImporte()- suma));  
    this.attrs.put("error", suma< this.getPrestamo().getPrestamo().getImporte() || suma> this.getPrestamo().getPrestamo().getImporte());  
  }

  public void doTabChange(TabChangeEvent event) {
    Double anticipo= 0D;
		if(event.getTab().getTitle().equals("General")) {
      if(!this.seleccionados.isEmpty()) { 
        for(Entity item: this.seleccionados)					
          anticipo+= item.toDouble("total");
      } // if
      this.prestamo.getPrestamo().setImporte(anticipo);
      this.attrs.put("anticipo", Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(anticipo)));
      this.doCalculo();
    } // if
  } 
  
  public void doRowSeleccionado() {
    LOG.info(this.conceptos.getRowCount());
  }

  private void toLoadLotes() {
    if(!this.seleccionados.isEmpty()) { 
      this.prestamo.getLotes().clear();
      for(Entity item: this.seleccionados) {
        this.prestamo.getLotes().add(new TcKeetAnticiposLotesDto(
          item.toDouble("total"), // Double anticipo, 
          this.prestamo.getIdAnticipo(), // Long idAnticipo, 
          item.toString("codigo"), // String codigo, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          item.toLong("idPagado"), // Long idPagado, 
          item.toLong("idContratoLote"), // Long idContratoLote, 
          item.toLong("idEstacion"), // Long idEstacion, 
          item.toLong("idAnticipoLote") // Long idAnticipoLote                
        ));
      }  
    } // if
  }
  
	public String toColor(Entity row) {
		return Objects.equals(row.toLong("idAnticipo"), this.prestamo.getPrestamo().getIdAnticipo())? "": "janal-display-none";
	} // toColor
  
}