package com.esfe.Asistencia.Servicios.Interfaces;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.esfe.Asistencia.Modelos.EstudianteGrupo;

public interface IEstudianteGrupoService {
    
    List<EstudianteGrupo> obtenerTodos();

    Page<EstudianteGrupo> buscarTodosPaginados(Pageable pageable);

    EstudianteGrupo buscarPorId(Integer id);

    EstudianteGrupo crearOEditar(EstudianteGrupo estudianteGrupo);

    void eliminarPorId(Integer id);
}
