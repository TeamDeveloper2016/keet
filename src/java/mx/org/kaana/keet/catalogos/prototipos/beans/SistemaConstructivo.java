package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.kajool.db.comun.operation.Delete;
import mx.org.kaana.kajool.db.comun.operation.IActions;


public class SistemaConstructivo {
	 private IActions sistema;
	 private Long idKey;

	public SistemaConstructivo(IActions sistema) {
		this.sistema = sistema;
	}
	 
 

	public boolean isMostrar() {
		return !(this.sistema instanceof Delete);
	}

	public IActions getSistema() {
		return sistema;
	}

	public void setSistema(IActions sistema) {
		this.sistema = sistema;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SistemaConstructivo other = (SistemaConstructivo) obj;
    if (getSistema().getDto().getKey() != other.getSistema().getDto().getKey() && (getSistema().getDto().getKey() == null || !getSistema().getDto().getKey().equals(other.getSistema().getDto().getKey()))) {
      return false;
    }
    return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
    hash = 68 * hash + (getSistema().getDto().getKey() != null ? getSistema().getDto().getKey().hashCode() : 0);
    return hash;
	}
	
	
	 
	 
					 
	
	
}
