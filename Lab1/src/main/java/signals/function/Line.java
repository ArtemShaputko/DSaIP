package signals.function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Line extends Function {
    double startBorder;
    double endBorder;
    double value;
    @Override
    public double getValue(double x) {
        if(x >= startBorder && x <= endBorder){
            return value;
        }
        return  0;
    }
}
