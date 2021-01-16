package mx.org.kaana.kajool.db.comun.operation;

import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import org.hibernate.Session;

/**
 *@company KAJOOL
 *@project KAJOOL (Control system polls)
 *@date 16/01/2021
 *@time 12:08:28 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Select implements IActions  {

  private IBaseDto dto;

  public Select(IBaseDto dto) { 
    this.dto= dto;
  }

	public IBaseDto getDto() {
		return dto;
	}

	@Override
  public Long ejecutar(Session session) throws Exception {
    return -1L;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.dto);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final Select other = (Select) obj;
    if (!Objects.equals(this.dto, other.dto))
      return false;
    return true;
  }


  @Override
  public Map getFields() {
    return this.dto.toMap();
  }  
  
}
