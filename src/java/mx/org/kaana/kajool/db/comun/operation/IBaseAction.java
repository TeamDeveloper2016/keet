package mx.org.kaana.kajool.db.comun.operation;

import java.io.Serializable;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import static mx.org.kaana.kajool.enums.ESql.DELETE;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import org.hibernate.Session;

public abstract class IBaseAction<T extends IBaseDto> implements IBaseDto, Serializable {

	private ESql sql;

	public IBaseAction() {
		this(ESql.INSERT);
	}
	
	public IBaseAction(ESql sql) {
		this.sql=sql;
	}

	public ESql getSql() {
		return sql;
	}

	public void setSql(ESql sql) {
		this.sql=sql;
	}
	
	public Long execute(Session session) throws Exception {
		Long result= -1L;
		switch(sql) {
			case INSERT:
				result= this.insert(session);
				break;
			case UPDATE:
				result= this.update(session);
				break;
			case DELETE:
				result= this.delete(session);
				break;
		} // switch
		return result;
	} 
	
	private Long delete(Session session) throws Exception {
    return DaoFactory.getInstance().delete(session, (IBaseDto)this);
  }
	
  private Long insert(Session session) throws Exception {
    return DaoFactory.getInstance().insert(session, (IBaseDto)this);
  }
	
	private Long update(Session session) throws Exception {
    return DaoFactory.getInstance().update(session, (IBaseDto)this);
  }

	@Override
	public abstract Long getKey();

	@Override
	public abstract void setKey(Long key);

	@Override
	public abstract Map<String, Object> toMap();

	@Override
	public abstract Object[] toArray();

	@Override
	public abstract boolean isValid();

	@Override
	public abstract Object toValue(String name);

	@Override
	public abstract String toAllKeys();

	@Override
	public abstract String toKeys();

	@Override
	public abstract Class toHbmClass();
	
}
