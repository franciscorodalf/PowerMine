package es.franciscorodalf.powermine.backend.model;

import javafx.beans.property.*;

/**
 * Clase que representa una entrada en el ranking de jugadores.
 * Utiliza propiedades JavaFX para binding bidireccional con la interfaz.
 */
public class RankingUsuario {
    /**
     * Posición del usuario en el ranking
     */
    private final IntegerProperty posicion;

    /**
     * Nombre del usuario
     */
    private final StringProperty nombreUsuario;

    /**
     * Puntuación total acumulada
     */
    private final IntegerProperty puntajeTotal;

    /**
     * Número de partidas ganadas
     */
    private final IntegerProperty victorias;

    public RankingUsuario(int posicion, String nombreUsuario, int puntajeTotal, int victorias) {
        this.posicion = new SimpleIntegerProperty(posicion);
        this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
        this.puntajeTotal = new SimpleIntegerProperty(puntajeTotal);
        this.victorias = new SimpleIntegerProperty(victorias);
    }

    public IntegerProperty posicionProperty() {
        return posicion;
    }

    public StringProperty nombreUsuarioProperty() {
        return nombreUsuario;
    }

    public IntegerProperty puntajeTotalProperty() {
        return puntajeTotal;
    }

    public IntegerProperty victoriasProperty() {
        return victorias;
    }
}
