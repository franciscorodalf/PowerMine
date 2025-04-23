package es.franciscorodalf.powermine.backend.model;

public class Poder {
    private final int id;
    private final String nombre;
    private final String emoji;
    private final String descripcion;

    public static final Poder RADAR = new Poder(1, "Radar", "🔍", "Revela las bombas cercanas");
    public static final Poder ESCUDO = new Poder(2, "Escudo", "🛡️", "Convierte una bomba en casilla segura");
    public static final Poder DESMINADOR = new Poder(3, "Desminador", "💪", "Elimina una mina aleatoria del tablero");
    public static final Poder VISION = new Poder(4, "Visión", "👁️", "Revela una casilla segura");
    public static final Poder FILA = new Poder(5, "Fila", "↔️", "Limpia toda la fila seleccionada");
    public static final Poder COLUMNA = new Poder(6, "Columna", "↕️", "Limpia toda la columna seleccionada");

    private Poder(int id, String nombre, String emoji, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.emoji = emoji;
        this.descripcion = descripcion;
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

    public static Poder[] getTodosLosPoderes() {
        return new Poder[] { RADAR, ESCUDO, DESMINADOR, VISION, FILA, COLUMNA };
    }
}
