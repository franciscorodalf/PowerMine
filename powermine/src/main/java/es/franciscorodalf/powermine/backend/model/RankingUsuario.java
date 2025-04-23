package es.franciscorodalf.powermine.backend.model;

import javafx.beans.property.*;

public class RankingUsuario {
    private final IntegerProperty posicion;
    private final StringProperty nombreUsuario;
    private final IntegerProperty puntajeTotal;
    private final IntegerProperty victorias;

    public RankingUsuario(int posicion, String nombreUsuario, int puntajeTotal, int victorias) {
        this.posicion = new SimpleIntegerProperty(posicion);
        this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
        this.puntajeTotal = new SimpleIntegerProperty(puntajeTotal);
        this.victorias = new SimpleIntegerProperty(victorias);
    }

    public IntegerProperty posicionProperty() { return posicion; }
    public StringProperty nombreUsuarioProperty() { return nombreUsuario; }
    public IntegerProperty puntajeTotalProperty() { return puntajeTotal; }
    public IntegerProperty victoriasProperty() { return victorias; }
}
