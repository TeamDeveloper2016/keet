package mx.org.kaana.keet.estimacion.backing;

import java.io.Serializable;
import java.time.ZoneId;
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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.EAccion.AGREGAR;
import static mx.org.kaana.kajool.enums.EAccion.CONSULTAR;
import static mx.org.kaana.kajool.enums.EAccion.MODIFICAR;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.estimacion.reglas.Estimaciones;
import mx.org.kaana.keet.estimacion.beans.Retencion;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.estimacion.reglas.Transaccion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetEstimacionesAccion")
@ViewScoped
public class Accion extends IBaseImportar implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;

	protected EAccion accion;	
  protected Estimaciones estimaciones;
  private List<Importado> documentos;

  public Estimaciones getEstimaciones() {
    return estimaciones;
  }

  public List<Importado> getDocumentos() {
    return documentos;
  }

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}
  
  public Boolean getIsCafu() {
    return Objects.equals(Configuracion.getInstance().getPropiedad("sistema.empresa.principal"), "cafu");
  }  
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
			if(JsfBase.getFlashAttribute("accion")== null)
			  UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idEstimacion", JsfBase.getFlashAttribute("idEstimacion")== null? -1L: JsfBase.getFlashAttribute("idEstimacion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Estimaciones/filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_CATALOGOS);
      this.documentos= new ArrayList<>();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:		
          this.estimaciones= new Estimaciones();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.estimaciones= new Estimaciones((Long)this.attrs.get("idEstimacion"));
          break;
      } // switch
      this.toLoadCatalog();
      this.doRowUpdateImporte();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() { 
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      // FALTA VERIFICAR SI EL MONTO DE TODAS LAS AMORTIZACIONES DE LOS ANTICIPOS ES MENOR O IGUAL AL ANTICIPO PAGADO DEL CONTRATO
      if(this.checkAnticipoGlobal()) {
        transaccion = new Transaccion(this.estimaciones, this.documentos);
        if (transaccion.ejecutar(this.accion)) {
          regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
          if(this.accion.equals(EAccion.AGREGAR)) 
            UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la estimaci�n ', '"+ this.estimaciones.getEstimacion().getConsecutivo()+ "');");
          else
            if(!this.accion.equals(EAccion.CONSULTAR)) 
              JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agreg�" : "modific�").concat(" la estimaci�n"), ETipoMensaje.INFORMACION);
        } // if
        else 
          JsfBase.addMessage("Ocurri� un error al registrar la estimaci�n !", ETipoMensaje.ERROR);      			
      } // if
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    JsfBase.setFlashAttribute("idEstimacion", this.estimaciones.getEstimacion().getIdEstimacion());
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idEstimacion", this.attrs.get("idEstimacion"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
  				this.estimaciones.getEstimacion().setIkEmpresa(empresas.get(0));
			  else 
				  this.estimaciones.getEstimacion().setIkEmpresa(empresas.get(empresas.indexOf(this.estimaciones.getEstimacion().getIkEmpresa())));
			} // if	
  		params.put("sucursales", this.estimaciones.getEstimacion().getIkEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.remove(0);
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      List<UISelectEntity> clientes= UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "clave");
      this.attrs.put("clientes", clientes);
			if(!clientes.isEmpty()) {
        if(this.accion.equals(EAccion.AGREGAR))
          this.estimaciones.getEstimacion().setIkCliente(clientes.get(0));
        else 
          this.estimaciones.getEstimacion().setIkCliente(clientes.get(clientes.indexOf(this.estimaciones.getEstimacion().getIkCliente())));
      } // if  
			this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally    
	}

	public void doLoadDesarrollos() {
		List<Columna> columns           = new ArrayList<>();
    Map<String, Object> params      = new HashMap<>();		
		List<UISelectEntity> desarrollos= null;
    try {
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
  		params.put(Constantes.SQL_CONDICION, "tc_keet_proyectos.id_empresa=" + this.estimaciones.getEstimacion().getIkEmpresa().getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
			if(!desarrollos.isEmpty()) {
				this.attrs.put("desarrollos", desarrollos);			
				if(this.accion.equals(EAccion.AGREGAR)) 
          this.estimaciones.getEstimacion().setIkDesarrollo(desarrollos.get(0));
        else
				  this.estimaciones.getEstimacion().setIkDesarrollo(desarrollos.get(desarrollos.indexOf(this.estimaciones.getEstimacion().getIkDesarrollo())));
        this.estimaciones.getEstimacion().setIkCliente(new UISelectEntity(this.estimaciones.getEstimacion().getIkDesarrollo().toLong("idCliente")));
			  this.attrs.put("cliente", this.estimaciones.getEstimacion().getIkDesarrollo().toString("razonSocial"));
			} // if
      else {
				this.attrs.put("desarrollos", new ArrayList<>());
				this.estimaciones.getEstimacion().setIkDesarrollo(new UISelectEntity(-1L));
				this.estimaciones.getEstimacion().setIkCliente(new UISelectEntity(-1L));
				this.attrs.put("cliente", "");
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
		Map<String, Object>params     = new HashMap<>();
		try {
			params.put("idDesarrollo", this.estimaciones.getEstimacion().getIkDesarrollo().getKey());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR)) 
          this.estimaciones.getEstimacion().setIkContrato(contratos.get(0));
        else  
          this.estimaciones.getEstimacion().setIkContrato(contratos.get(contratos.indexOf(this.estimaciones.getEstimacion().getIkContrato())));
      this.toLoadPeriodos();
		} // try 
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
  
	private void toLoadPeriodos() {
		List<UISelectEntity> periodos= null;
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
    try {
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("idTipoNomina", 1L);
      if(this.accion.equals(EAccion.AGREGAR)) 
  			params.put("ejercicio", Fecha.getAnioActual()- 1);
      else
  			params.put("ejercicio", this.estimaciones.getEstimacion().getIkNomina().toLong("ejercicio"));
			periodos= UIEntity.build("VistaNominaDto", "nominas", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("periodos", periodos);
      if(!periodos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR)) 
          this.estimaciones.getEstimacion().setIkNomina(periodos.get(0));
        else  
          this.estimaciones.getEstimacion().setIkNomina(periodos.get(periodos.indexOf(this.estimaciones.getEstimacion().getIkNomina())));
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doLoadPeriodos
  
	public void doUpdateCliente() {
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		Map<String, Object>params       = new HashMap<>();
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo = this.estimaciones.getEstimacion().getIkDesarrollo();
      UISelectEntity item= desarrollos.get(desarrollos.indexOf(desarrollo));
			this.attrs.put("cliente", item.toString("razonSocial"));			
      this.estimaciones.getEstimacion().setIkCliente(new UISelectEntity(item.toLong("idCliente")));
			this.doLoadContratos();
		} // try // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // doUpdateCliente  
  
	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "General":
        break;
      case "Archivos":
  			this.doLoadImportados("VistaEstimacionesDto", "importados", this.estimaciones.getEstimacion().toMap());
        break;
    } // switch    
	}

  public void doRowUpdateImporte() {
    for (Retencion item : this.estimaciones.getEstimacion().getRetenciones()) 
      item.setImporte(Numero.toRedondearSat(item.getPorcentaje()* this.estimaciones.getEstimacion().getImporte()/ 100D));
    this.doUpdateAccion(null);
  }
  
  public void doRowUpdateCuenta(Retencion row, Boolean porcentaje)  {
    try { 
      if(row!= null) {
        if(this.estimaciones.getEstimacion().getImporte()== null || this.estimaciones.getEstimacion().getImporte()<= 0D)
          this.estimaciones.getEstimacion().setImporte(row.getImporte());
        if(!Objects.equals(row.getPorcentaje(), 0D) || !Objects.equals(row.getImporte(), 0D)) {
          if(porcentaje) {
            row.setPorcentaje(Numero.toRedondearSat(row.getPorcentaje()));
            row.setImporte(Numero.toRedondearSat(row.getPorcentaje()* this.estimaciones.getEstimacion().getImporte()/ 100D));
          } // if
          else {
            row.setImporte(Numero.toRedondearSat(row.getImporte()));
            row.setPorcentaje(Numero.toRedondearSat(row.getImporte()* 100D/ this.estimaciones.getEstimacion().getImporte()));
          } // else
        } // if  
        this.doUpdateAccion(row);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateAccion(Retencion row) {
    double suma= this.estimaciones.getEstimacion().getImporte();
    for (Retencion item: this.estimaciones.getEstimacion().getRetenciones()) {
      if(Objects.equals(item.getIdDeduccion(), 1L))
        suma-= item.getImporte();
      else
        suma+= 0L; // item.getImporte();
      if(row== null && Objects.equals(item.getSql(), ESql.SELECT))
        item.setSql(ESql.UPDATE);
    } // for
    this.estimaciones.getEstimacion().setFacturar(Numero.toRedondearSat(suma));
    if(row!= null && Objects.equals(row.getSql(), ESql.SELECT))
      row.setSql(ESql.UPDATE);
    this.toUpdateTotal();
  }

  public void doUpdatePorcentaje() {
    List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
    if(contratos!= null && !contratos.isEmpty()) {
      int index= contratos.indexOf(this.estimaciones.getEstimacion().getIkContrato());
      if(index>= 0) {
        this.estimaciones.getEstimacion().setIkContrato(contratos.get(index));
        this.estimaciones.toLoadRetenciones(this.estimaciones.getEstimacion().getIdContrato());
        this.doRowUpdateImporte();
      } // if 
    } // if 
  } 
  
  private Boolean checkAnticipoGlobal() {
    Boolean regresar= Boolean.TRUE;
    Double anticipo = 0D; 
    Double importe  = 0D; 
    Value total     = null;
    List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
		Map<String, Object>params     = new HashMap<>();
		try {
      if(contratos!= null && !contratos.isEmpty()) {
        int index= contratos.indexOf(this.estimaciones.getEstimacion().getIkContrato());
        if(index>= 0) {
          this.estimaciones.getEstimacion().setIkContrato(contratos.get(index));
          anticipo= this.estimaciones.getEstimacion().getIkContrato().toDouble("anticipo");
          if(anticipo> 0D) {
            params.put("idContrato", this.estimaciones.getEstimacion().getIdContrato());
            params.put("idEstimacion", this.estimaciones.getEstimacion().getIdEstimacion());
            total= DaoFactory.getInstance().toField("VistaEstimacionesDto", "anticipo", params, "total");
            if(total!= null && total.getData()!= null) {
              for (Retencion item: this.estimaciones.getEstimacion().getRetenciones()) {
                if(Objects.equals(item.getIdTipoRetencion(), 1L))
                  importe= item.getImporte();
              } // for  
              regresar= (total.toDouble()+ importe)<= anticipo;
              if(!regresar)
                JsfBase.addMessage("El importe ["+ 
                  Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, importe)+ "] de la amortizaci�n de anticipo supera el anticipo del contrato ["+ 
                  Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, anticipo)+ "], acumulado de las amortizaciones de anticipos anteriores ["+ 
                  Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, total.toDouble())+ "], pendiente por amortizar del anticipo ["+
                  Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, anticipo- (total.toDouble()+ importe))+"] !", ETipoMensaje.ERROR);      			
            } // if  
          } // if 
        } // if 
      } // if 
		} // try
		catch (Exception e) {			
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
 
  public void doRowUpdateFecha(Retencion row) {
    if(Objects.equals(row.getSql(), ESql.SELECT))
      row.setSql(ESql.UPDATE);
  }

  private void toUpdateTotal() {
    double suma= 0D;
    for (Retencion item: this.estimaciones.getEstimacion().getRetenciones()) 
      suma+= item.getImporte();
    this.attrs.put("total", Global.format(EFormatoDinamicos.MONEDA_SAT_DECIMALES, suma));
  }

	public void doFileUpload(FileUploadEvent event) {
		this.doFileUpload(event, this.estimaciones.getEstimacion().getRegistro().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("estimaciones"));
    if(!Objects.equals(this.getPdf(), null)) {
      this.getPdf().setObservaciones(!Objects.equals((String)this.attrs.get("comentarios"), null)? ((String)this.attrs.get("comentarios")).toUpperCase(): null);
      this.attrs.put("comentarios", null);
      this.documentos.add(this.getPdf());
    } // if  
	} // doFileUpload	
  
  public void doDepurar(Importado row) {
    this.documentos.remove(row);
  }
 
  public void doDeleteDocument(Entity row) {
    Transaccion transaccion= new Transaccion(row.getKey());
    try {
      if(transaccion.ejecutar(EAccion.DEPURAR)) {
  			this.doLoadImportados("VistaEstimacionesDto", "importados", this.estimaciones.getEstimacion().toMap());
        JsfBase.addMessage("Se elimin� el documento de la estimaci�n", ETipoMensaje.INFORMACION);
      } // if  
      else
        JsfBase.addMessage("Ocurrio un error al eliminar el documento", ETipoMensaje.INFORMACION);
 		} // try
		catch (Exception e) {			
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
		} // catch		
 }
  
	@Override
	protected void finalize() throws Throwable {
		try {
			this.doCancelar();
		} // try
		finally {
			super.finalize();
		} // finally	
	}
  
}