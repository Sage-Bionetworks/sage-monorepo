import { Provider } from '@angular/core';
import { LOADING_ICON_COLORS } from '@sagebionetworks/explorers/constants';
import { LoadingIconColors } from '@sagebionetworks/explorers/models';

export const mockLoadingIconColors: LoadingIconColors = {
  colorInnermost: '#000000',
  colorCentral: '#000000',
  colorOutermost: '#000000',
};

export const provideLoadingIconColors = (loadingIconColors = mockLoadingIconColors): Provider[] => {
  return [
    {
      provide: LOADING_ICON_COLORS,
      useValue: loadingIconColors,
    },
  ];
};
