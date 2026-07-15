package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRequestDTO;
import com.PC.Store.SistemaWeb.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class controllerLogin {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String login(Model model) {
        model.addAttribute("usuarioForm", new UsuarioRequestDTO("", "", "", "USER"));
        return "fragments/loginlayout";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String correo,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        try {
            var usuario = usuarioService.autenticar(correo, password);
            session.setAttribute("usuarioId", usuario.idUsuario());
            session.setAttribute("usuarioNombre", usuario.nombre());
            session.setAttribute("usuarioRol", usuario.rol());
            if ("ADMIN".equalsIgnoreCase(usuario.rol())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/user/";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("usuarioForm", new UsuarioRequestDTO("", "", "", "USER"));
            return "fragments/loginlayout";
        }
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute("usuarioForm") @Valid UsuarioRequestDTO usuarioForm,
                            BindingResult result,
                            Model model) {
        if (result.hasErrors()) {
            return "fragments/loginlayout";
        }

        try {
            usuarioService.crear(usuarioForm);
            model.addAttribute("success", "Cuenta creada correctamente. Ahora puedes iniciar sesión.");
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }

        model.addAttribute("usuarioForm", new UsuarioRequestDTO("", "", "", "USER"));
        return "fragments/loginlayout";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
