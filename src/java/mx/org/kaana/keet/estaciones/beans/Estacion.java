package mx.org.kaana.keet.estaciones.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class Estacion extends TcKeetEstacionesDto{
	
	private UISelectEntity ikEmpaqueUnidadMedida;
	private boolean pantilla;
	private static int TAMANIO_NIVEL= 3; 

	public Estacion() {
		this(new UISelectEntity(-1L), false);
	}

	public Estacion(UISelectEntity ikEmpaqueUnidadMedida, boolean pantilla) {
		super();
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		this.pantilla = pantilla;
		setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
		this.setIdEstacionEstatus(1L);
	}
		
	public UISelectEntity getIkEmpaqueUnidadMedida() {
		return ikEmpaqueUnidadMedida;
	}

	public void setIkEmpaqueUnidadMedida(UISelectEntity ikEmpaqueUnidadMedida) {
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		if(this.ikEmpaqueUnidadMedida!= null)
			setIdEmpaqueUnidadMedida(this.ikEmpaqueUnidadMedida.getKey());
	}

	public boolean isPantilla() {
		return getIdPlantilla()!= null && getIdPlantilla().equals(EBooleanos.SI.getIdBooleano());
	}

	public void setPantilla(boolean pantilla) {
		this.pantilla = pantilla;
		setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
	}
	
	public String calcularClave(int direcccion) throws Exception{
		return calcularClave(this.getNivel(), direcccion);
	} //calcularClave
	
	public String calcularClave(Long nivel, int direcccion) throws Exception{
		return calcularClave(this.getClave(), nivel, direcccion);
	} //calcularClave
	
	public String calcularClave(String clave, Long nivel, int direcccion) throws Exception{
		String regresar= claveSinCeros(clave, nivel);
		int longitud= regresar.length();
		regresar= Cadena.rellenar(String.valueOf(Long.valueOf(regresar)+ direcccion), longitud, '0', true);
		regresar= regresar.concat(clave.substring(longitud, clave.length()));
		return regresar;
	} //calcularClave
	
	public String claveSinCeros() throws Exception{
		return claveSinCeros(this.getClave(), this.getNivel());
	} //calcularClave
	
	public String claveSinCeros(Long nivel) throws Exception{
		return claveSinCeros(this.getClave(), nivel);
	} //calcularClave
	
	public String claveSinCeros(String clave,Long nivel) throws Exception{
		return clave.substring(0, ((nivel.intValue())* TAMANIO_NIVEL));
	} //calcularClave
	
	public List<TcKeetEstacionesDto> getHijos() throws Exception{
		return getHijos(this.getClave(), this.getNivel());
	}

	public List<TcKeetEstacionesDto> getHijos(String clave, Long nivel) throws Exception{
		List<TcKeetEstacionesDto> regresar= null;
		Map<String, Object> params        = null;
		try {
			params=new HashMap<>();
      params.put("clave", claveSinCeros(clave, nivel));
      params.put("nivel", nivel);
      regresar=(List<TcKeetEstacionesDto>) DaoFactory.getInstance().toEntitySet(TcKeetEstacionesDto.class,"TcKeetEstacionesDto", "getHijos", params);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}


	
	
	
	
	
	
		
		

	
}
