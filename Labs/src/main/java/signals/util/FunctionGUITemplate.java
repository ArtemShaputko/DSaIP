package signals.util;

import signals.util.params.SignalDrawParams;

import java.util.List;

public record FunctionGUITemplate(
        String name,
        String xLabel,
        String yLabel,
        List<SignalDrawParams> params
) {
    public FunctionGUITemplate(String name,
                               String xLabel,
                               String yLabel,
                               SignalDrawParams params) {
        this(name, xLabel, yLabel, List.of(params));

    }
}
