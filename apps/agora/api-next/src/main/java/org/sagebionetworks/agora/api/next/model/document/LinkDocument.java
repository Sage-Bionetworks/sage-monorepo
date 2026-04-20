package org.sagebionetworks.agora.api.next.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
public class LinkDocument {

  @Field("link_text")
  private String linkText;

  @Field("link_url")
  private String linkUrl;
}
