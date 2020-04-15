package mx.org.kaana.keet.estaciones.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.cfg.Configuracion;
import mx.org.kaana.libs.cfg.Detalle;
import mx.org.kaana.libs.cfg.IArbol;
import mx.org.kaana.libs.cfg.Maestro;
import mx.org.kaana.kajool.db.comun.page.PageRecords;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;

public class Estaciones extends Maestro implements IArbol, Serializable {

	public Estaciones() {
		 super(
      new Configuracion("Configuración de estaciones", 10),
      new ArrayList<Detalle>(
      Arrays.asList(
      new Detalle(1, 3, 2, "0", "[1..999]", ""),
      new Detalle(2, 4, 2, "0", "[1..9999]", ""),
      new Detalle(3, 3, 2, "0", "[1..999]", ""),
      new Detalle(4, 3, 2, "0", "[1..999]", ""),
      new Detalle(5, 3, 2, "0", "[1..999]", ""),
      new Detalle(6, 3, 2, "0", "[1..999]", ""),
      new Detalle(7, 3, 2, "0", "[1..999]", ""),
      new Detalle(8, 3, 2, "0", "[1..999]", ""))));
	}

	 @Override
  public List<TcKeetEstacionesDto> toFather(String value) throws Exception {
    List<TcKeetEstacionesDto> regresar=new ArrayList<TcKeetEstacionesDto>();
    Map<String, Object> params    =new HashMap<String, Object>();
    String[] list=uniqueKey(toCodeAll(value), value);
    for (String clave : list) {
      params.put("clave", clave);
      TcKeetEstacionesDto dto=(TcKeetEstacionesDto) DaoFactory.getInstance().findIdentically(TcKeetEstacionesDto.class, params);
      if (dto!=null&&dto.getKey()!=-1L) {
        regresar.add(dto);
      } // if
    } // for
    return regresar;
  }

 public List<TcKeetEstacionesDto> toChildren(int aumentarNivel, String value, int level, int child) throws Exception {
    Map<String, Object> params=new HashMap<String, Object>();
    value=toOnlyKey(value, level+child);
    params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel="+(level+child+aumentarNivel))).concat(" "));
    List<TcKeetEstacionesDto> regresar=(List) DaoFactory.getInstance().findViewCriteria(TcKeetEstacionesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
    params.clear();
    return regresar;
  }
	
	private List<TcKeetEstacionesDto> toChildren(String value, int level, int child) throws Exception {
    return toChildren(0, value, level, child);
  }

  @Override
  public List<TcKeetEstacionesDto> toChildren(String value, int level) throws Exception {
    return toChildren(value, level, 0);
  }

  @Override
  public boolean isChild(String value, int level) throws Exception {
    List<TcKeetEstacionesDto> list=toChildren(value, level, 1);
    boolean regresar          =list.isEmpty();
    list.clear();
    return regresar;
  }

  private PageRecords toChildren(String value, int level, int child, int first, int records) throws Exception {
    Map<String, Object> params=new HashMap<String, Object>();
    value=toOnlyKey(value, level+child);
    params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel="+(level+child))).concat(" "));
    PageRecords regresar=DaoFactory.getInstance().findPage(TcKeetEstacionesDto.class, params, first, records);
    params.clear();
    return regresar;
  }

  public PageRecords toChildren(String value, int level, int first, int records) throws Exception {
    return toChildren(value, level, 0, first, records);
  }

  public int toCountChildren(String value, int level) throws Exception {
    int regresar              =0;
    List<TcKeetEstacionesDto> list=toChildren(value, level, 0);
    if (list!=null&&!list.isEmpty()) {
      TcKeetEstacionesDto dto=list.get(list.size()-1);
      String key=toValueKey(dto.getClave(), dto.getNivel().intValue());
      regresar=Numero.getInteger(key);
      // falta validar si aun se permite un hijo mas en este nivel
      // verificar si el total de hijos es menor a la longitud del nivel
      // y realizar una reclasificacion de la llave
    } // if		
    return regresar;
  }

  public String toNextKey(String value, int level, int increment) throws Exception {
    int child=toCountChildren(value, level);
    String tK=toOnlyKey(value, level);
    return toCode(tK+(child+increment));
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
    return regresar.toString();
  }

  private List<TcKeetEstacionesDto> toAllChildren(String value, int level, int child) throws Exception {
    Map<String, Object> params=new HashMap<String, Object>();
    value=toOnlyKey(value, level+child);
    params.put(Constantes.SQL_CONDICION, "clave like '".concat(value).concat("%'".concat(" and nivel>="+(level+child))).concat(" "));
    List<TcKeetEstacionesDto> regresar=(List) DaoFactory.getInstance().findViewCriteria(TcKeetEstacionesDto.class, params, Constantes.SQL_TODOS_REGISTROS);
    params.clear();
    return regresar;
  }

  public List<TcKeetEstacionesDto> toAllChildren(String value, int level) throws Exception {
    return toAllChildren(value, level, 0);
  }

  public TcKeetEstacionesDto getFather(String value) throws Exception {
    TcKeetEstacionesDto regresar=null;
    Map<String, Object> params=new HashMap<String, Object>();
    String[] list=uniqueKey(toCodeAll(value), value);
    if (list!=null&&list.length>0) {
      if (list.length==1) {
        //la clave es Padre y es única por lo que solo debe de eliminarse
        regresar=null;
      } // if
      else {
        params.put("clave", list[list.length-2]);
        regresar=(TcKeetEstacionesDto) DaoFactory.getInstance().findIdentically(TcKeetEstacionesDto.class, params);
      } // else
    } // if	
    return regresar;
  }
	
	public Integer getLongitud(int level) throws Exception {
    Integer regresar = 0;
		for(int i=0; i< level; i++)
			regresar= regresar+ this.getNiveles().get(i).getLongitud();
    return regresar;
  }
	
	public String toCodeByIdContrato(Long idContrato) throws Exception{
		String regresar= null;
		Value contrato = null;
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("idContrato", idContrato);
		contrato= DaoFactory.getInstance().toField("VistaContratosDto","getEstacionPatron", params, "patron");
		regresar= contrato.getData()!= null? contrato.getData$(): "";
		Methods.clean(params);
		return regresar;
	} // toCodeByIdContrato
	
}
