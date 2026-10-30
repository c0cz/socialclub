-- ACTIVIDADES
INSERT IGNORE INTO actividad (nombre, disciplina, dias, horario, edad_minima, edad_maxima, cupo_maximo, cuota_mensual, profesor) VALUES
('Fútbol infantil', 'Fútbol', 'Lun y Mié', '18:00', 6, 12, 40, 18000, 'Diego Ferreyra'),
('Vóley mixto', 'Vóley', 'Mar y Jue', '19:00', 13, 60, 24, 20000, 'Laura Ceballos'),
('Básquet juvenil', 'Básquet', 'Lun y Vie', '19:30', 12, 17, 20, 19000, 'Martín Oviedo'),
('Natación', 'Natación', 'Mar y Jue', '17:00', 5, 70, 30, 26000, 'Sofía Bustos'),
('Patín artístico', 'Patín', 'Mié y Vie', '17:30', 5, 15, 25, 21000, 'Carla Peralta'),
('Ajedrez', 'Ajedrez', 'Sáb', '10:00', 8, 99, 16, 9000, 'Raúl Giménez'),
('Funcional adultos', 'Fitness', 'Lun, Mié y Vie', '20:30', 18, 65, 30, 22000, 'Paula Luna'),
('Yoga', 'Bienestar', 'Mar y Jue', '9:00', 16, 99, 15, 17000, 'Inés Arguello');

-- SOCIOS (dos MOROSOS: 39789012 y 33890123)
INSERT IGNORE INTO socio (dni, nombre, apellido, fecha_nacimiento, email, telefono, estado_cuota) VALUES
('30111222', 'Lucía', 'Fernández', '1995-03-14', 'lucia@mail.com', '3515550001', 'AL_DIA'),
('32222333', 'Martín', 'Gómez', '1988-07-02', 'martin@mail.com', '3515550002', 'AL_DIA'),
('40333444', 'Camila', 'Rojas', '2000-11-21', 'camila@mail.com', '3515550003', 'AL_DIA'),
('28444555', 'Jorge', 'Paz', '1975-05-30', 'jorge@mail.com', '3515550004', 'AL_DIA'),
('45555666', 'Valentina', 'Ortiz', '2014-02-10', 'valentina@mail.com', '3515550005', 'AL_DIA'),
('46666777', 'Thiago', 'Medina', '2016-08-19', 'thiago@mail.com', '3515550006', 'AL_DIA'),
('47777888', 'Sofía', 'Luna', '2019-06-05', 'sofia@mail.com', '3515550007', 'AL_DIA'),
('44888999', 'Agustín', 'Vega', '2010-09-12', 'agustin@mail.com', '3515550008', 'AL_DIA'),
('43999000', 'Mía', 'Acosta', '2011-12-01', 'mia@mail.com', '3515550009', 'AL_DIA'),
('25123456', 'Rosa', 'Benítez', '1962-01-25', 'rosa@mail.com', '3515550010', 'AL_DIA'),
('22234567', 'Héctor', 'Silva', '1955-04-09', 'hector@mail.com', '3515550011', 'AL_DIA'),
('38345678', 'Julieta', 'Castro', '1998-10-03', 'julieta@mail.com', '3515550012', 'AL_DIA'),
('41456789', 'Nicolás', 'Ríos', '2002-02-17', 'nicolas@mail.com', '3515550013', 'AL_DIA'),
('35567890', 'Paula', 'Domínguez', '1992-06-28', 'paula@mail.com', '3515550014', 'AL_DIA'),
('48678901', 'Bruno', 'Sosa', '2013-03-23', 'bruno@mail.com', '3515550015', 'AL_DIA'),
('39789012', 'Carolina', 'Mansilla', '1996-09-09', 'carolina@mail.com', '3515550016', 'MOROSO'),
('33890123', 'Ramiro', 'Quiroga', '1990-12-15', 'ramiro@mail.com', '3515550017', 'MOROSO'),
('42901234', 'Florencia', 'Aguirre', '2001-04-04', 'florencia@mail.com', '3515550018', 'AL_DIA');

-- INSCRIPCIONES: Ajedrez con 15 de 16 lugares ocupados
INSERT INTO inscripcion (fecha_inscripcion, estado, socio_id, actividad_id)
SELECT CURDATE(), 'ACTIVA', s.id, a.id FROM socio s, actividad a
WHERE a.nombre = 'Ajedrez'
  AND s.dni IN ('30111222','32222333','40333444','28444555','45555666','46666777','44888999','43999000','25123456','22234567','38345678','41456789','35567890','48678901','42901234')
  AND NOT EXISTS (SELECT 1 FROM inscripcion i WHERE i.socio_id = s.id AND i.actividad_id = a.id);

-- Otras inscripciones (socio Camila Rojas queda con 3 actividades, para probar RN5)
INSERT INTO inscripcion (fecha_inscripcion, estado, socio_id, actividad_id)
SELECT CURDATE(), 'ACTIVA', s.id, a.id FROM socio s, actividad a
WHERE (
      (a.nombre = 'Fútbol infantil' AND s.dni IN ('45555666','46666777'))
   OR (a.nombre = 'Vóley mixto' AND s.dni IN ('40333444','38345678'))
   OR (a.nombre = 'Funcional adultos' AND s.dni IN ('40333444','35567890'))
   OR (a.nombre = 'Natación' AND s.dni IN ('47777888','25123456'))
   OR (a.nombre = 'Yoga' AND s.dni = '39789012')
)
AND NOT EXISTS (SELECT 1 FROM inscripcion i WHERE i.socio_id = s.id AND i.actividad_id = a.id);