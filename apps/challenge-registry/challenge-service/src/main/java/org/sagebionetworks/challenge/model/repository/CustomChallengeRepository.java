package org.sagebionetworks.challenge.model.repository;

import java.util.List;
import org.sagebionetworks.challenge.model.entity.ChallengeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomChallengeRepository {

  Page<ChallengeEntity> findAll(Pageable pageable, ChallengeFilter filter);

  List<ChallengeEntity> searchBy(String text, String... fields);
}
