import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'explorers-comparison-tool-table-link',
  imports: [RouterModule],
  templateUrl: './comparison-tool-table-link.component.html',
  styleUrls: ['./comparison-tool-table-link.component.scss'],
})
export class ComparisonToolTableLinkComponent {
  linkText = input<string>();
  linkUrl = input<string>();
}
