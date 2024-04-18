package org.sagebionetworks.openchallenges.challenge.service.model.search;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
import org.sagebionetworks.openchallenges.challenge.service.model.dto.EdamSectionDto;

public class EdamSectionValueBridge implements ValueBridge<String, String> {

  @Override
  public String toIndexedValue(String value, ValueBridgeToIndexedValueContext context) {
    for (EdamSectionDto section : EdamSectionDto.values()) {
      // The first condition is used to map EDAM concept class IDs to concept sections during mass
      // indexing. This value bridge is also called when sending search queries to Elasticsearch,
      // which is why the second condition is required to map the user input to an existing EDAM
      // concept.
      if (value.contains("/" + section.getValue() + "_") || section.getValue().equals(value)) {
        return section.getValue();
      }
    }
    return null;
  }
}
