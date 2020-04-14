package mx.org.kaana.keet.catalogos.diasfestivos.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetDiasFestivosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private TcKeetDiasFestivosDto dto;

	public Transaccion(TcKeetDiasFestivosDto dto) {
		this.dto = dto;
	}	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                    = true;
		Map<String, Object>params           = null;		
		List<TcKeetDiasFestivosDto>oficiales= null;
		TcKeetDiasFestivosDto anioFestivo   = null;
		try {
			switch(accion){
				case AGREGAR:										
					regresar= DaoFactory.getInstance().insert(sesion, this.dto)>= 1L;
					break;								
				case MODIFICAR:														
					regresar= DaoFactory.getInstance().update(sesion, this.dto)>= 1L;
					break;			
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dto)>= 1L;
					break;					
				case PROCESAR:
					params= new HashMap<>();
					params.put(Constantes.SQL_CONDICION, "ejercicio=".concat(String.valueOf(Fecha.getAnioActual())).concat(" and id_oficial=1 and id_empresa=").concat(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString()));
					oficiales= DaoFactory.getInstance().toEntitySet(sesion, TcKeetDiasFestivosDto.class, "TcKeetDiasFestivosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
					if(!oficiales.isEmpty()){
						for(TcKeetDiasFestivosDto festivo: oficiales){
							anioFestivo= new TcKeetDiasFestivosDto();
							anioFestivo.setDescripcion(festivo.getDescripcion());
							anioFestivo.setDia(festivo.getDia().plusYears(1L));
							anioFestivo.setEjercicio(festivo.getEjercicio()+1);
							anioFestivo.setFactor(festivo.getFactor());
							anioFestivo.setIdEmpresa(festivo.getIdEmpresa());
							anioFestivo.setIdOficial(1L);
							anioFestivo.setIdUsuario(JsfBase.getIdUsuario());
							if(DaoFactory.getInstance().findIdentically(sesion, TcKeetDiasFestivosDto.class, anioFestivo.toMap())== null)
								DaoFactory.getInstance().insert(sesion, anioFestivo);
						} // if
					} // if
					break;					
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
}