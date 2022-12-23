package org.sagebionetworks.challenge.model.repository;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeFilter {
  private List<String> status;
  private List<String> platforms;
  private List<String> difficulties;
  private List<String> incentives;
}
