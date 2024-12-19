import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ScoreBarChartComponent } from '@sagebionetworks/agora/charts';
import { WikiComponent } from '@sagebionetworks/agora/shared';

@Component({
  selector: 'agora-about',
  standalone: true,
  imports: [CommonModule, WikiComponent, ScoreBarChartComponent],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.scss'],
})
export class AboutComponent {
  wikiId = '612058';
  className = 'about-page-content';

  scoreDistribution = {
    distribution: [766, 4804, 4198, 4001, 3172, 2880, 3097, 1562, 323, 19],
    bins: [
      [0, 0.5],
      [0.5, 1],
      [1, 1.5],
      [1.5, 2],
      [2, 2.5],
      [2.5, 3],
      [3, 3.5],
      [3.5, 4],
      [4, 4.5],
      [4.5, 5],
    ],
    min: 0,
    max: 4.7438,
    mean: 1.9484,
    first_quartile: 1,
    third_quartile: 3,
    name: 'Target Risk Score',
    syn_id: 'syn25913473',
    wiki_id: '621071',
  };
}
