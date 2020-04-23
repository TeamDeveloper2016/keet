package mx.org.kaana.keet.catalogos.subcontratistas.backing;

import java.io.Serializable;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named(value = "keetCatalogosSubcontratistasFiltro")
@ViewScoped
public class Filtro extends mx.org.kaana.mantic.catalogos.proveedores.backing.Filtro implements Serializable {

	private static final long serialVersionUID = 828713143062667738L;
	
	@Override
	public Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= super.toPrepare();		
		regresar.put("idSubContratista", 1L);
		return regresar;		
	} // toPrepare
}
