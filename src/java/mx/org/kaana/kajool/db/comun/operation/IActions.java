package mx.org.kaana.kajool.db.comun.operation;

import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import org.hibernate.Session;

public interface IActions {

  public Long ejecutar(Session session) throws Exception;
	public IBaseDto getDto();
	public Map getFields();
	
}
