package com.esfe.Asistencia.controladores;

import java.util.*;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.esfe.Asistencia.Modelos.Estudiante;
import com.esfe.Asistencia.Servicios.Interfaces.IEstudianteService;
import com.esfe.Asistencia.Utilidades.PdfGeneratorService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    // ---------- LISTADO ---------- //
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Estudiante> estudiantes = estudianteService.buscarTodosPaginados(pageable);
        model.addAttribute("estudiantes", estudiantes);

        int totalPages = estudiantes.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                                                 .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "estudiante/index";
    }

    // ---------- CREAR ---------- //
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("estudiante", new Estudiante());
        model.addAttribute("action", "create");
        return "estudiante/mant";
    }

    // ---------- EDITAR ---------- //
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Estudiante estudiante = estudianteService.buscarPorId(id).orElseThrow();
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("action", "edit");
        return "estudiante/mant";
    }

    // ---------- VER ---------- //
    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Estudiante estudiante = estudianteService.buscarPorId(id).orElseThrow();
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("action", "view");
        return "estudiante/mant";
    }

    // ---------- ELIMINAR ---------- //
    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable Integer id, Model model) {
        Estudiante estudiante = estudianteService.buscarPorId(id).orElseThrow();
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("action", "delete");
        return "estudiante/mant";
    }

    // ---------- PROCESAR POST ---------- //
    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute Estudiante estudiante, BindingResult result,
                            RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "create");
            return "estudiante/mant";
        }
        estudianteService.crearOEditar(estudiante);
        redirect.addFlashAttribute("msg", "Estudiante creado correctamente");
        return "redirect:/estudiantes";
    }

    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute Estudiante estudiante, BindingResult result,
                              RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            return "estudiante/mant";
        }
        estudianteService.crearOEditar(estudiante);
        redirect.addFlashAttribute("msg", "Estudiante actualizado correctamente");
        return "redirect:/estudiantes";
    }

    @PostMapping("/delete")
    public String deleteEstudiante(@ModelAttribute Estudiante estudiante, RedirectAttributes redirect) {
        estudianteService.eliminarPorId(estudiante.getId());
        redirect.addFlashAttribute("msg", "Estudiante eliminado correctamente");
        return "redirect:/estudiantes";
    }

    // ---------- PDF ---------- //
    @GetMapping("/estudiantePDF")
    public void generarPdf(Model model, HttpServletResponse response) throws Exception {
        List<Estudiante> estudiantes = estudianteService.obtenerTodos();

        Map<String, Object> data = new HashMap<>();
        data.put("estudiantes", estudiantes);

        byte[] pdfBytes = pdfGeneratorService.generatePdfReport("estudiante/RPEstudiante", data);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=estudiantes.pdf");
        response.setContentLength(pdfBytes.length);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
