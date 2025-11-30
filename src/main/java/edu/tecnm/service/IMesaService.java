package edu.tecnm.service;

import java.util.List;
import edu.tecnm.model.Mesa;

public interface IMesaService {
    List<Mesa> buscarTodas();
    Mesa buscarPorId(Integer idMesa);
    void guardar(Mesa mesa);
    void eliminar(Integer idMesa);
}