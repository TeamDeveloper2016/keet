package mx.org.kaana.keet.nomina.reglas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 26/04/2020
 * @time 06:56:10 PM
 * @author @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Semanas {

	private static final Log LOG=LogFactory.getLog(Semanas.class);

	private int year;
	private int month;
	private int day;
	private int top;
	private LocalDate end;
	
	public Semanas() throws Exception {
		this(1);
	}
	
	public Semanas(int years) throws Exception {
	  Value data= DaoFactory.getInstance().toField("TcKeetNominasPeriodosDto", "ultimo", Collections.EMPTY_MAP, "ultimo");
		if(data!= null && data.getData()!= null) {
			LocalDate tomorrow= data.toDate().plusDays(1);
			this.year= tomorrow.getYear();
			this.month= tomorrow.getMonthValue();
			this.day= tomorrow.getDayOfMonth();
			this.top= this.year+ years;
		} // if
	}
	
	public Semanas(int year, int month, int day) {
		this.year= year;
		this.month= month;
		this.day= day;
	  this.top= year+ 1;	
	}

	public int getYear() {
		return year;
	}

	public LocalDate getStart() {
		return LocalDate.of(this.year, this.month, this.day);
	}
	
	public LocalDate getFinish() {
		return this.end== null? LocalDate.of(this.year, this.month, this.day): this.end;
	}
	
	public int getSemanaActual() throws Exception {
	  int regresar= 1;
		Value data= DaoFactory.getInstance().toField("TcKeetNominasPeriodosDto", "semana", Collections.EMPTY_MAP, "semana");
		if(data!= null && data.getData()!= null) 
			regresar= data.toInteger();
		return regresar;
	}
	
	public Long getSemanaEnCurso() {
	  Long regresar               = 1L;		
		TcKeetNominasPeriodosDto dto= null;
		try {			
			dto= getSemanaEnCursoDto();
			if(dto!= null && dto.isValid())
				regresar= dto.getOrden();
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
		return regresar;
	} // getSemanaEnCurso
	
	public TcKeetNominasPeriodosDto getSemanaEnCursoDto() {
	  TcKeetNominasPeriodosDto regresar= null;
		Map<String, Object>params        = null;
		try {
			params= new HashMap<>();
			params.put("fecha", LocalDate.now().toString());
			regresar= (TcKeetNominasPeriodosDto) DaoFactory.getInstance().toEntity(TcKeetNominasPeriodosDto.class, "TcKeetNominasPeriodosDto", "semanaEnCurso", params);			
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally		
		return regresar;
	} // getSemanaEnCursoDto
	
	public int getSemana() throws Exception {
	  int regresar= 1;
		Value data= DaoFactory.getInstance().toField("VistaNominaDto", "semana", Collections.EMPTY_MAP, "semana");
		if(data!= null && data.getData()!= null) 
			regresar= data.toInteger();
		return regresar;
	}
	
	public void process(int until) throws Exception {
		TcKeetNominasPeriodosDto semana= null;
		LocalDate data= LocalDate.of(this.year, this.month, this.day);
		LOG.info(data.getDayOfWeek().name()+ ", "+ data);
		if(until< data.getYear())
			until= data.getYear()+ until;
		int count= 1;
		int pivot= this.year;
		while (data.getYear()< until) {
			LocalDate cut= data.plusDays(3);
			this.end     = data.plusDays(6);
			// LOG.info(day.getDayOfWeek().name()+ ", "+ day+ " to "+ end+ " count  "+ count);
			semana= new TcKeetNominasPeriodosDto(
				LocalDateTime.of(cut.getYear(), cut.getMonthValue(), cut.getDayOfMonth(), 8, 0, 0, 0),  // LocalDateTime corte, 
				-1L, // Long idNominaPeriodo, 
				data, // LocalDate inicio, 
				this.end, // LocalDate termino, 
				new Long(count), // Long orden, 
				new Long(data.getYear()) // Long ejercicio
			);
			LOG.info(count+ ", "+ semana);
			data= this.end.plusDays(1);
			if(pivot!= data.getYear()) {
				count= 0;
				pivot= data.getYear();
			} // if	
			count++;
			DaoFactory.getInstance().insert(semana);
		} // while
	}
	
	public void process() throws Exception {
		this.process(this.top);
	}

	public static void main(String[] args) throws Exception {
		Semanas semanas= new Semanas(2);
		semanas.process();
	}
}