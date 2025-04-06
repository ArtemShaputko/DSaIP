package signals.util.params;

import signals.math.MathOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaveletDrawParams extends DiscreteDrawParams {
    private final List<double[]> initComponents;
    private List<double[]> curComponents;
    private final String basicName;

    public WaveletDrawParams(String name, List<double[]> components,
                             double startTime, double samplingRate) {
        super(name, computeInitialSignal(components), startTime, samplingRate);
        this.basicName = name;
        this.initComponents = deepCopy(components);
        this.curComponents = new ArrayList<>(initComponents);
    }

    public void modifyComponents(int level, double factor) {
        int levelPos = curComponents.size() - level;
        validateLevel(levelPos);
        double[] modifiedArray = Arrays.copyOf(initComponents.get(levelPos), initComponents.get(levelPos).length);
        for (int i = 0; i < modifiedArray.length; i++) {
            modifiedArray[i] *= factor;
        }
        curComponents.set(levelPos, modifiedArray);
        updateSignal();
        name = String.format("%s изменён L%d в %.2f раз", basicName, level, factor);
    }

    public int level() {
        return initComponents.size() - 1;
    }

    private static double[] computeInitialSignal(List<double[]> components) {
        return MathOperations.idwtDaubechies4(deepCopy(components), components.size() - 1);
    }

    private static List<double[]> deepCopy(List<double[]> original) {
        List<double[]> copy = new ArrayList<>();
        for (double[] array : original) {
            copy.add(Arrays.copyOf(array, array.length));
        }
        return copy;
    }

    private void validateLevel(int level) {
        if (level < 0 || level >= curComponents.size()) {
            throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    private void updateSignal() {
        signal = MathOperations.idwtDaubechies4(deepCopy(curComponents), curComponents.size() - 1);
    }
}
