import { GenomicLocation } from '@sagebionetworks/qtl/api-client';
import { formatGenomicLocation } from './genomic-location';

describe('formatGenomicLocation', () => {
  it('formats the location as chr<chromosome>:<start>-<end>', () => {
    const variantLocation: GenomicLocation = { chr: '11', start: 82717576, end: 82717577 };
    expect(formatGenomicLocation(variantLocation)).toBe('chr11:82717576-82717577');
  });

  it('keeps a non-numeric chromosome name as is', () => {
    const xChromosomeLocation: GenomicLocation = { chr: 'X', start: 155669, end: 155670 };
    expect(formatGenomicLocation(xChromosomeLocation)).toBe('chrX:155669-155670');
  });
});
