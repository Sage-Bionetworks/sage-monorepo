package org.sagebionetworks.model.ad.api.next.model.repository;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.SearchResultDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;

public interface ModelSearchRepository {

  List<SearchResultDocument> searchModels(String query, List<ModelOrganismDto> organisms);
}
