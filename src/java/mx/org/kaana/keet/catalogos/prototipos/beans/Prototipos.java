package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.keet.catalogos.prototipos.reglas.AdminSistemaConstructivo;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Prototipos extends TcKeetPrototiposDto {
  
  private UISelectEntity ikCliente;
  private AdminSistemaConstructivo ikSistemasConstructivos;

	public Prototipos() {
    super();
  }

	public AdminSistemaConstructivo getIkSistemasConstructivos() {
		return ikSistemasConstructivos;
	}
	
	public void setIkSistemasConstructivos(AdminSistemaConstructivo ikSistemasConstructivos) {
		this.ikSistemasConstructivos = ikSistemasConstructivos;
	}
	
  public UISelectEntity getIkCliente() {
    return ikCliente;
  }

  public void setIkCliente(UISelectEntity ikCliente) {
    this.ikCliente = ikCliente;
		if(this.ikCliente!= null)
			this.setIdCliente(this.ikCliente.getKey());
  }

	@Override
	public Class toHbmClass() {
		return TcKeetPrototiposDto.class;
	}
  
}
