import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SplitButtonModule } from 'primeng/splitbutton';
import { TooltipModule } from 'primeng/tooltip';
import { MessageService } from 'primeng/api';
import * as htmlToImage from 'html-to-image';

export type AudienceType = 'simple' | 'researcher';

export interface CaptureImageEvent {
  imageDataUrl: string;
  audienceType: AudienceType;
}

@Component({
  selector: 'agora-ai-assistant-button',
  imports: [CommonModule, SplitButtonModule, TooltipModule],
  templateUrl: './ai-assistant-button.component.html',
  styleUrls: ['./ai-assistant-button.component.scss'],
})
export class AiAssistantButtonComponent {
  private messageService = inject(MessageService);

  @Input() targetElement!: HTMLElement;
  @Input() label = 'AI Insights';
  @Input() disabled = false;
  @Input() rounded = true;
  @Input() outlined = true;
  @Input() size: 'small' | 'large' | undefined = 'small';

  @Output() captureStarted = new EventEmitter<AudienceType>();
  @Output() captureComplete = new EventEmitter<CaptureImageEvent>();
  @Output() captureError = new EventEmitter<Error>();

  isCapturing = false;

  aiInsightsMenuItems = [
    {
      label: 'Simple Explanation',
      command: () => this.captureImage('simple'),
    },
    {
      label: 'Researcher Insights',
      command: () => this.captureImage('researcher'),
    },
  ];

  captureImage(audienceType: AudienceType = 'simple') {
    if (!this.targetElement || this.isCapturing) {
      return;
    }

    this.isCapturing = true;

    // Emit immediately to open panel and show loading state
    this.captureStarted.emit(audienceType);

    // Use setTimeout to allow the UI to render before starting CPU-intensive capture
    setTimeout(async () => {
      try {
        const dataUrl = await htmlToImage.toPng(this.targetElement, {
          backgroundColor: '#ffffff',
          quality: 1.0,
          pixelRatio: 2,
        });

        this.captureComplete.emit({
          imageDataUrl: dataUrl,
          audienceType,
        });
      } catch (error) {
        console.error('Error capturing image:', error);
        this.captureError.emit(error as Error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to capture image for AI analysis.',
        });
      } finally {
        this.isCapturing = false;
      }
    }, 100);
  }
}
