import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Grafo no dirigido representado con matriz de adyacencia y Dijkstra. */
public class Edificios {
    private static final int INFINITO = 1_000_000_000;
    private final List<String> nombres = new ArrayList<>();
    private int[][] distancias = new int[5][5];

    public Edificios() {
        agregarEdificio("Ingenieria");
        agregarEdificio("Biblioteca");
        agregarEdificio("Cafeteria");
        agregarEdificio("Rectoria");
        agregarEdificio("Laboratorios");
    }

    public void agregarEdificio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El edificio debe tener un nombre");
        }
        if (nombres.contains(nombre)) {
            return;
        }
        if (nombres.size() == distancias.length) {
            ampliarMatriz();
        }
        nombres.add(nombre);
    }

    private void ampliarMatriz() {
        int nuevoTamano = distancias.length + 1;
        int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];
        for (int fila = 0; fila < nuevoTamano; fila++) {
            Arrays.fill(nuevaMatriz[fila], INFINITO);
        }
        for (int fila = 0; fila < distancias.length; fila++) {
            System.arraycopy(distancias[fila], 0, nuevaMatriz[fila], 0, distancias.length);
        }
        distancias = nuevaMatriz;
    }

    public void conectar(String edificioA, String edificioB, int metros) {
        if (metros <= 0) {
            throw new IllegalArgumentException("La distancia debe ser positiva");
        }
        int indiceA = nombres.indexOf(edificioA);
        int indiceB = nombres.indexOf(edificioB);
        if (indiceA < 0 || indiceB < 0) {
            throw new IllegalArgumentException("No se encontró uno de los edificios");
        }
        distancias[indiceA][indiceB] = metros;
        distancias[indiceB][indiceA] = metros;
    }

    public String ruta(String origen, String destino) {
        int indiceOrigen = nombres.indexOf(origen);
        int indiceDestino = nombres.indexOf(destino);
        if (indiceOrigen < 0 || indiceDestino < 0) {
            throw new IllegalArgumentException("No se encontró el edificio indicado");
        }

        int cantidad = nombres.size();
        int[] distanciasMinimas = new int[cantidad];
        int[] anterior = new int[cantidad];
        boolean[] visitados = new boolean[cantidad];
        Arrays.fill(distanciasMinimas, INFINITO);
        Arrays.fill(anterior, -1);
        distanciasMinimas[indiceOrigen] = 0;

        for (int paso = 0; paso < cantidad; paso++) {
            int actual = buscarNoVisitadoMasCercano(distanciasMinimas, visitados);
            if (actual < 0) {
                break;
            }
            visitados[actual] = true;
            actualizarVecinos(actual, distanciasMinimas, anterior, visitados);
        }

        if (distanciasMinimas[indiceDestino] == INFINITO) {
            return "No existe una ruta entre esos edificios";
        }
        return formarResultado(indiceDestino, indiceOrigen, anterior, distanciasMinimas);
    }

    private int buscarNoVisitadoMasCercano(int[] distanciasMinimas, boolean[] visitados) {
        int elegido = -1;
        for (int i = 0; i < nombres.size(); i++) {
            if (!visitados[i] && (elegido < 0 || distanciasMinimas[i] < distanciasMinimas[elegido])) {
                elegido = i;
            }
        }
        if (elegido >= 0 && distanciasMinimas[elegido] == INFINITO) {
            return -1;
        }
        return elegido;
    }

    private void actualizarVecinos(int actual, int[] distanciasMinimas, int[] anterior, boolean[] visitados) {
        for (int vecino = 0; vecino < nombres.size(); vecino++) {
            int distancia = distancias[actual][vecino];
            if (!visitados[vecino] && distancia > 0 && distancia < INFINITO
                    && distanciasMinimas[actual] + distancia < distanciasMinimas[vecino]) {
                distanciasMinimas[vecino] = distanciasMinimas[actual] + distancia;
                anterior[vecino] = actual;
            }
        }
    }

    private String formarResultado(int destino, int origen, int[] anterior, int[] distanciasMinimas) {
        List<String> camino = new ArrayList<>();
        for (int actual = destino; actual != -1; actual = anterior[actual]) {
            camino.add(0, nombres.get(actual));
        }
        if (camino.isEmpty() || !camino.get(0).equals(nombres.get(origen))) {
            return "No existe una ruta entre esos edificios";
        }
        return String.join(" -> ", camino) + " (" + distanciasMinimas[destino] + " m)";
    }

    public List<String> getNombres() {
        return new ArrayList<>(nombres);
    }
}
