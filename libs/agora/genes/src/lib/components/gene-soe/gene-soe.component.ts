import { Component, inject, Input } from '@angular/core';
import { Gene } from '@sagebionetworks/agora/api-client';
import { ConfigService } from '@sagebionetworks/agora/config';
import { AiInsightsService } from '@sagebionetworks/agora/services';
import {
  AiAssistantButtonComponent,
  AudienceType,
  CaptureImageEvent,
} from '@sagebionetworks/agora/ui';
import { MessageService } from 'primeng/api';
import { GeneBioDomainsComponent } from '../gene-biodomains/gene-biodomains.component';
import { GeneSoeChartsComponent } from '../gene-soe-charts/gene-soe-charts.component';
import { GeneSoeListComponent } from '../gene-soe-list/gene-soe-list.component';

@Component({
  selector: 'agora-gene-soe',
  imports: [
    GeneBioDomainsComponent,
    GeneSoeChartsComponent,
    GeneSoeListComponent,
    AiAssistantButtonComponent,
  ],
  templateUrl: './gene-soe.component.html',
  styleUrls: ['./gene-soe.component.scss'],
})
export class GeneSoeComponent {
  private configService = inject(ConfigService);
  private aiInsightsService = inject(AiInsightsService);
  private messageService = inject(MessageService);

  @Input() gene: Gene | undefined;

  onCaptureStarted(_audienceType: AudienceType) {
    this.aiInsightsService.startCapture();
  }

  onCaptureComplete(event: CaptureImageEvent) {
    const prompt = this.getPrompt(event.audienceType);
    const apiKey = this.configService.config.openRouterApiKey;
    this.aiInsightsService.analyzeImage(event.imageDataUrl, prompt, apiKey).subscribe({
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'AI analysis failed.',
        });
      },
    });
  }

  onCaptureError(_error: Error) {
    this.aiInsightsService.setCapturing(false);
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Failed to capture image for AI analysis.',
    });
  }

  private getPrompt(audienceType: AudienceType): string {
    const geneSymbol = this.gene?.hgnc_symbol || this.gene?.ensembl_gene_id || 'this gene';

    const prompts: Record<AudienceType, string> = {
      simple: `You are an AI assistant that analyzes scientific visualizations about Alzheimer's disease risk scores. \
Explain this visualization showing AD risk scores for ${geneSymbol} in simple terms for a non-researcher. \
Use analogies, avoid jargon, and focus on what it means in plain language. \
We are on the Gene Summary page in the Agora web application.`,
      researcher: `You are an AI assistant that analyzes scientific visualizations about Alzheimer's disease risk scores. \
Provide detailed scientific insights from this visualization showing AD risk scores for ${geneSymbol}, including: \
percentile rankings, comparative analysis with genome-wide distributions, score components (Genetic Risk Score, Multi-omic Risk Score), \
evidence strength, and research prioritization recommendations. \
We are on the Gene Summary page in the Agora web application.`,
    };

    return prompts[audienceType];
  }
}
