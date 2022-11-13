package com.bmo.parsers.parser;

import com.bmo.parsers.model.MermaidDiagram;
import org.junit.jupiter.api.Test;

class FileDiagramParserTest {

  @Test
  void parse() {
    FileDiagramParser fileDiagramParser = new FileDiagramParser("/schema.md");

    MermaidDiagram mermaidDiagram = fileDiagramParser.parse();

    System.out.println(mermaidDiagram);
  }
}