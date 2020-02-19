package mx.org.kaana.kajool.db.comun.sql;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 26/09/2014
 *@time 04:13:01 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class SourceEntity extends Entity {

	
	private static final long serialVersionUID=2877536536553277991L;

	@Override
	public Boolean toBoolean(String name) {
		return super.toBoolean(name.toUpperCase());		
	}

	@Override
	public String toField(String name) {
		return super.toField(name.toUpperCase());
	}

	@Override
	public Object toObject(String name) {
		return super.toObject(name.toUpperCase());
	}

	@Override
	public Object toValue(String name) {
		return  super.toValue(name.toUpperCase());
	}

	@Override
	public String toString(String name) {
		return super.toString(name.toUpperCase());
	}

	@Override
	public Long toLong(String name) {
		return super.toLong(name.toUpperCase());
	}

	@Override
	public Integer toInteger(String name) {
		return super.toInteger(name.toUpperCase());
	}

	@Override
	public Double toDouble(String name) {
		return super.toDouble(name.toUpperCase());
	}

	@Override
	public Float toFloat(String name) {
		return super.toFloat(name.toUpperCase());
	}

	@Override
	public Short toShort(String name) {
		return super.toShort(name.toUpperCase());
	}

	@Override
	public LocalDate toDate(String name) {
		return super.toDate(name.toUpperCase());
	}

	@Override
	public LocalDateTime toTimestamp(String name) {
		return super.toTimestamp(name.toUpperCase());
	}

	@Override
	public LocalTime toTime(String name) {
		return super.toTime(name.toUpperCase());
	}
	
	 @Override
  public Class toHbmClass() {
    return SourceEntity.class;
  }

	
	
	

	
	
	
	
}
