import { Component, EventEmitter, Input, Output, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DrawerModule } from 'primeng/drawer';

@Component({
  selector: 'agora-ai-insights-panel',
  imports: [CommonModule, DrawerModule],
  templateUrl: './ai-insights-panel.component.html',
  styleUrls: ['./ai-insights-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class AiInsightsPanelComponent {
  @Input() visible = false;
  @Output() visibleChange = new EventEmitter<boolean>();

  @Input() message = '';
  @Input() isCapturing = false;
  @Input() isStreaming = false;
  @Input() streamingResponse = '';
  @Input() showCloseIcon = true;
  @Input() dismissible = true;
  @Input() closeOnEscape = true;

  onVisibleChange(value: boolean) {
    this.visible = value;
    this.visibleChange.emit(value);
  }
}
