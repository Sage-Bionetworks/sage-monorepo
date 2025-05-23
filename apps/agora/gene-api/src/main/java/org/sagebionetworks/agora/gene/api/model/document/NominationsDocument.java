package org.sagebionetworks.agora.gene.api.model.document;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NominationsDocument {

  private Integer count;

  private Integer year;

  private List<String> teams;

  private List<String> studies;

  private List<String> inputs;

  private List<String> programs;

  private List<String> validations;
}
