package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRegistroRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioResponseDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class controllerLogin {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String login(@RequestParam(required = false) String registrado, Model model) {
        if (registrado != null) {
            model.addAttribute("exito", "Cuenta creada con éxito. Ya puedes iniciar sesión.");
        }
        return "fragments/loginlayout";
    }

    @GetMapping("/registro")
    public String registro() {
        return "fragments/registrolayout";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute @Valid UsuarioRegistroRequestDTO dto,
                                   BindingResult bindingResult,
                                   Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Revisa los datos ingresados");
            return "fragments/registrolayout";
        }

        try {
            usuarioService.registrarPublico(dto);
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "fragments/registrolayout";
        }

        return "redirect:/?registrado=true";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {

        Optional<UsuarioResponseDTO> usuarioOpt = usuarioService.autenticar(correo, password);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "fragments/loginlayout";
        }

        UsuarioResponseDTO usuario = usuarioOpt.get();
        session.setAttribute("usuarioId", usuario.idUsuario());
        session.setAttribute("usuarioNombre", usuario.nombre());
        session.setAttribute("usuarioRol", usuario.rol());

        if ("ADMIN".equalsIgnoreCase(usuario.rol())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/user/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}