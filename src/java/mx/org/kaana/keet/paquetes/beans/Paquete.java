package mx.org.kaana.keet.paquetes.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.keet.db.dto.TcKeetPaquetesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/09/2023
 *@time 11:08:38 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx
 */

public class Paquete extends TcKeetPaquetesDto implements Serializable {

  private static final long serialVersionUID = 3518579943910403842L;
  
  private List<Material> materiales;
  
  public Paquete() {
    this(-1L);
  }

  public Paquete(Long key) {
    super(key);
    this.materiales= new ArrayList<>();
  }

  public List<Material> getMateriales() {
    return materiales;
  }

  public void setMateriales(List<Material> materiales) {
    this.materiales = materiales;
  }
  
}
