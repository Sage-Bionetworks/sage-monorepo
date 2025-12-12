import {
  AfterViewChecked,
  Component,
  ElementRef,
  EventEmitter,
  inject,
  Input,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { DrawerModule } from 'primeng/drawer';
import { marked } from 'marked';

@Component({
  selector: 'agora-ai-insights-panel',
  imports: [CommonModule, DrawerModule],
  templateUrl: './ai-insights-panel.component.html',
  styleUrls: ['./ai-insights-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class AiInsightsPanelComponent implements AfterViewChecked {
  private sanitizer = inject(DomSanitizer);

  @ViewChild('drawerContent') drawerContentRef: ElementRef<HTMLDivElement> | undefined;

  @Input() visible = false;
  @Output() visibleChange = new EventEmitter<boolean>();

  @Input() isCapturing = false;
  @Input() isStreaming = false;
  @Input() isComplete = false;
  @Input() streamingResponse = '';
  @Input() showCloseIcon = true;
  @Input() dismissible = true;
  @Input() closeOnEscape = true;
  @Input() width = '1000px';

  private lastResponseLength = 0;
  private wasStreaming = false;

  constructor() {
    // Configure marked options
    marked.setOptions({
      breaks: true, // Convert line breaks to <br>
      gfm: true, // GitHub Flavored Markdown
    });
  }

  ngAfterViewChecked() {
    // Auto-scroll to bottom while streaming
    if (this.isStreaming && this.streamingResponse.length > this.lastResponseLength) {
      this.scrollToBottom();
      this.lastResponseLength = this.streamingResponse.length;
    }

    // Final scroll when streaming completes to show "Processing complete" message
    if (this.wasStreaming && !this.isStreaming) {
      // Use setTimeout to allow the DOM to update with the completion message
      setTimeout(() => this.scrollToBottom(), 0);
    }

    // Track streaming state for next check
    this.wasStreaming = this.isStreaming;

    // Reset tracking when streaming stops
    if (!this.isStreaming) {
      this.lastResponseLength = 0;
    }
  }

  private scrollToBottom() {
    if (this.drawerContentRef?.nativeElement) {
      const element = this.drawerContentRef.nativeElement;
      element.scrollTop = element.scrollHeight;
    }
  }

  get renderedResponse(): SafeHtml {
    if (!this.streamingResponse) {
      return '';
    }
    const html = marked.parse(this.streamingResponse) as string;
    return this.sanitizer.sanitize(1, html) || '';
  }

  onVisibleChange(value: boolean) {
    this.visible = value;
    this.visibleChange.emit(value);
  }
}
