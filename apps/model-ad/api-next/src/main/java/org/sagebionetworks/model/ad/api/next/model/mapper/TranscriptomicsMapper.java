package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.dto.FoldChangeResultDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsDto;
import org.sagebionetworks.model.ad.api.next.util.EnumConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TranscriptomicsMapper {

  private final LinkMapper linkMapper;

  public TranscriptomicsDto toDto(@Nullable TranscriptomicsDocument document) {
    if (document == null) {
      return null;
    }

    List<String> biodomains = document.getBiodomains() == null
      ? List.of()
      : List.copyOf(document.getBiodomains());

    String sex = normalizeSex(document.getSex());

    TranscriptomicsDto dto = new TranscriptomicsDto(
      getCompositeId(document, sex),
      document.getEnsemblGeneId(),
      getGeneSymbolWithFallback(document),
      biodomains,
      linkMapper.toNamedLinkDto(document.getName()),
      document.getMatchedControl(),
      document.getModelGroup(),
      document.getModelType(),
      document.getTissue(),
      EnumConverter.toSexDto(sex, "transcriptomics record")
    );

    dto.set4months(toFoldChangeDto(document.getFourMonths()));
    dto.set12months(toFoldChangeDto(document.getTwelveMonths()));
    dto.set18months(toFoldChangeDto(document.getEighteenMonths()));

    return dto;
  }

  private String getCompositeId(TranscriptomicsDocument document, String sex) {
    String ensemblGeneId = document.getEnsemblGeneId();
    String name = document.getName().getLinkText();

    return String.format("%s~%s~%s", ensemblGeneId, name, sex);
  }

  // TODO(MG-1004): remove once rna_de_aggregate stores singular Female/Male like the schema and
  // other collections. The source data currently uses plural Females/Males.
  private @Nullable String normalizeSex(@Nullable String sex) {
    if ("Females".equals(sex)) {
      return "Female";
    }
    if ("Males".equals(sex)) {
      return "Male";
    }
    return sex;
  }

  private String getGeneSymbolWithFallback(TranscriptomicsDocument document) {
    String geneSymbol = document.getGeneSymbol();
    if (geneSymbol == null || geneSymbol.isBlank()) {
      // Fallback to ensembl_gene_id if gene_symbol is null or blank
      return document.getEnsemblGeneId();
    }
    return geneSymbol;
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
