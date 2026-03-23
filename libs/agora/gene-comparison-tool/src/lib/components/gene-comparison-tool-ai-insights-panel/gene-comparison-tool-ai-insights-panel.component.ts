import { Component, ElementRef, Input, ViewChild, ViewEncapsulation, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { MarkdownModule } from 'ngx-markdown';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { TooltipModule } from 'primeng/tooltip';
import { GCTGene } from '@sagebionetworks/agora/api-client';
import { AiInsightsService, AiInsightsMessage, AiModel, AI_MODELS } from '../../ai-insights';

@Component({
  selector: 'agora-gene-comparison-tool-ai-insights-panel',
  imports: [CommonModule, FormsModule, MarkdownModule, ButtonModule, SelectModule, TooltipModule],
  templateUrl: './gene-comparison-tool-ai-insights-panel.component.html',
  styleUrls: ['./gene-comparison-tool-ai-insights-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolAiInsightsPanelComponent {
  @Input() pinnedGenes: GCTGene[] = [];
  @Input() category = '';
  @Input() subCategory = '';

  @ViewChild('messagesContainer') messagesContainer!: ElementRef;

  private readonly aiInsightsService = inject(AiInsightsService);
  private streamSubscription: Subscription | null = null;

  isOpen = false;
  messages: AiInsightsMessage[] = [];
  streamingContent = '';
  isStreaming = false;
  selectedModel: AiModel = 'anthropic/claude-sonnet-4-6';
  modelOptions = AI_MODELS;
  userInput = '';
  errorMessage = '';

  open() {
    this.isOpen = true;
  }

  close() {
    this.isOpen = false;
  }

  toggle() {
    if (this.isOpen) {
      this.close();
    } else {
      this.open();
    }
  }

  analyze() {
    if (this.isStreaming || this.pinnedGenes.length === 0) return;

    const geneIds = this.pinnedGenes.map((g) => g.ensembl_gene_id);
    const userMessage =
      'Analyze these pinned genes. Highlight commonalities, key differences, and potential research directions.';

    this.messages.push({ role: 'user', content: userMessage });
    this.startStream(geneIds, userMessage);
  }

  sendFollowUp() {
    const input = this.userInput.trim();
    if (!input || this.isStreaming) return;

    this.messages.push({ role: 'user', content: input });
    this.userInput = '';

    const geneIds = this.pinnedGenes.map((g) => g.ensembl_gene_id);
    this.startStream(geneIds, input, this.messages.slice(0, -1));
  }

  stopStreaming() {
    this.aiInsightsService.cancelStream();
    this.streamSubscription?.unsubscribe();
    this.streamSubscription = null;

    if (this.streamingContent) {
      this.messages.push({
        role: 'assistant',
        content: this.streamingContent,
      });
      this.streamingContent = '';
    }
    this.isStreaming = false;
  }

  clearConversation() {
    this.messages = [];
    this.streamingContent = '';
    this.errorMessage = '';
  }

  private startStream(
    geneIds: string[],
    userMessage: string,
    conversationHistory?: AiInsightsMessage[],
  ) {
    this.isStreaming = true;
    this.streamingContent = '';
    this.errorMessage = '';

    this.streamSubscription = this.aiInsightsService
      .streamGeneInsights(geneIds, userMessage, this.selectedModel, conversationHistory)
      .subscribe({
        next: (event) => {
          switch (event.type) {
            case 'content':
              this.streamingContent = event.fullContent || '';
              this.scrollToBottom();
              break;
            case 'done':
              this.messages.push({
                role: 'assistant',
                content: event.fullContent || '',
              });
              this.streamingContent = '';
              this.isStreaming = false;
              this.scrollToBottom();
              break;
            case 'error':
              this.errorMessage = event.error || 'An error occurred';
              this.isStreaming = false;
              break;
          }
        },
        error: () => {
          this.errorMessage = 'Failed to connect to AI service';
          this.isStreaming = false;
        },
        complete: () => {
          this.isStreaming = false;
        },
      });
  }

  private scrollToBottom() {
    setTimeout(() => {
      const el = this.messagesContainer?.nativeElement;
      if (el) {
        el.scrollTop = el.scrollHeight;
      }
    });
  }
}
