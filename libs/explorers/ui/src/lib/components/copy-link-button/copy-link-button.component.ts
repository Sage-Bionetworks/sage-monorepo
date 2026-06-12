import { Component, input } from '@angular/core';
import { TooltipButtonComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-copy-link-button',
  imports: [TooltipButtonComponent],
  templateUrl: './copy-link-button.component.html',
  styleUrls: ['./copy-link-button.component.scss'],
})
export class CopyLinkButtonComponent {
  onClick = input.required<() => void>();
  anchorId = input.required<string>();
  ariaLabel = input.required<string>();
  tooltipText = input.required<string>();
}
