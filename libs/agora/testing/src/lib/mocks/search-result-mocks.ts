import { SearchResult } from '@sagebionetworks/agora/api-client';

export const mockSearchResults: SearchResult[] = [
  { id: 'ENSG00000000010', match_field: 'alias', match_value: 'Alias 1', hgnc_symbol: 'GENE1' },
  { id: 'ENSG00000000011', match_field: 'alias', match_value: 'Alias 2', hgnc_symbol: 'GENE1' },
  { id: 'ENSG00000000012', match_field: 'hgnc_symbol', match_value: 'GENE2', hgnc_symbol: 'GENE2' },
  { id: 'ENSG00000000013', match_field: 'hgnc_symbol', match_value: 'GENE3', hgnc_symbol: 'GENE3' },
];
