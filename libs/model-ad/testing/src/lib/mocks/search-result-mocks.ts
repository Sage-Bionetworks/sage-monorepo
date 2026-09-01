import { ModelOrganism, SearchResult } from '@sagebionetworks/model-ad/api-client';

export const mockSearchResults: SearchResult[] = [
  {
    id: 'model1',
    match_field: 'name',
    match_value: 'model1',
    model_organism: ModelOrganism.Mouse,
  },
  {
    id: 'model2',
    match_field: 'aliases',
    match_value: 'Alias Model',
    model_organism: ModelOrganism.Mouse,
  },
  {
    id: 'model3',
    match_field: 'jax_id',
    match_value: 'JAX123',
    model_organism: ModelOrganism.Mouse,
  },
  {
    id: 'model4',
    match_field: 'rrid',
    match_value: 'RRID123',
    model_organism: ModelOrganism.Mouse,
  },
  {
    id: 'Presenilin 1',
    match_field: 'name',
    match_value: 'Presenilin 1',
    model_organism: ModelOrganism.Marmoset,
  },
];
