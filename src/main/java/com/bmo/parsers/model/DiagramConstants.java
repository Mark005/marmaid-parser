package com.bmo.parsers.model;

import com.bmo.parsers.model.DiagramElement.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiagramConstants {
  public static final String DEFINITION_START = "```mermaid";
  public static final String DEFINITION_END = "```";
  public static final String HEADER = "flowchart";
  public static final String SUBGRAPH_START = "subgraph";
  public static final String SUBGRAPH_END = "end";

  public static final List<String> PRE_ARROWS =
      Arrays.asList(
          "--",
          "---",
          "----",
          "-----",
          "------",
          "-------",
          "--------",
          "---------",
          "----------",
          "-----------",
          "------------",
          "-------------",
          "--------------",
          "---------------",
          "----------------",
          "-----------------"
      );
  public static final List<String> POST_ARROWS =
      Arrays.asList(
          "-->",
          "--->",
          "---->",
          "----->",
          "------>",
          "------->",
          "-------->",
          "--------->",
          "---------->",
          "----------->",
          "------------>",
          "------------->",
          "-------------->",
          "--------------->",
          "---------------->",
          "----------------->"
      );
  public static final List<String> ARROW_PARTS = joinLists(PRE_ARROWS, POST_ARROWS);

  public static final List<Triple<Type, String, String>> COMMENT_PARTS = Arrays.asList(
      new Triple<>(Type.DOUBLE_CIRCLE, "(((", ")))"),
      new Triple<>(Type.CIRCLE, "((", "))"),
      new Triple<>(Type.HEXAGON, "{{", "}}"),
      new Triple<>(Type.TRAPEZOID, "[/", "\\]"),
      new Triple<>(Type.TRAPEZOID_ALT, "[\\", "/]"),
      new Triple<>(Type.STADIUM_SHAPED, "([", "])"),
      new Triple<>(Type.SUBROUTINE, "[[", "]]"),
      new Triple<>(Type.CYLINDRICAL, "[(", ")]"),
      new Triple<>(Type.PARALLELOGRAM, "[/", "/]"),
      new Triple<>(Type.PARALLELOGRAM_ALT, "[\\", "\\]"),
      new Triple<>(Type.RHOMBUS, "{", "}"),
      new Triple<>(Type.ROUND, "(", ")")
  );



  public enum Arrow {
    PRE,
    POST
  }
  
  private static <T> List<T> joinLists(List<T>... lists) {
    List<T> list = new ArrayList<>();
    for (List<T> element : lists) {
      list.addAll(element);
    }
    return list;
  }
}
