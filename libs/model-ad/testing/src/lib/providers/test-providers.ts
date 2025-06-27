import { Provider } from '@angular/core';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/util';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';

export const LOADING_ICON_COLORS_PROVIDER: Provider[] = [
  {
    provide: LOADING_ICON_COLORS,
    useValue: MODEL_AD_LOADING_ICON_COLORS,
  },
];
