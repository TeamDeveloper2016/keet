package mx.org.kaana.keet.catalogos.rubros.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPuntosGruposDto;
import mx.org.kaana.keet.db.dto.TcKeetRubrosGruposDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;


public class RubroGrupo extends TcKeetRubrosGruposDto{
	
	private static final long serialVersionUID = -8612861466457643864L;
	private UISelectEntity ikPuntoGrupo;
	private UISelectEntity departamento;
	private List<UISelectEntity> puntosGrupos;
	private ESql accion;

	public RubroGrupo() {
		this(ESql.UPDATE, -1L);
	}
	
	public RubroGrupo(ESql accion, Long key) {
	  this(accion, key, new UISelectEntity(-1L));
	}

	public RubroGrupo(ESql accion, Long key, UISelectEntity ikPuntoGrupo) {
		super(key);
		this.accion = accion;
		this.ikPuntoGrupo = ikPuntoGrupo;
		this.puntosGrupos= new ArrayList<>();
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}
	
	
	public boolean isVisible(){
		return !this.accion.equals(ESql.DELETE);
	}
	
		
	public UISelectEntity getIkPuntoGrupo() {
		return ikPuntoGrupo;
	}

	public void setIkPuntoGrupo(UISelectEntity ikPuntoGrupo) {
		this.ikPuntoGrupo = ikPuntoGrupo;
		if(this.ikPuntoGrupo!= null)
			setIdPuntoGrupo(this.ikPuntoGrupo.getKey());
	}

	public UISelectEntity getDepartamento() {
		return departamento;
	}

	public void setDepartamento(UISelectEntity departamento) {
		this.departamento = departamento;
	}

	public List<UISelectEntity> getPuntosGrupos() {
		return puntosGrupos;
	}

	public void setPuntosGrupos(List<UISelectEntity> puntosGrupos) {
		this.puntosGrupos = puntosGrupos;
	}
	
	public void cargaPuntosGrupos(){
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_departamentos.id_departamento=".concat(this.departamento.getKey().toString()));
			params.put("sortOrder", "order by tc_keet_puntos_grupos.descripcion");
			this.puntosGrupos= UIEntity.seleccione("VistaPuntosControlDto", "lazy", params, "paquete");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // cargaPuntosGrupos

}
