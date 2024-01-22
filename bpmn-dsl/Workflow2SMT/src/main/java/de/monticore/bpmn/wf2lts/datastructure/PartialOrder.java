package de.monticore.bpmn.wf2lts.datastructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PartialOrder<T> {

  protected Map<T, Set<T>> poMap;

  public PartialOrder() {
    poMap = new HashMap<>();
  }

  public PartialOrder(PartialOrder<T> partialOrder) {
    this.poMap = new HashMap<>(partialOrder.poMap);
  }

  public void addPredecessorTo(T predecessor, T element) {
    if (poMap.containsKey(element)) {
      poMap.get(element).add(predecessor);
    }
    else {
      poMap.put(element, new HashSet<>(Set.of(predecessor)));
    }
    poMap.get(element).addAll(getPredecessors(predecessor));
  }

  public Set<T> getPredecessors(T element) {
    if (poMap.containsKey(element)) {
      return poMap.get(element);
    }
    else
      return new HashSet<>();
  }

  public boolean isPredecessor(T predecessor, T element) {
    return getPredecessors(element).contains(predecessor);
  }

}
