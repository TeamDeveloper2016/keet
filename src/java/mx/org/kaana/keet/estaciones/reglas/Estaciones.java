package mx.org.kaana.keet.estaciones.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.cfg.Configuracion;
import mx.org.kaana.libs.cfg.Detalle;
import mx.org.kaana.libs.cfg.IArbol;
import mx.org.kaana.libs.cfg.Maestro;
import mx.org.kaana.kajool.db.comun.page.PageRecords;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Estaciones extends Maestro implements IArbol, Serializable {

  private static final long serialVersionUID = -4944641733164493161L;

  private Session sesion;
  
	public Estaciones() {
    this(null);  
  }
  
	public Estaciones(Session sesion) {
		 super(
      new Configuracion("Configuración de estaciones", 10),
      new ArrayList<Detalle>(
      Arrays.asList(
      new Detalle(1, 3, 2, "0", "[1..999]", "", "Empresa"),  // 1er nivel idEmpresa a 3 digitos
      new Detalle(2, 4, 2, "0", "[1..9999]", "", "Ejercicio"), // 2do nivel ejercicio a 4 digitos
      new Detalle(3, 3, 2, "0", "[1..999]", "", "Contrato"),  // 3er nviel campo de orden de la tabla de contratos a 3 digitos para el caso de las plantillas se va a tener el 999
      new Detalle(4, 3, 2, "0", "[1..999]", "", "Prototipo"),  // 4to nivel campo de orden de la tabla de contratos_lotes a 3 digitos para el caso de las plantillas se va a tener el id_prototipo a 3 dígitos
      new Detalle(5, 3, 2, "0", "[1..999]", "", "Estación"),  // 5to nivel estación a  3 dígitos
      new Detalle(6, 3, 2, "0", "[1..999]", "", "Concepto"),  // 6to nivel concepto a 3 digitos
      new Detalle(7, 3, 2, "0", "[1..999]", "", "Material"),  // 7to nivel material a 3 digitos
      new Detalle(8, 3, 2, "0", "[1..999]", "", ""))));
     this.sesion= sesion;
	}

	@Override
  public List<TcKeetEstacionesDto> toFather(String value) throws Exception {
    List<TcKeetEstacionesDto> regresar= new ArrayList<>();
    String[] list                     = this.uniqueKey(toCodeAll(value), value);
    Map<String, Object> params        = null;
		try {
			params=new HashMap<>();
			for (String clave : list) {
				params.put("clave", clave);
        TcKeetEstacionesDto dto= null;
        if(this.sesion!= null)
				  dto=(TcKeetEstacionesDto) DaoFactory.getInstance().findIdentically(this.sesion, TcKeetEstacionesDto.class, params);
        else
				  dto=(TcKeetEstacionesDto) DaoFactory.getInstance().findIdentically(TcKeetEstacionesDto.class, params);
				if (dto!=null && dto.getKey()!=-1L) {
					regresar.add(dto);
				} // if
			} // for
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }

 public List<TcKeetEstacionesDto> toChildren(int aumentarNivel, String value, int level, int child) throws Exception {
	 List<TcKeetEstacionesDto> regresar= null;
    Map<String, Object> params       = new HashMap<>();
		try {
      value   = toOnlyKey(value, level+child);
      params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel="+(level+child+aumentarNivel))).concat(" "));
      if(this.sesion!= null)
        regresar= (List) DaoFactory.getInstance().findViewCriteria(this.sesion, TcKeetEstacionesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
      else
        regresar= (List) DaoFactory.getInstance().findViewCriteria(TcKeetEstacionesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }
	
	public List<TcKeetEstacionesDto> toChildren(String value, int level, int child) throws Exception {
    return toChildren(0, value, level, child);
  }

  @Override
  public List<TcKeetEstacionesDto> toChildren(String value, int level) throws Exception {
    return toChildren(value, level, 0);
  }

  @Override
  public boolean isChild(String value, int level) throws Exception {
    List<TcKeetEstacionesDto> list= toChildren(value, level, 1);
    boolean regresar              = list.isEmpty();
    list.clear();
    return regresar;
  }

  private PageRecords toChildren(String value, int level, int child, int first, int records) throws Exception {
		PageRecords regresar      = null;
    Map<String, Object> params= null;
		try {
			params  = new HashMap<>();
			value   = this.toOnlyKey(value, level+child);
			params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel="+(level+child))).concat(" "));
      if(this.sesion!= null)
			  regresar= DaoFactory.getInstance().findPage(this.sesion, TcKeetEstacionesDto.class, params, first, records);
      else
			  regresar= DaoFactory.getInstance().findPage(TcKeetEstacionesDto.class, params, first, records);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }

  public PageRecords toChildren(String value, int level, int first, int records) throws Exception {
    return toChildren(value, level, 0, first, records);
  }

  public int toCountChildren(String value, int level) throws Exception {
    int regresar= 0;
		try {
			List<TcKeetEstacionesDto> list= this.toChildren(value, level, 0);
			if (list!=null && !list.isEmpty()) {
				TcKeetEstacionesDto dto= list.get(list.size()-1);
				String key= this.toValueKey(dto.getClave(), dto.getNivel().intValue());
				regresar= Numero.getInteger(key);
				// falta validar si aun se permite un hijo mas en este nivel
				// verificar si el total de hijos es menor a la longitud del nivel
				// y realizar una reclasificacion de la llave
			} // if		
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }

  public String toNextKey(String value, int level, int increment) throws Exception {
    int child= this.toCountChildren(value, level);
    String tK= this.toOnlyKey(value, level);
    return this.toCode(tK+(child+increment));
  }

  public String toNextKey(String value, int level) throws Exception {
    return toNextKey(value, level, 1);
  }

  public String toForceNextKey(String value, int level) throws Exception {
    StringBuilder regresar = new StringBuilder();
    String parteIzq        = null;
    String parteDer        = null;
    String parteCen        = null;
    int incremento         = 0;
		try {
			parteIzq = value.substring(0, getLongitud(level-1));
			parteCen = value.substring(getLongitud(level-1), getLongitud(level));
			parteDer = value.substring(getLongitud(level));
			incremento = Integer.parseInt(parteCen);
			incremento++;
			regresar.append(parteIzq);
			if(incremento<10)
				regresar.append("0");
			regresar.append(String.valueOf(incremento));
			regresar.append(parteDer);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar.toString();
  }

  public List<TcKeetEstacionesDto> toAllChildren(String value, int level, int child) throws Exception {
		List<TcKeetEstacionesDto> regresar= null;
    Map<String, Object> params        = null;
		try {
			params= new HashMap<>();
			value = toOnlyKey(value, level+child);
			params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel>="+(level+ child))));
      if(this.sesion!= null)
			  regresar=(List) DaoFactory.getInstance().findViewCriteria(this.sesion, TcKeetEstacionesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
      else
			  regresar=(List) DaoFactory.getInstance().findViewCriteria(TcKeetEstacionesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }

  public List<TcKeetEstacionesDto> toAllChildren(String value, int level) throws Exception {
    return toAllChildren(value, level, 0);
  }

  public TcKeetEstacionesDto getFather(String value) throws Exception {
    TcKeetEstacionesDto regresar= null;
    Map<String, Object> params  = new HashMap<>();
    String[] list               = uniqueKey(toCodeAll(value), value);
		try {
			if (list!=null&&list.length>0) {
				if (list.length==1) {
					//la clave es Padre y es única por lo que solo debe de eliminarse
					regresar=null;
				} // if
				else {
					params.put("clave", list[list.length-2]);
          if(this.sesion!= null)
		  			regresar=(TcKeetEstacionesDto) DaoFactory.getInstance().findIdentically(this.sesion, TcKeetEstacionesDto.class, params);
          else
			  		regresar=(TcKeetEstacionesDto) DaoFactory.getInstance().findIdentically(TcKeetEstacionesDto.class, params);
				} // else
			} // if	
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
	
	public Integer getLongitud(int level) throws Exception {
    Integer regresar = 0;
		try { 
			for(int i=0; i< level; i++)
				regresar= regresar+ this.getNiveles().get(i).getLongitud();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
    return regresar;
  }
	
	public String toCodeByIdContrato(Long idContrato) throws Exception{
		String regresar= null;
		Value contrato = null;
	  Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idContrato", idContrato);
      if(this.sesion!= null)
  			contrato= DaoFactory.getInstance().toField(this.sesion, "VistaContratosDto", "getEstacionPatron", params, "patron");
      else
  			contrato= DaoFactory.getInstance().toField("VistaContratosDto", "getEstacionPatron", params, "patron");
			regresar= contrato.getData()!= null? contrato.getData$(): "";
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toCodeByIdContrato
	
}
