package de.monticore.bpmn.wf2smt;

import static java.util.Map.entry;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Model;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Z3Helper {

  private static final Map<String, Integer> globalZ3NameTable = new HashMap<>();

  /**
   * Combine all conditions using a logical xor. In latex you would write something like
   * \$\bigvee${0 \leq i < n} e_i from which the name comes.
   *
   * @param conditions A List containing bool expressions.
   * @return A bool expression that is true iff (at least one expression form conditions is true)
   *     xor conditions is empty.
   */
  public static Expr<BoolSort> BigOr(Context context, List<Expr<BoolSort>> conditions) {
    return conditions.stream().reduce(context::mkOr).orElse(context.mkTrue());
  }

  /**
   * Combine all conditions using a logical and.
   *
   * @param conditions A List containing bool expressions.
   * @return A bool expression that is true iff all expressions form conditions are true.
   */
  public static Expr<BoolSort> BigAnd(Context context, List<Expr<BoolSort>> conditions) {
    return conditions.stream().reduce(context::mkAnd).orElse(context.mkTrue());
  }

  /**
   * Create a unique name of the passed name. Z3Helper keeps track of declared names through this
   * function and ensures that the returned name was not declared before.
   */
  public static String uniqueName(String name) {
    if (Z3Helper.globalZ3NameTable.containsKey(name)) {
      int counter = Z3Helper.globalZ3NameTable.get(name);
      String incName = name + "_" + counter;
      Z3Helper.globalZ3NameTable.put(name, counter + 1);
      return incName;
    }
    Z3Helper.globalZ3NameTable.put(name, 1);
    return name;
  }

  /**
   * Creat a new EnumSort containing all elements of the provided list.
   *
   * @param ctx The context in which the EnumSort should be declared.
   * @param enumName The name of the EnumSort in z3.
   * @param values The values the EnumSort should contain
   * @param <K> The type of the values. They will be converted to strings using toString()
   * @return The EnumSort and a mapping from v in values to its corresponding constant in EnumSort.
   */
  static <K> Entry<EnumSort<K>, Map<K, Expr<EnumSort<K>>>> toEnum(
      Context ctx, String enumName, List<K> values) {

    Map<K, String> value2String =
        values.stream().collect(Collectors.toMap(Function.identity(), Objects::toString));
    EnumSort<K> enumSort =
        ctx.mkEnumSort(
            uniqueName(enumName), values.stream().map(value2String::get).toArray(String[]::new));
    var value2Sort =
        IntStream.range(0, values.size())
            .boxed()
            .collect(Collectors.toMap(values::get, index -> enumSort.getConsts()[index]));
    return entry(enumSort, value2Sort);
  }

  /**
   * Similar to stream.matchesAny(x -> x == target) this method encodes whether at least one item in
   * the list of options is the target.
   *
   * @param target The searched item in the list of options.
   * @param listOfOptions the list of values to be checked against (the stream source).
   * @return A boolean expression that is true iff at least one element in the list of options is
   *     the target.
   */
  public static Expr<BoolSort> matchesAny(
      Context ctx, Expr<EnumSort<String>> target, List<Expr<EnumSort<String>>> listOfOptions) {
    return BigOr(
        ctx,
        listOfOptions.stream()
            .map(finalSymbol -> ctx.mkEq(target, finalSymbol))
            .collect(Collectors.toList()));
  }

  /**
   * Use this method to check whether all indices from 0 to collection.size() match the given
   * predicate.
   *
   * @param collection The collection for which the indices should be generated.
   * @param predicate A function consuming an index and returning a boolean expression.
   * @return A boolean expression that is true iff the predicate returns an expression evaluating to
   *     true for all indices..
   */
  public static Expr<BoolSort> allIndicesMatch(
      Context ctx, Collection<?> collection, Function<Integer, Expr<BoolSort>> predicate) {
    return BigAnd(
        ctx,
        IntStream.range(0, collection.size())
            .mapToObj(predicate::apply)
            .collect(Collectors.toList()));
  }

  /**
   * @param model The model in which the list of symbols should be evaluated.
   * @param symbolList The symbols that should be evaluated in the model
   * @param size The upper bound (excluded) to which the symbolList should be evaluated
   * @return A list of strings of evaluated symbols (this will usually be the name of the symbol).
   */
  public static <T> List<String> evaluationOfList(
      Model model, List<Expr<EnumSort<T>>> symbolList, int size) {
    return symbolList.subList(0, size).stream()
        .map(symbolExpr -> model.evaluate(symbolExpr, true).toString())
        .collect(Collectors.toList());
  }

  /**
   * @param model The Model in which the expression should be evaluated.
   * @param intExpr The integer expression that should be evaluated
   * @return The evaluation of an IntExpression
   * @throws IllegalArgumentException if the expression cannot be evaluated successfully.
   */
  static int evaluationOfInt(Model model, IntExpr intExpr) {
    var evalIndex = model.evaluate(intExpr, true);
    if (!evalIndex.isIntNum()) {
      throw new IllegalArgumentException(intExpr.toString() + "is not evaluated to an int.");
    }
    return ((IntNum) evalIndex).getInt();
  }
}
