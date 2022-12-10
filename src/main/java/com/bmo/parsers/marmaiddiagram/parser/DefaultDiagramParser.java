package com.bmo.parsers.marmaiddiagram.parser;

import com.bmo.parsers.marmaiddiagram.exception.MermaidDiagramParsingException;
import com.bmo.parsers.marmaiddiagram.model.DiagramElement;
import com.bmo.parsers.marmaiddiagram.model.DiagramElement.Type;
import com.bmo.parsers.marmaiddiagram.model.DiagramConstants;
import com.bmo.parsers.marmaiddiagram.model.DiagramConstants.Arrow;
import com.bmo.parsers.marmaiddiagram.model.MermaidDiagram;
import com.bmo.parsers.marmaiddiagram.model.Transition;
import com.bmo.parsers.marmaiddiagram.model.Triple;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class DefaultDiagramParser implements DiagramParser {

  @Override
  public MermaidDiagram parse(List<String> rawDiagramStrings) {
    MermaidDiagram diagram = new MermaidDiagram();

    parseGraph(diagram, null, rawDiagramStrings);
    return diagram;
  }

  public void parseGraph(MermaidDiagram diagram, DiagramElement parent, List<String> block) {
    for (int i = 0; i < block.size(); i++) {
      String line = block.get(i);

      if (line.contains(DiagramConstants.SUBGRAPH_START)) {
        DiagramElement diagramElement = new DiagramElement();
        diagramElement.setType(Type.SUBGRAPH);
        diagramElement.setName(extractSubgraphName(line));
        if (parent != null) {
          diagramElement.setParent(parent);
        }

        diagram.addDiagramElement(diagramElement);

        int nextElementIndex = i + 1;

        int endOfTheSubgraph =
            nextElementIndex + findEndOfTheSubgraph(
                block.subList(nextElementIndex, block.size()));

        List<String> subList = block.subList(nextElementIndex, endOfTheSubgraph);
        parseGraph(diagram, diagramElement, subList);

        i = endOfTheSubgraph;
      }

      parseDiagramElementOrTransition(diagram, parent, line);
    }
  }

  private void parseDiagramElementOrTransition(MermaidDiagram diagram, DiagramElement parent, String line) {
    Arrow previousArrowElement = null;
    DiagramElement previousDiagramElement = null;
    Transition previousTransitionElement = null;

    validateTransitionElements(line);

    List<String> elements = splitElements(line);

    boolean isDiagramElement = true;
    for (int i = 0; i < elements.size(); i++) {
      String element = elements.get(i);
      if (i == 0) {
        previousDiagramElement = createDiagramElement(diagram, element);
      }

      if (isDiagramElement) {
        if (previousArrowElement == Arrow.PRE) {
          previousTransitionElement = addTransition(diagram, element, previousDiagramElement, null);
        }

        if (previousArrowElement == Arrow.POST) {
          previousDiagramElement = addBending(diagram, parent, element, previousDiagramElement, previousTransitionElement);
          previousTransitionElement = null;
        }
      }

      if (!isDiagramElement) {
        if (DiagramConstants.PRE_ARROWS.contains(element)) {
          previousArrowElement = Arrow.PRE;
        } else if (DiagramConstants.POST_ARROWS.contains(element)) {
          previousArrowElement = Arrow.POST;
        }
      }

      isDiagramElement = !isDiagramElement;
    }
  }

  private List<String> splitElements(String line) {
    List<String> result = new ArrayList<>();

    String[] split = line.split(" ");
    StringBuilder resultString = new StringBuilder();

    for (String element : split) {
      List<Triple<Type, String, String>> leftCommentParts =
          DiagramConstants.COMMENT_PARTS.stream()
              .filter(commentPart -> element.contains(commentPart.getSecond()))
              .collect(Collectors.toList());

      List<Triple<Type, String, String>> rightCommentParts =
          DiagramConstants.COMMENT_PARTS.stream()
              .filter(commentPart -> element.contains(commentPart.getThird()))
              .collect(Collectors.toList());

      boolean leftEmpty = CollectionUtils.isEmpty(leftCommentParts);
      boolean rightEmpty = CollectionUtils.isEmpty(rightCommentParts);

      if (resultString.length() == 0) {
        if (leftEmpty && rightEmpty ||
            !leftEmpty && !rightEmpty) {
          result.add(element);
        }

        if (!leftEmpty && rightEmpty) {
          resultString.append(element);
        }
      } else {
        if (rightEmpty) {
          resultString.append(" ").append(element);
          continue;
        }
        resultString.append(" ").append(element);

        result.add(resultString.toString());
        resultString.setLength(0);
      }
    }

    return result;
  }

  private Transition addTransition(
      MermaidDiagram diagram,
      String event,
      DiagramElement fromElement,
      DiagramElement toElement) {

    Transition transition = Transition.builder()
        .event(event)
        .from(fromElement)
        .to(toElement)
        .build();

    diagram.addTransition(transition);
    return transition;
  }

  private DiagramElement addBending(
      MermaidDiagram diagram,
      DiagramElement parent,
      String element,
      DiagramElement previousDiagramElement,
      Transition previousTransitionElement) {
    DiagramElement diagramElement = createDiagramElement(diagram, element);
    if (diagramElement.getType() != Type.SUBGRAPH) {
      diagramElement.setParent(parent);
    }

    if (previousTransitionElement == null) {
      previousTransitionElement =
          addTransition(diagram, null, previousDiagramElement, diagramElement);
    }
    previousTransitionElement.setTo(diagramElement);

    previousDiagramElement.addTransitionFrom(previousTransitionElement);
    diagramElement.addTransitionTo(previousTransitionElement);

    return diagramElement;
  }

  private DiagramElement createDiagramElement(MermaidDiagram diagram, String element) {
    Optional<Triple<Type, String, String>> typeTripleOpt =
        DiagramConstants.COMMENT_PARTS.stream()
            .filter(commentType ->
                Optional.ofNullable(StringUtils.substringBetween(
                            element,
                            commentType.getSecond(),
                            commentType.getThird()))
                    .map(String::length)
                    .orElse(0) != 0)
            .findFirst();

    String comment =
        typeTripleOpt.map(commentType -> StringUtils.substringBetween(
                element,
                commentType.getSecond(),
                commentType.getThird()))
            .orElse(null);
    String name =
        typeTripleOpt.map(commentType -> StringUtils.substringBefore(element, commentType.getSecond()))
            .orElse(element);
    Type type = typeTripleOpt.map(Triple::getFirst).orElse(Type.NONE);

    Optional<DiagramElement> elementByName = diagram.findElementByName(name);

    if (elementByName.isPresent()) {
      DiagramElement foundedElement = elementByName.get();

      if(StringUtils.isBlank(foundedElement.getComment())) {
        foundedElement.setComment(comment);
      }

      if(foundedElement.getType() == null) {
        foundedElement.setType(type);
      }

      if (type != Type.NONE &&
          foundedElement.getType() != type) {
        throw new MermaidDiagramParsingException(
            "Element [%s] have two different types [%s], [%s]",
            foundedElement.getName(),
            foundedElement.getType(),
            type);
      }

      return foundedElement;
    }

    DiagramElement diagramElement = DiagramElement.builder()
        .name(name)
        .comment(comment)
        .type(type)
        .build();

    diagram.addDiagramElement(diagramElement);
    return diagramElement;

  }

  private void validateTransitionElements(String line) {
    String[] elements = line.split(" ");

    for (String element : elements) {
      Optional<String> arrowPart = DiagramConstants.ARROW_PARTS
          .stream()
          .filter(element::contains)
          .max(String::compareTo);

      if (arrowPart.isPresent()) {
        boolean isValid = arrowPart
            .map(arrPart -> element.replace(arrPart, ""))
            .filter(""::equals)
            .isPresent();

        if (!isValid) {
          throw new MermaidDiagramParsingException(
              "Put space between arrow and element in line: %s", line);
        }
      }
    }
  }

  public int findEndOfTheSubgraph(List<String> block) {
    int subgraphCount = 1;
    for (int i = 0; i < block.size(); i++) {
      String line = block.get(i);
      if (line.contains(DiagramConstants.SUBGRAPH_START)) {
        subgraphCount ++;
      }

      if (line.contains(DiagramConstants.SUBGRAPH_END)) {
        subgraphCount --;
      }

      if (subgraphCount == 0) {
        return i;
      }
    }
    throw new MermaidDiagramParsingException("Subgraph should be closed");
  }

  public String extractSubgraphName(String source) {
    return Optional.of(source)
        .map(string -> string.replace(DiagramConstants.SUBGRAPH_START, ""))
        .map(StringUtils::normalizeSpace)
        .orElseThrow(() ->
            new MermaidDiagramParsingException("Subgraph name can't be empty"));
  }
}
