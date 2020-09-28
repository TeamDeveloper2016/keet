package mx.org.kaana.keet.controles.reglas;

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
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;

public class Controles extends Maestro implements IArbol, Serializable {

  private static final long serialVersionUID = 277149125233392106L;
  private String estatus;

	public Controles() {
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
     this.estatus= "";
	}

  public String getEstatus() {
    return estatus;
  }

  public void setEstatus(String estatus) {
    this.estatus = estatus;
  }

	@Override
  public List<TcKeetControlesDto> toFather(String value) throws Exception {
    List<TcKeetControlesDto> regresar= new ArrayList<>();
    String[] list                     = uniqueKey(toCodeAll(value), value);
    Map<String, Object> params        = null;
		try {
			params=new HashMap<>();
			for(String clave: list) {
				params.put("clave", clave);
				TcKeetControlesDto dto=(TcKeetControlesDto) DaoFactory.getInstance().findIdentically(TcKeetControlesDto.class, params);
				if (dto!=null&&dto.getKey()!=-1L) {
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

 public List<TcKeetControlesDto> toChildren(int aumentarNivel, String value, int level, int child) throws Exception {
	 List<TcKeetControlesDto> regresar= null;
    Map<String, Object> params       = new HashMap<>();
		try {
      value   = toOnlyKey(value, level+ child);
      params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel="+(level+ child+ aumentarNivel))).concat(!Cadena.isVacio(this.estatus)? this.estatus: ""));
      regresar= (List) DaoFactory.getInstance().findViewCriteria(TcKeetControlesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }
	
	private List<TcKeetControlesDto> toChildren(String value, int level, int child) throws Exception {
    return toChildren(0, value, level, child);
  }

  @Override
  public List<TcKeetControlesDto> toChildren(String value, int level) throws Exception {
    return toChildren(value, level, 0);
  }

  @Override
  public boolean isChild(String value, int level) throws Exception {
    List<TcKeetControlesDto> list= toChildren(value, level, 1);
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
			params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel="+(level+ child))).concat(!Cadena.isVacio(this.estatus)? this.estatus: ""));
			regresar= DaoFactory.getInstance().findPage(TcKeetControlesDto.class, params, first, records);
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
			List<TcKeetControlesDto> list= toChildren(value, level, 0);
			if (list!=null&&!list.isEmpty()) {
				TcKeetControlesDto dto=list.get(list.size()-1);
				String key=toValueKey(dto.getClave(), dto.getNivel().intValue());
				regresar=Numero.getInteger(key);
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

  private List<TcKeetControlesDto> toAllChildren(String value, int level, int child) throws Exception {
		List<TcKeetControlesDto> regresar= null;
    Map<String, Object> params        = null;
		try {
			params= new HashMap<>();
			value = toOnlyKey(value, level+ child);
			params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel>="+(level+ child))).concat(!Cadena.isVacio(this.estatus)? this.estatus: ""));
			regresar=(List) DaoFactory.getInstance().findViewCriteria(TcKeetControlesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;
  }

  public List<TcKeetControlesDto> toAllChildren(String value, int level) throws Exception {
    return toAllChildren(value, level, 0);
  }

  public TcKeetControlesDto getFather(String value) throws Exception {
    TcKeetControlesDto regresar= null;
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
					regresar=(TcKeetControlesDto) DaoFactory.getInstance().findIdentically(TcKeetControlesDto.class, params);
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
	
	public String toCodeByIdContrato(Long idContrato) throws Exception {
		String regresar= null;
		Value contrato = null;
	  Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idContrato", idContrato);
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
