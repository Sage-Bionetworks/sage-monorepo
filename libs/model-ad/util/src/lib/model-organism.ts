import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';

export const MODEL_ORGANISM_QUERY_KEY = 'modelOrganism';

export const MODEL_ORGANISMS = Object.values(ModelOrganism);

export function isModelOrganism(value: unknown): value is ModelOrganism {
  return typeof value === 'string' && (MODEL_ORGANISMS as string[]).includes(value);
}

export function parseModelOrganism(value: unknown): ModelOrganism | undefined {
  const normalized = typeof value === 'string' ? value.toLowerCase() : value;
  return isModelOrganism(normalized) ? normalized : undefined;
}

export function resolveModelOrganism(value: unknown): ModelOrganism {
  return parseModelOrganism(value) ?? ModelOrganism.Mouse;
}

export function isUnknownModelOrganism(value: unknown): boolean {
  return value != null && value !== '' && parseModelOrganism(value) === undefined;
}
