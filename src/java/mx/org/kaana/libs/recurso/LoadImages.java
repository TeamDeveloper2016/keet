package mx.org.kaana.libs.recurso;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.primefaces.model.DefaultStreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/06/2018
 *@time 05:09:52 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class LoadImages {

	public static DefaultStreamedContent getImage(String project, String name) throws FileNotFoundException {
		File file= toListFile(project, name);
		return toReturnFile(file);
	}
	
	public static DefaultStreamedContent getImage(String name) throws FileNotFoundException {
		File file= toListFile(name);
		return toReturnFile(file);
	} 
	
	public static DefaultStreamedContent getImage(Long idArticulo) throws FileNotFoundException, Exception {
		DefaultStreamedContent regresar= null;
		Map<String, Object> params     = null;
		try {
			params=new HashMap<>();
			params.put("idArticulo", idArticulo);
  		Value alias= (Value)DaoFactory.getInstance().toField("VistaArticulosDto", "imagen", params, "alias");
			if(alias!= null && !Cadena.isVacio(alias.toString()))
				regresar= getFile(alias.toString());
			else {
				InputStream stream = new FileInputStream(JsfBase.getRealPath("/resources/janal/img/sistema/sin-foto.png"));
        regresar= DefaultStreamedContent.builder().contentType("image/png").stream(()-> stream).build();
			} // else	
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	public static DefaultStreamedContent getFile(String name) throws FileNotFoundException {
		if(Cadena.isVacio(name)) {
			InputStream stream = new FileInputStream(JsfBase.getRealPath("/resources/janal/img/sistema/sin-foto.png"));
		  return DefaultStreamedContent.builder().contentType("image/png").stream(()-> stream).build(); // svg+xml
		} // if	
		else {
		  File file         = new File(name);
			InputStream stream= new FileInputStream(file);
			InputStream icono = new FileInputStream(JsfBase.getRealPath("/resources/janal/img/sistema/bonanza.svg"));
		  return file.exists()? DefaultStreamedContent.builder().contentType("image/jpg").stream(()-> stream).build(): DefaultStreamedContent.builder().contentType("image/svg+xml").stream(()-> icono).build();
		} // else	
	} 
	
	private static DefaultStreamedContent toReturnFile(File file) throws FileNotFoundException {
		DefaultStreamedContent regresar= null;
		if(file== null || !file.exists() || file.isDirectory()) {
			InputStream stream = new FileInputStream(JsfBase.getRealPath("/resources/janal/img/sistema/sin-foto.png"));
		  regresar= DefaultStreamedContent.builder().contentType("image/png").stream(()-> stream).build();
		} // if	
		else {
			InputStream stream = new FileInputStream(file.getAbsolutePath());
			regresar= DefaultStreamedContent.builder().contentType("image/jpg").stream(()-> stream).build();
		} // else	
		return regresar;
	}
	
	private static File toListFile(String project, String name) {
		File dir = new File(Configuracion.getInstance().getPropiedadSistemaServidor("path.image").concat(project).concat("/"));		
		return toReturnListFile(dir, name);
	}
	
	private static File toListFile(String name) {
		File dir = new File(Configuracion.getInstance().getPropiedadSistemaServidor("path.image"));		
		return toReturnListFile(dir, name);
	}
	
	private static File toReturnListFile(File dir, String name) {		
		List<String> filters= new ArrayList<>();
		filters.add(name+ "*.png");
		filters.add(name+ "*.jpg");
		FileFilter fileFilter = new WildcardFileFilter(filters, IOCase.INSENSITIVE);
		File[] files = dir.listFiles(fileFilter);
		return files== null || files.length<= 0? new File(Configuracion.getInstance().getPropiedadSistemaServidor("path.image")+ name+ ".jpg"): files[0];
	}
	
	public static void main(String ... args) throws Exception {
		try {
			LoadImages.getImage("1");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	}
	
}
