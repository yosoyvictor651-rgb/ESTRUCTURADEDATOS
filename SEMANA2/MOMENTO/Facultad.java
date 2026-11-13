/** Registro simple para el arreglo fijo Facultad[5]. */
public class Facultad {
    private final String nombre;
    public Facultad(String nombre){this.nombre=nombre;}
    public String getNombre(){return nombre;}
    @Override public String toString(){return nombre;}
}
