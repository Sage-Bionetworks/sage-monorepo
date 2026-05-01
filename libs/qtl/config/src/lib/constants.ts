import { LoadingIconColors } from '@sagebionetworks/explorers/models';

// TODO: update to QTL-specific colors (QTL-69)
export const QTL_LOADING_ICON_COLORS: LoadingIconColors = {
  colorInnermost: '#00C9BA',
  colorCentral: '#6F51C7',
  colorOutermost: '#00737C',
};

export const ROUTE_PATHS = {
  HOME: '',
  ABOUT: 'about',
} as const;
