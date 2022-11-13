package com.bmo.parsers.model;

import com.bmo.parsers.model.DiagramElement.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

@Data
public class MermaidDiagram implements Element {
  private String name;
  private List<DiagramElement> diagramElements = new ArrayList<>();

  private List<Transition> transitions = new ArrayList<>();

  public MermaidDiagram addDiagramElement(DiagramElement diagramElement) {
    diagramElements.add(diagramElement);
    return this;
  }

  public MermaidDiagram addTransition(Transition transition) {
    transitions.add(transition);
    return this;
  }

  public Optional<DiagramElement> findElementByName(String name) {
    return diagramElements.stream()
        .filter(diagramElement -> diagramElement.getName().equals(name))
        .findFirst();
  }

  public List<String> getEvents() {
    return transitions.stream()
        .map(Transition::getEvent)
        .filter(Objects::isNull)
        .collect(Collectors.toList());
  }

  public Optional<DiagramElement> findInitialState() {
    return diagramElements.stream()
        .filter(diagramElement -> CollectionUtils.isEmpty(diagramElement.getTransitionTo()))
        .findFirst();
  }

  public List<DiagramElement> getNotGraphStates() {
    return diagramElements.stream()
        .filter(diagramElement -> diagramElement.getType() != Type.SUBGRAPH)
        .collect(Collectors.toList());
  }
}
