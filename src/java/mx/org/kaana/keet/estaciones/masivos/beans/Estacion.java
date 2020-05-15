package mx.org.kaana.keet.estaciones.masivos.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/05/2020
 *@time 11:38:25 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Estacion extends TcKeetEstacionesDto implements Cloneable, Serializable {

	private static final long serialVersionUID=-6170305461966609460L;

	@Override
	public Class toHbmClass() {
		return TcKeetEstacionesDto.class;
	}
	
	@Override
	public Estacion clone() throws CloneNotSupportedException {
		Estacion clone= new Estacion();
		clone.setIdEstacion(-1L);
		clone.setIdPlantilla(this.getIdPlantilla());
		clone.setNivel(this.getNivel());
		clone.setClave(this.getClave());
		clone.setUltimo(this.getUltimo());
		clone.setCodigo(this.getCodigo());
		clone.setNombre(this.getNombre());
		clone.setDescripcion(this.getDescripcion());
		clone.setIdEmpaqueUnidadMedida(this.getIdEmpaqueUnidadMedida());
		clone.setInicio(this.getInicio());
		clone.setTermino(this.getTermino());
		clone.setCantidad(this.getCantidad());
		clone.setIdEstacionEstatus(this.getIdEstacionEstatus());
		clone.setIdUsuario(this.getIdUsuario());
		clone.setRegistro(LocalDateTime.now());
		clone.setCosto(0D);
		clone.setAbono1(0D);
		clone.setAbono2(0D);
		clone.setAbono3(0D);
		clone.setAbono4(0D);
		clone.setAbono5(0D);
		clone.setAbono6(0D);
		clone.setAbono7(0D);
		clone.setAbono8(0D);
		clone.setAbono9(0D);
		clone.setAbono10(0D);
		clone.setAbono11(0D);
		clone.setAbono12(0D);
		clone.setAbono13(0D);
		clone.setAbono14(0D);
		clone.setAbono15(0D);
		clone.setAbono16(0D);
		clone.setAbono17(0D);
		clone.setAbono18(0D);
		clone.setAbono19(0D);
		clone.setAbono20(0D);
		clone.setAbono21(0D);
		clone.setAbono22(0D);
		clone.setAbono23(0D);
		clone.setAbono24(0D);
		clone.setAbono25(0D);
		clone.setAbono26(0D);
		clone.setAbono27(0D);
		clone.setAbono28(0D);
		clone.setAbono29(0D);
		clone.setAbono30(0D);
		clone.setAbono31(0D);
		clone.setAbono32(0D);
		clone.setAbono33(0D);
		clone.setAbono34(0D);
		clone.setAbono35(0D);
		clone.setAbono36(0D);
		clone.setAbono37(0D);
		clone.setAbono38(0D);
		clone.setAbono39(0D);
		clone.setAbono40(0D);
		clone.setAbono41(0D);
		clone.setAbono42(0D);
		clone.setAbono43(0D);
		clone.setAbono44(0D);
		clone.setAbono45(0D);
		clone.setAbono46(0D);
		clone.setAbono47(0D);
		clone.setAbono48(0D);
		clone.setAbono49(0D);
		clone.setAbono50(0D);
		clone.setAbono51(0D);
		clone.setAbono52(0D);
		clone.setAbono53(0D);
		clone.setAbono54(0D);
		clone.setAbono55(0D);
		clone.setCargo1(0D);
		clone.setCargo2(0D);
		clone.setCargo3(0D);
		clone.setCargo4(0D);
		clone.setCargo5(0D);
		clone.setCargo6(0D);
		clone.setCargo7(0D);
		clone.setCargo8(0D);
		clone.setCargo9(0D);
		clone.setCargo10(0D);
		clone.setCargo11(0D);
		clone.setCargo12(0D);
		clone.setCargo13(0D);
		clone.setCargo14(0D);
		clone.setCargo15(0D);
		clone.setCargo16(0D);
		clone.setCargo17(0D);
		clone.setCargo18(0D);
		clone.setCargo19(0D);
		clone.setCargo20(0D);
		clone.setCargo21(0D);
		clone.setCargo22(0D);
		clone.setCargo23(0D);
		clone.setCargo24(0D);
		clone.setCargo25(0D);
		clone.setCargo26(0D);
		clone.setCargo27(0D);
		clone.setCargo28(0D);
		clone.setCargo29(0D);
		clone.setCargo30(0D);
		clone.setCargo31(0D);
		clone.setCargo32(0D);
		clone.setCargo33(0D);
		clone.setCargo34(0D);
		clone.setCargo35(0D);
		clone.setCargo36(0D);
		clone.setCargo37(0D);
		clone.setCargo38(0D);
		clone.setCargo39(0D);
		clone.setCargo40(0D);
		clone.setCargo41(0D);
		clone.setCargo42(0D);
		clone.setCargo43(0D);
		clone.setCargo44(0D);
		clone.setCargo45(0D);
		clone.setCargo46(0D);
		clone.setCargo47(0D);
		clone.setCargo48(0D);
		clone.setCargo49(0D);
		clone.setCargo50(0D);
		clone.setCargo51(0D);
		clone.setCargo52(0D);
		clone.setCargo53(0D);
		clone.setCargo54(0D);
		return clone;
	}
	
}
