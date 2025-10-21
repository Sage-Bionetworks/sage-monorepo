import { Component, computed, inject, input } from '@angular/core';
import { HeatmapCircleColorKey, HeatmapCircleData } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService, HelperService } from '@sagebionetworks/explorers/services';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-heatmap-circle',
  imports: [TooltipModule],
  templateUrl: './heatmap-circle.component.html',
  styleUrls: ['./heatmap-circle.component.scss'],
})
export class HeatmapCircleComponent<T extends HeatmapCircleData = HeatmapCircleData> {
  helperService = inject(HelperService);
  comparisonToolFilterService = inject(ComparisonToolFilterService);

  // First value drives color: log2_fc, correlation
  // Second value is adj_p_val
  data = input<T>();
  getCircleTooltip = input<(data: T | null | undefined) => string>((data) => {
    if (!data) {
      return 'No data available';
    }

    const { value, key } = this.resolveColorMetric(data);
    const knownColorMetricToDisplayName = [
      { field: 'log2_fc', displayName: 'L2FC' },
      { field: 'correlation', displayName: 'Correlation' },
    ];
    const displayName = knownColorMetricToDisplayName.find(
      (item) => item.field === key,
    )?.displayName;

    return (
      `${displayName || key}: ` +
      this.formatNumericValue(value) +
      '\n' +
      'P-value: ' +
      this.formatNumericValue(data.adj_p_val)
    );
  });

  significanceThresholdActive = this.comparisonToolFilterService.significanceThresholdActive;
  significanceThreshold = this.comparisonToolFilterService.significanceThreshold;

  colorValue = computed(() => {
    const data = this.data();
    const { value } = this.resolveColorMetric(data);
    return value;
  });
  adjustedPValue = computed(() => {
    const data = this.data();
    return data?.adj_p_val ?? null;
  });

  circleClass = computed(() => {
    const colorValue = this.colorValue();
    return this.getCircleClass(colorValue);
  });
  circleStyle = computed(() => {
    const adjustedPValue = this.adjustedPValue();
    const colorValue = this.colorValue();
    return this.getCircleStyle(adjustedPValue, colorValue);
  });

  private resolveColorMetric(data: T | null | undefined): {
    key: string | null;
    value: number | null;
  } {
    if (!data) {
      return { key: null, value: null };
    }

    const colorKey = (Object.keys(data) as Array<keyof T>).find(
      (field): field is HeatmapCircleColorKey<T> => field !== 'adj_p_val',
    );

    if (!colorKey) {
      return { key: null, value: null };
    }

    const result = data[colorKey];

    return {
      key: colorKey,
      value: result ?? null,
    };
  }

  private formatNumericValue(val: number | null | undefined) {
    return val === null || val === undefined
      ? 'N/A'
      : this.helperService.getSignificantFigures(val, 3);
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
    } catch (e) {
      return;
    }
  }

  getCircleColor(colorValue: number | undefined | null) {
    if (colorValue === undefined || colorValue === null) return '#F0F0F0';

    const rounded = this.helperService.getSignificantFigures(colorValue, 3);
    if (rounded > 0) {
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

  getCircleSize(adjustedPValue: number | null | undefined) {
    // define min and max size of possible circles in pixels
    const MIN_SIZE = 6;
    const MAX_SIZE = 50;

    // sizeValue shouldn't be undefined but if it is, don't show a circle
    // null means there is no data in which case, also don't show a circle
    if (adjustedPValue === null || adjustedPValue === undefined) return 0;

    // if significance cutoff radio button selected and
    // sizeValue > significance threshhold, don't show
    if (this.significanceThresholdActive() && adjustedPValue > this.significanceThreshold()) {
      return 0;
    }

    const pValue = 1 - (this.nRoot(adjustedPValue, 3) || 0);
    const size = Math.round(pValue * MAX_SIZE);

    // ensure the smallest circles have a min size to be easily hoverable/clickable
    return size < MIN_SIZE ? MIN_SIZE : size;
  }

  getCircleStyle(adjustedPValue: number | null | undefined, colorValue: number | null | undefined) {
    const size = this.getCircleSize(adjustedPValue);
    const color = this.getCircleColor(colorValue);

    return {
      display: size > 0 ? 'block' : 'none',
      width: size + 'px',
      height: size + 'px',
      backgroundColor: color,
    };
  }

  getCircleClass(colorValue: number | null | undefined) {
    const baseClass = 'heatmap-circle';
    if (colorValue === null || colorValue === undefined) {
      return baseClass;
    }
    return `${baseClass} ${colorValue >= 0 ? 'plus' : 'minus'}`;
  }
}
