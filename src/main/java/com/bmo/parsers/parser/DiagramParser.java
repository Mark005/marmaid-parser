package com.bmo.parsers.parser;

import com.bmo.parsers.model.MermaidDiagram;
import java.util.List;

public interface DiagramParser {
  MermaidDiagram parse(List<String> rawDiagramStrings);
}
