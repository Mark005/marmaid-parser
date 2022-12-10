package com.bmo.parsers.marmaiddiagram.parser;

import com.bmo.parsers.marmaiddiagram.model.MermaidDiagram;
import java.util.List;

public interface DiagramParser {
  MermaidDiagram parse(List<String> rawDiagramStrings);
}
