package mx.org.kaana.keet.estmaciones.backing;

import java.io.Serializable;
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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.estmaciones.reglas.Estimaciones;
import mx.org.kaana.keet.estmaciones.beans.Retencion;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.keet.ingresos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
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
public class Accion extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;

	protected EAccion accion;	
  protected Estimaciones estimaciones;

  public Estimaciones getEstimaciones() {
    return estimaciones;
  }

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
			// if(JsfBase.getFlashAttribute("accion")== null)
			//	UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.MODIFICAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idEstimacion", JsfBase.getFlashAttribute("idEstimacion")== null? 1L: JsfBase.getFlashAttribute("idEstimacion"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Estimaciones/filtro": JsfBase.getFlashAttribute("retorno"));
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
      // transaccion = new Transaccion(this.ingreso, this.comprobante, this.articulos, this.getXml(), this.getPdf());
      if (transaccion.ejecutar(this.accion)) {
        regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
        if(this.accion.equals(EAccion.AGREGAR)) 
          UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la estimación ', '"+ this.estimaciones.getEstimacion().getConsecutivo()+ "');");
        else
          if(!this.accion.equals(EAccion.CONSULTAR)) 
            JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la estimación"), ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al registrar la estimación !", ETipoMensaje.ERROR);      			
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
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
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
		List<Columna> columns           = null;
    Map<String, Object> params      = null;		
		List<UISelectEntity> desarrollos= null;
    try {
			params= new HashMap<>();					
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + this.estimaciones.getEstimacion().getIkEmpresa().getKey());
			columns= new ArrayList<>();
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
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.estimaciones.getEstimacion().getIkDesarrollo().getKey());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.estimaciones.getEstimacion().setIkContrato(contratos.get(0));
        else  
          this.estimaciones.getEstimacion().setIkContrato(contratos.get(contratos.indexOf(this.estimaciones.getEstimacion().getIkContrato())));
		} // try // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
  
	public void doUpdateCliente() {
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		List<Columna> columns           = null;
		Map<String, Object>params       = new HashMap<>();
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo = this.estimaciones.getEstimacion().getIkDesarrollo();
      UISelectEntity item= desarrollos.get(desarrollos.indexOf(desarrollo));
			this.attrs.put("cliente", item.toString("razonSocial"));			
      this.estimaciones.getEstimacion().setIkCliente(new UISelectEntity(item.toLong("idCliente")));
      this.estimaciones.getEstimacion().setCliente((TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, this.estimaciones.getEstimacion().getIkCliente().getKey()));
      params.put("idCliente", this.estimaciones.getEstimacion().getCliente()!= null? this.estimaciones.getEstimacion().getCliente().getIdCliente(): -1L);
			this.doLoadContratos();
		} // try // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doUpdateCliente  
  
	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "General":
        break;
    } // switch    
	}

  public void doRowUpdateImporte()  {
    for (Retencion item : this.estimaciones.getEstimacion().getRetenciones()) {
      item.setImporte(Numero.toRedondearSat(item.getPorcentaje()* this.estimaciones.getEstimacion().getImporte()/ 100D));
    } // for
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
      } // if  
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