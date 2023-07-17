package de.monticore.bpmn.wf2smt;

import static java.util.Map.entry;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Z3Helper {

    private static final Map<String, Integer> globalZ3NameTable = new HashMap<>();

    public static Expr<BoolSort> BigOr(Context context, List<Expr<BoolSort>> elements) {
        return Z3Helper.Reduce(elements, context::mkOr);
    }

    public static Expr<BoolSort> BigAnd(Context context, List<Expr<BoolSort>> elements) {
        return Z3Helper.Reduce(elements, context::mkAnd);
    }

    public static Expr<BoolSort> Reduce(List<Expr<BoolSort>> elements,
        BiFunction<Expr<BoolSort>, Expr<BoolSort>, Expr<BoolSort>> combiner) {
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("");
        }
        if (elements.size() == 1) {
            return elements.get(0);
        }
        return combiner.apply(elements.get(0), Z3Helper.Reduce(elements.subList(1, elements.size()), combiner));
    }


    public static String gn(String name) {
        if (Z3Helper.globalZ3NameTable.containsKey(name)) {
            int counter = Z3Helper.globalZ3NameTable.get(name);
            String incName = name + "_" + counter;
            Z3Helper.globalZ3NameTable.put(name, counter + 1);
            return incName;
        }
        Z3Helper.globalZ3NameTable.put(name, 1);
        return name;
    }

    static <K> Entry<EnumSort<K>, Map<K, Expr<EnumSort<K>>>> toEnum(
        Context ctx,
        String enumName,
        List<K> values
    ) {

        Map<K, String> value2String = values
            .stream()
            .collect(Collectors.toMap(Function.identity(), Objects::toString));
        EnumSort<K> enumSort = ctx.mkEnumSort(gn(enumName),
            values.stream().map(value2String::get).toArray(String[]::new));
        var value2Sort = IntStream.range(0, values.size())
            .boxed()
            .collect(Collectors.toMap(
                values::get,
                index -> enumSort.getConsts()[index])
            );
        return entry(enumSort, value2Sort);
    }
}
