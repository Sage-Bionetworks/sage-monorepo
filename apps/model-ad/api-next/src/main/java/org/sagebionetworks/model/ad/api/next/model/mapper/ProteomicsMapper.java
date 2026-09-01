package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIdentifier;
import org.sagebionetworks.model.ad.api.next.util.EnumConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProteomicsMapper {

  private final LinkMapper linkMapper;
  private final FoldChangeMapper foldChangeMapper;

  public ProteomicsDto toDto(@Nullable ProteomicsDocument document) {
    if (document == null) {
      return null;
    }

    List<String> biodomains = document.getBiodomains() == null
      ? List.of()
      : List.copyOf(document.getBiodomains());

    ProteomicsDto dto = new ProteomicsDto(
      getCompositeId(document),
      document.getEnsemblGeneId(),
      document.getGeneSymbol(),
      document.getUniprotid(),
      document.getUniqueId(),
      document.getDisplaySymbol(),
      biodomains,
      linkMapper.toNamedLinkDto(document.getName()),
      document.getMatchedControl(),
      document.getModelGroup(),
      document.getModelType(),
      document.getTissue(),
      EnumConverter.toSexDto(document.getSex(), "proteomics record")
    );

    dto.set4months(foldChangeMapper.toNullableDto(document.getFourMonths()));
    dto.set12months(foldChangeMapper.toNullableDto(document.getTwelveMonths()));
    dto.set18months(foldChangeMapper.toNullableDto(document.getEighteenMonths()));
    dto.set24months(foldChangeMapper.toNullableDto(document.getTwentyFourMonths()));

    return dto;
  }

  private String getCompositeId(ProteomicsDocument document) {
    return ProteomicsIdentifier.builder()
      .uniqueId(document.getUniqueId())
      .name(document.getName().getLinkText())
      .sex(document.getSex())
      .build()
      .toCompositeId();
  }
}
