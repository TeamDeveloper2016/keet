package mx.org.kaana.keet.compras.codigos.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesCodigosDto;
import mx.org.kaana.libs.formato.Error;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  
  private Long idOrdenCodigo;
	private String messageError;

	public Transaccion() {
    this(-1L);
	} // Transaccion

	public Transaccion(Long idOrdenCodigo) {
    this.idOrdenCodigo= idOrdenCodigo;
	} // Transaccion

	public String getMessageError() {
		return messageError;
	}
  
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= Boolean.FALSE;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" los códigos");
			switch(accion) {
				case AGREGAR:
          regresar= this.toAddCodigos(sesion);
					break;
				case ELIMINAR:
          regresar= this.toCleanCodigo(sesion);
					break;				
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	// ejecutar

  private Boolean toAddCodigos(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {
      for (int x= 0; x< 50; x++) {
        TcKeetOrdenesCodigosDto codigo= new TcKeetOrdenesCodigosDto(
          null, // LocalDateTime utilizado, 
          this.toRandomWord(), // String codigo, 
          null, // Long idUsuario, 
          null, // Long idOrdenCompra, 
          -1L // Long idOrdenCodigo
        );
        DaoFactory.getInstance().insert(sesion, codigo);
      } // for
      regresar= Boolean.TRUE;      
    } // try
    catch (Exception e) {			
      throw e;      
    } // catch
    return regresar;
  }

  private Boolean toCleanCodigo(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {
      TcKeetOrdenesCodigosDto codigo= (TcKeetOrdenesCodigosDto)DaoFactory.getInstance().findById(sesion, TcKeetOrdenesCodigosDto.class, this.idOrdenCodigo);
      if(codigo!= null) {
        codigo.setIdOrdenCompra(null);
        codigo.setUtilizado(null);
        codigo.setIdUsuario(null);
        regresar= DaoFactory.getInstance().update(sesion, codigo)> 0L;
      } // if  
    } // try
    catch (Exception e) {			
      throw e;      
    } // catch    
    return regresar;
  }

  private String toRandomWord() {
		//La variable palabra almacena el resultado final
		StringBuilder word= new StringBuilder();
		//La longitud de la palabra la decidimos al azar
		int lenght = (int)(Math.random()*(8- 7+ 1)+ 7);
		//Generamos palabra
    int character= 48;
		for (int i=0; i<lenght; i++){
      if(word.length()%2== 0)
			  character= (int)Math.floor(Math.random()*(122- 97)+ 97);
      else
		  	character= (int)Math.floor(Math.random()*(57- 48)+ 48);
			//para pasar el código a carácter basta con hacer un cast a char
			word.append((char)character);
		} // for
		return word.toString().toUpperCase();
	}
  
}