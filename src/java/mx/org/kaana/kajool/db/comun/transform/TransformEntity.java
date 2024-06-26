package mx.org.kaana.kajool.db.comun.transform;

import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;

public final class TransformEntity extends Transformer {
	
	private static final long serialVersionUID=-8492836222778970682L;

  @Override
  public Object tuple(Object[] data, String[] fields, String[] bdNames) throws InstantiationException, IllegalAccessException {
    Entity regresar = new Entity();
    for (int x=0; x< data.length; x++) {
			Object value= data[x];
				if(data[x]!= null)
					if(data[x] instanceof java.sql.Timestamp)
						value= ((java.sql.Timestamp)data[x]).toLocalDateTime();
					else	
					if(data[x] instanceof java.sql.Date)
						value= ((java.sql.Date)data[x]).toLocalDate();
					else	
					if(data[x] instanceof java.sql.Time)
						value= ((java.sql.Time)data[x]).toLocalTime();
			regresar.put(fields[x], new Value(fields[x], value, bdNames[x]));
    } // for
    return regresar;
  }

}
