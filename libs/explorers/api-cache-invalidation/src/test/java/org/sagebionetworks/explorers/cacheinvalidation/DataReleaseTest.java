package org.sagebionetworks.explorers.cacheinvalidation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DataReleaseTest {

  @Test
  @DisplayName("should describe the release the way the footer shows it")
  void shouldDescribeTheReleaseTheWayTheFooterShowsIt() {
    assertThat(new DataRelease("syn73774820", "191")).hasToString("syn73774820-v191");
  }
}
