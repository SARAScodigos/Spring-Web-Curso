package com.PC.Store.SistemaWeb;

import com.PC.Store.SistemaWeb.model.Usuario;
import com.PC.Store.SistemaWeb.repository.UsuarioRepository;
import com.PC.Store.SistemaWeb.util.PasswordHashUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SistemaWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaWebApplication.class, args);
	}

	@Bean
	CommandLineRunner seedAdmin(UsuarioRepository usuarioRepository) {
		return args -> {
			if (usuarioRepository.findByCorreo("admin").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setNombre("Administrador");
				admin.setCorreo("admin");
				admin.setPassword(PasswordHashUtil.encode("14753admin"));
				admin.setRol("ADMIN");
				usuarioRepository.save(admin);
			}
		};
	}
}
