package org.sagebionetworks.qtl.api.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.qtl.api.exception.ComparisonToolConfigNotFoundException;
import org.sagebionetworks.qtl.api.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.qtl.api.model.mapper.ComparisonToolConfigMapper;
import org.sagebionetworks.qtl.api.model.repository.ComparisonToolConfigRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ComparisonToolConfigService {

  private final ComparisonToolConfigRepository repository;
  private final ComparisonToolConfigMapper comparisonToolConfigMapper;

  public List<ComparisonToolConfigDto> getConfigsByPage(ComparisonToolPageDto page) {
    List<ComparisonToolConfigDocument> documents = repository.findByPage(page.getValue());

    if (documents == null || documents.isEmpty()) {
      throw new ComparisonToolConfigNotFoundException(page.getValue());
    }

    return documents.stream().map(comparisonToolConfigMapper::toDto).toList();
  }
}
