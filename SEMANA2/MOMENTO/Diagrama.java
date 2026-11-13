/** Diagramas UML en texto Mermaid, mantenidos fuera del menú y la lógica del sistema. */
public class Diagramas {
    public static void main(String[] args) {
        System.out.println("classDiagram\n  Persona <|-- Estudiante\n  Persona <|-- Profesor\n  Estudiante o-- Materia : historial\n  Materia o-- Aula : horario\n  Universidad *-- Edificios\n  class Persona { nombre; id; email }\n  class Estudiante { semestre; Double[10][20] notas; LinkedList historial }\n  class Materia { codigo; creditos; cupos; LinkedList prerequisitos; Queue espera }\n  class Aula { boolean[7][24] ocupada }\n  class Edificios { int[N][N] distancias; rutaDijkstra() }");
    }
}
