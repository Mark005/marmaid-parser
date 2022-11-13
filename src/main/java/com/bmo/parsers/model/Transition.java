package com.bmo.parsers.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transition {

  private String event;

  private DiagramElement from;

  private DiagramElement to;

  public boolean hasEvent() {
    return StringUtils.isNotBlank(event);
  }
}
