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

  public SelectionItem(String key, String item) {
		this(key, item, 1L);
  }

  public SelectionItem(String key, String item, Long idActivo) {
    this.item=item;
    this.key=key;
		this.idActivo= idActivo;
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
		return "SelectionItem{"+"item="+item+", key="+key+", idActivo="+idActivo+'}';
	}

}
