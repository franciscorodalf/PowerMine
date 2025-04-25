package es.franciscorodalf.powermine.backend.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase que representa los poderes especiales disponibles en el juego.
 * Define los diferentes tipos de poderes, sus características y costos.
 */
public class Poder {
    private final int id;
    private final String nombre;
    private final String emoji;
    private final String descripcion;
    private final int costePuntos;
    private final Rareza rareza;

    /**
     * Enumeración de niveles de rareza para los poderes
     */
    public enum Rareza {
        COMUN("Común", "⚪"),
        RARO("Raro", "🔵"),
        EPICO("Épico", "🟣"),
        LEGENDARIO("Legendario", "🟡");

        private final String nombre;
        public final String emoji;

        Rareza(String nombre, String emoji) {
            this.nombre = nombre;
            this.emoji = emoji;
        }
    }

    public static final Poder RADAR = new Poder(1, "Radar", "🔍", "Revela las bombas en un área de 5x5", 30,
            Rareza.COMUN);
    public static final Poder ESCUDO = new Poder(2, "Escudo", "🛡️", "Protege dos casillas aleatorias", 50,
            Rareza.RARO);
    public static final Poder DESMINADOR = new Poder(3, "Desminador", "💪", "Elimina una mina aleatoria", 40,
            Rareza.COMUN);
    public static final Poder VISION = new Poder(4, "Visión", "👁️", "Revela una zona segura", 35, Rareza.COMUN);
    public static final Poder TSUNAMI = new Poder(5, "Tsunami", "🌊", "Limpia toda una fila", 60, Rareza.EPICO);
    public static final Poder TERREMOTO = new Poder(6, "Terremoto", "🌋", "Limpia toda una columna", 60, Rareza.EPICO);

    /**
     * Constructor privado para crear nuevos poderes
     * 
     * @param id          Identificador único del poder
     * @param nombre      Nombre del poder
     * @param emoji       Emoji representativo
     * @param descripcion Descripción del efecto
     * @param costePuntos Costo en puntos para usar
     * @param rareza      Nivel de rareza del poder
     */
    private Poder(int id, String nombre, String emoji, String descripcion, int costePuntos, Rareza rareza) {
        this.id = id;
        this.nombre = nombre;
        this.emoji = emoji;
        this.descripcion = descripcion;
        this.costePuntos = costePuntos;
        this.rareza = rareza;
    }

    // Añadir método para buscar poder por ID
    public static Poder getPorId(int id) {
        for (Poder poder : getTodosLosPoderes()) {
            if (poder.getId() == id) {
                return poder;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCostePuntos() {
        return costePuntos;
    }

    public Rareza getRareza() {
        return rareza;
    }

    public static Poder[] getTodosLosPoderes() {
        return new Poder[] { RADAR, ESCUDO, DESMINADOR, VISION, TSUNAMI, TERREMOTO };
    }

    public static List<Poder> getPoderesPorRareza(Rareza rareza) {
        return Arrays.stream(getTodosLosPoderes())
                .filter(p -> p.getRareza() == rareza)
                .collect(Collectors.toList());
    }

    public String getInfoCompleta() {
        return String.format("%s %s (%s)\nCoste: %d puntos\n%s",
                emoji, nombre, rareza.emoji,
                costePuntos, descripcion);
    }
}
