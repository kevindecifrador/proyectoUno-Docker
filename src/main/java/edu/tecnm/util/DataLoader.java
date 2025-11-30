package edu.tecnm.util;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.tecnm.model.Perfil;
import edu.tecnm.service.IPerfilService;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private IPerfilService perfilService;

    @Override
    public void run(String... args) throws Exception {
        
        List<Perfil> perfilesExistentes = perfilService.buscarTodos();

        if (perfilesExistentes.isEmpty()) {
            System.out.println("--- Cargando PERFILES iniciales (BD Vacía detected)... ---");
            
            // ID 1
            perfilService.guardar(new Perfil("ADMIN"));
            
            // ID 2
            perfilService.guardar(new Perfil("MESERO"));
            
            // ID 3
            perfilService.guardar(new Perfil("CLIENTE"));
            
            // ID 4
            perfilService.guardar(new Perfil("COCINERO"));
            
            // ID 5
            perfilService.guardar(new Perfil("CAJERO"));
            
            // ID 6
            perfilService.guardar(new Perfil("SUPERVISOR"));

            System.out.println("--- Perfiles cargados correctamente. ---");
            
        } else {
            System.out.println("--- Los perfiles ya existen. Omitiendo carga. ---");
        }
    }
}