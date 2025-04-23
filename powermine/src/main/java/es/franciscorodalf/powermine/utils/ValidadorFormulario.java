package es.franciscorodalf.powermine.utils;

import java.util.regex.Pattern;

public class ValidadorFormulario {

    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern PATRON_ESPACIOS = Pattern.compile(".*\\s+.*");

    /**
     * Verifica si un campo está vacío o solo contiene espacios.
     *
     * @param texto Texto a validar
     * @return true si está vacío o nulo
     */
    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    /**
     * Verifica si el texto contiene espacios en blanco.
     *
     * @param texto Texto a validar
     * @return true si contiene uno o más espacios
     */
    public static boolean contieneEspacios(String texto) {
        return texto != null && PATRON_ESPACIOS.matcher(texto).matches();
    }

    /**
     * Valida si el texto tiene formato de correo electrónico válido.
     *
     * @param correo Texto a validar
     * @return true si es un correo válido
     */
    public static boolean esCorreoValido(String correo) {
        return correo != null && PATRON_CORREO.matcher(correo).matches();
    }

    /**
     * Verifica si dos contraseñas coinciden exactamente.
     *
     * @param pass1 Primera contraseña
     * @param pass2 Confirmación de contraseña
     * @return true si son iguales
     */
    public static boolean contrasenasCoinciden(String pass1, String pass2) {
        return pass1 != null && pass1.equals(pass2);
    }

    /**
     * Verifica si una contraseña cumple condiciones básicas.
     * (mínimo 4 caracteres, sin espacios).
     *
     * @param contrasena Contraseña a evaluar
     * @return true si es válida
     */
    public static boolean contrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= 4 && !contieneEspacios(contrasena);
    }
}
