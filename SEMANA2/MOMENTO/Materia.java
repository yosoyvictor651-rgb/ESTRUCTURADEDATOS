public class Materia {
    private final String codigo;
    private final String nombre;
    private final int creditos;
    private final int cuposMaximos;
    private final LinkedList<String> prerequisitos = new LinkedList<>();
    private final LinkedList<String> inscritos = new LinkedList<>();
    private final Queue<String> espera = new ArrayDeque<>();

    public Materia(String codigo, String nombre, int cuposMaximos, int creditos) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("La materia debe tener un código");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("La materia debe tener un nombre");
        }
        if (cuposMaximos < 1 || creditos < 1) {
            throw new IllegalArgumentException("Cupos y créditos deben ser positivos");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.cuposMaximos = cuposMaximos;
        this.creditos = creditos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public LinkedList<String> getPrerequisitos() {
        return new LinkedList<>(prerequisitos);
    }

    public LinkedList<String> getInscritos() {
        return new LinkedList<>(inscritos);
    }

    public void agregarPrerequisito(String codigoMateria) {
        if (!prerequisitos.contains(codigoMateria)) {
            prerequisitos.add(codigoMateria);
        }
    }

    public boolean cumplePrerequisitos(Estudiante estudiante) {
        for (String codigoMateria : prerequisitos) {
            if (!estudiante.aprobo(codigoMateria)) {
                return false;
            }
        }
        return true;
    }

    /** Retorna true si obtiene cupo y false si queda en espera. */
    public boolean inscribir(String idEstudiante) {
        if (inscritos.contains(idEstudiante)) {
            throw new IllegalArgumentException("El estudiante ya está inscrito");
        }
        if (espera.contains(idEstudiante)) {
            return false;
        }
        if (inscritos.size() < cuposMaximos) {
            inscritos.add(idEstudiante);
            return true;
        }
        espera.add(idEstudiante);
        return false;
    }

    public boolean cancelar(String idEstudiante) {
        return inscritos.remove(idEstudiante);
    }

    public String promoverEspera() {
        if (inscritos.size() >= cuposMaximos || espera.isEmpty()) {
            return null;
        }
        String idEstudiante = espera.remove();
        inscritos.add(idEstudiante);
        return idEstudiante;
    }

    public boolean retirarDeEspera(String idEstudiante) {
        return espera.remove(idEstudiante);
    }

    public void devolverAEsperaAlFrente(String idEstudiante) {
        ((ArrayDeque<String>) espera).addFirst(idEstudiante);
    }

    public String esperaTexto() {
        return espera.toString();
    }
}
