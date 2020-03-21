/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.keet.db.dto.TcKeetNombresDiasDto;

/**
 *
 * @author CRISTOBAL.HERRERA
 */
public class DiaHabil extends TcKeetNombresDiasDto{

	public DiaHabil() {
		super();
	}

	public DiaHabil(String nombre) {
		super(null, null, null, -1L, nombre);
	}
	
	
	@Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNombresDiasDto other = (TcKeetNombresDiasDto) obj;
    if (getNombre()!= other.getNombre() && (getNombre() == null || !getNombre().equals(other.getNombre()))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 64 * hash + (getNombre() != null ? getNombre().hashCode() : 0);
    return hash;
  }
	
	 @Override
  public Class toHbmClass() {
    return TcKeetNombresDiasDto.class;
  }


	
}
