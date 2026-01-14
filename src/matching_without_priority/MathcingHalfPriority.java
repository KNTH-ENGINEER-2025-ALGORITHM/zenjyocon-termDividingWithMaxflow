package matching_without_priority;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap.SimpleEntry;

import maximumflow.MaximumFlow;

public class MathcingHalfPriority {
    static final int TERMS = 12;
    static final int CHOOSE = 4;
    static final int INF = Integer.MAX_VALUE;
    private static final SimpleEntry<Integer, Integer> START = new SimpleEntry<>(0, 0);
    private static final SimpleEntry<Integer, Integer> GOAL = new SimpleEntry<>(3, 0);
    static int num;
    static int groups;
    static int[] headCounts;
    static int[][] term;
    static int[] maxPerTerm;
    static double[] standardRates;
    static void read(Path path) {
        try (var reader = Files.newBufferedReader(path)) {
            headCounts = new int[groups];
            term = new int[groups][CHOOSE];
            num = 0;
            int idx = 0;
            for (int t = 0; t < groups; t++) {
                var sp = reader.readLine().split(",");
                assert sp.length == 2 + CHOOSE && idx == Integer.parseInt(sp[0]);
                headCounts[idx] = Integer.parseInt(sp[1]);
                for (int j = 0; j < CHOOSE; j++) {
                    term[idx][j] = Integer.parseInt(sp[2+j]);
                }
                num += headCounts[idx];
                idx++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static int countLines(Path path) {
        int cnt = 0;
        try (var reader = Files.newBufferedReader(path)) {
            var line = "";
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
                cnt++;
            }
            return cnt;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
    static double[] convertToStandardRates(double[] rates) {
        double sum = 0;
        for (int i = 0; i < rates.length; i++) {
            sum += rates[i];
        }
        double[] res = new double[rates.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = rates[i] / sum;
        }
        return res;
    }
    static void init(Path path, double[] rates) {
        assert TERMS == rates.length;
        groups = countLines(path);
        read(path);
        standardRates = convertToStandardRates(rates);
        maxPerTerm = new int[standardRates.length];
        for (int i = 0; i < standardRates.length; i++) {
            maxPerTerm[i] = (int)Math.ceil(groups * standardRates[i]);
        }
    }
    public static void solve(Path inputPath, Path outputPath, double[] rates) {
        init(inputPath, rates);
        MaximumFlow<SimpleEntry<Integer, Integer>, Integer> Z = new MaximumFlow<>(groups*2+2, MaximumFlow.INTEGER_CAPACITY_INFO, INF);
        for (int i = 0; i < groups; i++) {
            Z.addEdge(START, new SimpleEntry<>(1, i), 1);
        }
        for (int i = 0; i < TERMS; i++) {
            Z.addEdge(new SimpleEntry<>(2, i), GOAL, maxPerTerm[i]);
        }
        for (int i = 0; i < groups; i++) {
            for (int j = 0; j < CHOOSE; j++) {
                Z.addEdge(new SimpleEntry<>(1, i), new SimpleEntry<>(2, term[i][j]), 1);
            }
        }
        System.out.println(Z.getMaxFlow(START, GOAL)+" / "+num);

        var tb = Z.traceBack();
        
        try (var writer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE)) {
            writer.write(MaximumFlow.graphToString(tb).replaceAll("=", "_"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
