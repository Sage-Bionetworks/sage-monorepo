import { Component, computed, inject, input } from '@angular/core';
import { HeatmapCircleData } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService, HelperService } from '@sagebionetworks/explorers/services';
import { TooltipModule } from 'primeng/tooltip';
import {
  ADJUSTED_P_VALUE_LABEL,
  knownColorMetricToDisplayName,
} from '../../comparison-tool.variables';
import { resolveHeatmapCircleMetrics } from './heatmap-circle.utils';

// Used as the circle's CSS class, so manually keep in sync with the stylesheet's selectors
type CircleValueSign = 'zero' | 'plus' | 'minus';

const CIRCLE_CLASS = 'heatmap-circle';

const HIDDEN_CIRCLE_STYLE = {
  display: 'none',
  width: '0px',
  height: '0px',
  backgroundColor: 'transparent',
};

@Component({
  selector: 'explorers-heatmap-circle',
  imports: [TooltipModule],
  templateUrl: './heatmap-circle.component.html',
  styleUrls: ['./heatmap-circle.component.scss'],
})
export class HeatmapCircleComponent<T extends HeatmapCircleData = HeatmapCircleData> {
  helperService = inject(HelperService);
  comparisonToolFilterService = inject(ComparisonToolFilterService);

  data = input<T>();
  getCircleTooltip = input<(data: T | null | undefined) => string>((data) => {
    return this.getDefaultTooltip(data);
  });

  significanceThresholdActive = this.comparisonToolFilterService.significanceThresholdActive;
  significanceThreshold = this.comparisonToolFilterService.significanceThreshold;

  private metrics = computed(() => resolveHeatmapCircleMetrics(this.data()));

  circleClass = computed(() => {
    const metrics = this.metrics();
    return metrics.isDrawable
      ? `${CIRCLE_CLASS} ${this.getCircleValueSign(metrics.colorValue)}`
      : CIRCLE_CLASS;
  });

  circleStyle = computed(() => {
    const metrics = this.metrics();
    if (!metrics.isDrawable) {
      return HIDDEN_CIRCLE_STYLE;
    }

    const size = this.getCircleSize(metrics.adjustedPValue);
    const color = this.getCircleColor(metrics.colorValue);
    return {
      display: size > 0 ? 'block' : 'none',
      width: size + 'px',
      height: size + 'px',
      backgroundColor: color,
    };
  });

  private getDefaultTooltip(data: T | null | undefined): string {
    const metrics = resolveHeatmapCircleMetrics(data);
    if (!metrics.isDrawable) {
      return 'No data available';
    }

    const { colorKey, colorValue, adjustedPValue } = metrics;
    const displayName = knownColorMetricToDisplayName.find(
      (item) => item.field === colorKey,
    )?.displayName;

    return (
      `${displayName || colorKey}: ` +
      this.formatNumericValue(colorValue) +
      '\n' +
      `${ADJUSTED_P_VALUE_LABEL}: ` +
      this.formatNumericValue(adjustedPValue)
    );
  }

  private formatNumericValue(val: number) {
    return this.helperService.getSignificantFigures(val, 3);
  }

  nRoot(x: number, n: number) {
    try {
      const negate = n % 2 === 1 && x < 0;
      if (negate) {
        x = -x;
      }
      const possible = Math.pow(x, 1 / n);
      n = Math.pow(possible, n);
      if (Math.abs(x - n) < 1 && x > 0 === n > 0) {
        return negate ? -possible : possible;
      }
      return;
    } catch {
      return;
    }
  }

  private getCircleValueSign(colorValue: number): CircleValueSign {
    if (colorValue === 0) return 'zero';
    return colorValue > 0 ? 'plus' : 'minus';
  }

  private getCircleColor(colorValue: number) {
    const sign = this.getCircleValueSign(colorValue);
    if (sign === 'zero') return 'var(--color-gray-400)';

    const rounded = this.helperService.getSignificantFigures(colorValue, 3);
    if (sign === 'plus') {
      if (rounded < 0.1) {
        return '#B5CBEF';
      } else if (rounded < 0.2) {
        return '#84A5DB';
      } else if (rounded < 0.3) {
        return '#5E84C3';
      } else if (rounded < 0.4) {
        return '#3E68AA';
      } else {
        return '#245299';
      }
    } else {
      if (rounded > -0.1) {
        return '#FBB8C5';
      } else if (rounded > -0.2) {
        return '#F78BA0';
      } else if (rounded > -0.3) {
        return '#F16681';
      } else if (rounded > -0.4) {
        return '#EC4769';
      } else {
        return '#D72247';
      }
    }
  }

  private getCircleSize(adjustedPValue: number) {
    // define min and max size of possible circles in pixels
    const MIN_SIZE = 6;
    const MAX_SIZE = 50;

    // if significance cutoff radio button selected and
    // adjustedPValue > significance threshhold, don't show
    if (this.significanceThresholdActive() && adjustedPValue > this.significanceThreshold()) {
      return 0;
    }

    const pValue = 1 - (this.nRoot(adjustedPValue, 3) || 0);
    const size = Math.round(pValue * MAX_SIZE);

    // ensure the smallest circles have a min size to be easily hoverable/clickable
    return size < MIN_SIZE ? MIN_SIZE : size;
  }
}
