import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

/** Coordina las estructuras de datos y las operaciones del sistema. */
public class Universidad {
    private final HashMap<String, Estudiante> estudiantes = new HashMap<>();
    private final HashMap<String, Materia> materias = new HashMap<>();
    private final TreeMap<String, Aula> aulas = new TreeMap<>();
    private final Facultad[] facultades = new Facultad[5];
    private final Edificios edificios = new Edificios();
    private final Stack<Operacion> pilaDeshacer = new Stack<>();
    private final Stack<Operacion> pilaRehacer = new Stack<>();
    private final Stack<String> reportesAtras = new Stack<>();
    private final Stack<String> reportesAdelante = new Stack<>();
    private final Queue<String[]> solicitudesBatch = new ArrayDeque<>();

    public void agregarFacultad(Facultad facultad) {
        for (int i = 0; i < facultades.length; i++) {
            if (facultades[i] == null) {
                facultades[i] = facultad;
                return;
            }
        }
        throw new IllegalStateException("Solo se permiten cinco facultades");
    }

    public void registrarEstudiante(Estudiante estudiante) {
        if (estudiantes.containsKey(estudiante.getId())) {
            throw new IllegalArgumentException("Ya existe un estudiante con ese ID");
        }
        estudiantes.put(estudiante.getId(), estudiante);
        pilaDeshacer.push(new Operacion("Registrar estudiante",
                () -> estudiantes.remove(estudiante.getId()),
                () -> estudiantes.put(estudiante.getId(), estudiante)));
        pilaRehacer.clear();
    }

    public Estudiante buscarEstudiante(String id) {
        Estudiante estudiante = estudiantes.get(id);
        if (estudiante == null) {
            throw new IllegalArgumentException("No existe estudiante con ID: " + id);
        }
        return estudiante;
    }

    public void eliminarEstudiante(String id) {
        Estudiante estudiante = buscarEstudiante(id);
        estudiantes.remove(id);
        pilaDeshacer.push(new Operacion("Eliminar estudiante",
                () -> estudiantes.put(id, estudiante),
                () -> estudiantes.remove(id)));
        pilaRehacer.clear();
    }

    public Collection<Estudiante> listarEstudiantes() {
        return new ArrayList<>(estudiantes.values());
    }

    public void crearMateria(Materia materia) {
        if (materias.containsKey(materia.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una materia con ese código");
        }
        materias.put(materia.getCodigo(), materia);
    }

    public Materia buscarMateria(String codigo) {
        Materia materia = materias.get(codigo);
        if (materia == null) {
            throw new IllegalArgumentException("No existe materia con código: " + codigo);
        }
        return materia;
    }

    public void agregarPrerequisito(String codigoMateria, String codigoPrerequisito) {
        Materia materia = buscarMateria(codigoMateria);
        buscarMateria(codigoPrerequisito);
        if (codigoMateria.equals(codigoPrerequisito)) {
            throw new IllegalArgumentException("Una materia no puede ser prerrequisito de sí misma");
        }
        materia.agregarPrerequisito(codigoPrerequisito);
    }

    public void inscribir(String idEstudiante, String codigoMateria) {
        Estudiante estudiante = buscarEstudiante(idEstudiante);
        Materia materia = buscarMateria(codigoMateria);
        if (!materia.cumplePrerequisitos(estudiante)) {
            throw new IllegalStateException("No cumple los prerrequisitos: " + materia.getPrerequisitos());
        }

        boolean obtuvoCupo = materia.inscribir(idEstudiante);
        if (obtuvoCupo) {
            pilaDeshacer.push(new Operacion("Inscribir estudiante",
                    () -> materia.cancelar(idEstudiante),
                    () -> materia.inscribir(idEstudiante)));
        } else {
            pilaDeshacer.push(new Operacion("Agregar a la cola de espera",
                    () -> materia.retirarDeEspera(idEstudiante),
                    () -> materia.inscribir(idEstudiante)));
        }
        pilaRehacer.clear();
    }

    public void cancelarInscripcion(String idEstudiante, String codigoMateria) {
        Materia materia = buscarMateria(codigoMateria);
        if (!materia.cancelar(idEstudiante)) {
            throw new IllegalArgumentException("El estudiante no estaba inscrito");
        }
        String promovido = materia.promoverEspera();

        pilaDeshacer.push(new Operacion("Cancelar inscripción",
                () -> {
                    if (promovido != null) {
                        materia.cancelar(promovido);
                    }
                    materia.inscribir(idEstudiante);
                    if (promovido != null) {
                        materia.devolverAEsperaAlFrente(promovido);
                    }
                },
                () -> {
                    materia.cancelar(idEstudiante);
                    materia.promoverEspera();
                }));
        pilaRehacer.clear();
    }

    public void crearAula(String nombre, int capacidad) {
        if (aulas.containsKey(nombre)) {
            throw new IllegalArgumentException("Ya existe un aula con ese nombre");
        }
        aulas.put(nombre, new Aula(nombre, capacidad));
    }

    public Aula buscarAula(String nombre) {
        Aula aula = aulas.get(nombre);
        if (aula == null) {
            throw new IllegalArgumentException("No existe aula: " + nombre);
        }
        return aula;
    }

    public Collection<Aula> listarAulas() {
        return new ArrayList<>(aulas.values());
    }

    public void reservarHorario(String nombreAula, int dia, int hora, int duracion) {
        Aula aula = buscarAula(nombreAula);
        aula.reservar(dia, hora, duracion);
        pilaDeshacer.push(new Operacion("Reservar horario",
                () -> aula.liberar(dia, hora, duracion),
                () -> aula.reservar(dia, hora, duracion)));
        pilaRehacer.clear();
    }

    public void liberarHorario(String nombreAula, int dia, int hora, int duracion) {
        Aula aula = buscarAula(nombreAula);
        aula.liberar(dia, hora, duracion);
        pilaDeshacer.push(new Operacion("Liberar horario",
                () -> aula.reservar(dia, hora, duracion),
                () -> aula.liberar(dia, hora, duracion)));
        pilaRehacer.clear();
    }

    public Edificios getEdificios() {
        return edificios;
    }

    public void registrarNota(String id, int semestre, String codigo, double nota) {
        Estudiante estudiante = buscarEstudiante(id);
        Double notaAnterior = estudiante.obtenerNota(semestre, codigo);
        estudiante.registrarNota(semestre, codigo, nota);

        pilaDeshacer.push(new Operacion("Registrar nota",
                () -> {
                    if (notaAnterior == null) {
                        estudiante.eliminarNota(semestre, codigo);
                    } else {
                        estudiante.registrarNota(semestre, codigo, notaAnterior);
                    }
                },
                () -> estudiante.registrarNota(semestre, codigo, nota)));
        pilaRehacer.clear();
    }

    public void guardarReporte(String reporte) {
        reportesAtras.push(reporte);
        reportesAdelante.clear();
    }

    public String reporteAnterior() {
        if (reportesAtras.size() < 2) {
            return "No hay reporte anterior";
        }
        reportesAdelante.push(reportesAtras.pop());
        return reportesAtras.peek();
    }

    public String reporteSiguiente() {
        if (reportesAdelante.empty()) {
            return "No hay reporte siguiente";
        }
        String reporte = reportesAdelante.pop();
        reportesAtras.push(reporte);
        return reporte;
    }

    public String mostrarColaEspera(String codigoMateria) {
        return buscarMateria(codigoMateria).esperaTexto();
    }

    public void deshacer() {
        if (pilaDeshacer.empty()) {
            throw new IllegalStateException("No hay operaciones para deshacer");
        }
        Operacion operacion = pilaDeshacer.pop();
        operacion.deshacer();
        pilaRehacer.push(operacion);
    }

    public void rehacer() {
        if (pilaRehacer.empty()) {
            throw new IllegalStateException("No hay operaciones para rehacer");
        }
        Operacion operacion = pilaRehacer.pop();
        operacion.rehacer();
        pilaDeshacer.push(operacion);
    }

    public void limpiarHistorial() {
        pilaDeshacer.clear();
        pilaRehacer.clear();
    }

    public void encolarSolicitud(String idEstudiante, String codigoMateria) {
        solicitudesBatch.add(new String[]{idEstudiante, codigoMateria});
    }

    public String procesarBatch() {
        int exitosas = 0;
        int fallidas = 0;
        StringBuilder detalleFallidas = new StringBuilder();
        while (!solicitudesBatch.isEmpty()) {
            String[] solicitud = solicitudesBatch.remove();
            try {
                inscribir(solicitud[0], solicitud[1]);
                exitosas++;
            } catch (RuntimeException error) {
                fallidas++;
                detalleFallidas.append(solicitud[0]).append(" -> ")
                        .append(solicitud[1]).append(": ").append(error.getMessage()).append('\n');
            }
        }
        return "Exitosas: " + exitosas + " | Fallidas: " + fallidas
                + (fallidas == 0 ? "" : "\n" + detalleFallidas);
    }

    public String procesarArchivoCsv(String ruta) throws Excepciones.ArchivoInvalidoException {
        int solicitudesLeidas = 0;
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                String[] campos = linea.split(",", -1);
                if (campos.length != 2 || campos[0].trim().isEmpty() || campos[1].trim().isEmpty()) {
                    throw new Excepciones.ArchivoInvalidoException(
                            "Formato inválido en la línea " + numeroLinea + ". Use ID,CODIGO_MATERIA");
                }
                encolarSolicitud(campos[0].trim(), campos[1].trim());
                solicitudesLeidas++;
            }
        } catch (IOException error) {
            throw new Excepciones.ArchivoInvalidoException("No se pudo leer el archivo: " + error.getMessage());
        }
        return "Solicitudes leídas: " + solicitudesLeidas + "\n" + procesarBatch();
    }
}
