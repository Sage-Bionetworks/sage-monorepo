import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';

export const MODEL_ORGANISMS = Object.values(ModelOrganism);

export function isModelOrganism(value: unknown): value is ModelOrganism {
  return typeof value === 'string' && (MODEL_ORGANISMS as string[]).includes(value);
}

export function resolveModelOrganism(raw: unknown): ModelOrganism {
  const normalized = typeof raw === 'string' ? raw.toLowerCase() : raw;
  return isModelOrganism(normalized) ? normalized : ModelOrganism.Mouse;
}
