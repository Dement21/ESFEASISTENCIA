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

import com.esfe.Asistencia.Modelos.Docente;
import com.esfe.Asistencia.Servicios.Interfaces.IDocenteService;
import com.esfe.Asistencia.Utilidades.PdfGeneratorService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/docentes")
public class DocenteController{

    @Autowired
    private IDocenteService docenteService;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size){
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Docente> docentes = docenteService.buscarTodosPaginados (pageable);
        model.addAttribute("docentes", docentes);

        int totalPages = docentes.getTotalPages();
         if (totalPages > 0){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                .boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "docente/index";
    }

    // ---------- CREAR --------- //
    @GetMapping("/create")
    public String create(Model  model) {
        model.addAttribute("docente", new Docente ());
        model.addAttribute("action", "create");
        return "docente/mant";
    }
    
    // --------- EDITAR -------- // 
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Docente docente = docenteService.buscarPorId(id).orElseThrow();
        model.addAttribute("docente", docente);
        model.addAttribute("action", "edit");
        return "docente/mant";
    }
    
    // --------- Ver (solo lectrura) ------------ //
    @GetMapping("/view/{id}")
    public String view (@PathVariable Integer id, Model model) {
        Docente docente = docenteService.buscarPorId(id).orElseThrow();
        model.addAttribute("docente", docente);
        model.addAttribute("action", "view");
        return "docente/mant" ;
    }
    
    //--------- ELIMINAR ------------- //
    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable  Integer id, Model model ) {
        Docente docente = docenteService.buscarPorId(id).orElseThrow();
        model.addAttribute("docente", docente);
        model.addAttribute("action", "delete");
        return "docente/mant" ;
    }

    //-------- PROCESAR POST SEGUN ACTION ---------- //
    @PostMapping("/create")
    public String saveNuevo(@ModelAttribute Docente docente,  BindingResult result,
                            RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action",  "create");
            return "docente/mant";
        }
        docenteService.crearOEditar(docente);
        redirect.addFlashAttribute("msg","Docente creado correctamente");
        return "redirect:/docentes";
    }
    
    @PostMapping("/edit")
    public String saveEditado(@ModelAttribute Docente docente, BindingResult result, 
                              RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("action", "edit");
            return  "docente/mant";
        }
        docenteService.crearOEditar(docente);
        redirect.addFlashAttribute("msg", "Docente actualizado correctamente");
        return "redirect:/docentes";
    }
    
    @PostMapping("/delete")
    public String deleteDocente(@ModelAttribute Docente docente,  RedirectAttributes redirect) {
        docenteService.eliminarPorId(docente.getId());
        redirect.addFlashAttribute("msg","Docente eliminado corectamente");
        return "redirect:/docentes";
    }

    @GetMapping("/docentePDF")
    public void generarPdf (Model model, HttpServletResponse response) throws Exception {
        // 1. Obtener datos a mostrar en el PDF
        List<Docente> docentes = docenteService.obtenerTodos(); // método para traer todos sin paginar

        // 2. Preparar datos para Thymeleaf
        Map<String, Object> data = new HashMap<>();
        data.put( "docentes", docentes);

        // 3. Generar PDF (con el nombre de la plantilla Thymeleaf que quieres usar)
        byte[] pdfBytes = pdfGeneratorService.generatePdfReport( "docente/RPDocente", data);

        // 4. Configurar la respuesta HTTP para descargar o mostrar el PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=docentes.pdf");
        response.setContentLength(pdfBytes.length);

        // 5. Escribir bytes en el output stream
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

}