import java.util.Scanner;

/** Menú de consola para ejecutar y probar las operaciones principales. */
public class Main {
    public static void main(String[] args) {
        Universidad universidad = new Universidad();
        cargarDatosIniciales(universidad);

        Scanner entrada = new Scanner(System.in);
        System.out.println("Datos de prueba cargados.");
        System.out.println(universidad.buscarEstudiante("2024001").mostrarInformacion());
        mostrarAulas(universidad);

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            System.out.print("Opción: ");
            String opcion = entrada.nextLine();
            try {
                salir = ejecutarOpcion(opcion, universidad, entrada);
            } catch (Exception error) {
                System.out.println("Error: " + error.getMessage());
            }
        }
        entrada.close();
    }

    private static void mostrarMenu() {
        System.out.println("\nSISTEMA DE GESTIÓN ACADÉMICA");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Buscar estudiante por ID");
        System.out.println("3. Listar estudiantes");
        System.out.println("4. Eliminar estudiante");
        System.out.println("5. Crear materia");
        System.out.println("6. Agregar prerrequisito");
        System.out.println("7. Inscribir estudiante");
        System.out.println("8. Cancelar inscripción");
        System.out.println("9. Registrar nota");
        System.out.println("10. Ver reporte académico");
        System.out.println("11. Crear aula");
        System.out.println("12. Reservar horario");
        System.out.println("13. Buscar ruta entre edificios");
        System.out.println("14. Deshacer");
        System.out.println("15. Rehacer");
        System.out.println("16. Listar aulas y capacidades");
        System.out.println("17. Procesar solicitudes desde CSV");
        System.out.println("18. Mostrar cola de espera de una materia");
        System.out.println("19. Ver reporte anterior");
        System.out.println("20. Ver reporte siguiente");
        System.out.println("21. Liberar horario de aula");
        System.out.println("22. Agregar o cambiar conexión entre edificios");
        System.out.println("23. Salir");
    }

    private static boolean ejecutarOpcion(String opcion, Universidad universidad, Scanner entrada) throws Exception {
        switch (opcion) {
            case "1": {
                System.out.print("ID: ");
                String id = entrada.nextLine();
                System.out.print("Nombre: ");
                String nombre = entrada.nextLine();
                System.out.print("Correo: ");
                String correo = entrada.nextLine();
                System.out.print("Semestre: ");
                int semestre = Integer.parseInt(entrada.nextLine());
                universidad.registrarEstudiante(new Estudiante(nombre, id, correo, semestre));
                System.out.println("Estudiante registrado.");
                break;
            }
            case "2": {
                System.out.print("ID: ");
                System.out.println(universidad.buscarEstudiante(entrada.nextLine()).mostrarInformacion());
                break;
            }
            case "3": {
                for (Estudiante estudiante : universidad.listarEstudiantes()) {
                    System.out.println(estudiante.mostrarInformacion());
                }
                break;
            }
            case "4": {
                System.out.print("ID: ");
                universidad.eliminarEstudiante(entrada.nextLine());
                System.out.println("Estudiante eliminado.");
                break;
            }
            case "5": {
                System.out.print("Código: ");
                String codigo = entrada.nextLine();
                System.out.print("Nombre: ");
                String nombre = entrada.nextLine();
                System.out.print("Cupos: ");
                int cupos = Integer.parseInt(entrada.nextLine());
                System.out.print("Créditos: ");
                int creditos = Integer.parseInt(entrada.nextLine());
                universidad.crearMateria(new Materia(codigo, nombre, cupos, creditos));
                System.out.println("Materia creada.");
                break;
            }
            case "6": {
                System.out.print("Código de la materia: ");
                String codigoMateria = entrada.nextLine();
                System.out.print("Código del prerrequisito: ");
                universidad.agregarPrerequisito(codigoMateria, entrada.nextLine());
                System.out.println("Prerrequisito agregado.");
                break;
            }
            case "7": {
                System.out.print("ID del estudiante: ");
                String id = entrada.nextLine();
                System.out.print("Código de la materia: ");
                universidad.inscribir(id, entrada.nextLine());
                System.out.println("Solicitud procesada. Si no hay cupos, queda en espera.");
                break;
            }
            case "8": {
                System.out.print("ID del estudiante: ");
                String id = entrada.nextLine();
                System.out.print("Código de la materia: ");
                universidad.cancelarInscripcion(id, entrada.nextLine());
                System.out.println("Inscripción cancelada.");
                break;
            }
            case "9": {
                System.out.print("ID del estudiante: ");
                String id = entrada.nextLine();
                System.out.print("Semestre: ");
                int semestre = Integer.parseInt(entrada.nextLine());
                System.out.print("Código de la materia: ");
                String codigo = entrada.nextLine();
                System.out.print("Nota de 0 a 5: ");
                double nota = Double.parseDouble(entrada.nextLine());
                universidad.registrarNota(id, semestre, codigo, nota);
                System.out.println("Nota registrada.");
                break;
            }
            case "10": {
                System.out.print("ID del estudiante: ");
                String id = entrada.nextLine();
                String reporte = universidad.buscarEstudiante(id).reporte();
                universidad.guardarReporte(reporte);
                System.out.println(reporte);
                break;
            }
            case "11": {
                System.out.print("Código/nombre del aula: ");
                String nombre = entrada.nextLine();
                System.out.print("Capacidad: ");
                int capacidad = Integer.parseInt(entrada.nextLine());
                universidad.crearAula(nombre, capacidad);
                System.out.println("Aula creada.");
                break;
            }
            case "12": {
                System.out.print("Aula: ");
                Aula aula = universidad.buscarAula(entrada.nextLine());
                System.out.print("Día (0 domingo a 6 sábado): ");
                int dia = Integer.parseInt(entrada.nextLine());
                System.out.print("Hora (0 a 23): ");
                int hora = Integer.parseInt(entrada.nextLine());
                System.out.print("Duración en horas: ");
                int duracion = Integer.parseInt(entrada.nextLine());
                universidad.reservarHorario(aula.getNombre(), dia, hora, duracion);
                System.out.println("Horario reservado.");
                break;
            }
            case "13": {
                System.out.print("Edificio de origen: ");
                String origen = entrada.nextLine();
                System.out.print("Edificio de destino: ");
                System.out.println(universidad.getEdificios().ruta(origen, entrada.nextLine()));
                break;
            }
            case "14": {
                universidad.deshacer();
                System.out.println("Operación deshecha.");
                break;
            }
            case "15": {
                universidad.rehacer();
                System.out.println("Operación rehecha.");
                break;
            }
            case "16": {
                mostrarAulas(universidad);
                break;
            }
            case "17": {
                System.out.print("Ruta del archivo CSV: ");
                System.out.println(universidad.procesarArchivoCsv(entrada.nextLine()));
                break;
            }
            case "18": {
                System.out.print("Código de la materia: ");
                System.out.println(universidad.mostrarColaEspera(entrada.nextLine()));
                break;
            }
            case "19": {
                System.out.println(universidad.reporteAnterior());
                break;
            }
            case "20": {
                System.out.println(universidad.reporteSiguiente());
                break;
            }
            case "21": {
                System.out.print("Aula: ");
                Aula aula = universidad.buscarAula(entrada.nextLine());
                System.out.print("Día (0 domingo a 6 sábado): ");
                int dia = Integer.parseInt(entrada.nextLine());
                System.out.print("Hora (0 a 23): ");
                int hora = Integer.parseInt(entrada.nextLine());
                System.out.print("Duración en horas: ");
                int duracion = Integer.parseInt(entrada.nextLine());
                universidad.liberarHorario(aula.getNombre(), dia, hora, duracion);
                System.out.println("Horario liberado.");
                break;
            }
            case "22": {
                System.out.print("Primer edificio: ");
                String edificioA = entrada.nextLine();
                System.out.print("Segundo edificio: ");
                String edificioB = entrada.nextLine();
                System.out.print("Distancia en metros: ");
                int metros = Integer.parseInt(entrada.nextLine());
                universidad.getEdificios().conectar(edificioA, edificioB, metros);
                System.out.println("Conexión guardada.");
                break;
            }
            case "23": {
                return true;
            }
            default: {
                System.out.println("Opción no válida.");
                break;
            }
        }
        return false;
    }

    private static void cargarDatosIniciales(Universidad universidad) {
        Estudiante ana = new Estudiante("Ana Maria Gomez", "2024001", "ana@universidad.edu", 3);
        ana.registrarNota(1, "PROG101", 4.5);
        ana.registrarNota(1, "MAT101", 2.8);
        universidad.registrarEstudiante(ana);

        universidad.registrarEstudiante(
                new Estudiante("Luis Perez", "2024002", "luis@universidad.edu", 2));
        universidad.crearMateria(new Materia("PROG101", "Programacion I", 2, 3));
        universidad.crearMateria(new Materia("MAT101", "Matematicas I", 1, 4));
        universidad.crearAula("AULA-101", 30);
        universidad.crearAula("LAB-202", 24);

        Edificios edificios = universidad.getEdificios();
        edificios.conectar("Ingenieria", "Biblioteca", 100);
        edificios.conectar("Biblioteca", "Cafeteria", 80);
        edificios.conectar("Cafeteria", "Rectoria", 180);
        edificios.conectar("Ingenieria", "Laboratorios", 150);
        edificios.conectar("Laboratorios", "Rectoria", 120);
        universidad.limpiarHistorial();
    }

    private static void mostrarAulas(Universidad universidad) {
        System.out.println("Aulas registradas:");
        for (Aula aula : universidad.listarAulas()) {
            System.out.println(aula.getNombre() + " | Capacidad: " + aula.getCapacidad() + " estudiantes");
        }
    }
}
