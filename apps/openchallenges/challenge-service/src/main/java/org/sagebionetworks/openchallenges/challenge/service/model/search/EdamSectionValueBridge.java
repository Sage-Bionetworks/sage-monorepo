package org.sagebionetworks.openchallenges.challenge.service.model.search;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamSectionDto;

public class EdamSectionValueBridge implements ValueBridge<String, String> {

  @Override
  public String toIndexedValue(String value, ValueBridgeToIndexedValueContext context) {
    for (EdamSectionDto section : EdamSectionDto.values()) {
      if (value.contains("/" + section.getValue() + "_")) {
        return section.getValue();
      }
    }
    return "banana";
  }
}
