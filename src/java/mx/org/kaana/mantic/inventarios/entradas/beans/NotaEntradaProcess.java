package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.util.List;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/10/2020
 *@time 05:49:36 PM 
 *@author Team Developer 2016 <team.developer2016@gmail.com>
 */

public class NotaEntradaProcess {

	private NotaEntrada notaEntrada;
	private List<Articulo> articulos;
	private List lotes;
	private List familias;

  public NotaEntradaProcess() {
    this(null, null, null, null);
  }

  public NotaEntradaProcess(NotaEntrada notaEntrada, List<Articulo> articulos, List lotes, List familias) {
    this.notaEntrada = notaEntrada;
    this.articulos = articulos;
    this.lotes = lotes;
    this.familias = familias;
  }

  public NotaEntrada getNotaEntrada() {
    return notaEntrada;
  }

  public void setNotaEntrada(NotaEntrada notaEntrada) {
    this.notaEntrada = notaEntrada;
  }

  public List<Articulo> getArticulos() {
    return articulos;
  }

  public void setArticulos(List<Articulo> articulos) {
    this.articulos = articulos;
  }

  public List getLotes() {
    return lotes;
  }

  public void setLotes(List lotes) {
    this.lotes = lotes;
  }

  public List getFamilias() {
    return familias;
  }

  public void setFamilias(List familias) {
    this.familias = familias;
  }
    
}
