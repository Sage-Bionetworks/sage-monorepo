package org.sagebionetworks.openchallenges.organization.service.model.search;

import java.util.List;
import org.hibernate.search.mapper.pojo.bridge.binding.ValueBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.ValueBinder;
import org.sagebionetworks.openchallenges.organization.service.model.entity.ChallengeParticipationEntity;

public class UniqueChallengeCountValueBinder implements ValueBinder {

  @SuppressWarnings("unchecked")
  @Override
  public void bind(ValueBindingContext<?> context) {
    // Pass the erased type and the bridge
    context.bridge(
      (Class<List<ChallengeParticipationEntity>>) (Class<?>) List.class,
      new UniqueChallengeCountValueBridge()
    );
  }
}
