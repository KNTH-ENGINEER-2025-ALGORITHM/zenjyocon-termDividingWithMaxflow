package maximumflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;



/**
 * @implNote copilot に感謝
 */
interface Operator<T extends Comparable<T>> {
    T add(T a, T b);
    T neg(T a);
}
/**
 * 重みの計算に必要な演算をサポートするクラス。
 * @apiNote Operatorをimplementしたクラスを作り、それを使ってCapacityInfoを作る。
 */
class CapacityInfo<T extends Comparable<T>> {
    public final T ZERO;
    public final Operator<T> OPERATOR;
    /**
     * 
     * @param zeroValue 0 に値するもの
     * @param operator 足し算および(-1)倍を定義するためのクラス
     */
    public CapacityInfo(T zeroValue, Operator<T> operator) {
        this.ZERO = zeroValue;
        this.OPERATOR = operator;
    }
    /**
     * 一般的な比較可能型の足し算
     * @param a
     * @param b
     * @return {@code a} + {@code b}
     */
    public T add(T a, T b) {
        return OPERATOR.add(a, b);
    }
    /**
     * 一般的な比較可能型の引き算
     * @param a
     * @param b
     * @return {@code a} - {@code b}
     */
    public T remove(T a, T b) {
        return OPERATOR.add(a, OPERATOR.neg(b));
    }
    /**
     * a,bの小さいほう(等しければa)を返す
     * @param a
     * @param b
     * @return the smaller of {@code a} and {@code b}
     */
    public T min(T a, T b) {
        return a.compareTo(b) <= 0 ? a : b;
    }
    /**
     * a,bの大きいほう(等しければa)を返す
     * @param a
     * @param b
     * @return the larger of {@code a} and {@code b}
     */
    public T max(T a, T b) {
        return a.compareTo(b) >= 0 ? a : b;
    }
}

/**
 * 重み付き有向グラフの辺を管理する。逆向きの辺の参照も持つ。
 * @param T : グラフの頂点の型
 * @param U : 重みの型({@code Comparable}を継承している必要がある)
 */
class DirectedEdge<T, U extends Comparable<U>> {
    private T from;
    private T to;
    private U weight;
    private U originalWeight;
    private DirectedEdge<T,U> reversed;
    private final CapacityInfo<U> CAPACITY_INFO;
    
    /**
     * 
     * @param from 始点
     * @param to 終点
     * @param weight 重み
     * @param capacityInfo 演算定義
     */
    protected DirectedEdge(T from, T to, U weight, CapacityInfo<U> capacityInfo) {
        this(from, to, weight, capacityInfo, null);
    }
    /**
     * 
     * @param from 始点
     * @param to 終点
     * @param weight 重み
     * @param capacityInfo 演算定義
     * @param reversed 逆向きの辺の参照渡し
     */
    protected DirectedEdge(T from, T to, U weight, CapacityInfo<U> capacityInfo, DirectedEdge<T,U> reversed) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.originalWeight = weight;
        this.CAPACITY_INFO = capacityInfo;
        this.reversed = reversed;
        assert weight.compareTo(CAPACITY_INFO.ZERO) >= 0;
    }
    public T getFrom() {
        return from;
    }
    public T getTo() {
        return to;
    }
    /**
     * 
     * @return 現在の重み
     */
    public U getWeight() {
        return weight;
    }
    /**
     * 
     * @return 最初の重み
     */
    public U getOriginalWeight() {
        return originalWeight;
    }
    /**
     * 重みを設定する。増減にも使用できる。
     * @param weight 新しい重み
     */
    public void setWeight(U weight) {
        this.weight = weight;
    }
    /**
     * 逆向きの辺を参照渡しで取得する。
     * @return 逆向きの辺の参照渡し
     */
    public DirectedEdge<T, U> getReversed() {
        return reversed;
    }
    /**
     * 逆向きの辺を参照渡しで設定。
     * @param reversed 逆向きの辺の参照渡し
     */
    public void setReversed(DirectedEdge<T, U> reversed) {
        this.reversed = reversed;
    }
}
/**
 * 最大フロー量を求める。
 * @implNote By Ford-Fulkerson Method
 * @implNote 計算量 O(FM) (F:最大フロー,M:辺の数)
 * @implNote Super Thanks to 競技プログラミングの鉄則
 */
public class MaximumFlow<T, U extends Comparable<U>> {
    public static final CapacityInfo<Long> LONG_CAPACITY_INFO = new CapacityInfo<>(0L, new LongOperator());
    private static class LongOperator implements Operator<Long> {
        @Override
        public Long add(Long a, Long b) {
            return a + b;
        }
        @Override
        public Long neg(Long a) {
            return -a;
        }
    }
    public static final CapacityInfo<Integer> INTEGER_CAPACITY_INFO = new CapacityInfo<>(0, new IntegerOperator());
    private static class IntegerOperator implements Operator<Integer> {
        @Override
        public Integer add(Integer a, Integer b) {
            return a + b;
        }
        @Override
        public Integer neg(Integer a) {
            return -a;
        }
    }
    public static <T, U extends Comparable<U>> String graphToString(Map<T, HashSet<DirectedEdge<T, U>>> graph) {
        StringBuilder sb = new StringBuilder();
        for (var entry : graph.entrySet()) {
            for (var edge : entry.getValue()) {
                assert entry.getKey().equals(edge.getFrom());
                sb.append(edge.getFrom()+","+edge.getTo()+","+edge.getWeight()+"\n");
            }
        }
        return sb.toString();
    }
    public static <T extends Comparable<T>, U extends Comparable<U>> String graphToSortedString(HashMap<T, HashSet<DirectedEdge<T, U>>> graph) {
        var tm = new TreeMap<>(graph);
        return graphToString(tm);
    }

    /**
     * 初期化用のサイズ
     */
    private final int size;
    /**
     * DFSで訪れたノードを記録する。
     */
    private HashMap<T, Boolean> used;
    /**
     * グラフ本体
     */
    private HashMap<T, HashSet<DirectedEdge<T, U>> > graph;
    /**
     * 入力時のグラフ
     */
    private HashMap<T, HashSet<DirectedEdge<T, U>> > originalGraph;
    /**
     * 演算定義
     */
    private final CapacityInfo<U> CAPACITY_INFO;
    /**
     * 最初に流すフロー。最大フロー量より明らかに大きいもの。
     */
    private final U BIG_ENOUGH;

    /**
     * 大まかな頂点数を指定して新たな最大フロー求解クラスを作成する。
     * 
     * @param size 容量。{@code initialCapacity}を設定することで高速化する。あまり大事ではない。
     * @param capacityInfo 演算定義
     * @param bigEnough 最初に流すフロー。最大フロー量より明らかに大きいもの。
     */
    public MaximumFlow(int size, CapacityInfo<U> capacityInfo, U bigEnough) {
        this.size = size;
        used = new HashMap<>(this.size);
        graph = new HashMap<>(this.size);
        originalGraph = new HashMap<>(this.size);
        this.CAPACITY_INFO = capacityInfo;
        this.BIG_ENOUGH = bigEnough;
    }
    /**
     * 辺を追加する。
     * @param from 始点
     * @param to 終点
     * @param limit フローの限界量
     */
    public void addEdge(T from, T to, U limit) {
        DirectedEdge<T,U> edge = new DirectedEdge<>(from, to, limit, CAPACITY_INFO);
        // ↓0ではだめ
        DirectedEdge<T,U> reversed = new DirectedEdge<>(to, from, CAPACITY_INFO.ZERO, CAPACITY_INFO, edge);
        edge.setReversed(reversed);
        graph.putIfAbsent(from, new HashSet<>());
        graph.get(from).add(edge);
        graph.putIfAbsent(to, new HashSet<>());
        graph.get(to).add(reversed);
        originalGraph.putIfAbsent(from, new HashSet<>());
        originalGraph.get(from).add(edge);
    }

    /**
     * 深さ優先探索で流せるフローを見つける。
     * @param pos 今いるところ
     * @param goal ゴール
     * @param F 暫定フロー量
     * @return 流したフローの量
     */
    protected U dfs(T pos, T goal, U F) {
        if (pos.equals(goal)) {
            return F;
        }
        used.putIfAbsent(pos, Boolean.TRUE);
        used.replace(pos, Boolean.TRUE);

        var curNode = graph.get(pos);

        for (var edge : curNode) {
            // 0であるか
            /* 容量0以下の辺 */
            if (edge.getWeight().compareTo(CAPACITY_INFO.ZERO) <= 0) {
                continue;
            }

            /* すでに訪問した頂点 */
            if (used.getOrDefault(edge.getTo(), Boolean.FALSE)) {
                continue;
            }

            U flow = dfs(edge.getTo(), goal, CAPACITY_INFO.min(F, edge.getWeight()));

            /* フローを流せる */
            if (flow.compareTo(CAPACITY_INFO.ZERO) > 0) {
                edge.setWeight(CAPACITY_INFO.remove(edge.getWeight(), flow));
                edge.getReversed().setWeight(CAPACITY_INFO.add(edge.getReversed().getWeight(), flow));
                return flow;
            }
        }

        /* 見つからなかった */
        return CAPACITY_INFO.ZERO;
    }

    /**
     * 与えられた始点から与えられた終点までの最大フローの総流量を求める。
     * @param s 始点
     * @param t 終点
     * @return 最大フローの総流
     */
    public U getMaxFlow(T s, T t) {
        U totalFlow = CAPACITY_INFO.ZERO;
        while (true) {
            for (var entry : used.entrySet()) {
                entry.setValue(Boolean.FALSE);
            }
            U F = dfs(s, t, BIG_ENOUGH);

            if (F.compareTo(CAPACITY_INFO.ZERO) <= 0) {
                break;
            }
            totalFlow = CAPACITY_INFO.add(totalFlow, F);
        }
        return totalFlow;
    }

    public HashMap<T, HashSet<DirectedEdge<T, U>>> traceBack() {
        HashMap<T, HashSet<DirectedEdge<T, U>>> res = new HashMap<>();
        for (var entry : originalGraph.entrySet()) {
            res.put(entry.getKey(), new HashSet<>());
            for (var edge : entry.getValue()) {
                assert entry.getKey().equals(edge.getFrom());
                if (CAPACITY_INFO.remove(edge.getOriginalWeight(), edge.getWeight()).compareTo(CAPACITY_INFO.ZERO) <= 0) {
                    continue;
                }
                res.get(entry.getKey()).add(new DirectedEdge<>(edge.getFrom(), edge.getTo(), CAPACITY_INFO.remove(edge.getOriginalWeight(), edge.getWeight()), CAPACITY_INFO));
            }
        }
        return res;
    }
}

// 132ms。<s>Mapやジェネリクスを使っているのでやや遅い。</s>