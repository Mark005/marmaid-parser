package com.bmo.parsers.marmaiddiagram.parser;

import com.bmo.parsers.marmaiddiagram.exception.MermaidDiagramParsingException;
import com.bmo.parsers.marmaiddiagram.model.DiagramConstants;
import com.bmo.parsers.marmaiddiagram.model.MermaidDiagram;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class FileDiagramParser {

  private String path;
  private List<String> content;
  private DiagramParser diagramParser;

  public FileDiagramParser(String path) {
    this(path, new DefaultDiagramParser());
  }

  public FileDiagramParser(String path, DiagramParser diagramParser) {
    try (InputStream in = getClass().getResourceAsStream(path)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));

      this.path = path;
      content = reader.lines().collect(Collectors.toList());
      System.out.println("\n\n\n\n" + this.path);
      System.out.println("\n\n\n\n" + content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.diagramParser = diagramParser;
  }

  public MermaidDiagram parse() {
    List<String> lines = content;

    List<String> rawDiagramStrings = getDiagramBody(lines);

    MermaidDiagram diagram = diagramParser.parse(rawDiagramStrings);
    diagram.setName(getFilenameWithoutExtension());
    return diagram;
  }


  private List<String> getDiagramBody(List<String> lines) {
    List<String> withoutWhitespaces = lines.stream()
        .filter(StringUtils::isNoneBlank)
        .collect(Collectors.toList());
    List<String> diagramDefinition = extractDiagramDefinition(withoutWhitespaces);
    String removed = diagramDefinition.remove(0);
    if (!removed.contains(DiagramConstants.HEADER)) {
      throw new MermaidDiagramParsingException(
          "Wrong diagram structure: missed [%s] keyword", DiagramConstants.HEADER);
    }
    return diagramDefinition;
  }

  private List<String> extractDiagramDefinition(List<String> lines) {
    List<String> diagram = new ArrayList<>();
    boolean isDiagram = false;
    Iterator<String> iterator = lines.iterator();
    while (iterator.hasNext()) {
      String next = iterator.next();
      String nextWithoutWhitespaces = next.replaceAll("\\s", "");

      if (DiagramConstants.DEFINITION_END.equals(nextWithoutWhitespaces)) {
        isDiagram = false;
      }

      if (isDiagram) {
        diagram.add(next);
      }

      if (DiagramConstants.DEFINITION_START.equals(nextWithoutWhitespaces)) {
        isDiagram = true;
      }
    }
    return diagram.stream()
        .map(StringUtils::normalizeSpace)
        .collect(Collectors.toList());
  }

//  private List<String> getFileAsStrings() {
//    try {
//      return Files.readAllLines(path);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }

  private String getFilenameWithoutExtension() {
    String fileNameWithExtension = path.substring(path.lastIndexOf("/"), path.length() - 1);
    return fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));
  }
}
