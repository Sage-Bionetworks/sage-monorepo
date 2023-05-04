package org.sagebionetworks.openchallenges.challenge.service.model.search;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class ChallengeInputDataTypeNameValueBridge implements ValueBridge<String, String> {

  @Override
  public String toIndexedValue(String value, ValueBridgeToIndexedValueContext context) {
    // This will replace all the characters except alphanumeric ones
    return value == null ? null : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
  }
}
