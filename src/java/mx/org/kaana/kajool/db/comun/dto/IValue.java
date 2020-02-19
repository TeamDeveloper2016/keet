package mx.org.kaana.kajool.db.comun.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public interface IValue  {

  public String    toString(String name);
  public Long      toLong(String name);
  public Integer   toInteger(String name);
  public Float     toFloat(String name);
  public Double    toDouble(String name);
  public Short     toShort(String name);
  public Boolean   toBoolean (String name);
  public LocalDate toDate (String name);
  public LocalDateTime toTimestamp(String name);
  public LocalTime toTime (String name);

}


