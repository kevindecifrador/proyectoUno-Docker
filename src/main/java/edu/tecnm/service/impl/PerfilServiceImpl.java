package edu.tecnm.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.tecnm.model.Perfil;
import edu.tecnm.repository.PerfilRepository;
import edu.tecnm.service.IPerfilService;

@Service
public class PerfilServiceImpl implements IPerfilService {

    @Autowired
    private PerfilRepository perfilRepo;

    @Override
    public List<Perfil> buscarTodos() {
        return perfilRepo.findAll();
    }

    @Override
    public Perfil buscarPorId(Integer idPerfil) {
        Optional<Perfil> optional = perfilRepo.findById(idPerfil);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    public void guardar(Perfil perfil) {
        perfilRepo.save(perfil);
    }
}