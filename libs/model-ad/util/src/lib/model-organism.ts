import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';

export const MODEL_ORGANISMS = Object.values(ModelOrganism);

export function isModelOrganism(value: unknown): value is ModelOrganism {
  return typeof value === 'string' && (MODEL_ORGANISMS as string[]).includes(value);
}

export function resolveModelOrganism(value: unknown): ModelOrganism {
  const normalized = typeof value === 'string' ? value.toLowerCase() : value;
  return isModelOrganism(normalized) ? normalized : ModelOrganism.Mouse;
}
