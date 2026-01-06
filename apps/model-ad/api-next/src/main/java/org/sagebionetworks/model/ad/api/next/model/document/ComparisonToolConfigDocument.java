package org.sagebionetworks.model.ad.api.next.model.document;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "ui_config")
public class ComparisonToolConfigDocument {

  @Id
  private ObjectId id;

  private String page;

  private List<String> dropdowns;

  @Field("row_count")
  private String rowCount;

  private List<ComparisonToolConfigColumn> columns;

  private List<ComparisonToolConfigFilter> filters;

  @Builder
  @Getter
  @Setter
  public static class ComparisonToolConfigColumn {

    private @Nullable String name;

    private String type;

    @Field("data_key")
    private String dataKey;

    private @Nullable String tooltip;

    @Field("sort_tooltip")
    private @Nullable String sortTooltip;

    @Field("link_text")
    private @Nullable String linkText;

    @Field("link_url")
    private @Nullable String linkUrl;

    @Field("is_exported")
    private Boolean isExported;

    @Field("is_hidden")
    private Boolean isHidden;
  }

  @Builder
  @Getter
  @Setter
  public static class ComparisonToolConfigFilter {

    private String name;

    @Field("data_key")
    private String dataKey;

    @Field("short_name")
    private @Nullable String shortName;

    @Field("query_param_key")
    private String queryParamKey;

    private List<String> values;
  }
}
