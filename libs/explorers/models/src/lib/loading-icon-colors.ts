import { InjectionToken } from '@angular/core';

export type LoadingIconColors = {
  colorInnermost: string;
  colorCentral: string;
  colorOutermost: string;
};

export const LOADING_ICON_COLORS = new InjectionToken<LoadingIconColors>('LOADING_ICON_COLORS');
