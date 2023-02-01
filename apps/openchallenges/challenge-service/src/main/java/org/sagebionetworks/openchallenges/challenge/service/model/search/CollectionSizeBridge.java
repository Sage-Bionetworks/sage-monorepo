package org.sagebionetworks.openchallenges.challenge.service.model.search;

import java.util.Collection;
import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class CollectionSizeBridge implements ValueBridge<Collection, Integer> {

  @Override
  public Integer toIndexedValue(Collection value, ValueBridgeToIndexedValueContext context) {
    return value == null ? null : value.size();
  }
}
