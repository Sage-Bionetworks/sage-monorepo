import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';
import { isModelOrganism, MODEL_ORGANISMS, resolveModelOrganism } from './model-organism';

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
});
