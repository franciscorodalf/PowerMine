package es.franciscorodalf.powermine.backend.model;

import java.util.Objects;

public class Poder {
    private int id;
    private String nombre;
    private String descripcion;

    public Poder() {
    }

    public Poder(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Poder id(int id) {
        setId(id);
        return this;
    }

    public Poder nombre(String nombre) {
        setNombre(nombre);
        return this;
    }

    public Poder descripcion(String descripcion) {
        setDescripcion(descripcion);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Poder)) {
            return false;
        }
        Poder poder = (Poder) o;
        return id == poder.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", nombre='" + getNombre() + "'" +
                ", descripcion='" + getDescripcion() + "'" +
                "}";
    }

}
