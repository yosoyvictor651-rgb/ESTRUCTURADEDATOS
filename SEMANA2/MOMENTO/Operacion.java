public class Operacion {
    private final String descripcion;
    private final Runnable accionDeshacer;
    private final Runnable accionRehacer;

    public Operacion(String descripcion, Runnable accionDeshacer, Runnable accionRehacer) {
        this.descripcion = descripcion;
        this.accionDeshacer = accionDeshacer;
        this.accionRehacer = accionRehacer;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void deshacer() {
        accionDeshacer.run();
    }

    public void rehacer() {
        accionRehacer.run();
    }
}
