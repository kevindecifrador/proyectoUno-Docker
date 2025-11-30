package edu.tecnm.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.tecnm.model.Cliente;
import edu.tecnm.model.Empleado;
import edu.tecnm.model.Usuario;
import edu.tecnm.repository.ClienteRepository;
import edu.tecnm.repository.EmpleadoRepository;
import edu.tecnm.repository.UsuarioRepository;
import edu.tecnm.service.IUsuarioService;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private EmpleadoRepository repoEmpleado;
    
    @Override
    public void guardar(Usuario usuario) {
        
        // 1. Verificamos si es MODIFICACION (tiene ID) y si la contraseña viene VACÍA
        if (usuario.getId() != null && (usuario.getPassword() == null || usuario.getPassword().isEmpty())) {
            
            // Buscamos al usuario original en la BD para recuperar su contraseña actual
            // (Usamos .get() porque sabemos que el ID existe si estamos editando)
            Usuario usuarioExistente = usuarioRepo.findById(usuario.getId()).get();
            
            // Le volvemos a poner la contraseña que ya tenía (que ya está encriptada)
            usuario.setPassword(usuarioExistente.getPassword());
            
        } else {
            // 2. CASO CONTRARIO: Es un usuario NUEVO o SÍ escribieron una contraseña nueva
            
            String passwordPlano = usuario.getPassword();
            
            // Encriptamos la nueva contraseña
            String passwordEncriptado = passwordEncoder.encode(passwordPlano);
            usuario.setPassword(passwordEncriptado);
        }

        // 3. Guardamos (JPA actualizará los datos, manteniendo la password correcta)
        usuarioRepo.save(usuario);
    }
    
    @Override
    public Usuario buscarPorUsername(String username) {
        Optional<Usuario> optional = usuarioRepo.findByUsername(username);
        if (optional.isPresent()) return optional.get();
        return null;
    }

    @Override
    public List<Usuario> buscarTodos() {
        return usuarioRepo.findAll();
    }
    
    @Override
    public Usuario buscarPorId(Integer idUsuario) {
        Optional<Usuario> optional = usuarioRepo.findById(idUsuario);
        if (optional.isPresent()) return optional.get();
        return null;
    }
    
    @Override
    public void eliminar(Integer idUsuario) {
        usuarioRepo.deleteById(idUsuario);
    }
    
    @Override
    public List<Usuario> findClientesSinAsignar() {
        return usuarioRepo.findClientesSinAsignar();
    }
    
    @Override
    @Transactional
    public void sincronizarRolUsuario(Usuario usuario) {

        boolean esCliente = usuario.getPerfiles().stream()
                .anyMatch(p -> p.getPerfil().equalsIgnoreCase("CLIENTE"));

        boolean esEmpleado = usuario.getPerfiles().stream()
                .anyMatch(p ->
                        p.getPerfil().equalsIgnoreCase("MESERO")     ||
                        p.getPerfil().equalsIgnoreCase("COCINERO")  ||
                        p.getPerfil().equalsIgnoreCase("SUPERVISOR")||
                        p.getPerfil().equalsIgnoreCase("EMPLEADO")  ||
                        p.getPerfil().equalsIgnoreCase("CAJERO")
                );

        Integer idUsuario = usuario.getId();

        // Si ahora es CLIENTE: borra registro de EMPLEADO (si existe)
        if (esCliente) {
            Empleado emp = repoEmpleado.findByUsuario_Id(idUsuario);
            if (emp != null) {
                repoEmpleado.delete(emp);
            }
            return;
        }

        // Si ahora es algún tipo de EMPLEADO: borra CLIENTE (si existe)
        if (esEmpleado) {
            Cliente cli = clienteRepo.findByUsuario_Id(idUsuario);
            if (cli != null) {
                clienteRepo.delete(cli);
            }
        }
    }
}