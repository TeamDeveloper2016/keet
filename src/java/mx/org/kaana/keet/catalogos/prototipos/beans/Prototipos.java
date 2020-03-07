/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *
 * @author CRISTOBAL.HERRERA
 */
public class Prototipos  extends TcKeetPrototiposDto{
  
  private UISelectEntity ikCliente;
  private UISelectEntity ikMuro;
  private UISelectEntity ikAzotea;
  private UISelectEntity ikEntrepiso;

  public UISelectEntity getIkCliente() {
    return ikCliente;
  }

  public void setIkCliente(UISelectEntity ikCliente) {
    this.ikCliente = ikCliente;
  }

  public UISelectEntity getIkMuro() {
    return ikMuro;
  }

  public void setIkMuro(UISelectEntity ikMuro) {
    this.ikMuro = ikMuro;
  }

  public UISelectEntity getIkAzotea() {
    return ikAzotea;
  }

  public void setIkAzotea(UISelectEntity ikAzotea) {
    this.ikAzotea = ikAzotea;
  }

  public UISelectEntity getIkEntrepiso() {
    return ikEntrepiso;
  }

  public void setIkEntrepiso(UISelectEntity ikEntrepiso) {
    this.ikEntrepiso = ikEntrepiso;
  }
  
  
  
  
  
}
