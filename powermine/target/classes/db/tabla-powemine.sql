-- Crear tabla de usuarios
CREATE TABLE usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_usuario TEXT NOT NULL UNIQUE,
    correo TEXT NOT NULL UNIQUE,
    contrasenia TEXT NOT NULL
);

-- Crear tabla de partidas
CREATE TABLE partidas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    dificultad TEXT NOT NULL, -- 'Fácil', 'Medio', 'Difícil'
    puntaje INTEGER NOT NULL,
    ganada BOOLEAN NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
);

-- Crear tabla de poderes disponibles en el juego
CREATE TABLE poderes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE,
    descripcion TEXT
);

-- Crear tabla que registra los poderes usados en una partida
CREATE TABLE poderes_usados (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_partida INTEGER NOT NULL,
    nombre_poder TEXT NOT NULL,
    turno INTEGER,
    FOREIGN KEY (id_partida) REFERENCES partidas(id)
);

-- Crear vista con estadísticas generales por usuario
CREATE VIEW estadisticas_usuario AS
SELECT 
    u.id AS id_usuario,
    u.nombre_usuario,
    COUNT(p.id) AS partidas_jugadas,
    SUM(CASE WHEN p.ganada THEN 1 ELSE 0 END) AS partidas_ganadas,
    SUM(p.puntaje) AS puntaje_total
FROM usuarios u
LEFT JOIN partidas p ON u.id = p.id_usuario
GROUP BY u.id;
