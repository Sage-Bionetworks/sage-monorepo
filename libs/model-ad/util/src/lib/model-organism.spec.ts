import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';
import {
  isModelOrganism,
  isUnknownModelOrganism,
  MODEL_ORGANISMS,
  parseModelOrganism,
  resolveModelOrganism,
} from './model-organism';

describe('model-organism', () => {
  describe('MODEL_ORGANISMS', () => {
    it('should contain the generated organism values', () => {
      expect(MODEL_ORGANISMS).toEqual(
        expect.arrayContaining([ModelOrganism.Mouse, ModelOrganism.Marmoset]),
      );
    });
  });

  describe('isModelOrganism', () => {
    it('should return true for valid organism values', () => {
      expect(isModelOrganism('mouse')).toBe(true);
      expect(isModelOrganism('marmoset')).toBe(true);
    });

    it('should return false for invalid values', () => {
      expect(isModelOrganism('Mouse')).toBe(false);
      expect(isModelOrganism('rat')).toBe(false);
      expect(isModelOrganism('')).toBe(false);
      expect(isModelOrganism(null)).toBe(false);
      expect(isModelOrganism(undefined)).toBe(false);
      expect(isModelOrganism(42)).toBe(false);
    });
  });

  describe('parseModelOrganism', () => {
    it('should return the organism for valid values regardless of case', () => {
      expect(parseModelOrganism('mouse')).toBe(ModelOrganism.Mouse);
      expect(parseModelOrganism('Marmoset')).toBe(ModelOrganism.Marmoset);
      expect(parseModelOrganism('MOUSE')).toBe(ModelOrganism.Mouse);
    });

    it('should return undefined for absent, empty, or unknown values', () => {
      expect(parseModelOrganism(null)).toBeUndefined();
      expect(parseModelOrganism(undefined)).toBeUndefined();
      expect(parseModelOrganism('')).toBeUndefined();
      expect(parseModelOrganism('rat')).toBeUndefined();
      expect(parseModelOrganism(42)).toBeUndefined();
    });
  });

  describe('resolveModelOrganism', () => {
    it('should return the organism when valid', () => {
      expect(resolveModelOrganism('mouse')).toBe(ModelOrganism.Mouse);
      expect(resolveModelOrganism('marmoset')).toBe(ModelOrganism.Marmoset);
    });

    it('should match case-insensitively', () => {
      expect(resolveModelOrganism('Mouse')).toBe(ModelOrganism.Mouse);
      expect(resolveModelOrganism('Marmoset')).toBe(ModelOrganism.Marmoset);
      expect(resolveModelOrganism('MARMOSET')).toBe(ModelOrganism.Marmoset);
    });

    it('should fall back to mouse for absent, empty, or unknown values', () => {
      expect(resolveModelOrganism(null)).toBe(ModelOrganism.Mouse);
      expect(resolveModelOrganism(undefined)).toBe(ModelOrganism.Mouse);
      expect(resolveModelOrganism('')).toBe(ModelOrganism.Mouse);
      expect(resolveModelOrganism('rat')).toBe(ModelOrganism.Mouse);
    });
  });

  describe('isUnknownModelOrganism', () => {
    it('should not report absent or empty values', () => {
      expect(isUnknownModelOrganism(null)).toBe(false);
      expect(isUnknownModelOrganism(undefined)).toBe(false);
      expect(isUnknownModelOrganism('')).toBe(false);
    });

    it('should not report valid values regardless of case', () => {
      expect(isUnknownModelOrganism('mouse')).toBe(false);
      expect(isUnknownModelOrganism('Marmoset')).toBe(false);
    });

    it('should report present values that are not an organism', () => {
      const repeatedQueryParam = [ModelOrganism.Mouse, ModelOrganism.Marmoset];
      expect(isUnknownModelOrganism('rat')).toBe(true);
      expect(isUnknownModelOrganism('  ')).toBe(true);
      expect(isUnknownModelOrganism(repeatedQueryParam)).toBe(true);
      expect(isUnknownModelOrganism(42)).toBe(true);
    });
  });
});
