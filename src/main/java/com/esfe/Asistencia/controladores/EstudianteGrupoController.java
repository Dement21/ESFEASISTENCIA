package com.esfe.Asistencia.controladores;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esfe.Asistencia.Modelos.*;
import com.esfe.Asistencia.Servicios.Interfaces.*;

@Controller
@RequestMapping("/inscripciones")
public class EstudianteGrupoController {

    @Autowired
    private IEstudianteGrupoService estudianteGrupoService;

    @Autowired
    private IGrupoService grupoService;

    @Autowired
    private IEstudianteService estudianteService;

    // ---------- LISTADO ---------- //
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<EstudianteGrupo> inscripciones = estudianteGrupoService.buscarTodosPaginados(pageable);

        model.addAttribute("inscripciones", inscripciones);

        int totalPages = inscripciones.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                                                 .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "inscripcion/index";
    }

    // ---------- CREAR ---------- //
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("estudianteGrupo", new EstudianteGrupo());
        model.addAttribute("estudiantes", estudianteService.obtenerTodos());
        model.addAttribute("grupos", grupoService.obtenerTodos());
        model.addAttribute("action", "create");
        return "inscripcion/mant";
    }

    // ---------- EDITAR ---------- //
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        EstudianteGrupo estudianteGrupo = estudianteGrupoService.buscarPorId(id);
        model.addAttribute("estudianteGrupo", estudianteGrupo);
        model.addAttribute("estudiantes", estudianteService.obtenerTodos());
        model.addAttribute("grupos", grupoService.obtenerTodos());
        model.addAttribute("action", "edit");
        return "inscripcion/mant";
    }

    // ---------- VER ---------- //
    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        EstudianteGrupo estudianteGrupo = estudianteGrupoService.buscarPorId(id);
        model.addAttribute("estudianteGrupo", estudianteGrupo);
        model.addAttribute("estudiantes", estudianteService.obtenerTodos());
        model.addAttribute("grupos", grupoService.obtenerTodos());
        model.addAttribute("action", "view");
        return "inscripcion/mant";
    }

    // ---------- ELIMINAR ---------- //
    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        EstudianteGrupo estudianteGrupo = estudianteGrupoService.buscarPorId(id);
        model.addAttribute("estudianteGrupo", estudianteGrupo);
        model.addAttribute("estudiantes", estudianteService.obtenerTodos());
        model.addAttribute("grupos", grupoService.obtenerTodos());
        model.addAttribute("action", "delete");
        return "inscripcion/mant";
    }

    // ---------- PROCESAR POST ---------- //
    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute EstudianteGrupo estudianteGrupo, BindingResult result,
                            RedirectAttributes redirect, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("estudiantes", estudianteService.obtenerTodos());
            model.addAttribute("grupos", grupoService.obtenerTodos());
            model.addAttribute("action", "create");
            return "inscripcion/mant";
        }

        estudianteGrupoService.crearOEditar(estudianteGrupo);
        redirect.addFlashAttribute("msg", "Inscripción creada correctamente");
        return "redirect:/inscripciones";
    }

    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute EstudianteGrupo estudianteGrupo, BindingResult result,
                              RedirectAttributes redirect, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("estudiantes", estudianteService.obtenerTodos());
            model.addAttribute("grupos", grupoService.obtenerTodos());
            model.addAttribute("action", "edit");
            return "inscripcion/mant";
        }

        estudianteGrupoService.crearOEditar(estudianteGrupo);
        redirect.addFlashAttribute("msg", "Inscripción actualizada correctamente");
        return "redirect:/inscripciones";
    }

    @PostMapping("/delete")
    public String deleteEstudianteGrupo(@ModelAttribute EstudianteGrupo estudianteGrupo, RedirectAttributes redirect) {
        estudianteGrupoService.eliminarPorId(estudianteGrupo.getId());
        redirect.addFlashAttribute("msg", "Inscripción eliminada correctamente");
        return "redirect:/inscripciones";
    }
}
