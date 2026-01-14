package run;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.swing.JFrame;

import gui.MainWindow;
import matching_without_priority.MathcingHalfPriority;

public class Main {
    public static final double[] DEFAULT_TERM_RATES = new double[]{1,1,1,1,1,1,1,1,1,1,1,1};
    public static void main(String[] args) {
        MainWindow frame = new MainWindow();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(10, 10, 600, 400);
        frame.setTitle("ターム分け");
        frame.setVisible(true);
    }
    public static void fromGui(JFrame jFrame, Path inputPath, Path outputPath, double[] termRates) {
        System.err.println(inputPath);
        System.err.println(outputPath);
        var sp = inputPath.toString().split("\\.");
        System.err.println(Arrays.toString(sp));
        System.err.println(outputPath.toString() + "/" + sp[sp.length - 1] + "_" + LocalDateTime.now() + ".csv");
        MathcingHalfPriority.solve(inputPath, Paths.get(outputPath.toString() + "\\" + sp[sp.length - 1] + "_" + LocalDateTime.now().toString().replace(":", "_") + ".csv"), termRates == null ? DEFAULT_TERM_RATES : termRates);
        jFrame.dispose();
    }
}
