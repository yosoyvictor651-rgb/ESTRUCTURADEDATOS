/** Reserva franjas horarias usando la matriz nativa solicitada. */
public class Aula {
    private final String nombre;
    private final int capacidad;
    private final boolean[][] ocupada = new boolean[7][24];

    public Aula(String nombre, int capacidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El aula debe tener un nombre");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        this.nombre = nombre;
        this.capacidad = capacidad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean disponible(int dia, int hora) {
        validarHorario(dia, hora, 1);
        return !ocupada[dia][hora];
    }

    public void reservar(int dia, int hora, int duracion) {
        validarHorario(dia, hora, duracion);
        for (int h = hora; h < hora + duracion; h++) {
            if (ocupada[dia][h]) {
                throw new IllegalStateException("Horario conflictivo el día " + dia + " a las " + h);
            }
        }
        for (int h = hora; h < hora + duracion; h++) {
            ocupada[dia][h] = true;
        }
    }

    public void liberar(int dia, int hora, int duracion) {
        validarHorario(dia, hora, duracion);
        for (int h = hora; h < hora + duracion; h++) {
            ocupada[dia][h] = false;
        }
    }

    private void validarHorario(int dia, int hora, int duracion) {
        if (dia < 0 || dia > 6) {
            throw new IllegalArgumentException("El día debe estar entre 0 y 6");
        }
        if (hora < 0 || duracion < 1 || hora + duracion > 24) {
            throw new IllegalArgumentException("La hora o duración está fuera del rango del día");
        }
    }
}
