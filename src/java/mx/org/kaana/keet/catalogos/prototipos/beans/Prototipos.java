package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Prototipos extends TcKeetPrototiposDto {
  
  private UISelectEntity ikCliente;
  private UISelectEntity ikMuro;
  private UISelectEntity ikAzotea;
  private UISelectEntity ikEntrepiso;

  public UISelectEntity getIkCliente() {
    return ikCliente;
  }

  public void setIkCliente(UISelectEntity ikCliente) {
    this.ikCliente = ikCliente;
		if(this.ikCliente!= null)
			this.setIdCliente(this.ikCliente.getKey());
  }

  public UISelectEntity getIkMuro() {
    return ikMuro;
  }

  public void setIkMuro(UISelectEntity ikMuro) {
    this.ikMuro = ikMuro;
		if(this.ikMuro!= null)
			this.setIdSistemaMuro(this.ikMuro.getKey());
  }

  public UISelectEntity getIkAzotea() {
    return ikAzotea;
  }

  public void setIkAzotea(UISelectEntity ikAzotea) {
    this.ikAzotea = ikAzotea;
		if(this.ikAzotea!= null)
			this.setIdSistemaAzotea(this.ikAzotea.getKey());
  }

  public UISelectEntity getIkEntrepiso() {
    return ikEntrepiso;
  }

  public void setIkEntrepiso(UISelectEntity ikEntrepiso) {
    this.ikEntrepiso = ikEntrepiso;
		if(this.ikEntrepiso!= null)
			this.setIdSistemaEntrepiso(this.ikEntrepiso.getKey());
  }

	@Override
	public Class toHbmClass() {
		return TcKeetPrototiposDto.class;
	}
  
}
