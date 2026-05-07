export type SvgIconBackgroundShape = 'circle' | 'square';

export interface SvgIconConfig {
  imagePath: string;
  altText?: string;
  width?: number;
  height?: number;
  color?: string;
  enableHoverEffects?: boolean;
  backgroundColor?: string;
  backgroundShape?: SvgIconBackgroundShape;
  backgroundPadding?: number;
}
