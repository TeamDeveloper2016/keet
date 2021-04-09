package mx.org.kaana.kajool.db.comun.sql;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import mx.org.kaana.libs.formato.Fecha;

public class Value implements Serializable, Comparable<Value> {
	private static final long serialVersionUID=5231961032480104777L;

  private String name;
  private Object data;
  private String field;

  public Value(String name) {
    this(name, null);
  }

  public Value(String name, Object data) {
		this(name, data, name);
  }

  public Value(String name, Object data, String field) {
    this.name = name;
    this.data = data;
    this.field= field;
  }

  public Object getData() {
    return data;
  }

  public String getData$() {
    String regresar = null;
    if (data instanceof LocalDate)
			regresar = ((LocalDate)data).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    else if (data instanceof LocalTime)
      regresar = ((LocalDate)data).format(DateTimeFormatter.ofPattern("hh:mm:ss"));
    else if (data instanceof LocalDateTime)
      regresar = ((LocalDate)data).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    else
      regresar = data.toString();
    return regresar;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getName() {
    return name;
  }

  private void setName(String name) {
    this.name = name;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getField() {
    return field;
  }

  public String toString(String value) {
    Object regresar = this.getData();
    return regresar!= null? regresar.toString(): value;
  }

  public Long toLong(Long value) {
    Object regresar = getData();
    return regresar!=null? ((Number)regresar).longValue(): value;
  }

  public Integer toInteger(Integer value) {
    Object regresar = getData();
    return regresar!=null? ((Number)regresar).intValue(): value;
  }

  public Double toDouble(Double value) {
    Object regresar = getData();
    return regresar!=null? ((Number)regresar).doubleValue(): value;
  }

  public Float toFloat(Float value) {
    Object regresar = getData();
    return regresar!=null? ((Number)regresar).floatValue(): value;
  }

  public Short toShort(Short value) {
    Object regresar = getData();
    return regresar!=null? ((Number)regresar).shortValue(): value;
  }

  public Boolean toBoolean(Boolean value) {
    Object regresar = getData();
    return regresar!=null ? (Boolean)regresar: value;
  }

  public LocalDate toDate(LocalDate value) {
    Object regresar = getData();
    return regresar!=null? (LocalDate)regresar: value;
  }

  public LocalDateTime toTimestamp(LocalDateTime value) {
    Object regresar = getData();
    return regresar!=null? (LocalDateTime)regresar: value;
  }

  public LocalTime toTime(LocalTime value) {
    Object regresar = getData();
    return regresar!=null? (LocalTime)regresar: value;
  }

  @Override
  public String toString() {
    return toString(null);
  }

  public Long toLong() {
    return toLong(null);
  }

  public Integer toInteger() {
    return toInteger(null);
  }

  public Double toDouble() {
    return toDouble(null);
  }

  public Float toFloat() {
    return toFloat(null);
  }

  public Short toShort() {
    return toShort(null);
  }

  public Boolean toBoolean() {
    return toBoolean(null);
  }

  public LocalDate toDate() {
    return toDate(null);
  }

  public LocalDateTime toTimestamp() {
    return toTimestamp(null);
  }

  public LocalTime toTime() {
    return toTime(null);
  }

  public Long getToLong() {
    return toLong(null);
  }

  public Integer getToInteger() {
    return toInteger(null);
  }

  public Double getToDouble() {
    return toDouble(null);
  }

  public Float getToFloat() {
    return toFloat(null);
  }

  public Short getToShort() {
    return toShort(null);
  }

  public Boolean getToBoolean() {
    return toBoolean(null);
  }

  public LocalDate getToDate() {
    return toDate(null);
  }

  public LocalDateTime getToTimestamp() {
    return toTimestamp(null);
  }

  public LocalTime getToTime() {
    return toTime(null);
  }
	
  public String getToString() {
    return toString(null);
  }

	@Override
	public boolean equals(Object obj) {
		if(obj==null) {
			return false;
		}
		if(getClass()!=obj.getClass()) {
			return false;
		}
		final Value other=(Value) obj;
		if(this.data!=other.data&&(this.data==null||!this.data.equals(other.data))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash=7;
		hash=89*hash+(this.data!=null ? this.data.hashCode() : 0);
		return hash;
	}

	@Override
	public int compareTo(Value o) {
		int regresar;
		try {
	    if (data instanceof LocalDate)
			regresar= ((LocalDate)this.data).isBefore((LocalDate)o.data) ? -1 :(((LocalDate)this.data).equals(((LocalDate)o.data))  ? 0 : 1);
    else if (data instanceof LocalTime)
			regresar= ((LocalTime)this.data).isBefore((LocalTime)o.data) ? -1 :(((LocalTime)this.data).equals(((LocalTime)o.data))  ? 0 : 1);
    else if (data instanceof LocalDateTime)
			regresar= ((LocalDateTime)this.data).isBefore((LocalDateTime)o.data) ? -1 :(((LocalDateTime)this.data).equals(((LocalDateTime)o.data))  ? 0 : 1);
    else
			regresar = this.data.toString().compareTo(o.toString());
		} // try
		catch(Exception e) {
			regresar = 0;
		} // catch
		return regresar;
	}

  @Override
  public Value clone() {  
    return new Value(this.name, this.data, this.field);  
  }
  
}
