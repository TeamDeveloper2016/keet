package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_keet_estaciones")
public class TcKeetEstacionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="inicio")
  private LocalDateTime inicio;
  @Column (name="abono_50")
  private Double abono50;
  @Column (name="abono_51")
  private Double abono51;
  @Column (name="abono_52")
  private Double abono52;
  @Column (name="abono_53")
  private Double abono53;
  @Column (name="abono_54")
  private Double abono54;
  @Column (name="cargo_27")
  private Double cargo27;
  @Column (name="abono_55")
  private Double abono55;
  @Column (name="cargo_28")
  private Double cargo28;
  @Column (name="cargo_29")
  private Double cargo29;
  @Column (name="acumulado_53")
  private Double acumulado53;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_estacion")
  private Long idEstacion;
  @Column (name="cargo_20")
  private Double cargo20;
  @Column (name="acumulado_54")
  private Double acumulado54;
  @Column (name="cargo_21")
  private Double cargo21;
  @Column (name="acumulado_51")
  private Double acumulado51;
  @Column (name="cargo_22")
  private Double cargo22;
  @Column (name="acumulado_52")
  private Double acumulado52;
  @Column (name="cargo_23")
  private Double cargo23;
  @Column (name="cargo_24")
  private Double cargo24;
  @Column (name="cargo_25")
  private Double cargo25;
  @Column (name="acumulado_55")
  private Double acumulado55;
  @Column (name="cargo_26")
  private Double cargo26;
  @Column (name="ultimo")
  private Long ultimo;
  @Column (name="acumulado_50")
  private Double acumulado50;
  @Column (name="abono_40")
  private Double abono40;
  @Column (name="abono_41")
  private Double abono41;
  @Column (name="abono_42")
  private Double abono42;
  @Column (name="abono_43")
  private Double abono43;
  @Column (name="cargo_16")
  private Double cargo16;
  @Column (name="abono_44")
  private Double abono44;
  @Column (name="cargo_17")
  private Double cargo17;
  @Column (name="abono_45")
  private Double abono45;
  @Column (name="cargo_18")
  private Double cargo18;
  @Column (name="abono_46")
  private Double abono46;
  @Column (name="acumulado_48")
  private Double acumulado48;
  @Column (name="cargo_19")
  private Double cargo19;
  @Column (name="abono_47")
  private Double abono47;
  @Column (name="acumulado_49")
  private Double acumulado49;
  @Column (name="abono_48")
  private Double abono48;
  @Column (name="abono_49")
  private Double abono49;
  @Column (name="acumulado_42")
  private Double acumulado42;
  @Column (name="acumulado_43")
  private Double acumulado43;
  @Column (name="cargo_10")
  private Double cargo10;
  @Column (name="acumulado_40")
  private Double acumulado40;
  @Column (name="cargo_11")
  private Double cargo11;
  @Column (name="acumulado_41")
  private Double acumulado41;
  @Column (name="cargo_12")
  private Double cargo12;
  @Column (name="acumulado_46")
  private Double acumulado46;
  @Column (name="nivel")
  private Long nivel;
  @Column (name="cargo_13")
  private Double cargo13;
  @Column (name="acumulado_47")
  private Double acumulado47;
  @Column (name="cargo_14")
  private Double cargo14;
  @Column (name="acumulado_44")
  private Double acumulado44;
  @Column (name="cargo_15")
  private Double cargo15;
  @Column (name="acumulado_45")
  private Double acumulado45;
  @Column (name="cargo_50")
  private Double cargo50;
  @Column (name="cargo_51")
  private Double cargo51;
  @Column (name="cargo_49")
  private Double cargo49;
  @Column (name="cargo_41")
  private Double cargo41;
  @Column (name="cargo_42")
  private Double cargo42;
  @Column (name="cargo_43")
  private Double cargo43;
  @Column (name="cargo_44")
  private Double cargo44;
  @Column (name="cargo_45")
  private Double cargo45;
  @Column (name="cargo_46")
  private Double cargo46;
  @Column (name="cargo_47")
  private Double cargo47;
  @Column (name="cargo_48")
  private Double cargo48;
  @Column (name="abono_7")
  private Double abono7;
  @Column (name="abono_6")
  private Double abono6;
  @Column (name="clave")
  private String clave;
  @Column (name="abono_5")
  private Double abono5;
  @Column (name="abono_4")
  private Double abono4;
  @Column (name="abono_3")
  private Double abono3;
  @Column (name="abono_2")
  private Double abono2;
  @Column (name="id_empaque_unidad_medida")
  private Long idEmpaqueUnidadMedida;
  @Column (name="abono_1")
  private Double abono1;
  @Column (name="cargo_40")
  private Double cargo40;
  @Column (name="cargo_2")
  private Double cargo2;
  @Column (name="cargo_1")
  private Double cargo1;
  @Column (name="abono_9")
  private Double abono9;
  @Column (name="abono_8")
  private Double abono8;
  @Column (name="cargo_8")
  private Double cargo8;
  @Column (name="cargo_38")
  private Double cargo38;
  @Column (name="cargo_7")
  private Double cargo7;
  @Column (name="cargo_39")
  private Double cargo39;
  @Column (name="cargo_9")
  private Double cargo9;
  @Column (name="cargo_4")
  private Double cargo4;
  @Column (name="cargo_3")
  private Double cargo3;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="cargo_6")
  private Double cargo6;
  @Column (name="id_plantilla")
  private Long idPlantilla;
  @Column (name="cargo_5")
  private Double cargo5;
  @Column (name="cargo_30")
  private Double cargo30;
  @Column (name="cargo_31")
  private Double cargo31;
  @Column (name="cargo_32")
  private Double cargo32;
  @Column (name="cargo_33")
  private Double cargo33;
  @Column (name="cargo_34")
  private Double cargo34;
  @Column (name="cargo_35")
  private Double cargo35;
  @Column (name="cargo_36")
  private Double cargo36;
  @Column (name="cargo_37")
  private Double cargo37;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="acumulado_9")
  private Double acumulado9;
  @Column (name="acumulado_8")
  private Double acumulado8;
  @Column (name="acumulado_7")
  private Double acumulado7;
  @Column (name="acumulado_6")
  private Double acumulado6;
  @Column (name="acumulado_5")
  private Double acumulado5;
  @Column (name="acumulado_4")
  private Double acumulado4;
  @Column (name="abono_10")
  private Double abono10;
  @Column (name="acumulado_3")
  private Double acumulado3;
  @Column (name="abono_11")
  private Double abono11;
  @Column (name="acumulado_17")
  private Double acumulado17;
  @Column (name="acumulado_2")
  private Double acumulado2;
  @Column (name="abono_12")
  private Double abono12;
  @Column (name="acumulado_18")
  private Double acumulado18;
  @Column (name="acumulado_1")
  private Double acumulado1;
  @Column (name="abono_13")
  private Double abono13;
  @Column (name="acumulado_15")
  private Double acumulado15;
  @Column (name="abono_14")
  private Double abono14;
  @Column (name="acumulado_16")
  private Double acumulado16;
  @Column (name="abono_15")
  private Double abono15;
  @Column (name="abono_16")
  private Double abono16;
  @Column (name="abono_17")
  private Double abono17;
  @Column (name="acumulado_19")
  private Double acumulado19;
  @Column (name="abono_18")
  private Double abono18;
  @Column (name="abono_19")
  private Double abono19;
  @Column (name="acumulado_10")
  private Double acumulado10;
  @Column (name="acumulado_13")
  private Double acumulado13;
  @Column (name="acumulado_14")
  private Double acumulado14;
  @Column (name="acumulado_11")
  private Double acumulado11;
  @Column (name="acumulado_12")
  private Double acumulado12;
  @Column (name="codigo")
  private String codigo;
  @Column (name="cargo_52")
  private Double cargo52;
  @Column (name="cargo_53")
  private Double cargo53;
  @Column (name="cargo_54")
  private Double cargo54;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="cargo_55")
  private Double cargo55;
  @Column (name="id_estacion_estatus")
  private Long idEstacionEstatus;
  @Column (name="nombre")
  private String nombre;
  @Column (name="abono_30")
  private Double abono30;
  @Column (name="abono_31")
  private Double abono31;
  @Column (name="abono_32")
  private Double abono32;
  @Column (name="abono_33")
  private Double abono33;
  @Column (name="acumulado_39")
  private Double acumulado39;
  @Column (name="abono_34")
  private Double abono34;
  @Column (name="abono_35")
  private Double abono35;
  @Column (name="acumulado_37")
  private Double acumulado37;
  @Column (name="abono_36")
  private Double abono36;
  @Column (name="acumulado_38")
  private Double acumulado38;
  @Column (name="abono_37")
  private Double abono37;
  @Column (name="abono_38")
  private Double abono38;
  @Column (name="abono_39")
  private Double abono39;
  @Column (name="acumulado_31")
  private Double acumulado31;
  @Column (name="acumulado_32")
  private Double acumulado32;
  @Column (name="termino")
  private LocalDateTime termino;
  @Column (name="acumulado_30")
  private Double acumulado30;
  @Column (name="acumulado_35")
  private Double acumulado35;
  @Column (name="acumulado_36")
  private Double acumulado36;
  @Column (name="acumulado_33")
  private Double acumulado33;
  @Column (name="acumulado_34")
  private Double acumulado34;
  @Column (name="costo")
  private Double costo;
  @Column (name="abono_20")
  private Double abono20;
  @Column (name="abono_21")
  private Double abono21;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="abono_22")
  private Double abono22;
  @Column (name="acumulado_28")
  private Double acumulado28;
  @Column (name="abono_23")
  private Double abono23;
  @Column (name="acumulado_29")
  private Double acumulado29;
  @Column (name="abono_24")
  private Double abono24;
  @Column (name="acumulado_26")
  private Double acumulado26;
  @Column (name="abono_25")
  private Double abono25;
  @Column (name="acumulado_27")
  private Double acumulado27;
  @Column (name="abono_26")
  private Double abono26;
  @Column (name="abono_27")
  private Double abono27;
  @Column (name="abono_28")
  private Double abono28;
  @Column (name="abono_29")
  private Double abono29;
  @Column (name="acumulado_20")
  private Double acumulado20;
  @Column (name="acumulado_21")
  private Double acumulado21;
  @Column (name="acumulado_24")
  private Double acumulado24;
  @Column (name="acumulado_25")
  private Double acumulado25;
  @Column (name="acumulado_22")
  private Double acumulado22;
  @Column (name="acumulado_23")
  private Double acumulado23;

  public TcKeetEstacionesDto() {
    this(new Long(-1L));
  }

  public TcKeetEstacionesDto(Long key) {
    this(LocalDateTime.now(), null, null, null, null, null, null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, LocalDateTime.now(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetEstacionesDto(LocalDateTime inicio, Double abono50, Double abono51, Double abono52, Double abono53, Double abono54, Double cargo27, Double abono55, Double cargo28, Double cargo29, Double acumulado53, Long idEstacion, Double cargo20, Double acumulado54, Double cargo21, Double acumulado51, Double cargo22, Double acumulado52, Double cargo23, Double cargo24, Double cargo25, Double acumulado55, Double cargo26, Long ultimo, Double acumulado50, Double abono40, Double abono41, Double abono42, Double abono43, Double cargo16, Double abono44, Double cargo17, Double abono45, Double cargo18, Double abono46, Double acumulado48, Double cargo19, Double abono47, Double acumulado49, Double abono48, Double abono49, Double acumulado42, Double acumulado43, Double cargo10, Double acumulado40, Double cargo11, Double acumulado41, Double cargo12, Double acumulado46, Long nivel, Double cargo13, Double acumulado47, Double cargo14, Double acumulado44, Double cargo15, Double acumulado45, Double cargo50, Double cargo51, Double cargo49, Double cargo41, Double cargo42, Double cargo43, Double cargo44, Double cargo45, Double cargo46, Double cargo47, Double cargo48, Double abono7, Double abono6, String clave, Double abono5, Double abono4, Double abono3, Double abono2, Long idEmpaqueUnidadMedida, Double abono1, Double cargo40, Double cargo2, Double cargo1, Double abono9, Double abono8, Double cargo8, Double cargo38, Double cargo7, Double cargo39, Double cargo9, Double cargo4, Double cargo3, Long idUsuario, Double cargo6, Long idPlantilla, Double cargo5, Double cargo30, Double cargo31, Double cargo32, Double cargo33, Double cargo34, Double cargo35, Double cargo36, Double cargo37, String descripcion, Double acumulado9, Double acumulado8, Double acumulado7, Double acumulado6, Double acumulado5, Double acumulado4, Double abono10, Double acumulado3, Double abono11, Double acumulado17, Double acumulado2, Double abono12, Double acumulado18, Double acumulado1, Double abono13, Double acumulado15, Double abono14, Double acumulado16, Double abono15, Double abono16, Double abono17, Double acumulado19, Double abono18, Double abono19, Double acumulado10, Double acumulado13, Double acumulado14, Double acumulado11, Double acumulado12, String codigo, Double cargo52, Double cargo53, Double cargo54, Double cantidad, Double cargo55, Long idEstacionEstatus, String nombre, Double abono30, Double abono31, Double abono32, Double abono33, Double acumulado39, Double abono34, Double abono35, Double acumulado37, Double abono36, Double acumulado38, Double abono37, Double abono38, Double abono39, Double acumulado31, Double acumulado32, LocalDateTime termino, Double acumulado30, Double acumulado35, Double acumulado36, Double acumulado33, Double acumulado34, Double costo, Double abono20, Double abono21, Double abono22, Double acumulado28, Double abono23, Double acumulado29, Double abono24, Double acumulado26, Double abono25, Double acumulado27, Double abono26, Double abono27, Double abono28, Double abono29, Double acumulado20, Double acumulado21, Double acumulado24, Double acumulado25, Double acumulado22, Double acumulado23) {
    setInicio(inicio);
    setAbono50(abono50);
    setAbono51(abono51);
    setAbono52(abono52);
    setAbono53(abono53);
    setAbono54(abono54);
    setCargo27(cargo27);
    setAbono55(abono55);
    setCargo28(cargo28);
    setCargo29(cargo29);
    setAcumulado53(acumulado53);
    setIdEstacion(idEstacion);
    setCargo20(cargo20);
    setAcumulado54(acumulado54);
    setCargo21(cargo21);
    setAcumulado51(acumulado51);
    setCargo22(cargo22);
    setAcumulado52(acumulado52);
    setCargo23(cargo23);
    setCargo24(cargo24);
    setCargo25(cargo25);
    setAcumulado55(acumulado55);
    setCargo26(cargo26);
    setUltimo(ultimo);
    setAcumulado50(acumulado50);
    setAbono40(abono40);
    setAbono41(abono41);
    setAbono42(abono42);
    setAbono43(abono43);
    setCargo16(cargo16);
    setAbono44(abono44);
    setCargo17(cargo17);
    setAbono45(abono45);
    setCargo18(cargo18);
    setAbono46(abono46);
    setAcumulado48(acumulado48);
    setCargo19(cargo19);
    setAbono47(abono47);
    setAcumulado49(acumulado49);
    setAbono48(abono48);
    setAbono49(abono49);
    setAcumulado42(acumulado42);
    setAcumulado43(acumulado43);
    setCargo10(cargo10);
    setAcumulado40(acumulado40);
    setCargo11(cargo11);
    setAcumulado41(acumulado41);
    setCargo12(cargo12);
    setAcumulado46(acumulado46);
    setNivel(nivel);
    setCargo13(cargo13);
    setAcumulado47(acumulado47);
    setCargo14(cargo14);
    setAcumulado44(acumulado44);
    setCargo15(cargo15);
    setAcumulado45(acumulado45);
    setCargo50(cargo50);
    setCargo51(cargo51);
    setCargo49(cargo49);
    setCargo41(cargo41);
    setCargo42(cargo42);
    setCargo43(cargo43);
    setCargo44(cargo44);
    setCargo45(cargo45);
    setCargo46(cargo46);
    setCargo47(cargo47);
    setCargo48(cargo48);
    setAbono7(abono7);
    setAbono6(abono6);
    setClave(clave);
    setAbono5(abono5);
    setAbono4(abono4);
    setAbono3(abono3);
    setAbono2(abono2);
    setIdEmpaqueUnidadMedida(idEmpaqueUnidadMedida);
    setAbono1(abono1);
    setCargo40(cargo40);
    setCargo2(cargo2);
    setCargo1(cargo1);
    setAbono9(abono9);
    setAbono8(abono8);
    setCargo8(cargo8);
    setCargo38(cargo38);
    setCargo7(cargo7);
    setCargo39(cargo39);
    setCargo9(cargo9);
    setCargo4(cargo4);
    setCargo3(cargo3);
    setIdUsuario(idUsuario);
    setCargo6(cargo6);
    setIdPlantilla(idPlantilla);
    setCargo5(cargo5);
    setCargo30(cargo30);
    setCargo31(cargo31);
    setCargo32(cargo32);
    setCargo33(cargo33);
    setCargo34(cargo34);
    setCargo35(cargo35);
    setCargo36(cargo36);
    setCargo37(cargo37);
    setDescripcion(descripcion);
    setAcumulado9(acumulado9);
    setAcumulado8(acumulado8);
    setAcumulado7(acumulado7);
    setAcumulado6(acumulado6);
    setAcumulado5(acumulado5);
    setAcumulado4(acumulado4);
    setAbono10(abono10);
    setAcumulado3(acumulado3);
    setAbono11(abono11);
    setAcumulado17(acumulado17);
    setAcumulado2(acumulado2);
    setAbono12(abono12);
    setAcumulado18(acumulado18);
    setAcumulado1(acumulado1);
    setAbono13(abono13);
    setAcumulado15(acumulado15);
    setAbono14(abono14);
    setAcumulado16(acumulado16);
    setAbono15(abono15);
    setAbono16(abono16);
    setAbono17(abono17);
    setAcumulado19(acumulado19);
    setAbono18(abono18);
    setAbono19(abono19);
    setAcumulado10(acumulado10);
    setAcumulado13(acumulado13);
    setAcumulado14(acumulado14);
    setAcumulado11(acumulado11);
    setAcumulado12(acumulado12);
    setCodigo(codigo);
    setCargo52(cargo52);
    setCargo53(cargo53);
    setCargo54(cargo54);
    setCantidad(cantidad);
    setCargo55(cargo55);
    setIdEstacionEstatus(idEstacionEstatus);
    setNombre(nombre);
    setAbono30(abono30);
    setAbono31(abono31);
    setAbono32(abono32);
    setAbono33(abono33);
    setAcumulado39(acumulado39);
    setAbono34(abono34);
    setAbono35(abono35);
    setAcumulado37(acumulado37);
    setAbono36(abono36);
    setAcumulado38(acumulado38);
    setAbono37(abono37);
    setAbono38(abono38);
    setAbono39(abono39);
    setAcumulado31(acumulado31);
    setAcumulado32(acumulado32);
    setTermino(termino);
    setAcumulado30(acumulado30);
    setAcumulado35(acumulado35);
    setAcumulado36(acumulado36);
    setAcumulado33(acumulado33);
    setAcumulado34(acumulado34);
    setCosto(costo);
    setAbono20(abono20);
    setAbono21(abono21);
    setRegistro(LocalDateTime.now());
    setAbono22(abono22);
    setAcumulado28(acumulado28);
    setAbono23(abono23);
    setAcumulado29(acumulado29);
    setAbono24(abono24);
    setAcumulado26(acumulado26);
    setAbono25(abono25);
    setAcumulado27(acumulado27);
    setAbono26(abono26);
    setAbono27(abono27);
    setAbono28(abono28);
    setAbono29(abono29);
    setAcumulado20(acumulado20);
    setAcumulado21(acumulado21);
    setAcumulado24(acumulado24);
    setAcumulado25(acumulado25);
    setAcumulado22(acumulado22);
    setAcumulado23(acumulado23);
  }
	
  public void setInicio(LocalDateTime inicio) {
    this.inicio = inicio;
  }

  public LocalDateTime getInicio() {
    return inicio;
  }

  public void setAbono50(Double abono50) {
    this.abono50 = abono50;
  }

  public Double getAbono50() {
    return abono50;
  }

  public void setAbono51(Double abono51) {
    this.abono51 = abono51;
  }

  public Double getAbono51() {
    return abono51;
  }

  public void setAbono52(Double abono52) {
    this.abono52 = abono52;
  }

  public Double getAbono52() {
    return abono52;
  }

  public void setAbono53(Double abono53) {
    this.abono53 = abono53;
  }

  public Double getAbono53() {
    return abono53;
  }

  public void setAbono54(Double abono54) {
    this.abono54 = abono54;
  }

  public Double getAbono54() {
    return abono54;
  }

  public void setCargo27(Double cargo27) {
    this.cargo27 = cargo27;
  }

  public Double getCargo27() {
    return cargo27;
  }

  public void setAbono55(Double abono55) {
    this.abono55 = abono55;
  }

  public Double getAbono55() {
    return abono55;
  }

  public void setCargo28(Double cargo28) {
    this.cargo28 = cargo28;
  }

  public Double getCargo28() {
    return cargo28;
  }

  public void setCargo29(Double cargo29) {
    this.cargo29 = cargo29;
  }

  public Double getCargo29() {
    return cargo29;
  }

  public void setAcumulado53(Double acumulado53) {
    this.acumulado53 = acumulado53;
  }

  public Double getAcumulado53() {
    return acumulado53;
  }

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacion() {
    return idEstacion;
  }

  public void setCargo20(Double cargo20) {
    this.cargo20 = cargo20;
  }

  public Double getCargo20() {
    return cargo20;
  }

  public void setAcumulado54(Double acumulado54) {
    this.acumulado54 = acumulado54;
  }

  public Double getAcumulado54() {
    return acumulado54;
  }

  public void setCargo21(Double cargo21) {
    this.cargo21 = cargo21;
  }

  public Double getCargo21() {
    return cargo21;
  }

  public void setAcumulado51(Double acumulado51) {
    this.acumulado51 = acumulado51;
  }

  public Double getAcumulado51() {
    return acumulado51;
  }

  public void setCargo22(Double cargo22) {
    this.cargo22 = cargo22;
  }

  public Double getCargo22() {
    return cargo22;
  }

  public void setAcumulado52(Double acumulado52) {
    this.acumulado52 = acumulado52;
  }

  public Double getAcumulado52() {
    return acumulado52;
  }

  public void setCargo23(Double cargo23) {
    this.cargo23 = cargo23;
  }

  public Double getCargo23() {
    return cargo23;
  }

  public void setCargo24(Double cargo24) {
    this.cargo24 = cargo24;
  }

  public Double getCargo24() {
    return cargo24;
  }

  public void setCargo25(Double cargo25) {
    this.cargo25 = cargo25;
  }

  public Double getCargo25() {
    return cargo25;
  }

  public void setAcumulado55(Double acumulado55) {
    this.acumulado55 = acumulado55;
  }

  public Double getAcumulado55() {
    return acumulado55;
  }

  public void setCargo26(Double cargo26) {
    this.cargo26 = cargo26;
  }

  public Double getCargo26() {
    return cargo26;
  }

  public void setUltimo(Long ultimo) {
    this.ultimo = ultimo;
  }

  public Long getUltimo() {
    return ultimo;
  }

  public void setAcumulado50(Double acumulado50) {
    this.acumulado50 = acumulado50;
  }

  public Double getAcumulado50() {
    return acumulado50;
  }

  public void setAbono40(Double abono40) {
    this.abono40 = abono40;
  }

  public Double getAbono40() {
    return abono40;
  }

  public void setAbono41(Double abono41) {
    this.abono41 = abono41;
  }

  public Double getAbono41() {
    return abono41;
  }

  public void setAbono42(Double abono42) {
    this.abono42 = abono42;
  }

  public Double getAbono42() {
    return abono42;
  }

  public void setAbono43(Double abono43) {
    this.abono43 = abono43;
  }

  public Double getAbono43() {
    return abono43;
  }

  public void setCargo16(Double cargo16) {
    this.cargo16 = cargo16;
  }

  public Double getCargo16() {
    return cargo16;
  }

  public void setAbono44(Double abono44) {
    this.abono44 = abono44;
  }

  public Double getAbono44() {
    return abono44;
  }

  public void setCargo17(Double cargo17) {
    this.cargo17 = cargo17;
  }

  public Double getCargo17() {
    return cargo17;
  }

  public void setAbono45(Double abono45) {
    this.abono45 = abono45;
  }

  public Double getAbono45() {
    return abono45;
  }

  public void setCargo18(Double cargo18) {
    this.cargo18 = cargo18;
  }

  public Double getCargo18() {
    return cargo18;
  }

  public void setAbono46(Double abono46) {
    this.abono46 = abono46;
  }

  public Double getAbono46() {
    return abono46;
  }

  public void setAcumulado48(Double acumulado48) {
    this.acumulado48 = acumulado48;
  }

  public Double getAcumulado48() {
    return acumulado48;
  }

  public void setCargo19(Double cargo19) {
    this.cargo19 = cargo19;
  }

  public Double getCargo19() {
    return cargo19;
  }

  public void setAbono47(Double abono47) {
    this.abono47 = abono47;
  }

  public Double getAbono47() {
    return abono47;
  }

  public void setAcumulado49(Double acumulado49) {
    this.acumulado49 = acumulado49;
  }

  public Double getAcumulado49() {
    return acumulado49;
  }

  public void setAbono48(Double abono48) {
    this.abono48 = abono48;
  }

  public Double getAbono48() {
    return abono48;
  }

  public void setAbono49(Double abono49) {
    this.abono49 = abono49;
  }

  public Double getAbono49() {
    return abono49;
  }

  public void setAcumulado42(Double acumulado42) {
    this.acumulado42 = acumulado42;
  }

  public Double getAcumulado42() {
    return acumulado42;
  }

  public void setAcumulado43(Double acumulado43) {
    this.acumulado43 = acumulado43;
  }

  public Double getAcumulado43() {
    return acumulado43;
  }

  public void setCargo10(Double cargo10) {
    this.cargo10 = cargo10;
  }

  public Double getCargo10() {
    return cargo10;
  }

  public void setAcumulado40(Double acumulado40) {
    this.acumulado40 = acumulado40;
  }

  public Double getAcumulado40() {
    return acumulado40;
  }

  public void setCargo11(Double cargo11) {
    this.cargo11 = cargo11;
  }

  public Double getCargo11() {
    return cargo11;
  }

  public void setAcumulado41(Double acumulado41) {
    this.acumulado41 = acumulado41;
  }

  public Double getAcumulado41() {
    return acumulado41;
  }

  public void setCargo12(Double cargo12) {
    this.cargo12 = cargo12;
  }

  public Double getCargo12() {
    return cargo12;
  }

  public void setAcumulado46(Double acumulado46) {
    this.acumulado46 = acumulado46;
  }

  public Double getAcumulado46() {
    return acumulado46;
  }

  public void setNivel(Long nivel) {
    this.nivel = nivel;
  }

  public Long getNivel() {
    return nivel;
  }

  public void setCargo13(Double cargo13) {
    this.cargo13 = cargo13;
  }

  public Double getCargo13() {
    return cargo13;
  }

  public void setAcumulado47(Double acumulado47) {
    this.acumulado47 = acumulado47;
  }

  public Double getAcumulado47() {
    return acumulado47;
  }

  public void setCargo14(Double cargo14) {
    this.cargo14 = cargo14;
  }

  public Double getCargo14() {
    return cargo14;
  }

  public void setAcumulado44(Double acumulado44) {
    this.acumulado44 = acumulado44;
  }

  public Double getAcumulado44() {
    return acumulado44;
  }

  public void setCargo15(Double cargo15) {
    this.cargo15 = cargo15;
  }

  public Double getCargo15() {
    return cargo15;
  }

  public void setAcumulado45(Double acumulado45) {
    this.acumulado45 = acumulado45;
  }

  public Double getAcumulado45() {
    return acumulado45;
  }

  public void setCargo50(Double cargo50) {
    this.cargo50 = cargo50;
  }

  public Double getCargo50() {
    return cargo50;
  }

  public void setCargo51(Double cargo51) {
    this.cargo51 = cargo51;
  }

  public Double getCargo51() {
    return cargo51;
  }

  public void setCargo49(Double cargo49) {
    this.cargo49 = cargo49;
  }

  public Double getCargo49() {
    return cargo49;
  }

  public void setCargo41(Double cargo41) {
    this.cargo41 = cargo41;
  }

  public Double getCargo41() {
    return cargo41;
  }

  public void setCargo42(Double cargo42) {
    this.cargo42 = cargo42;
  }

  public Double getCargo42() {
    return cargo42;
  }

  public void setCargo43(Double cargo43) {
    this.cargo43 = cargo43;
  }

  public Double getCargo43() {
    return cargo43;
  }

  public void setCargo44(Double cargo44) {
    this.cargo44 = cargo44;
  }

  public Double getCargo44() {
    return cargo44;
  }

  public void setCargo45(Double cargo45) {
    this.cargo45 = cargo45;
  }

  public Double getCargo45() {
    return cargo45;
  }

  public void setCargo46(Double cargo46) {
    this.cargo46 = cargo46;
  }

  public Double getCargo46() {
    return cargo46;
  }

  public void setCargo47(Double cargo47) {
    this.cargo47 = cargo47;
  }

  public Double getCargo47() {
    return cargo47;
  }

  public void setCargo48(Double cargo48) {
    this.cargo48 = cargo48;
  }

  public Double getCargo48() {
    return cargo48;
  }

  public void setAbono7(Double abono7) {
    this.abono7 = abono7;
  }

  public Double getAbono7() {
    return abono7;
  }

  public void setAbono6(Double abono6) {
    this.abono6 = abono6;
  }

  public Double getAbono6() {
    return abono6;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setAbono5(Double abono5) {
    this.abono5 = abono5;
  }

  public Double getAbono5() {
    return abono5;
  }

  public void setAbono4(Double abono4) {
    this.abono4 = abono4;
  }

  public Double getAbono4() {
    return abono4;
  }

  public void setAbono3(Double abono3) {
    this.abono3 = abono3;
  }

  public Double getAbono3() {
    return abono3;
  }

  public void setAbono2(Double abono2) {
    this.abono2 = abono2;
  }

  public Double getAbono2() {
    return abono2;
  }

  public void setIdEmpaqueUnidadMedida(Long idEmpaqueUnidadMedida) {
    this.idEmpaqueUnidadMedida = idEmpaqueUnidadMedida;
  }

  public Long getIdEmpaqueUnidadMedida() {
    return idEmpaqueUnidadMedida;
  }

  public void setAbono1(Double abono1) {
    this.abono1 = abono1;
  }

  public Double getAbono1() {
    return abono1;
  }

  public void setCargo40(Double cargo40) {
    this.cargo40 = cargo40;
  }

  public Double getCargo40() {
    return cargo40;
  }

  public void setCargo2(Double cargo2) {
    this.cargo2 = cargo2;
  }

  public Double getCargo2() {
    return cargo2;
  }

  public void setCargo1(Double cargo1) {
    this.cargo1 = cargo1;
  }

  public Double getCargo1() {
    return cargo1;
  }

  public void setAbono9(Double abono9) {
    this.abono9 = abono9;
  }

  public Double getAbono9() {
    return abono9;
  }

  public void setAbono8(Double abono8) {
    this.abono8 = abono8;
  }

  public Double getAbono8() {
    return abono8;
  }

  public void setCargo8(Double cargo8) {
    this.cargo8 = cargo8;
  }

  public Double getCargo8() {
    return cargo8;
  }

  public void setCargo38(Double cargo38) {
    this.cargo38 = cargo38;
  }

  public Double getCargo38() {
    return cargo38;
  }

  public void setCargo7(Double cargo7) {
    this.cargo7 = cargo7;
  }

  public Double getCargo7() {
    return cargo7;
  }

  public void setCargo39(Double cargo39) {
    this.cargo39 = cargo39;
  }

  public Double getCargo39() {
    return cargo39;
  }

  public void setCargo9(Double cargo9) {
    this.cargo9 = cargo9;
  }

  public Double getCargo9() {
    return cargo9;
  }

  public void setCargo4(Double cargo4) {
    this.cargo4 = cargo4;
  }

  public Double getCargo4() {
    return cargo4;
  }

  public void setCargo3(Double cargo3) {
    this.cargo3 = cargo3;
  }

  public Double getCargo3() {
    return cargo3;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setCargo6(Double cargo6) {
    this.cargo6 = cargo6;
  }

  public Double getCargo6() {
    return cargo6;
  }

  public void setIdPlantilla(Long idPlantilla) {
    this.idPlantilla = idPlantilla;
  }

  public Long getIdPlantilla() {
    return idPlantilla;
  }

  public void setCargo5(Double cargo5) {
    this.cargo5 = cargo5;
  }

  public Double getCargo5() {
    return cargo5;
  }

  public void setCargo30(Double cargo30) {
    this.cargo30 = cargo30;
  }

  public Double getCargo30() {
    return cargo30;
  }

  public void setCargo31(Double cargo31) {
    this.cargo31 = cargo31;
  }

  public Double getCargo31() {
    return cargo31;
  }

  public void setCargo32(Double cargo32) {
    this.cargo32 = cargo32;
  }

  public Double getCargo32() {
    return cargo32;
  }

  public void setCargo33(Double cargo33) {
    this.cargo33 = cargo33;
  }

  public Double getCargo33() {
    return cargo33;
  }

  public void setCargo34(Double cargo34) {
    this.cargo34 = cargo34;
  }

  public Double getCargo34() {
    return cargo34;
  }

  public void setCargo35(Double cargo35) {
    this.cargo35 = cargo35;
  }

  public Double getCargo35() {
    return cargo35;
  }

  public void setCargo36(Double cargo36) {
    this.cargo36 = cargo36;
  }

  public Double getCargo36() {
    return cargo36;
  }

  public void setCargo37(Double cargo37) {
    this.cargo37 = cargo37;
  }

  public Double getCargo37() {
    return cargo37;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setAcumulado9(Double acumulado9) {
    this.acumulado9 = acumulado9;
  }

  public Double getAcumulado9() {
    return acumulado9;
  }

  public void setAcumulado8(Double acumulado8) {
    this.acumulado8 = acumulado8;
  }

  public Double getAcumulado8() {
    return acumulado8;
  }

  public void setAcumulado7(Double acumulado7) {
    this.acumulado7 = acumulado7;
  }

  public Double getAcumulado7() {
    return acumulado7;
  }

  public void setAcumulado6(Double acumulado6) {
    this.acumulado6 = acumulado6;
  }

  public Double getAcumulado6() {
    return acumulado6;
  }

  public void setAcumulado5(Double acumulado5) {
    this.acumulado5 = acumulado5;
  }

  public Double getAcumulado5() {
    return acumulado5;
  }

  public void setAcumulado4(Double acumulado4) {
    this.acumulado4 = acumulado4;
  }

  public Double getAcumulado4() {
    return acumulado4;
  }

  public void setAbono10(Double abono10) {
    this.abono10 = abono10;
  }

  public Double getAbono10() {
    return abono10;
  }

  public void setAcumulado3(Double acumulado3) {
    this.acumulado3 = acumulado3;
  }

  public Double getAcumulado3() {
    return acumulado3;
  }

  public void setAbono11(Double abono11) {
    this.abono11 = abono11;
  }

  public Double getAbono11() {
    return abono11;
  }

  public void setAcumulado17(Double acumulado17) {
    this.acumulado17 = acumulado17;
  }

  public Double getAcumulado17() {
    return acumulado17;
  }

  public void setAcumulado2(Double acumulado2) {
    this.acumulado2 = acumulado2;
  }

  public Double getAcumulado2() {
    return acumulado2;
  }

  public void setAbono12(Double abono12) {
    this.abono12 = abono12;
  }

  public Double getAbono12() {
    return abono12;
  }

  public void setAcumulado18(Double acumulado18) {
    this.acumulado18 = acumulado18;
  }

  public Double getAcumulado18() {
    return acumulado18;
  }

  public void setAcumulado1(Double acumulado1) {
    this.acumulado1 = acumulado1;
  }

  public Double getAcumulado1() {
    return acumulado1;
  }

  public void setAbono13(Double abono13) {
    this.abono13 = abono13;
  }

  public Double getAbono13() {
    return abono13;
  }

  public void setAcumulado15(Double acumulado15) {
    this.acumulado15 = acumulado15;
  }

  public Double getAcumulado15() {
    return acumulado15;
  }

  public void setAbono14(Double abono14) {
    this.abono14 = abono14;
  }

  public Double getAbono14() {
    return abono14;
  }

  public void setAcumulado16(Double acumulado16) {
    this.acumulado16 = acumulado16;
  }

  public Double getAcumulado16() {
    return acumulado16;
  }

  public void setAbono15(Double abono15) {
    this.abono15 = abono15;
  }

  public Double getAbono15() {
    return abono15;
  }

  public void setAbono16(Double abono16) {
    this.abono16 = abono16;
  }

  public Double getAbono16() {
    return abono16;
  }

  public void setAbono17(Double abono17) {
    this.abono17 = abono17;
  }

  public Double getAbono17() {
    return abono17;
  }

  public void setAcumulado19(Double acumulado19) {
    this.acumulado19 = acumulado19;
  }

  public Double getAcumulado19() {
    return acumulado19;
  }

  public void setAbono18(Double abono18) {
    this.abono18 = abono18;
  }

  public Double getAbono18() {
    return abono18;
  }

  public void setAbono19(Double abono19) {
    this.abono19 = abono19;
  }

  public Double getAbono19() {
    return abono19;
  }

  public void setAcumulado10(Double acumulado10) {
    this.acumulado10 = acumulado10;
  }

  public Double getAcumulado10() {
    return acumulado10;
  }

  public void setAcumulado13(Double acumulado13) {
    this.acumulado13 = acumulado13;
  }

  public Double getAcumulado13() {
    return acumulado13;
  }

  public void setAcumulado14(Double acumulado14) {
    this.acumulado14 = acumulado14;
  }

  public Double getAcumulado14() {
    return acumulado14;
  }

  public void setAcumulado11(Double acumulado11) {
    this.acumulado11 = acumulado11;
  }

  public Double getAcumulado11() {
    return acumulado11;
  }

  public void setAcumulado12(Double acumulado12) {
    this.acumulado12 = acumulado12;
  }

  public Double getAcumulado12() {
    return acumulado12;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCargo52(Double cargo52) {
    this.cargo52 = cargo52;
  }

  public Double getCargo52() {
    return cargo52;
  }

  public void setCargo53(Double cargo53) {
    this.cargo53 = cargo53;
  }

  public Double getCargo53() {
    return cargo53;
  }

  public void setCargo54(Double cargo54) {
    this.cargo54 = cargo54;
  }

  public Double getCargo54() {
    return cargo54;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setCargo55(Double cargo55) {
    this.cargo55 = cargo55;
  }

  public Double getCargo55() {
    return cargo55;
  }

  public void setIdEstacionEstatus(Long idEstacionEstatus) {
    this.idEstacionEstatus = idEstacionEstatus;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setAbono30(Double abono30) {
    this.abono30 = abono30;
  }

  public Double getAbono30() {
    return abono30;
  }

  public void setAbono31(Double abono31) {
    this.abono31 = abono31;
  }

  public Double getAbono31() {
    return abono31;
  }

  public void setAbono32(Double abono32) {
    this.abono32 = abono32;
  }

  public Double getAbono32() {
    return abono32;
  }

  public void setAbono33(Double abono33) {
    this.abono33 = abono33;
  }

  public Double getAbono33() {
    return abono33;
  }

  public void setAcumulado39(Double acumulado39) {
    this.acumulado39 = acumulado39;
  }

  public Double getAcumulado39() {
    return acumulado39;
  }

  public void setAbono34(Double abono34) {
    this.abono34 = abono34;
  }

  public Double getAbono34() {
    return abono34;
  }

  public void setAbono35(Double abono35) {
    this.abono35 = abono35;
  }

  public Double getAbono35() {
    return abono35;
  }

  public void setAcumulado37(Double acumulado37) {
    this.acumulado37 = acumulado37;
  }

  public Double getAcumulado37() {
    return acumulado37;
  }

  public void setAbono36(Double abono36) {
    this.abono36 = abono36;
  }

  public Double getAbono36() {
    return abono36;
  }

  public void setAcumulado38(Double acumulado38) {
    this.acumulado38 = acumulado38;
  }

  public Double getAcumulado38() {
    return acumulado38;
  }

  public void setAbono37(Double abono37) {
    this.abono37 = abono37;
  }

  public Double getAbono37() {
    return abono37;
  }

  public void setAbono38(Double abono38) {
    this.abono38 = abono38;
  }

  public Double getAbono38() {
    return abono38;
  }

  public void setAbono39(Double abono39) {
    this.abono39 = abono39;
  }

  public Double getAbono39() {
    return abono39;
  }

  public void setAcumulado31(Double acumulado31) {
    this.acumulado31 = acumulado31;
  }

  public Double getAcumulado31() {
    return acumulado31;
  }

  public void setAcumulado32(Double acumulado32) {
    this.acumulado32 = acumulado32;
  }

  public Double getAcumulado32() {
    return acumulado32;
  }

  public void setTermino(LocalDateTime termino) {
    this.termino = termino;
  }

  public LocalDateTime getTermino() {
    return termino;
  }

  public void setAcumulado30(Double acumulado30) {
    this.acumulado30 = acumulado30;
  }

  public Double getAcumulado30() {
    return acumulado30;
  }

  public void setAcumulado35(Double acumulado35) {
    this.acumulado35 = acumulado35;
  }

  public Double getAcumulado35() {
    return acumulado35;
  }

  public void setAcumulado36(Double acumulado36) {
    this.acumulado36 = acumulado36;
  }

  public Double getAcumulado36() {
    return acumulado36;
  }

  public void setAcumulado33(Double acumulado33) {
    this.acumulado33 = acumulado33;
  }

  public Double getAcumulado33() {
    return acumulado33;
  }

  public void setAcumulado34(Double acumulado34) {
    this.acumulado34 = acumulado34;
  }

  public Double getAcumulado34() {
    return acumulado34;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setAbono20(Double abono20) {
    this.abono20 = abono20;
  }

  public Double getAbono20() {
    return abono20;
  }

  public void setAbono21(Double abono21) {
    this.abono21 = abono21;
  }

  public Double getAbono21() {
    return abono21;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setAbono22(Double abono22) {
    this.abono22 = abono22;
  }

  public Double getAbono22() {
    return abono22;
  }

  public void setAcumulado28(Double acumulado28) {
    this.acumulado28 = acumulado28;
  }

  public Double getAcumulado28() {
    return acumulado28;
  }

  public void setAbono23(Double abono23) {
    this.abono23 = abono23;
  }

  public Double getAbono23() {
    return abono23;
  }

  public void setAcumulado29(Double acumulado29) {
    this.acumulado29 = acumulado29;
  }

  public Double getAcumulado29() {
    return acumulado29;
  }

  public void setAbono24(Double abono24) {
    this.abono24 = abono24;
  }

  public Double getAbono24() {
    return abono24;
  }

  public void setAcumulado26(Double acumulado26) {
    this.acumulado26 = acumulado26;
  }

  public Double getAcumulado26() {
    return acumulado26;
  }

  public void setAbono25(Double abono25) {
    this.abono25 = abono25;
  }

  public Double getAbono25() {
    return abono25;
  }

  public void setAcumulado27(Double acumulado27) {
    this.acumulado27 = acumulado27;
  }

  public Double getAcumulado27() {
    return acumulado27;
  }

  public void setAbono26(Double abono26) {
    this.abono26 = abono26;
  }

  public Double getAbono26() {
    return abono26;
  }

  public void setAbono27(Double abono27) {
    this.abono27 = abono27;
  }

  public Double getAbono27() {
    return abono27;
  }

  public void setAbono28(Double abono28) {
    this.abono28 = abono28;
  }

  public Double getAbono28() {
    return abono28;
  }

  public void setAbono29(Double abono29) {
    this.abono29 = abono29;
  }

  public Double getAbono29() {
    return abono29;
  }

  public void setAcumulado20(Double acumulado20) {
    this.acumulado20 = acumulado20;
  }

  public Double getAcumulado20() {
    return acumulado20;
  }

  public void setAcumulado21(Double acumulado21) {
    this.acumulado21 = acumulado21;
  }

  public Double getAcumulado21() {
    return acumulado21;
  }

  public void setAcumulado24(Double acumulado24) {
    this.acumulado24 = acumulado24;
  }

  public Double getAcumulado24() {
    return acumulado24;
  }

  public void setAcumulado25(Double acumulado25) {
    this.acumulado25 = acumulado25;
  }

  public Double getAcumulado25() {
    return acumulado25;
  }

  public void setAcumulado22(Double acumulado22) {
    this.acumulado22 = acumulado22;
  }

  public Double getAcumulado22() {
    return acumulado22;
  }

  public void setAcumulado23(Double acumulado23) {
    this.acumulado23 = acumulado23;
  }

  public Double getAcumulado23() {
    return acumulado23;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdEstacion();
  }

  @Override
  public void setKey(Long key) {
  	this.idEstacion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono50());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono51());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono52());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono53());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono54());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo27());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono55());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo28());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo29());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado53());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo20());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado54());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo21());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado51());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo22());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado52());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo23());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo24());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo25());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado55());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo26());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUltimo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado50());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono40());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono41());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono42());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono43());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo16());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono44());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo17());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono45());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo18());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono46());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado48());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo19());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono47());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado49());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono48());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono49());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado42());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado43());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo10());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado40());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo11());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado41());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo12());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado46());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNivel());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo13());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado47());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo14());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado44());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo15());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado45());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo50());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo51());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo49());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo41());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo42());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo43());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo44());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo45());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo46());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo47());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo48());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono7());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono6());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono5());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono4());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono3());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono2());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpaqueUnidadMedida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono1());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo40());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo2());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo1());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono9());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono8());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo8());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo38());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo7());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo39());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo9());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo4());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo3());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo6());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPlantilla());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo5());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo30());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo31());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo32());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo33());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo34());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo35());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo36());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo37());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado9());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado8());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado7());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado6());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado5());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado4());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono10());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado3());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono11());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado17());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado2());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono12());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado18());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado1());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono13());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado15());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono14());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado16());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono15());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono16());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono17());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado19());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono18());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono19());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado10());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado13());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado14());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado11());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado12());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo52());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo53());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo54());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCargo55());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono30());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono31());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono32());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono33());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado39());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono34());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono35());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado37());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono36());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado38());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono37());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono38());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono39());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado31());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado32());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado30());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado35());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado36());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado33());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado34());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono20());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono21());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono22());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado28());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono23());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado29());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono24());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado26());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono25());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado27());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono26());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono27());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono28());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono29());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado20());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado21());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado24());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado25());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado22());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAcumulado23());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("inicio", getInicio());
		regresar.put("abono50", getAbono50());
		regresar.put("abono51", getAbono51());
		regresar.put("abono52", getAbono52());
		regresar.put("abono53", getAbono53());
		regresar.put("abono54", getAbono54());
		regresar.put("cargo27", getCargo27());
		regresar.put("abono55", getAbono55());
		regresar.put("cargo28", getCargo28());
		regresar.put("cargo29", getCargo29());
		regresar.put("acumulado53", getAcumulado53());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("cargo20", getCargo20());
		regresar.put("acumulado54", getAcumulado54());
		regresar.put("cargo21", getCargo21());
		regresar.put("acumulado51", getAcumulado51());
		regresar.put("cargo22", getCargo22());
		regresar.put("acumulado52", getAcumulado52());
		regresar.put("cargo23", getCargo23());
		regresar.put("cargo24", getCargo24());
		regresar.put("cargo25", getCargo25());
		regresar.put("acumulado55", getAcumulado55());
		regresar.put("cargo26", getCargo26());
		regresar.put("ultimo", getUltimo());
		regresar.put("acumulado50", getAcumulado50());
		regresar.put("abono40", getAbono40());
		regresar.put("abono41", getAbono41());
		regresar.put("abono42", getAbono42());
		regresar.put("abono43", getAbono43());
		regresar.put("cargo16", getCargo16());
		regresar.put("abono44", getAbono44());
		regresar.put("cargo17", getCargo17());
		regresar.put("abono45", getAbono45());
		regresar.put("cargo18", getCargo18());
		regresar.put("abono46", getAbono46());
		regresar.put("acumulado48", getAcumulado48());
		regresar.put("cargo19", getCargo19());
		regresar.put("abono47", getAbono47());
		regresar.put("acumulado49", getAcumulado49());
		regresar.put("abono48", getAbono48());
		regresar.put("abono49", getAbono49());
		regresar.put("acumulado42", getAcumulado42());
		regresar.put("acumulado43", getAcumulado43());
		regresar.put("cargo10", getCargo10());
		regresar.put("acumulado40", getAcumulado40());
		regresar.put("cargo11", getCargo11());
		regresar.put("acumulado41", getAcumulado41());
		regresar.put("cargo12", getCargo12());
		regresar.put("acumulado46", getAcumulado46());
		regresar.put("nivel", getNivel());
		regresar.put("cargo13", getCargo13());
		regresar.put("acumulado47", getAcumulado47());
		regresar.put("cargo14", getCargo14());
		regresar.put("acumulado44", getAcumulado44());
		regresar.put("cargo15", getCargo15());
		regresar.put("acumulado45", getAcumulado45());
		regresar.put("cargo50", getCargo50());
		regresar.put("cargo51", getCargo51());
		regresar.put("cargo49", getCargo49());
		regresar.put("cargo41", getCargo41());
		regresar.put("cargo42", getCargo42());
		regresar.put("cargo43", getCargo43());
		regresar.put("cargo44", getCargo44());
		regresar.put("cargo45", getCargo45());
		regresar.put("cargo46", getCargo46());
		regresar.put("cargo47", getCargo47());
		regresar.put("cargo48", getCargo48());
		regresar.put("abono7", getAbono7());
		regresar.put("abono6", getAbono6());
		regresar.put("clave", getClave());
		regresar.put("abono5", getAbono5());
		regresar.put("abono4", getAbono4());
		regresar.put("abono3", getAbono3());
		regresar.put("abono2", getAbono2());
		regresar.put("idEmpaqueUnidadMedida", getIdEmpaqueUnidadMedida());
		regresar.put("abono1", getAbono1());
		regresar.put("cargo40", getCargo40());
		regresar.put("cargo2", getCargo2());
		regresar.put("cargo1", getCargo1());
		regresar.put("abono9", getAbono9());
		regresar.put("abono8", getAbono8());
		regresar.put("cargo8", getCargo8());
		regresar.put("cargo38", getCargo38());
		regresar.put("cargo7", getCargo7());
		regresar.put("cargo39", getCargo39());
		regresar.put("cargo9", getCargo9());
		regresar.put("cargo4", getCargo4());
		regresar.put("cargo3", getCargo3());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("cargo6", getCargo6());
		regresar.put("idPlantilla", getIdPlantilla());
		regresar.put("cargo5", getCargo5());
		regresar.put("cargo30", getCargo30());
		regresar.put("cargo31", getCargo31());
		regresar.put("cargo32", getCargo32());
		regresar.put("cargo33", getCargo33());
		regresar.put("cargo34", getCargo34());
		regresar.put("cargo35", getCargo35());
		regresar.put("cargo36", getCargo36());
		regresar.put("cargo37", getCargo37());
		regresar.put("descripcion", getDescripcion());
		regresar.put("acumulado9", getAcumulado9());
		regresar.put("acumulado8", getAcumulado8());
		regresar.put("acumulado7", getAcumulado7());
		regresar.put("acumulado6", getAcumulado6());
		regresar.put("acumulado5", getAcumulado5());
		regresar.put("acumulado4", getAcumulado4());
		regresar.put("abono10", getAbono10());
		regresar.put("acumulado3", getAcumulado3());
		regresar.put("abono11", getAbono11());
		regresar.put("acumulado17", getAcumulado17());
		regresar.put("acumulado2", getAcumulado2());
		regresar.put("abono12", getAbono12());
		regresar.put("acumulado18", getAcumulado18());
		regresar.put("acumulado1", getAcumulado1());
		regresar.put("abono13", getAbono13());
		regresar.put("acumulado15", getAcumulado15());
		regresar.put("abono14", getAbono14());
		regresar.put("acumulado16", getAcumulado16());
		regresar.put("abono15", getAbono15());
		regresar.put("abono16", getAbono16());
		regresar.put("abono17", getAbono17());
		regresar.put("acumulado19", getAcumulado19());
		regresar.put("abono18", getAbono18());
		regresar.put("abono19", getAbono19());
		regresar.put("acumulado10", getAcumulado10());
		regresar.put("acumulado13", getAcumulado13());
		regresar.put("acumulado14", getAcumulado14());
		regresar.put("acumulado11", getAcumulado11());
		regresar.put("acumulado12", getAcumulado12());
		regresar.put("codigo", getCodigo());
		regresar.put("cargo52", getCargo52());
		regresar.put("cargo53", getCargo53());
		regresar.put("cargo54", getCargo54());
		regresar.put("cantidad", getCantidad());
		regresar.put("cargo55", getCargo55());
		regresar.put("idEstacionEstatus", getIdEstacionEstatus());
		regresar.put("nombre", getNombre());
		regresar.put("abono30", getAbono30());
		regresar.put("abono31", getAbono31());
		regresar.put("abono32", getAbono32());
		regresar.put("abono33", getAbono33());
		regresar.put("acumulado39", getAcumulado39());
		regresar.put("abono34", getAbono34());
		regresar.put("abono35", getAbono35());
		regresar.put("acumulado37", getAcumulado37());
		regresar.put("abono36", getAbono36());
		regresar.put("acumulado38", getAcumulado38());
		regresar.put("abono37", getAbono37());
		regresar.put("abono38", getAbono38());
		regresar.put("abono39", getAbono39());
		regresar.put("acumulado31", getAcumulado31());
		regresar.put("acumulado32", getAcumulado32());
		regresar.put("termino", getTermino());
		regresar.put("acumulado30", getAcumulado30());
		regresar.put("acumulado35", getAcumulado35());
		regresar.put("acumulado36", getAcumulado36());
		regresar.put("acumulado33", getAcumulado33());
		regresar.put("acumulado34", getAcumulado34());
		regresar.put("costo", getCosto());
		regresar.put("abono20", getAbono20());
		regresar.put("abono21", getAbono21());
		regresar.put("registro", getRegistro());
		regresar.put("abono22", getAbono22());
		regresar.put("acumulado28", getAcumulado28());
		regresar.put("abono23", getAbono23());
		regresar.put("acumulado29", getAcumulado29());
		regresar.put("abono24", getAbono24());
		regresar.put("acumulado26", getAcumulado26());
		regresar.put("abono25", getAbono25());
		regresar.put("acumulado27", getAcumulado27());
		regresar.put("abono26", getAbono26());
		regresar.put("abono27", getAbono27());
		regresar.put("abono28", getAbono28());
		regresar.put("abono29", getAbono29());
		regresar.put("acumulado20", getAcumulado20());
		regresar.put("acumulado21", getAcumulado21());
		regresar.put("acumulado24", getAcumulado24());
		regresar.put("acumulado25", getAcumulado25());
		regresar.put("acumulado22", getAcumulado22());
		regresar.put("acumulado23", getAcumulado23());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getInicio(), getAbono50(), getAbono51(), getAbono52(), getAbono53(), getAbono54(), getCargo27(), getAbono55(), getCargo28(), getCargo29(), getAcumulado53(), getIdEstacion(), getCargo20(), getAcumulado54(), getCargo21(), getAcumulado51(), getCargo22(), getAcumulado52(), getCargo23(), getCargo24(), getCargo25(), getAcumulado55(), getCargo26(), getUltimo(), getAcumulado50(), getAbono40(), getAbono41(), getAbono42(), getAbono43(), getCargo16(), getAbono44(), getCargo17(), getAbono45(), getCargo18(), getAbono46(), getAcumulado48(), getCargo19(), getAbono47(), getAcumulado49(), getAbono48(), getAbono49(), getAcumulado42(), getAcumulado43(), getCargo10(), getAcumulado40(), getCargo11(), getAcumulado41(), getCargo12(), getAcumulado46(), getNivel(), getCargo13(), getAcumulado47(), getCargo14(), getAcumulado44(), getCargo15(), getAcumulado45(), getCargo50(), getCargo51(), getCargo49(), getCargo41(), getCargo42(), getCargo43(), getCargo44(), getCargo45(), getCargo46(), getCargo47(), getCargo48(), getAbono7(), getAbono6(), getClave(), getAbono5(), getAbono4(), getAbono3(), getAbono2(), getIdEmpaqueUnidadMedida(), getAbono1(), getCargo40(), getCargo2(), getCargo1(), getAbono9(), getAbono8(), getCargo8(), getCargo38(), getCargo7(), getCargo39(), getCargo9(), getCargo4(), getCargo3(), getIdUsuario(), getCargo6(), getIdPlantilla(), getCargo5(), getCargo30(), getCargo31(), getCargo32(), getCargo33(), getCargo34(), getCargo35(), getCargo36(), getCargo37(), getDescripcion(), getAcumulado9(), getAcumulado8(), getAcumulado7(), getAcumulado6(), getAcumulado5(), getAcumulado4(), getAbono10(), getAcumulado3(), getAbono11(), getAcumulado17(), getAcumulado2(), getAbono12(), getAcumulado18(), getAcumulado1(), getAbono13(), getAcumulado15(), getAbono14(), getAcumulado16(), getAbono15(), getAbono16(), getAbono17(), getAcumulado19(), getAbono18(), getAbono19(), getAcumulado10(), getAcumulado13(), getAcumulado14(), getAcumulado11(), getAcumulado12(), getCodigo(), getCargo52(), getCargo53(), getCargo54(), getCantidad(), getCargo55(), getIdEstacionEstatus(), getNombre(), getAbono30(), getAbono31(), getAbono32(), getAbono33(), getAcumulado39(), getAbono34(), getAbono35(), getAcumulado37(), getAbono36(), getAcumulado38(), getAbono37(), getAbono38(), getAbono39(), getAcumulado31(), getAcumulado32(), getTermino(), getAcumulado30(), getAcumulado35(), getAcumulado36(), getAcumulado33(), getAcumulado34(), getCosto(), getAbono20(), getAbono21(), getRegistro(), getAbono22(), getAcumulado28(), getAbono23(), getAcumulado29(), getAbono24(), getAcumulado26(), getAbono25(), getAcumulado27(), getAbono26(), getAbono27(), getAbono28(), getAbono29(), getAcumulado20(), getAcumulado21(), getAcumulado24(), getAcumulado25(), getAcumulado22(), getAcumulado23()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idEstacion~");
    regresar.append(getIdEstacion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEstacion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEstacionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEstacion()!= null && getIdEstacion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEstacionesDto other = (TcKeetEstacionesDto) obj;
    if (getIdEstacion() != other.idEstacion && (getIdEstacion() == null || !getIdEstacion().equals(other.idEstacion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstacion() != null ? getIdEstacion().hashCode() : 0);
    return hash;
  }

}


