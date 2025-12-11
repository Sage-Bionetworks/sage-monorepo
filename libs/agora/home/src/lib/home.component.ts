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

  testOpenRouterService(): void {
    console.log('🧪 Testing OpenRouter API Service...');

    // Simple 1x1 red pixel PNG (base64)
    const testImageBase64 =
      'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg==';
    const imageUrl = `data:image/png;base64,${testImageBase64}`;

    const template =
      'You are an AI assistant that analyzes visualizations. Describe what you see in the image.';
    const modelId = 'google/gemini-3-pro-preview';

    console.log('📋 Template:', template);
    console.log('📋 Model ID:', modelId);
    console.log('📋 Image URL:', imageUrl.substring(0, 50) + '...');

    this.openRouterService
      .explainVisualization(imageUrl, template, this.OPENROUTER_API_KEY, modelId)
      .subscribe({
        next: (response) => {
          console.log('✅ SUCCESS! OpenRouter API Response received:', response);
        },
        error: (error) => {
          console.error('❌ ERROR! OpenRouter API call failed:', error);
        },
      });
  }
}
