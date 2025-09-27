import {
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { OverallScoresDistribution } from '@sagebionetworks/agora/api-client';
import { ScoreBarChartComponent } from '@sagebionetworks/agora/charts';
import { GCTScorePanelData } from '@sagebionetworks/agora/models';
import { WikiComponent } from '@sagebionetworks/agora/shared';
import { Popover, PopoverModule } from 'primeng/popover';
import * as helpers from '../../gene-comparison-tool.helpers';

@Component({
  selector: 'agora-gene-comparison-tool-score-panel',
  imports: [PopoverModule, WikiComponent, ScoreBarChartComponent],
  templateUrl: './gene-comparison-tool-score-panel.component.html',
  styleUrls: ['./gene-comparison-tool-score-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolScorePanelComponent {
  event: any = null;
  dataIndex = 1;

  @Input() data: GCTScorePanelData | undefined;

  barColor = '#8B8AD1';

  @Output() navigateToMethodologyEvent: EventEmitter<object> = new EventEmitter<object>();
  @Output() navigateToFeedbackEvent: EventEmitter<object> = new EventEmitter<object>();

  @ViewChild('popover') popover!: Popover;

  scoreDistribution: OverallScoresDistribution | undefined;

  getValuePosition(data: any) {
    const percentage = Math.round(((data.value - data.min) / (data.max - data.min)) * 100);
    return { left: percentage + '%' };
  }

  getIntervalPositions(data: any) {
    const minPercentage = Math.round(((data.intervalMin - data.min) / (data.max - data.min)) * 100);

    const maxPercentage =
      100 - Math.round(((data.intervalMax - data.min) / (data.max - data.min)) * 100);

    return { left: minPercentage + '%', right: maxPercentage + '%' };
  }

  show(event: Event, data?: GCTScorePanelData) {
    this.event = event;

    if (data && data.distributions) {
      this.data = data;
      const dataKey = helpers.lookupScoreDataKey(data.columnName);
      this.scoreDistribution = data.distributions.find(
        (scores) => scores.name.toUpperCase() === dataKey,
      );
      this.popover.toggle(event);
    }
  }

  hide() {
    this.popover.hide();
  }

  toggle(event: Event, data?: GCTScorePanelData) {
    if (event.target === this.event?.target && this.popover.overlayVisible) {
      this.hide();
    } else {
      this.show(event, data);
    }
  }
}
