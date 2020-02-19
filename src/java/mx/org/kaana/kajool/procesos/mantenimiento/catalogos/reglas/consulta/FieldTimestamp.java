package mx.org.kaana.kajool.procesos.mantenimiento.catalogos.reglas.consulta;

import java.time.LocalDateTime;
import java.util.List;
import javax.faces.component.UIComponentBase;
import mx.org.kaana.kajool.procesos.mantenimiento.catalogos.reglas.accion.IValue;
import mx.org.kaana.kajool.procesos.mantenimiento.catalogos.reglas.criterios.ICriterio;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22/09/2015
 *@time 03:52:42 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class FieldTimestamp extends mx.org.kaana.kajool.procesos.mantenimiento.catalogos.reglas.criterios.FieldTimestamp implements ICriterio, IValue {

  private static final long serialVersionUID = 1327021033647921896L;

  public FieldTimestamp(String nombre, String titulo) {
    super(nombre, titulo, LocalDateTime.now());
  }

  @Override
  public List<UIComponentBase> create(String value) {
    return super.clone(value);
  }

  @Override
  public Object getValue() {
    if(super.getValue()!= null)
      return super.getValue();
    return LocalDateTime.now();
  }

}
