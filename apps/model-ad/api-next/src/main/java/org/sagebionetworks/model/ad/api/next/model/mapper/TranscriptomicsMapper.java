package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIdentifier;
import org.sagebionetworks.model.ad.api.next.util.EnumConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TranscriptomicsMapper {

  private final LinkMapper linkMapper;
  private final FoldChangeMapper foldChangeMapper;

  public TranscriptomicsDto toDto(@Nullable TranscriptomicsDocument document) {
    if (document == null) {
      return null;
    }

    List<String> biodomains = document.getBiodomains() == null
      ? List.of()
      : List.copyOf(document.getBiodomains());

    TranscriptomicsDto dto = new TranscriptomicsDto(
      getCompositeId(document),
      document.getEnsemblGeneId(),
      getGeneSymbolWithFallback(document),
      biodomains,
      linkMapper.toNamedLinkDto(document.getName()),
      document.getMatchedControl(),
      document.getModelGroup(),
      document.getModelType(),
      document.getTissue(),
      EnumConverter.toSexDto(document.getSex(), "transcriptomics record")
    );

    dto.set4months(foldChangeMapper.toNullableDto(document.getFourMonths()));
    dto.set12months(foldChangeMapper.toNullableDto(document.getTwelveMonths()));
    dto.set18months(foldChangeMapper.toNullableDto(document.getEighteenMonths()));
    dto.set24months(foldChangeMapper.toNullableDto(document.getTwentyFourMonths()));

    return dto;
  }

  private String getCompositeId(TranscriptomicsDocument document) {
    return TranscriptomicsIdentifier.builder()
      .ensemblGeneId(document.getEnsemblGeneId())
      .name(document.getName().getLinkText())
      .sex(document.getSex())
      .build()
      .toCompositeId();
  }

  private String getGeneSymbolWithFallback(TranscriptomicsDocument document) {
    String geneSymbol = document.getGeneSymbol();
    if (geneSymbol == null || geneSymbol.isBlank()) {
      // Fallback to ensembl_gene_id if gene_symbol is null or blank
      return document.getEnsemblGeneId();
    }
    return geneSymbol;
  }
}
