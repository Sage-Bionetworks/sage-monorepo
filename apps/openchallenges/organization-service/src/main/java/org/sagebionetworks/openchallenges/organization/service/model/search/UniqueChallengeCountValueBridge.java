package org.sagebionetworks.openchallenges.organization.service.model.search;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeParticipationEntity;

public class UniqueChallengeCountValueBridge
  implements ValueBridge<List<ChallengeParticipationEntity>, Integer> {

  @Override
  public Integer toIndexedValue(
    List<ChallengeParticipationEntity> value,
    ValueBridgeToIndexedValueContext context
  ) {
    if (value == null || value.isEmpty()) {
      return 0;
    }

    // Extract unique challenge IDs
    Set<Long> uniqueIds = value
      .stream()
      .filter(Objects::nonNull)
      .map(obj -> ((ChallengeParticipationEntity) obj).getChallengeId())
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());

    return uniqueIds.size();
  }
}
