import { Sex } from '@sagebionetworks/model-ad/api-client';
import { BOXPLOT_POINT_STYLES } from '@sagebionetworks/model-ad/config';

export function getPointStylesBySex(sexFilter: Sex[] | undefined) {
  if (sexFilter) {
    return BOXPLOT_POINT_STYLES.filter((s) => sexFilter.includes(s.label as Sex));
  }
  return BOXPLOT_POINT_STYLES;
}
