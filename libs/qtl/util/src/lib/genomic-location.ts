import { GenomicLocation } from '@sagebionetworks/qtl/api-client';

/** Formats a location the way genome browsers such as IGV and UCSC accept it, e.g. `chr11:82717576-82717577`. */
export function formatGenomicLocation({ chr, start, end }: GenomicLocation): string {
  return `chr${chr}:${start}-${end}`;
}
