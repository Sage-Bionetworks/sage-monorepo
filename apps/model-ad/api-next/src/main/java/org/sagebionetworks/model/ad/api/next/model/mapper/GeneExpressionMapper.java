package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.dto.FoldChangeResultDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionDto;
import org.sagebionetworks.model.ad.api.next.util.EnumConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GeneExpressionMapper {

  public GeneExpressionDto toDto(@Nullable GeneExpressionDocument document) {
    if (document == null) {
      return null;
    }

    List<String> biodomains = document.getBiodomains() == null
      ? List.of()
      : List.copyOf(document.getBiodomains());

    GeneExpressionDto dto = new GeneExpressionDto(
      getCompositeId(document),
      document.getEnsemblGeneId(),
      document.getGeneSymbol(),
      biodomains,
      document.getName(),
      document.getMatchedControl(),
      document.getModelGroup(),
      document.getModelType(),
      document.getTissue(),
      EnumConverter.toSexCohortDto(document.getSexCohort(), "gene expression record")
    );

    dto.set4months(toFoldChangeDto(document.getFourMonths()));
    dto.set12months(toFoldChangeDto(document.getTwelveMonths()));
    dto.set18months(toFoldChangeDto(document.getEighteenMonths()));

    return dto;
  }

  private String getCompositeId(GeneExpressionDocument document) {
    String ensemblGeneId = document.getEnsemblGeneId();
    String name = document.getName();

    return String.format("%s~%s", ensemblGeneId, name);
  }

  private @Nullable FoldChangeResultDto toFoldChangeDto(@Nullable FoldChangeResult document) {
    if (document == null) {
      return null;
    }
    Double log2Fc = document.getLog2Fc();
    Double adjPVal = document.getAdjPVal();
    if (log2Fc == null || adjPVal == null) {
      return null;
    }
    return new FoldChangeResultDto(BigDecimal.valueOf(log2Fc), BigDecimal.valueOf(adjPVal));
  }
}
