package edu.tecnm.service.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import edu.tecnm.model.Mesa;
import edu.tecnm.repository.MesaRepository;
import edu.tecnm.service.IMesaService;

@Service
@Primary
public class MesaServiceJpa implements IMesaService {

    @Autowired
    private MesaRepository repoMesa;

    @Override
    public List<Mesa> buscarTodas() {
        return repoMesa.findAll();
    }

    @Override
    public Mesa buscarPorId(Integer idMesa) {
        Optional<Mesa> optional = repoMesa.findById(idMesa);
        return optional.orElse(null);
    }

    @Override
    public void guardar(Mesa mesa) {
        repoMesa.save(mesa);
    }

    @Override
    public void eliminar(Integer idMesa) {
        repoMesa.deleteById(idMesa);
    }
}