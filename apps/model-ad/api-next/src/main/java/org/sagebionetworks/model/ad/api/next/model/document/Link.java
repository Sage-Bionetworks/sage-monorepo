package org.sagebionetworks.model.ad.api.next.model.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Builder
@Getter
@Setter
public class Link {

  @Field("link_text")
  private String linkText;

  @Field("link_url")
  private String linkUrl;
}
