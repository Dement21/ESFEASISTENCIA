package com.esfe.Asistencia.Servicios.Interfaces;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.esfe.Asistencia.Modelos.Estudiante;

public interface IEstudianteService {
    Page<Estudiante> buscarTodosPaginados(Pageable pageable);

    List<Estudiante> obtenerTodos();

    Optional<Estudiante> buscarPorId(Integer id);

    Estudiante crearOEditar(Estudiante estudiante);

    void eliminarPorId(Integer id);
}
