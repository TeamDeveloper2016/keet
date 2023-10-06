package mx.org.kaana.keet.test.personas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.db.dto.TcKeetContratosPersonalDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.formato.Error;

public class Desarrollo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      Map<String, Object> params = new HashMap<>();
      List<Persona> personas= new ArrayList<>();
      personas.add(new Persona(9L, 9L));
      personas.add(new Persona(56L, 9L));
      personas.add(new Persona(112L, 9L));
      personas.add(new Persona(121L, 21L));
      personas.add(new Persona(74L, 12L));
      personas.add(new Persona(38L, 9L));
      personas.add(new Persona(67L, 17L));
      personas.add(new Persona(94L, 11L));
      personas.add(new Persona(50L, 11L));
      personas.add(new Persona(59L, 17L));
      personas.add(new Persona(101L, 12L));
      personas.add(new Persona(135L, 16L));
      personas.add(new Persona(129L, 2L));
      personas.add(new Persona(137L, 2L));
      personas.add(new Persona(106L, 1L));
      personas.add(new Persona(36L, 9L));
      personas.add(new Persona(54L, 2L));
      personas.add(new Persona(125L, 1L));
      personas.add(new Persona(20L, 17L));
      personas.add(new Persona(89L, 2L));
      personas.add(new Persona(108L, 3L));
      personas.add(new Persona(142L, 2L));
      personas.add(new Persona(126L, 12L));
      personas.add(new Persona(39L, 3L));
      personas.add(new Persona(96L, 3L));
      personas.add(new Persona(132L, 12L));
      personas.add(new Persona(86L, 17L));
      personas.add(new Persona(64L, 2L));
      personas.add(new Persona(171L, 19L));
      personas.add(new Persona(176L, 17L));
      personas.add(new Persona(162L, 2L));
      personas.add(new Persona(268L, 4L));
      personas.add(new Persona(199L, 13L));
      personas.add(new Persona(185L, 2L));
      personas.add(new Persona(209L, 13L));
      personas.add(new Persona(124L, 13L));
      personas.add(new Persona(161L, 13L));
      personas.add(new Persona(208L, 11L));
      personas.add(new Persona(190L, 17L));
      personas.add(new Persona(104L, 13L));
      personas.add(new Persona(157L, 19L));
      personas.add(new Persona(30L, 21L));
      personas.add(new Persona(109L, 12L));
      personas.add(new Persona(178L, 3L));
      personas.add(new Persona(16L, 3L));
      personas.add(new Persona(202L, 3L));
      personas.add(new Persona(156L, 16L));
      personas.add(new Persona(85L, 9L));
      personas.add(new Persona(147L, 9L));
      personas.add(new Persona(58L, 21L));
      personas.add(new Persona(14L, 13L));
      personas.add(new Persona(175L, 18L));
      personas.add(new Persona(97L, 3L));
      personas.add(new Persona(191L, 2L));
      personas.add(new Persona(31L, 2L));
      personas.add(new Persona(42L, 2L));
      personas.add(new Persona(122L, 13L));
      personas.add(new Persona(267L, 4L));
      personas.add(new Persona(155L, 17L));
      personas.add(new Persona(186L, 2L));
      personas.add(new Persona(140L, 12L));
      personas.add(new Persona(177L, 18L));
      personas.add(new Persona(103L, 2L));
      personas.add(new Persona(204L, 21L));
      personas.add(new Persona(144L, 2L));
      personas.add(new Persona(194L, 2L));
      personas.add(new Persona(146L, 1L));
      personas.add(new Persona(187L, 3L));
      personas.add(new Persona(117L, 9L));
      personas.add(new Persona(80L, 3L));
      personas.add(new Persona(45L, 21L));
      personas.add(new Persona(166L, 1L));
      personas.add(new Persona(257L, 13L));
      personas.add(new Persona(2L, 1L));
      personas.add(new Persona(3L, 1L));
      personas.add(new Persona(266L, 11L));
      personas.add(new Persona(216L, 11L));
      personas.add(new Persona(230L, 9L));
      personas.add(new Persona(211L, 3L));
      personas.add(new Persona(258L, 1L));
      personas.add(new Persona(244L, 13L));
      personas.add(new Persona(213L, 16L));
      personas.add(new Persona(237L, 2L));
      personas.add(new Persona(210L, 21L));
      personas.add(new Persona(218L, 3L));
      personas.add(new Persona(248L, 21L));
      personas.add(new Persona(223L, 13L));
      personas.add(new Persona(219L, 11L));
      personas.add(new Persona(236L, 9L));
      personas.add(new Persona(240L, 2L));
      personas.add(new Persona(233L, 17L));
      personas.add(new Persona(226L, 2L));
      personas.add(new Persona(227L, 2L));
      personas.add(new Persona(247L, 3L));
      personas.add(new Persona(234L, 13L));
      personas.add(new Persona(228L, 3L));
      personas.add(new Persona(245L, 21L));
      personas.add(new Persona(257L, 13L));
      personas.add(new Persona(220L, 21L));
      personas.add(new Persona(236L, 13L));
      personas.add(new Persona(221L, 16L));
      personas.add(new Persona(269L, 4L));
      personas.add(new Persona(259L, 11L));
      personas.add(new Persona(260L, 2L));
      personas.add(new Persona(243L, 21L));
      personas.add(new Persona(256L, 2L));
      personas.add(new Persona(224L, 3L));
      personas.add(new Persona(270L, 3L));
      personas.add(new Persona(84L, 9L));
      personas.add(new Persona(198L, 1L));
      personas.add(new Persona(255L, 1L));
      personas.add(new Persona(250L, 1L));
      personas.add(new Persona(148L, 17L));
      personas.add(new Persona(152L, 16L));
      try {        
        int count= 1;
        for (Persona item : personas) {
          params.put("idEmpresaPersona", item.getIdEmpresaPersona());
          TcKeetContratosPersonalDto dto= (TcKeetContratosPersonalDto)DaoFactory.getInstance().toEntity(TcKeetContratosPersonalDto.class, "TcKeetContratosPersonalDto", "existe", params);
          if(dto== null) {	
            dto= new TcKeetContratosPersonalDto();							
            dto.setIdDesarrollo(item.getIdDesarrollo());
            dto.setIdEmpresaPersona(item.getIdEmpresaPersona());
            dto.setIdUsuario(2L);
            dto.setIdVigente(1L);
            dto.setObservaciones("ASIGNACION DE EMPLEADO AL DESARROLLO " + item.getIdDesarrollo());
            DaoFactory.getInstance().insert(dto);
          } // if
          else {
            params.put("idDesarrollo", item.getIdDesarrollo());
            TcKeetContratosPersonalDto otro= (TcKeetContratosPersonalDto)DaoFactory.getInstance().toEntity(TcKeetContratosPersonalDto.class, "TcKeetContratosPersonalDto", "igual", params);
            if(otro== null) {	
              dto.setIdDesarrollo(item.getIdDesarrollo());
              dto.setObservaciones("SE REASIGNO EL EMPLEADO AL DESARROLLO " + item.getIdDesarrollo());
              dto.setRegistro(LocalDateTime.now());
              DaoFactory.getInstance().update(dto);
            } // if  
          } // else
          count++;
          System.out.println("count: "+ count);
        } // for					        
      } // try
      catch (Exception e) {
        Error.mensaje(e);
        JsfBase.addMessageError(e);        
      } // catch	
      finally {
        Methods.clean(params);
      } // finally
    }

}
