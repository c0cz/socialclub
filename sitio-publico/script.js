// 1. La clase: un "molde" para crear actividades
class Actividad {
  constructor(nombre, disciplina, dias, horario, edadMin, edadMax, profesor, cuota) {
    this.nombre = nombre;
    this.disciplina = disciplina;
    this.dias = dias;
    this.horario = horario;
    this.edadMin = edadMin;
    this.edadMax = edadMax;
    this.profesor = profesor;
    this.cuota = cuota;
  }

  esAptaParaEdad(edad) {
    return edad >= this.edadMin && edad <= this.edadMax;
  }
}

// 2. El arreglo con las 8 actividades del enunciado
const actividades = [
  new Actividad("Fútbol infantil", "Fútbol", "Lun y Mié", "18:00", 6, 12, "Diego Ferreyra", 18000),
  new Actividad("Vóley mixto", "Vóley", "Mar y Jue", "19:00", 13, 60, "Laura Ceballos", 20000),
  new Actividad("Básquet juvenil", "Básquet", "Lun y Vie", "19:30", 12, 17, "Martín Oviedo", 19000),
  new Actividad("Natación", "Natación", "Mar y Jue", "17:00", 5, 70, "Sofía Bustos", 26000),
  new Actividad("Patín artístico", "Patín", "Mié y Vie", "17:30", 5, 15, "Carla Peralta", 21000),
  new Actividad("Ajedrez", "Ajedrez", "Sáb", "10:00", 8, 99, "Raúl Giménez", 9000),
  new Actividad("Funcional adultos", "Fitness", "Lun, Mié y Vie", "20:30", 18, 65, "Paula Luna", 22000),
  new Actividad("Yoga", "Bienestar", "Mar y Jue", "9:00", 16, 99, "Inés Arguello", 17000)
];

// 3. Referencias a elementos del DOM
const catalogo = document.getElementById("catalogo");
const filtroDisciplina = document.getElementById("filtroDisciplina");
const filtroEdad = document.getElementById("filtroEdad");

// 4. Dibujar las tarjetas
function mostrarActividades(lista) {
  catalogo.innerHTML = "";

  if (lista.length === 0) {
    catalogo.textContent = "No hay actividades para ese filtro.";
    return;
  }

  lista.forEach(function (act) {
    const tarjeta = document.createElement("article");
    tarjeta.classList.add("tarjeta");
    tarjeta.innerHTML =
      "<h3>" + act.nombre + "</h3>" +
      "<p>" + act.dias + " - " + act.horario + " hs</p>" +
      "<p>Edades: " + act.edadMin + " a " + act.edadMax + "</p>" +
      "<p>Profesor/a: " + act.profesor + "</p>" +
      "<p>Cuota: $ " + act.cuota.toLocaleString("es-AR") + "</p>";
    catalogo.appendChild(tarjeta);
  });
}

// 5. Llenar el select con las disciplinas (sin repetir)
function cargarDisciplinas() {
  const disciplinas = [];
  actividades.forEach(function (act) {
    if (!disciplinas.includes(act.disciplina)) {
      disciplinas.push(act.disciplina);
    }
  });
  disciplinas.forEach(function (d) {
    const opcion = document.createElement("option");
    opcion.value = d;
    opcion.textContent = d;
    filtroDisciplina.appendChild(opcion);
  });
}

// 6. Aplicar los filtros
function aplicarFiltros() {
  const disciplina = filtroDisciplina.value;
  const edad = parseInt(filtroEdad.value);

  const resultado = actividades.filter(function (act) {
    const okDisciplina = disciplina === "" || act.disciplina === disciplina;
    const okEdad = isNaN(edad) || act.esAptaParaEdad(edad);
    return okDisciplina && okEdad;
  });

  mostrarActividades(resultado);
}

// 7. Eventos y arranque (solo si estamos en actividades.html)
if (catalogo) {
  filtroDisciplina.addEventListener("change", aplicarFiltros);
  filtroEdad.addEventListener("input", aplicarFiltros);
  cargarDisciplinas();
  mostrarActividades(actividades);
}



// ----- PREINSCRIPCIÓN -----

// RN7: 7 u 8 dígitos numéricos, sin puntos
function esDniValido(dni) {
  return /^\d{7,8}$/.test(dni);
}

function esEmailValido(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

// Escribe el mensaje de error junto a su campo
function mostrarError(campo, mensaje) {
  document.getElementById("error-" + campo).textContent = mensaje;
}

const formulario = document.getElementById("formPreinscripcion");

if (formulario) {
  const selectActividad = document.getElementById("actividad");

  // Reutilizamos el arreglo de actividades para llenar el select
  actividades.forEach(function (act) {
    const opcion = document.createElement("option");
    opcion.value = act.nombre;
    opcion.textContent = act.nombre;
    selectActividad.appendChild(opcion);
  });

  formulario.addEventListener("submit", function (evento) {
    evento.preventDefault(); // evita que la página se recargue

    const nombre = document.getElementById("nombre").value.trim();
    const apellido = document.getElementById("apellido").value.trim();
    const dni = document.getElementById("dni").value.trim();
    const email = document.getElementById("email").value.trim();
    const actividad = selectActividad.value;

    let hayErrores = false;

    // Limpiamos errores anteriores
    ["nombre", "apellido", "dni", "email", "actividad"].forEach(function (c) {
      mostrarError(c, "");
    });
    document.getElementById("mensajeExito").textContent = "";

    if (nombre === "") {
      mostrarError("nombre", "El nombre es obligatorio");
      hayErrores = true;
    }
    if (apellido === "") {
      mostrarError("apellido", "El apellido es obligatorio");
      hayErrores = true;
    }
    if (!esDniValido(dni)) {
      mostrarError("dni", "El DNI debe tener 7 u 8 números, sin puntos");
      hayErrores = true;
    }
    if (!esEmailValido(email)) {
      mostrarError("email", "Ingresá un email válido");
      hayErrores = true;
    }
    if (actividad === "") {
      mostrarError("actividad", "Elegí una actividad");
      hayErrores = true;
    }

    if (!hayErrores) {
      document.getElementById("mensajeExito").textContent =
        "¡Gracias " + nombre + "! Recibimos tu preinscripción.";
      formulario.reset();
    }
  });

  // Objeto del navegador: history
  document.getElementById("btnVolver").addEventListener("click", function () {
    history.back();
  });
}