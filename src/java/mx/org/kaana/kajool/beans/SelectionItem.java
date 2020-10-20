package mx.org.kaana.kajool.beans;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date Oct 10, 2012
 *@time 11:32:47 AM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;

public class SelectionItem implements Serializable {

  private static final long serialVersionUID = 6739783685039614798L;

  private String item;
  private String key;
	private Long idActivo;
	private Long idNomina;
	private Long idSeguro;
	private String nss;
	private Long tipo;

  public SelectionItem(String key, String item) {
		this(key, item, 1L, 2L, null, null);
  }

  public SelectionItem(String key, String item, Long idActivo, Long idNomina, Long idSeguro, String nss) {
		this(key, item, idActivo, idNomina, idSeguro, nss, null);
	}
	
  public SelectionItem(String key, String item, Long idActivo, Long idNomina,  Long idSeguro, String nss, Long tipo) {
    this.item    = item;
    this.key     = key;
		this.idActivo= idActivo;
		this.idNomina= idNomina;
		this.idSeguro= idSeguro;
		this.nss     = nss;
		this.tipo    = tipo;
  }

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item=item;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key=key;
  }

	public Long getIdActivo() {
		return idActivo;
	}

	public void setIdActivo(Long idActivo) {
		this.idActivo=idActivo;
	}

	public Long getIdNomina() {
		return idNomina;
	}

	public void setIdNomina(Long idNomina) {
		this.idNomina=idNomina;
	}

  public Long getIdSeguro() {
    return idSeguro;
  }

  public void setIdSeguro(Long idSeguro) {
    this.idSeguro = idSeguro;
  }

	public String getNss() {
		return nss;
	}

	public void setNss(String nss) {
		this.nss=nss;
	}

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}	
	
  @Override
  public boolean equals(Object obj) {
    if (obj==null) {
      return false;
    }
    if (getClass()!=obj.getClass()) {
      return false;
    }
    final SelectionItem other=(SelectionItem) obj;
    if ((this.key==null) ? (other.key!=null) : !this.key.equals(other.key)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash=7;
    hash=73*hash+(this.key!=null ? this.key.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return "SelectionItem{" + "item=" + item + ", key=" + key + ", idActivo=" + idActivo + ", idNomina=" + idNomina + ", idSeguro=" + idSeguro + ", nss=" + nss + ", tipo=" + tipo + '}';
  }
  
}
