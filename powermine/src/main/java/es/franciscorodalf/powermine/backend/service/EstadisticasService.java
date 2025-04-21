package es.franciscorodalf.powermine.backend.service;

import es.franciscorodalf.powermine.backend.dao.PartidaDAO;

public class EstadisticasService {

    private final PartidaDAO partidaDAO;

    public EstadisticasService() {
        this.partidaDAO = new PartidaDAO();
    }

    public int obtenerPartidasJugadas(int idUsuario) {
        return partidaDAO.obtenerPartidasPorUsuario(idUsuario).size();
    }

    public int obtenerPartidasGanadas(int idUsuario) {
        return partidaDAO.contarPartidasGanadas(idUsuario);
    }

    public int obtenerPuntajeTotal(int idUsuario) {
        return partidaDAO.obtenerPuntajeTotal(idUsuario);
    }
}
