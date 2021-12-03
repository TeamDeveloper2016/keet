package mx.org.kaana.keet.catalogos.contratos.destajos.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 2/12/2021
 *@time 10:09:14 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Lote implements Serializable {

  private static final long serialVersionUID = 4086421716021322781L;

  private String name;
  private String field;
  private String symbol;
  private String style;
  private String font;

  public Lote(String name) {
    this(name, null);
  }
  
  public Lote(String name, String field) {
    this(name, field, "");
  }

  public Lote(String name, String field, String symbol) {
    this(name, field, symbol, "janal-column-left janal-wid-10");
  }

  public Lote(String name, String field, String symbol, String style) {
    this(name, field, symbol, style, "");
  }

  public Lote(String name, String field, String symbol, String style, String font) {
    this.name  = name;
    this.field = field;
    this.symbol= symbol;
    this.style = style;
    this.font  = font;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getFont() {
    return font;
  }

  public void setFont(String font) {
    this.font = font;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + Objects.hashCode(this.name);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Lote other = (Lote) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Lote{" + "name=" + name + ", field=" + field + ", symbol=" + symbol + ", style=" + style + ", font=" + font + '}';
  }

  
}
