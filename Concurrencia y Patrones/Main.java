import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Decorator: patrón de diseño que permite agregarle funcionalidad extra a
 * un objeto en tiempo de ejecución, envolviéndolo en otro objeto que
 * implementa la misma interfaz, en vez de modificar su clase original o
 * crear una subclase para cada combinación posible.
 *
 * Fork/Join: framework de Java (java.util.concurrent) para paralelizar
 * tareas mediante la estrategia "divide y vencerás": un problema grande
 * se parte (fork) en subproblemas más chicos que se ejecutan en paralelo,
 * y luego sus resultados se combinan (join). Se apoya en un ForkJoinPool,
 * que usa work-stealing para repartir el trabajo entre threads de forma
 * eficiente.
 *
 * RecursiveAction: clase base del framework Fork/Join para definir tareas
 * que no devuelven ningún resultado (equivalente a void). Se sobrescribe
 * su método compute() para dividir el trabajo en partes más chicas de
 * forma recursiva hasta llegar a un caso base manejable directamente.
**/


// interfaz común
interface ArrayProcessor {
    void process(int[] array);
}

// componente concreto: hace el trabajo real, usando Fork/Join por dentro
class DoubleValuesProcessor implements ArrayProcessor {

    @Override
    public void process(int[] array) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new DoubleValuesAction(array, 0, array.length));
    }
}

// la tarea recursiva que realmente divide y procesa el array
class DoubleValuesAction extends RecursiveAction {

    private final int[] array;
    private final int inicio, fin;
    private static final int UMBRAL = 1000;

    public DoubleValuesAction(int[] array, int inicio, int fin) {
        this.array = array;
        this.inicio = inicio;
        this.fin = fin;
    }

    @Override
    protected void compute() {
        if (fin - inicio <= UMBRAL) {
            for (int i = inicio; i < fin; i++) {
                array[i] *= 2;
            }
        } else {
            int medio = (inicio + fin) / 2;
            invokeAll(
                new DoubleValuesAction(array, inicio, medio),
                new DoubleValuesAction(array, medio, fin)
            );
        }
    }
}

// decorator base: implementa la misma interfaz y envuelve un ArrayProcessor
abstract class ProcessorDecorator implements ArrayProcessor {

    protected ArrayProcessor wrapped;

    public ProcessorDecorator(ArrayProcessor processor) {
        this.wrapped = processor;
    }

    @Override
    public void process(int[] array) {
        wrapped.process(array);
    }
}

// decorator que agrega logging
class LoggingDecorator extends ProcessorDecorator {

    public LoggingDecorator(ArrayProcessor processor) {
        super(processor);
    }

    @Override
    public void process(int[] array) {
        System.out.println("Procesando array de tamaño " + array.length + "...");
        super.process(array);
        System.out.println("Procesamiento terminado.");
    }
}

// decorator que mide el tiempo de ejecución
class TimingDecorator extends ProcessorDecorator {

    public TimingDecorator(ArrayProcessor processor) {
        super(processor);
    }

    @Override
    public void process(int[] array) {
        long inicio = System.nanoTime();
        super.process(array);
        long fin = System.nanoTime();
        System.out.println("Tardó " + (fin - inicio) / 1_000_000 + " ms");
    }
}

// Uso
public class Main {
    public static void main(String[] args) {
        int[] datos = new int[1000000];
        for (int i = 0; i < datos.length; i++) datos[i] = i;

        ArrayProcessor processor = new DoubleValuesProcessor();
        processor = new LoggingDecorator(processor);
        processor = new TimingDecorator(processor);

        processor.process(datos);

        System.out.println("Ejemplo: datos[0] = " + datos[0]);
    }
}