package org.sagebionetworks.bixarena.api.api;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.VoteDto;
import org.sagebionetworks.bixarena.api.model.dto.VotePageDto;
import org.sagebionetworks.bixarena.api.model.dto.VoteSearchQueryDto;
import org.sagebionetworks.bixarena.api.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteApiDelegateImpl implements VoteApiDelegate {

  private final VoteService voteService;
  private final NativeWebRequest request;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<VotePageDto> listVotes(VoteSearchQueryDto voteSearchQuery) {
    log.info("Listing votes with query: {}", voteSearchQuery);
    return ResponseEntity.ok(voteService.listVotes(voteSearchQuery));
  }

  @Override
  public ResponseEntity<VoteDto> getVote(UUID voteId) {
    log.info("Getting vote with ID: {}", voteId);
    return ResponseEntity.ok(voteService.getVote(voteId));
  }
}
