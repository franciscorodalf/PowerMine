package es.franciscorodalf.powermine.backend.model;
import java.util.Objects;

public class Usuario {
    private int id;
    private String nombreUsuario;
    private String correo;
    private String contrasenia;


    public Usuario() {
    }

    public Usuario(int id, String nombreUsuario, String correo, String contrasenia) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return this.nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return this.correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return this.contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public Usuario id(int id) {
        setId(id);
        return this;
    }

    public Usuario nombreUsuario(String nombreUsuario) {
        setNombreUsuario(nombreUsuario);
        return this;
    }

    public Usuario correo(String correo) {
        setCorreo(correo);
        return this;
    }

    public Usuario contrasenia(String contrasenia) {
        setContrasenia(contrasenia);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Usuario)) {
            return false;
        }
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", nombreUsuario='" + getNombreUsuario() + "'" +
            ", correo='" + getCorreo() + "'" +
            ", contrasenia='" + getContrasenia() + "'" +
            "}";
    }
    
}
