/*
 * Clase: Cadena.java
 * Creado: 21 de mayo de 2007, 12:16 AM
 * Write by: team.developer@gmail.com
 */
package mx.org.kaana.libs.formato;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.List;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Fecha {
	
	private static final Log LOG=LogFactory.getLog(Fecha.class);
	
  public static final int FECHA_NOMBRE_MES = 1;
  public static final int FECHA_CORTA = 2;
  public static final int FECHA_NOMBRE_DIA = 3;
  public static final int FECHA_EXTENDIDA = 4;
  public static final int FECHA_MINIMA = 5;
  public static final int FECHA_LARGA = 6;
  public static final int FECHA_HORA = 7;
  public static final int FECHA_HORA_CORTA= 23;
  public static final int HORA_CORTA = 8;
  public static final int HORA_LARGA = 9;
  public static final int FECHA_HORA_EXTENDIDA = 10;
  public static final int FECHA_ESTANDAR = 11;
  public static final int FECHA_MYSQL = 14;
  public static final int HORA_MYSQL = 15;
  public static final int FECHA_HORA_MYSQL = 16;
  public static final int FECHA_ORACLE = 17;
  public static final int HORA_ORACLE = 18;
  public static final int FECHA_HORA_ORACLE = 19;
  public static final int FECHA_HORA_LARGA = 20;
  public static final int DIA_FECHA_HORA = 21;
  public static final int DIA_FECHA_HORA_CORTA = 22;
  public static final int FECHA_NOMBRE_MES_CORTO = 24;
  public static final int DIA_FECHA = 25;
  public static final int DIA_CORTO_FECHA = 26;
  public static final int DIA_FECHA_CORTA = 27;
  public static final int CODIGO_SEGURIDAD = 28;
  

	protected Fecha (){
	}
	
  public static String getRegistro() {
    Calendar fecha = Calendar.getInstance();
    return formatear("yyyyMMddHHmmssS", fecha.getTime());
  } // getPatron

  public static Date getRegistroTypeDate() {
    Calendar fecha = Calendar.getInstance();
    return fecha.getTime();
  }

  public static String getNombreDiaCorto(int dia) {
    String nombreDia[] = { "DO", "LU", "MA", "MI", "JU", "VI", "SA" };
    return nombreDia[dia - 1];
  } // getNombreDiaCorto

  public static String getNombreMes(int mes) {
    String[] nombreMes =
    { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
      "Diciembre" };
    return nombreMes[mes];
  } // getNombreMes

  public static String getNombreMesCorto(int mes) {
    String[] nombreMes = { "ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC" };
    return nombreMes[mes];
  } // getNombreMesCorto

  public static int getNumeroMesCorto(String mes) {
    String[] nombreMes = { "ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC" };
    return  Arrays.asList(nombreMes).indexOf(mes.substring(0,3).toUpperCase())+1;
  } // getNumeroMes

  public static int getNumeroMesLargo(String mes) {
    String[] nombreMes =
    { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" };
    List<String> meses= Arrays.asList(nombreMes);
    return meses.indexOf(Cadena.letraCapital(mes))+ 1;
  } 
  
  public static String getNombreDia(int dia) {
    String nombreDia[] = { "Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado" };
    return nombreDia[dia - 1];
  } // getNombreDia

  public static String getNombreDiaAcronimo(int dia) {
    String nombreDia[] = { "DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB" };
    return nombreDia[dia - 1];
  } // getNombreDia

  public static String formatear(int patron, String fecha) {
    if (fecha != null && fecha.length() > 0) {
      int anio = Integer.parseInt(fecha.substring(0, 4));
      int mes = Integer.parseInt(fecha.substring(4, 6)) - 1;
      int dia = Integer.parseInt(fecha.substring(6, 8));
      GregorianCalendar calendario = new GregorianCalendar(anio, mes, dia);
      switch (patron) {
      case FECHA_NOMBRE_MES_CORTO: // Fecha en dd/mes/yyyy   26/Nov/2003
        fecha = calendario.get(calendario.DATE) + "/" + getNombreMesCorto(calendario.get(calendario.MONTH)) + "/" + fecha.substring(0,4);
        break;
      case FECHA_NOMBRE_MES: // Fecha en dd/mes/yyyy   26/Noviembre/2003
        fecha = calendario.get(calendario.DATE) + "/" + getNombreMes(calendario.get(calendario.MONTH)) + "/" + fecha.substring(0,4);
        break;
      case FECHA_CORTA: // Fecha en dd/mm/yyyy    26/11/2003
        fecha = fecha.substring(6, 8) + "/" + fecha.substring(4, 6) + "/" + fecha.substring(0, 4);
        break;
      case FECHA_NOMBRE_DIA: // Fecha en:  nombre del dia, dd/mm/yyyy    Miercoles, 26/11/2003
        fecha =
            getNombreDia(calendario.get(calendario.DAY_OF_WEEK)) + ", " + fecha.substring(6, 8) + "/" + fecha.substring(4, 6) +
            "/" + fecha.substring(0, 4);
        break;
      case FECHA_EXTENDIDA: // Fecha en:  nombre del dia, dia mes anio   Miercoles, 26 de Noviembre del 2003
        fecha =
            getNombreDia(calendario.get(calendario.DAY_OF_WEEK)) + ", " + calendario.get(calendario.DATE) + " de " + getNombreMes(calendario.get(calendario.MONTH)) +
            " de " + calendario.get(calendario.YEAR);
        break;
      case FECHA_MINIMA: // Fecha en dd/mm/yy   26/11/03
        fecha = fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(fecha.substring(2, 4));
        break;
      case FECHA_LARGA: // Fecha en:  dia mes anio  26 de Noviembre del 2003
        fecha =
            calendario.get(calendario.DATE) + " de " + getNombreMes(calendario.get(calendario.MONTH)) + " de " + calendario.get(calendario.YEAR);
        break;
      case FECHA_HORA: // Fecha en dd/mmm/yyyy hh:mm:ss  03/12/2007 12:26:00
        fecha =
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(
            fecha.substring(0, 4)).concat(" ").concat(fecha.substring(8,10)).concat(":").concat(
            fecha.substring(10,12)).concat(":").concat(fecha.substring(12,14));
        break;
      case FECHA_HORA_CORTA: // Fecha en dd/mmm/yyyy hh:mm:ss  03/12/2007 12:26
        fecha =
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(
            fecha.substring(0, 4)).concat(" ").concat(fecha.substring(8,10)).concat(":").concat(
            fecha.substring(10,12));
        break;
      case HORA_CORTA: // Fecha en HH:mm:ss  12:26
        fecha = fecha.substring(8, 10).concat(":").concat(fecha.substring(10, 12));
        break;
      case HORA_LARGA: // Fecha en HH:mm:ss  12:26:00
        fecha =
            fecha.substring(8, 10).concat(":").concat(fecha.substring(10, 12)).concat(":").concat(fecha.substring(12, 14));
        break;
      case FECHA_HORA_LARGA: // Fecha en yyyyMMddhhmmss 20071203241600
        fecha = fecha.substring(0, 14);
        break;
      case FECHA_ESTANDAR: // Fecha en yyyyMMdd 20071203
        fecha = fecha.substring(0, 8);
        break;
      case FECHA_MYSQL: // Fecha en yyyy-MM-ddd  2008-08-18
        fecha = fecha.substring(0, 4).concat("-").concat(fecha.substring(4, 6)).concat("-").concat(fecha.substring(6, 8));
        break;
      case HORA_MYSQL: // Fecha en hh:mm:ss 02:06:30
        fecha =
            fecha.substring(8, 10).concat(":").concat(fecha.substring(10, 12)).concat(":").concat(fecha.substring(12, 14));
        break;
      case FECHA_HORA_MYSQL: // Fecha en yyyy-MM-ddd hh:mm:ss 2008-08-18 02:06:30
        fecha =
            fecha.substring(0, 4).concat("-").concat(fecha.substring(4, 6)).concat("-").concat(
            fecha.substring(6, 8)).concat(" ").concat(fecha.substring(8,10)).concat(":").concat(
            fecha.substring(10,12)).concat(":").concat(fecha.substring(12,14));
        break;
      case FECHA_ORACLE: // Fecha en yyyy-MM-ddd  2008-08-18
        fecha = fecha.substring(0, 4).concat("-").concat(fecha.substring(4, 6)).concat("-").concat(fecha.substring(6, 8));
        break;
      case HORA_ORACLE: // Fecha en hh:mm:ss 02:06:30
        fecha =
            fecha.substring(8, 10).concat(":").concat(fecha.substring(10, 12)).concat(":").concat(fecha.substring(12, 14));
        break;
      case FECHA_HORA_ORACLE: // Fecha en yyyy-MM-ddd hh:mm:ss 2008-08-18 02:06:30
        fecha =
            fecha.substring(0, 4).concat("-").concat(fecha.substring(4, 6)).concat("-").concat(
            fecha.substring(6, 8)).concat(" ").concat(fecha.substring(8,10)).concat(":").concat(
            fecha.substring(10,12)).concat(":").concat(fecha.substring(12,14));
        break;
      case FECHA_HORA_EXTENDIDA:
        fecha = getNombreDia(calendario.get(calendario.DAY_OF_WEEK))  + ", " + calendario.get(calendario.DATE) + " de " + getNombreMes(calendario.get(calendario.MONTH)) +
                " de " + calendario.get(calendario.YEAR)+" "+fecha.substring(8, 10).concat(":").concat(fecha.substring(10, 12)).concat(":").concat(fecha.substring(12, 14));

        break;
      case DIA_FECHA: // Fecha en Dia, dd/mmm/yyyy Miercoles, 03/12/2007
        fecha =
					  getNombreDia(calendario.get(calendario.DAY_OF_WEEK))  + ", " +
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(
            fecha.substring(0, 4));
        break;
      case DIA_CORTO_FECHA: // Fecha en Dia, dd/mmm/yyyy MIE, 03/12/2007
        fecha =
					  getNombreDiaAcronimo(calendario.get(calendario.DAY_OF_WEEK))  + ", " +
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(
            fecha.substring(0, 4));
        break;
      case DIA_FECHA_HORA: // Fecha en Dia, dd/mmm/yyyy hh:mm:ss  Miercoles, 03/12/2007 12:26:00
        fecha =
					  getNombreDia(calendario.get(calendario.DAY_OF_WEEK))  + ", " +
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(
            fecha.substring(0, 4)).concat(" ").concat(fecha.substring(8,10)).concat(":").concat(
            fecha.substring(10,12)).concat(":").concat(fecha.substring(12,14));
        break;
      case DIA_FECHA_HORA_CORTA: // Fecha en Dia, dd/mmm/yyyy hh:mm:ss Miercoles, 03/12/2007 12:26
        fecha =
					  getNombreDia(calendario.get(calendario.DAY_OF_WEEK))  + ", " +
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(
            fecha.substring(0, 4)).concat(" ").concat(fecha.substring(8,10)).concat(":").concat(
            fecha.substring(10,12));
        break;
      case DIA_FECHA_CORTA: // Fecha en Dia, dd/mmm/yyyy Miercoles, 03/12/2007
        fecha =
					  getNombreDia(calendario.get(calendario.DAY_OF_WEEK))+ ", "+
            fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(fecha.substring(0, 4));
        break;
      case CODIGO_SEGURIDAD: // ddhhmmss  03122633 202109121448580
        fecha = fecha.substring(6, 8).concat(fecha.substring(8,10)).concat(fecha.substring(10,12)).concat(fecha.substring(12,14));
        break;
      } // switch
    }
    else
      fecha = "";
    return fecha;
  } // formatear

  public static String formatear(String patron, Date fecha) {
    SimpleDateFormat formato = new SimpleDateFormat(patron);
    return formato.format(fecha);
  } // formatear

  public static String formatear(int patron, LocalDateTime fecha) {
		DateTimeFormatter pattern= DateTimeFormatter.ofPattern("yyyyMMddHHmmssS");
    return formatear(patron, pattern.format(fecha));
  } // formatear

  public static String formatear(int patron, LocalDate fecha) {
		DateTimeFormatter pattern= DateTimeFormatter.ofPattern("yyyyMMdd0000000");
    return formatear(patron, pattern.format(fecha));
  } // formatear

  public static String formatear(int patron, LocalTime fecha) {
		DateTimeFormatter pattern= DateTimeFormatter.ofPattern("20200101HHmmssS");
    return formatear(patron, pattern.format(fecha));
  } // formatear

  public static String formatear(int patron, Date fecha) {
    return formatear(patron, formatear("yyyyMMddHHmmssS", fecha));
  } // formatear

  public static String formatear(int patron, Calendar fecha) {
    return formatear(patron, formatear("yyyyMMddHHmmssS", fecha.getTime()));
  } // formatear

  public static String toRegistro() {
    return formatear("yyyyMMddHHmmssS", Calendar.getInstance().getTime());
  } // formatear

  public static String formatear(int patron, Timestamp fecha) {
		Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(fecha.getTime());
    return formatear(patron, calendar);
  } // formatear

	public static String formatear(String patron, Timestamp fecha) {
		Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(fecha.getTime());
    return formatear(patron, calendar);
  } // formatear

	public static String formatear(String patron, Calendar fecha) {
    return  formatear(patron, fecha.getTime());
  } // formatear
	
  public static String formatear(int patron) {
    return formatear(patron, formatear("yyyyMMddHHmmssS", Calendar.getInstance().getTime()));
  } // formatear

  public static String getHoy() {
    return formatear(FECHA_CORTA);
  } // getHoy

  public static String getHoyMesCorto() {
    return formatear(FECHA_NOMBRE_MES_CORTO);
  } // getHoy

  public static String getHoyEstandar() {
    return formatear(FECHA_ESTANDAR);
  } // getHoy

  public static String getHoyExtendido() {
    return formatear(FECHA_HORA);
  } // getHoy

  public static String getHoyCorreo() {
    return formatear(FECHA_EXTENDIDA);
  } // getHoy

  private static String temporal(int patron, int dias) {
    Calendar dia= Calendar.getInstance();
    dia.add(Calendar.DATE, dias);
    return formatear(patron, formatear("yyyyMMddHHmmssS", dia.getTime()));
  } // formatear

  public static String getHoy(int dias) {
    return temporal(FECHA_CORTA, dias);
  } // getHoy

  public static String getHoyMesCorto(int dias) {
    return temporal(FECHA_NOMBRE_MES_CORTO, dias);
  } // getHoyMesCorto
  
  public static String getHoyEstandar(int dias) {
    return temporal(FECHA_ESTANDAR, dias);
  } // getHoyEstandar

  public static String getFormatoHoras(int horas) {
    Calendar dia= Calendar.getInstance();
    dia.add(Calendar.HOUR, horas);
    SimpleDateFormat formato = new SimpleDateFormat("yyyyMMddHH");
    return formato.format(dia.getTime());
  } // horas
  
  public static int getDiasEnElMes(int anio, int mes) {
    int dias[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    if ((anio % 4 == 0 && !(anio % 100 == 0)) || anio % 400 == 0)
      dias[1] = 29;
    return dias[mes];
  } // getDiasEnElMes

  public static int diasSiguienteMes(int anio, int mes) {
    if (mes + 1 > 11) {
      anio += 1;
      mes = 0;
    }
    else
      mes++;
    return getDiasEnElMes(anio, mes);
  } // siguienteMes

  public static String reversa(String fecha) {
    return fecha.substring(6, 8).concat("/").concat(fecha.substring(4, 6)).concat("/").concat(fecha.substring(0, 4));
  }

  public static String getFecha(String fecha) {
    return fecha.substring(6, 10).concat(fecha.substring(3, 5)).concat(fecha.substring(0, 2));
  }

  public static int getDiaActual() {
    return Calendar.getInstance().get(Calendar.DATE);
  }

  public static int getMesActual() {
    return Calendar.getInstance().get(Calendar.MONTH) + 1;
  }

  public static int getTrimestreActual() {
    return (Calendar.getInstance().get(Calendar.MONTH)/3)+1;
  }

  public static int getAnioActual() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }

  public static int diferenciasDias(String ayer, String hoy) {
    int diasDiferiencia = 0;
    if (ayer != null && (ayer.length() != 8 || ayer.length() != 14)) {
      if (hoy != null && (hoy.length() != 8 || hoy.length() != 14)) {
        if (ayer.length() == 14)
          ayer = ayer.substring(0, 8);
        if (hoy.length() == 14)
          hoy = hoy.substring(0, 8);
        int anioAyer = Integer.parseInt(ayer.substring(0, 4));
        int anioHoy = Integer.parseInt(hoy.substring(0, 4));
        // fecha anterior
        java.util.Calendar fechaAyer = java.util.Calendar.getInstance();
        fechaAyer.set(anioAyer, Integer.parseInt(ayer.substring(4, 6)) - 1, Integer.parseInt(ayer.substring(6, 8)));

        // fecha posterior
        java.util.Calendar fechaHoy = java.util.Calendar.getInstance();
        fechaHoy.set(anioHoy, Integer.parseInt(hoy.substring(4, 6)) - 1, Integer.parseInt(hoy.substring(6, 8)));

        // fecha pivote
        java.util.Calendar pivote = java.util.Calendar.getInstance();
        if (anioAyer < anioHoy) {
          for (int x = anioAyer; x < anioHoy; x++) {
            pivote.set(x, 11, 31); // 31 de Diciembre de los a�os anteriores
            diasDiferiencia += pivote.get(pivote.DAY_OF_YEAR);
          } // for
          diasDiferiencia += fechaHoy.get(fechaHoy.DAY_OF_YEAR);
          diasDiferiencia -= fechaAyer.get(fechaAyer.DAY_OF_YEAR);
        }
        else if (anioHoy < anioAyer) {
          for (int x = anioHoy; x < anioAyer; x++) {
            pivote.set(x, 11, 31); // 31 de Diciembre de los a�os anteriores
            diasDiferiencia += pivote.get(pivote.DAY_OF_YEAR);
          } // for
          diasDiferiencia += fechaAyer.get(fechaAyer.DAY_OF_YEAR);
          diasDiferiencia -= fechaHoy.get(fechaHoy.DAY_OF_YEAR);
        }
        else {
          diasDiferiencia = fechaHoy.get(fechaHoy.DAY_OF_YEAR) - fechaAyer.get(fechaAyer.DAY_OF_YEAR);
        } // if
        fechaAyer = null;
        fechaHoy = null;
        pivote = null;
      } // if hoy
    } // if ayer
    return Math.abs(diasDiferiencia);
  } // restar

  public static long diferenciaSegundo(long antes, long despues) {
    long regresar = 0;
    long diferencia = despues - antes;
    regresar = diferencia / 1000;
    return regresar;
  }

  public static long diferenciaMinutos(long antes, long despues) {
    long regresar = 0;
    long diferencia = despues - antes;
    regresar = diferencia / 1000 / 60;
    return regresar;
  } // minutos

  public static long diferenciaHoras(long antes, long despues) {
    long regresar = 0;
    long diferencia = despues - antes;
    regresar = diferencia / 1000 / 60 / 60;
    return regresar;
  } // horas

  public static long milisegundos(String hora) {
    Calendar calendario = Calendar.getInstance();
    // formatos permitidos
    // hora= 6  => HHMMSS
    // hora= 8  => HH:MM:SS
    // hora= 14 => YYYYMMDDHHMMSS
    if (hora != null && (hora.length() != 6 || hora.length() != 8 || hora.length() != 14)) {
      if (hora.length() == 14)
        hora = hora.substring(8);
      if (hora.length() == 8)
        hora = hora.substring(0, 2).concat(hora.substring(3, 5)).concat(hora.substring(6, 8));
      calendario.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hora.substring(0, 2)));
      calendario.set(Calendar.MINUTE, Integer.parseInt(hora.substring(2, 4)));
      calendario.set(Calendar.SECOND, Integer.parseInt(hora.substring(4, 6)));
    } // if
    return calendario.getTimeInMillis();
  } // milesegundos

  public static long diferenciaHoras(String antes, String despues) {
    return diferenciaHoras(milisegundos(antes), milisegundos(despues));
  } // diferenciaHoras

  public static long diferenciaMinutos(String antes, String despues) {
    return diferenciaMinutos(milisegundos(antes), milisegundos(despues));
  } // diferenciaMinutos

  public static long horas(int dias) {
    return dias * 24;
  } // diferenciaMinutos

  public static String[] meses(int iMes) {
    String[] meses = new String[12];
    String[] nombreMes =
    { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
      "Diciembre" };
    System.arraycopy(nombreMes, 0, meses, 0, iMes);
    return meses;
  }

  public static String getFechaHoraBD(Date date) {
    String regresar = null;
    String db = Configuracion.getInstance().getPropiedad("sistema.bd.type");
    if (db.equalsIgnoreCase("mysql"))
      regresar = formatear(FECHA_HORA_MYSQL, date);
    else if (db.equalsIgnoreCase("oracle"))
      regresar = formatear(FECHA_HORA_ORACLE, date);
    else
      regresar = getHoyExtendido();
    return regresar;
  } // getFechaHoraBD

  public static String getFechaBD(Date date) {
    String regresar = null;
    String db = Configuracion.getInstance().getPropiedad("sistema.bd.type");
    if (db.equalsIgnoreCase("mysql"))
      regresar = formatear(FECHA_MYSQL, date);
    else if (db.equalsIgnoreCase("oracle"))
      regresar = formatear(FECHA_ORACLE, date);
    else
      regresar = getHoy();
    return regresar;
  } // getFechaBD

  public static String getHoraBD(Date date) {
    String regresar = null;
    String db = Configuracion.getInstance().getPropiedad("sistema.bd.type");
    if (db.equalsIgnoreCase("mysql"))
      regresar = formatear(HORA_CORTA, date);
    else if (db.equalsIgnoreCase("oracle"))
      regresar = formatear(HORA_ORACLE, date);
    else
      regresar = formatear(HORA_CORTA);
    return regresar;
  } // getHoraBD


  public static String getFechaBD() {
    return getFechaBD(getRegistroTypeDate());
  }

  public static Calendar getFechaHora(String fecha) {
    // dd/MM/yyyy HH:mm:ss
    // 0123456789012345678
    // yyyyMMddHHmmss
    // 01234567890123
     // yyyyMMddHHmmssS
     // 01234567890123456
    // yyyy-MM-dd HH:mm:ss.S
    // 012345678901234567012
     Calendar calendar = Calendar.getInstance();
    if(!Cadena.isVacio(fecha)) {
      if (fecha.length() == 6) // yyyyMMdd
        fecha = reversa(fecha);
      else
        if (fecha.length() == 8) // dd/mm/yy
          fecha = fecha.substring(0, 6).concat("19").concat(fecha.substring(6)).concat(" 00:00:00");
      else
        if (fecha.length() == 10) // dd/mm/yyyy
          fecha = getFormatoEspaniol(fecha).concat(" 00:00:00");
      else
        if (fecha.length() == 12) // yyyyMMddHHmm
          fecha= reversa(fecha.substring(0, 8)).concat(" ").concat(fecha.substring(8, 10)).concat(":").concat(fecha.substring(10, 12)).concat(":");
      else
        if (fecha.length() == 14 || fecha.length() == 15 || fecha.length() == 16 || fecha.length() == 17) // yyyyMMddHHmmss YYYYMMDDHHmmssS
          fecha = reversa(fecha.substring(0, 8)).concat(" ").concat(fecha.substring(8, 10)).concat(":").concat(fecha.substring(10, 12)).concat(":").concat(fecha.substring(12, 14));
        else
          if (fecha.length()>= 18) // dd/MM/yyyy HH:mm:ss.S
            fecha = getFormatoEspaniol(fecha.substring(0, 10)).concat(fecha.substring(10));
      calendar = Calendar.getInstance();
      calendar.set(Calendar.DATE, Numero.getInteger(fecha.substring(0, 2)));
      calendar.set(Calendar.MONTH, Numero.getInteger(fecha.substring(3, 5)) - 1);
      calendar.set(Calendar.YEAR, Numero.getInteger(fecha.substring(6, 10)));
      calendar.set(Calendar.HOUR_OF_DAY, Numero.getInteger(fecha.substring(11, 13)));
      calendar.set(Calendar.MINUTE, Numero.getInteger(fecha.substring(14, 16)));
      calendar.set(Calendar.SECOND, Numero.getInteger(fecha.substring(17, 19)));
    } // if  
    return calendar;
  }

  private static String getFormatoEspaniol(String fecha) {
    if (fecha.indexOf("/") == 4 || fecha.indexOf("-") == 4)
      return fecha.substring(8, 10).concat("/").concat(fecha.substring(5, 7)).concat("/").concat(fecha.substring(0, 4));
    else
      return fecha;
  }

  public static Calendar getFechaCalendar(String fecha) {
    // DD/MM/YYYY DD-MM-YYYY
    return getFechaHora(fecha.concat(" 00:00:00"));
  }

  public static Calendar getHora(String hora) {
    // HH:MM:SS
    return getFechaHora("01/01/9999 ".concat(hora).concat(hora.length() == 5 ? ":00" : ""));
  } // getHora

  public static String getHoraExtendida() {
    return formatear(HORA_LARGA);
  } // getHoraExtendida
	
	public static String getFechaDiaAnterior(int patron, Timestamp fecha) {
		Calendar calendar= Calendar.getInstance();		
		calendar.setTimeInMillis(fecha.getTime());
		calendar.add(GregorianCalendar.DATE, -1);			
    return formatear(patron, calendar);
  } // formatear

  public static int getWorkingDaysBetween(Date startDate, Date endDate, List<Date> holiDays) {
    Calendar startCal = Calendar.getInstance();
    startCal.setTime(new java.sql.Date(startDate.getTime()));
    Calendar endCal = Calendar.getInstance();
    endCal.setTime(endDate);
    int workDays = 0;
    //Return 1 if start and end are the same
    if (startCal.get(Calendar.DAY_OF_MONTH)== endCal.get(Calendar.DAY_OF_MONTH) && startCal.get(Calendar.MONTH)== endCal.get(Calendar.MONTH) && startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR) )
      return 1;
    if (startCal.getTimeInMillis()> endCal.getTimeInMillis()) {
      startCal.setTime(endDate);
      endCal.setTime(new java.sql.Date(startDate.getTime()));
    } // if
    do {
      if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        boolean isNotHoliDay= true;
        for (Date date: holiDays) {
          isNotHoliDay= !((date.getMonth()+1)== startCal.get(Calendar.MONTH) && date.getDate()== startCal.get(Calendar.DATE));
          if(!isNotHoliDay)
            break;
        } // for
        if(isNotHoliDay)
          ++workDays;
      } // if
      startCal.add(Calendar.DAY_OF_MONTH, 1);
    } while (startCal.get(Calendar.DAY_OF_MONTH)!= endCal.get(Calendar.DAY_OF_MONTH) || (startCal.get(Calendar.MONTH)!= endCal.get(Calendar.MONTH) || startCal.get(Calendar.YEAR) != endCal.get(Calendar.YEAR)));
    return workDays;
  }

  public static int getWorkingDaysBetween(Date startDate, Date endDate) {
    return getWorkingDaysBetween(startDate, endDate, new ArrayList<Date>());
  }

  public static int getBetweenDays(Date startDate, Date endDate) {
    Calendar startCal = Calendar.getInstance();
    startCal.setTime(startDate);
    Calendar endCal = Calendar.getInstance();
    endCal.setTime(endDate);
    //Return 0 if start and end are the same
    if (startCal.get(Calendar.DAY_OF_MONTH) == endCal.get(Calendar.DAY_OF_MONTH) && startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH) && startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR))
      return 1;
    if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
      startCal.setTime(endDate);
      endCal.setTime(startDate);
    } // if
    return (int)((endCal.getTimeInMillis() - startCal.getTimeInMillis())/ (1000 * 60 * 60 * 24))+ 2;
  }

  public static String toFormatSecondsToHour(long seconds) {
    long hours  = (long)(seconds/ 60/ 60);
    long minutes= (long)((seconds/ 60)- (hours* 60));
    seconds     = (long)(seconds- (hours* 60* 60)- (minutes* 60)); 
    long years  = (long) (hours/ 8760);
    hours       = (long) (hours- (years* 8760));
    long months = (long) (hours/ 720);
    hours       = (long) (hours- (months* 720));
		long days   = (long) (hours/ 24);
		hours       = (long) (hours- (days* 24));
    return (years> 0? years+ " a�o(s)": "")+ (years>0 && months> 0? ", con ": "")+ (months> 0? months+ " mese(s)": "")+ (months>0 && days> 0? ", con ": "")+ (days> 0? days+ " dias": "")+ (days>0 && hours> 0? ", con ": "")+ (hours> 0? Cadena.rellenar(String.valueOf(hours), 2, '0', Boolean.TRUE)+ " horas": "")+ (hours>0 && minutes> 0? ", con ": "")+ (minutes> 0? Cadena.rellenar(String.valueOf(minutes), 2, '0', Boolean.TRUE)+ " minutos": "")+ (minutes>0 && seconds> 0? ", con ": "")+ (seconds> 0? Cadena.rellenar(String.valueOf(seconds), 2, '0', Boolean.TRUE)+ " segundos": "");
  }

  public static java.sql.Date toDateDefault(String fecha) {
    // DD/MM/YYYY DD-MM-YYYY
		java.sql.Date regresar= null;
		try {
      regresar= new java.sql.Date(Fecha.getFechaHora(fecha.concat(" 00:00:00")).getTimeInMillis());
		} // try
		catch(Exception e) {
			regresar= new java.sql.Date(Fecha.getRegistroTypeDate().getTime());
		} // catch
	  return regresar;
  }
	
  public static java.time.LocalDate toLocalDateDefault(String fecha) {
    // DD/MM/YYYY DD-MM-YYYY
		java.time.LocalDate regresar= null;
		try {
			DateTimeFormatter formatter= null;
			if(fecha.indexOf("-")>= 0)
			  DateTimeFormatter.ofPattern("dd-MM-yyyy");
			else
			  DateTimeFormatter.ofPattern("dd/MM/yyyy");
      regresar= LocalDate.parse(fecha, formatter);
		} // try
		catch(Exception e) {
			regresar= LocalDate.now();
		} // catch
	  return regresar;
  }
	
  public static Calendar toCalendar(String date) {
		// date equals 2018-11-01
		return toCalendar(date, "00:00:00");
	}

	public static Calendar toCalendar(String date, String time) {
		// date equals 2018-11-01 and time equals 23:59:59
		Calendar regresar= Calendar.getInstance();
		regresar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
		regresar.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7))- 1);
		regresar.set(Calendar.DATE, Integer.parseInt(date.substring(8, 10)));
		regresar.set(Calendar.HOUR, Integer.parseInt(time.substring(0, 2)));
		regresar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));
		regresar.set(Calendar.SECOND, Integer.parseInt(time.substring(6, 8)));
		return regresar;
	}
	
	public static LocalDateTime toLocalDateTime(String date, String time) {
		// date equals 2018-11-01 and time equals 23:59:59
    LocalDateTime regresar= null;
    try {
      regresar= LocalDateTime.of(
        Integer.parseInt(date.substring(0, 4)),
        Integer.parseInt(date.substring(5, 7)),
        Integer.parseInt(date.substring(8, 10)),
        Integer.parseInt(time.substring(0, 2)),
        Integer.parseInt(time.substring(3, 5)),
        Integer.parseInt(time.substring(6, 8))
      );
    } // try
    catch(Exception e) {
      regresar= LocalDateTime.now();
    } // catch
		return regresar;
	}

	public static LocalDateTime toLocalDateTime(String date) {
    // date equals 2019-12-10T18:24:31.961Z
    //             012345678901234567890123
    LocalDateTime regresar= LocalDateTime.now();
    try {
      regresar= LocalDateTime.of(
        Integer.parseInt(date.substring(0, 4)),
        Integer.parseInt(date.substring(5, 7)),
        Integer.parseInt(date.substring(8, 10)),
        Integer.parseInt(date.substring(11, 13)),
        Integer.parseInt(date.substring(14, 16)),
        Integer.parseInt(date.substring(17, 19)),
        0
      );
      if(date.length()> 20) 
        regresar.plusNanos(Integer.parseInt(date.substring(20, 23)));
    } // try
    catch(Exception e) {
      regresar= LocalDateTime.now();
    } // catch
		return regresar;
  }
  
	public static LocalDate toLocalDate(String date) {
		//             0123456789
		// date equals 01-01-2018
    //             0123456789
    // date equals 2019-12-10T18:24:31
		LocalDate regresar= null;
		try {
      if(date.length()<= 10)
        regresar= LocalDate.of(
          Integer.parseInt(date.substring(6, 10)),
          Integer.parseInt(date.substring(3, 5)),
          Integer.parseInt(date.substring(0, 2))
        );
      else
        regresar= LocalDate.of(
          Integer.parseInt(date.substring(0, 4)),
          Integer.parseInt(date.substring(5, 7)),
          Integer.parseInt(date.substring(8, 10))
        );
		} // try
		catch(Exception e) {
			regresar= LocalDate.now();
			Error.mensaje(e);
		} // catch
		return regresar;
	}

  public static String getLocalDateTimeBD(LocalDateTime date) {
    String regresar = null;
    String db = Configuracion.getInstance().getPropiedad("sistema.bd.type");
    if (db.equalsIgnoreCase("mysql"))
      regresar = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    else if (db.equalsIgnoreCase("oracle"))
      regresar = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    else
      regresar = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return regresar;
  } // getLocalDateTimeBD
	
  public static String getLocalDateBD(LocalDate date) {
    String regresar = null;
    String db = Configuracion.getInstance().getPropiedad("sistema.bd.type");
    if (db.equalsIgnoreCase("mysql"))
      regresar = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    else if (db.equalsIgnoreCase("oracle"))
      regresar = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    else
      regresar = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    return regresar;
  } // getLocalDateBD
	
  public static String getLocalTimeBD(LocalTime date) {
    String regresar = null;
    String db = Configuracion.getInstance().getPropiedad("sistema.bd.type");
    if (db.equalsIgnoreCase("mysql"))
      regresar = date.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    else if (db.equalsIgnoreCase("oracle"))
      regresar = date.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    else
      regresar = date.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    return regresar;
  } // getLocalTimeBD

  public static LocalDate getLocalDate(Calendar calendar) {
    Calendar regresar = calendar.getInstance();
    Date date = regresar.getTime();
    Instant instant = date.toInstant();
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
    return zonedDateTime.toLocalDate();
  }   
        
	public static void main(String ... args) {
    String value= "12/12/2017 17:15:19";
    LocalDateTime date= Instant.ofEpochMilli(Fecha.getFechaHora(value).getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		LOG.info(date);
		LOG.info(DAYS.between(date.toLocalDate(), LocalDate.now()));
		LOG.info(SECONDS.between(date, LocalDateTime.now()));
		LOG.info(Fecha.toFormatSecondsToHour(SECONDS.between(date, LocalDateTime.now())));
		LOG.info(Fecha.toFormatSecondsToHour(DAYS.between(date.toLocalDate(), LocalDate.now())* 86400));
	}

} // Fecha
