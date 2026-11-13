/** Tipos de persona relacionados en un solo archivo para facilitar la revisión. */
public abstract class Persona {
    private final String nombre;
    private final String id;
    private final String email;

    public Persona(String nombre, String id, String email) {
        this.nombre = nombre;
        this.id = id;
        this.email = email;
    }

    public String getNombre() { return nombre; }
    public String getId() { return id; }
    public String getEmail() { return email; }
    public abstract String mostrarInformacion();
}

/** Estudiante guarda hasta 20 notas en cada uno de sus 10 semestres. */
class Estudiante extends Persona {
    private final int semestre;
    private final Double[][] notas = new Double[10][20];
    private final String[][] codigos = new String[10][20];
    private final java.util.LinkedList<String> historialMaterias = new java.util.LinkedList<>();
    private final int[] usadas = new int[10];

    public Estudiante(String nombre, String id, String email, int semestre) {
        super(nombre, id, email);
        if (semestre < 1 || semestre > 10) {
            throw new IllegalArgumentException("Semestre entre 1 y 10");
        }
        this.semestre = semestre;
    }

    public int getSemestre() { return semestre; }
    public java.util.LinkedList<String> getHistorialMaterias() { return new java.util.LinkedList<>(historialMaterias); }
    public void agregarMateriaCursada(String codigo) {
        if (!historialMaterias.contains(codigo)) {
            historialMaterias.add(codigo);
        }
    }

    public void registrarNota(int semestre, String codigo, double nota) {
        validarSemestre(semestre);
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 5");
        }
        int fila = semestre - 1;
        for (int i = 0; i < usadas[fila]; i++) {
            if (codigo.equals(codigos[fila][i])) {
                notas[fila][i] = nota;
                agregarMateriaCursada(codigo);
                return;
            }
        }
        if (usadas[fila] == 20) {
            throw new IllegalStateException("Máximo 20 materias por semestre");
        }
        codigos[fila][usadas[fila]] = codigo;
        notas[fila][usadas[fila]++] = nota;
        agregarMateriaCursada(codigo);
    }

    public Double obtenerNota(int semestre, String codigo) {
        validarSemestre(semestre);
        int fila = semestre - 1;
        for (int i = 0; i < usadas[fila]; i++) {
            if (codigo.equals(codigos[fila][i])) {
                return notas[fila][i];
            }
        }
        return null;
    }

    public void eliminarNota(int semestre, String codigo) {
        validarSemestre(semestre);
        int fila = semestre - 1;
        for (int i = 0; i < usadas[fila]; i++) {
            if (codigo.equals(codigos[fila][i])) {
                for (int j = i; j < usadas[fila] - 1; j++) {
                    codigos[fila][j] = codigos[fila][j + 1];
                    notas[fila][j] = notas[fila][j + 1];
                }
                usadas[fila]--;
                codigos[fila][usadas[fila]] = null;
                notas[fila][usadas[fila]] = null;
                historialMaterias.remove(codigo);
                return;
            }
        }
    }

    public boolean aprobo(String codigo) {
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < usadas[f]; c++) {
                if (codigo.equals(codigos[f][c]) && notas[f][c] >= 3.0) {
                    return true;
                }
            }
        }
        return false;
    }

    public double promedioSemestre(int semestre) {
        validarSemestre(semestre);
        int fila = semestre - 1;
        if (usadas[fila] == 0) {
            return 0;
        }
        double suma = 0;
        for (int i = 0; i < usadas[fila]; i++) suma += notas[fila][i];
        return suma / usadas[fila];
    }

    public double promedioAcumulado() {
        double suma = 0; int total = 0;
        for (int f = 0; f < 10; f++) {
            for (int c = 0; c < usadas[f]; c++) {
                suma += notas[f][c];
                total++;
            }
        }
        return total == 0 ? 0 : suma / total;
    }

    public String reporte() {
        StringBuilder salida = new StringBuilder("Reporte de " + getNombre() + " (ID " + getId() + ")\n");
        int aprobadas = 0, reprobadas = 0;
        for (int f = 0; f < 10; f++) {
            if (usadas[f] > 0) {
                salida.append("Semestre ").append(f + 1).append(":\n");
                for (int c = 0; c < usadas[f]; c++) {
                    double nota = notas[f][c];
                    salida.append("  ").append(codigos[f][c]).append(": ").append(nota).append('\n');
                    if (nota < 3.0) {
                        reprobadas++;
                    } else {
                        aprobadas++;
                    }
                }
                salida.append("  Promedio: ").append(String.format("%.2f", promedioSemestre(f + 1))).append('\n');
            }
        }
        return salida.append("Promedio acumulado: ").append(String.format("%.2f", promedioAcumulado()))
                .append(" | Aprobadas: ").append(aprobadas).append(" | Reprobadas: ").append(reprobadas).toString();
    }

    private void validarSemestre(int semestre) {
        if (semestre < 1 || semestre > 10) {
            throw new IllegalArgumentException("Semestre inválido");
        }
    }

    @Override public String mostrarInformacion() {
        return "ID: " + getId() + " | " + getNombre() + " | " + getEmail() + " | Semestre " + semestre
                + " | Promedio " + String.format("%.2f", promedioAcumulado());
    }
}

/** Profesor se mantiene en el archivo Persona.java para no fragmentar el modelo. */
class Profesor extends Persona {
    private final String departamento;
    public Profesor(String nombre, String id, String email, String departamento) {
        super(nombre, id, email);
        this.departamento = departamento;
    }
    @Override public String mostrarInformacion() { return "Profesor ID: " + getId() + " | " + getNombre() + " | " + departamento; }
}
