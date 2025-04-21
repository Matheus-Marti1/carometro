package br.edu.fateczl.carometro.config;

import br.edu.fateczl.carometro.model.entity.Administrador;
import br.edu.fateczl.carometro.repository.AdministradorRepository;
import br.edu.fateczl.carometro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!prod")
public class DataInitializer implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    @Value("${admin.initialization.enabled:false}")
    private boolean enableInitialization;

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${admin.department:TI}")
    private String adminDepartment;

    @Override
    public void run(String... args) {
        if (!enableInitialization) {
            log.info("Admin initialization disabled. Skipping...");
            return;
        }

        if (ObjectUtils.isEmpty(adminEmail) || ObjectUtils.isEmpty(adminPassword)) {
            log.warn("Admin credentials not properly configured. Skipping initialization.");
            return;
        }

        // Cria um administrador inicial se não existir
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            Administrador admin = Administrador.createAdministrador(
                    "Administrador",
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    adminDepartment,
                    true
            );

            administradorRepository.save(admin);

            log.info("Initial administrator account created");
        } else {
            log.info("Admin account already exists, skipping initialization");
        }
    }
}
