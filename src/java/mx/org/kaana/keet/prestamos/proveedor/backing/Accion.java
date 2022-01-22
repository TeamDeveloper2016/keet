package mx.org.kaana.keet.prestamos.proveedor.backing;

import java.io.Serializable;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
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
import mx.org.kaana.keet.prestamos.proveedor.beans.RegistroAnticipo;
import mx.org.kaana.keet.prestamos.proveedor.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetPrestamosProveedorAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 327393488565639367L;
	private RegistroAnticipo prestamo;

	public RegistroAnticipo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(RegistroAnticipo prestamo) {
		this.prestamo = prestamo;
	}	
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idAnticipo", JsfBase.getFlashAttribute("idAnticipo"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("disponible", 0);
      this.attrs.put("antiguedad", 0);
      this.attrs.put("limite", 0);
      this.attrs.put("calculo", 500D);
      this.attrs.put("fecha", Fecha.formatear(Fecha.FECHA_CORTA, LocalDate.now()));      
      this.attrs.put("error", Boolean.FALSE);
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.loadCatalogos();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		List<Columna>campos= null;
		try {
			campos= new ArrayList<>();
			campos.add(new Columna("deudor", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
			this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally{
			Methods.clean(campos);
		} // finally
	} // loadCatalogos
	
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.prestamo= new RegistroAnticipo();
          this.prestamo.getPrestamo().setIdAfectaNomina(1L);
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

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
      if(!(Boolean)this.attrs.get("error")) {
        eaccion= (EAccion) this.attrs.get("accion");      
        transaccion = new Transaccion(this.prestamo);
        if (transaccion.ejecutar(eaccion)) {
          JsfBase.setFlashAttribute("idAnticipoProcess", this.prestamo.getPrestamo().getIdAnticipo());
          regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
          JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el anticipo de forma correcta."), ETipoMensaje.INFORMACION);
        } // if
        else 
          JsfBase.addMessage("Ocurrió un error al registrar el proyecto", ETipoMensaje.ERROR);      			
      } // if
      else 
        JsfBase.addMessage("Los importes de los pagos semanales no coincide con el anticipo", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion
	
  public void doLoadDisponible() {  
		Entity entity = null;
    try {			
			this.attrs.put("idMoroso", this.prestamo.getPrestamo().getIkDeudor().getKey());
			entity= (Entity)DaoFactory.getInstance().toEntity("VistaMorososDto", "byIdDeudor", this.attrs);
			this.attrs.put("disponible", Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.getDouble(entity.toString("disponible"))));	
			this.attrs.put("limite", Numero.formatear(Numero.MILES_CON_DECIMALES, Numero.getDouble(entity.toString("limite"))));	
			this.attrs.put("fecha", Fecha.formatear(Fecha.FECHA_CORTA, entity.toDate("ingreso")));		
			this.attrs.put("antiguedad", DAYS.between(entity.toDate("ingreso"), LocalDate.now()));	
			this.attrs.put("dias", Fecha.toFormatSecondsToHour(DAYS.between(entity.toDate("ingreso"), LocalDate.now())* 86400));	
      this.getPrestamo().getPrestamo().setIkDeudor(new UISelectEntity(entity));
			UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:importe', {validaciones: 'requerido|flotante|mayor({\"cuanto\":0})|menor-igual({\"cuanto\": "+ entity.toString("disponible") + "})', mascara: 'libre'});");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadSaldo

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
    for (Double pago: this.getPrestamo().getPagos()) 
      suma+= pago;      
    this.attrs.put("diferencia", (this.getPrestamo().getPrestamo().getImporte()- suma));  
    this.attrs.put("error", suma< this.getPrestamo().getPrestamo().getImporte() || suma> this.getPrestamo().getPrestamo().getImporte());  
  }
  
}