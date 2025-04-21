package es.franciscorodalf.powermine.backend.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Partida {
    private int id;
    private int idUsuario;
    private String dificultad;
    private int puntaje;
    private boolean ganada;
    private LocalDateTime fecha;

    public Partida() {
    }

    public Partida(int id, int idUsuario, String dificultad, int puntaje, boolean ganada, LocalDateTime fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.dificultad = dificultad;
        this.puntaje = puntaje;
        this.ganada = ganada;
        this.fecha = fecha;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDificultad() {
        return this.dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public int getPuntaje() {
        return this.puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public boolean isGanada() {
        return this.ganada;
    }

    public boolean getGanada() {
        return this.ganada;
    }

    public void setGanada(boolean ganada) {
        this.ganada = ganada;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Partida id(int id) {
        setId(id);
        return this;
    }

    public Partida idUsuario(int idUsuario) {
        setIdUsuario(idUsuario);
        return this;
    }

    public Partida dificultad(String dificultad) {
        setDificultad(dificultad);
        return this;
    }

    public Partida puntaje(int puntaje) {
        setPuntaje(puntaje);
        return this;
    }

    public Partida ganada(boolean ganada) {
        setGanada(ganada);
        return this;
    }

    public Partida fecha(LocalDateTime fecha) {
        setFecha(fecha);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Partida)) {
            return false;
        }
        Partida partida = (Partida) o;
        return id == partida.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", idUsuario='" + getIdUsuario() + "'" +
                ", dificultad='" + getDificultad() + "'" +
                ", puntaje='" + getPuntaje() + "'" +
                ", ganada='" + isGanada() + "'" +
                ", fecha='" + getFecha() + "'" +
                "}";
    }

}
