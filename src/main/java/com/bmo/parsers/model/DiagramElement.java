package com.bmo.parsers.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramElement implements Element {

  @EqualsAndHashCode.Include
  private String name;

  private String comment;

  @EqualsAndHashCode.Include
  private Type type;

  private Element parent;

  @ToString.Exclude
  private List<Transition> transitionFrom;

  @ToString.Exclude
  private List<Transition> transitionTo;

  public enum Type {
    NONE,
    SUBGRAPH,
    ROUND,
    STADIUM_SHAPED,
    SUBROUTINE,
    CYLINDRICAL,
    CIRCLE,
    RHOMBUS,
    HEXAGON,
    PARALLELOGRAM,
    PARALLELOGRAM_ALT,
    TRAPEZOID,
    TRAPEZOID_ALT,
    DOUBLE_CIRCLE
  }

  public DiagramElement addTransitionFrom(Transition transition) {
    if (transitionFrom == null) {
      transitionFrom = new ArrayList<>();
    }

    transitionFrom.add(transition);
    return this;
  }

  public DiagramElement addTransitionTo(Transition transition) {
    if (transitionTo == null) {
      transitionTo = new ArrayList<>();
    }

    transitionTo.add(transition);
    return this;
  }

  public boolean isInitial() {
    return CollectionUtils.isEmpty(transitionTo);
  }

  public boolean isEnd() {
    return CollectionUtils.isEmpty(transitionFrom);
  }
}

