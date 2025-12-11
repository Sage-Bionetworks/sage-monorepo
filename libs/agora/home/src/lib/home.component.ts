import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { OpenRouterApiService } from '@sagebionetworks/agora/services';
import { SearchInputComponent } from '@sagebionetworks/agora/ui';
import { HomeCardComponent, SvgImageComponent } from '@sagebionetworks/explorers/ui';

@Component({
  selector: 'agora-home',
  imports: [SvgImageComponent, RouterLink, HomeCardComponent, SearchInputComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  ROUTE_PATHS = ROUTE_PATHS;

  private readonly openRouterService = inject(OpenRouterApiService);

  // TODO: Replace with your actual OpenRouter API key from .env
  private readonly OPENROUTER_API_KEY = 'changeme';

  streamingResponse = '';
  isStreaming = false;

  testOpenRouterService(): void {
    console.log('🧪 Testing OpenRouter API Service with Streaming...');

    // Use a complex chart/visualization image URL for testing
    // Example: A chart from a public source
    const imageUrl = 'https://matplotlib.org/stable/_images/sphx_glr_scatter_001.png';

    const template =
      'You are an AI assistant that analyzes scientific visualizations and data charts. Provide a detailed description of the visualization including: the type of chart, axes labels, data patterns, trends, and any notable features.';
    const modelId = 'openai/gpt-4o';

    console.log('📋 Template:', template);
    console.log('📋 Model ID:', modelId);
    console.log('📋 Image URL:', imageUrl);

    this.streamingResponse = '';
    this.isStreaming = true;

    this.openRouterService
      .explainVisualizationStream(imageUrl, template, this.OPENROUTER_API_KEY, modelId)
      .subscribe({
        next: (textDelta) => {
          this.streamingResponse += textDelta;
          console.log('📝 Received delta:', textDelta);
        },
        error: (error) => {
          console.error('❌ ERROR! OpenRouter API call failed:', error);
          this.isStreaming = false;
        },
        complete: () => {
          console.log('✅ SUCCESS! Streaming completed');
          this.isStreaming = false;
        },
      });
  }
}
