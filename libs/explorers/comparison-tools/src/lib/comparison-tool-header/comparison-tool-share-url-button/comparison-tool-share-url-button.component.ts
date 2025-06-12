import { Component, inject, input } from '@angular/core';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'explorers-comparison-tool-share-url-button',
  imports: [ButtonModule, TooltipModule, ToastModule],
  templateUrl: './comparison-tool-share-url-button.component.html',
  styleUrls: ['./comparison-tool-share-url-button.component.scss'],
})
export class ComparisonToolShareURLButtonComponent {
  messageService = inject(MessageService);

  tooltip = input('');

  toastDuration = 5000;

  copyUrl() {
    navigator.clipboard.writeText(window.location.href);
    this.messageService.clear();
    this.messageService.add({
      key: 'share-url-button-toast',
      severity: 'info',
      summary: 'URL Copied',
      detail:
        'URL copied to clipboard! Use this URL to share or bookmark the current table configuration.',
    });
  }
}
