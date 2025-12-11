// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

// -------------------------------------------------------------------------- //
// Service
// -------------------------------------------------------------------------- //
@Injectable({
  providedIn: 'root',
})
export class OpenRouterApiService {
  private readonly http = inject(HttpClient);
  private readonly apiKey: string;
  private readonly baseUrl = 'https://openrouter.ai/api/v1';

  constructor() {
    this.apiKey = process.env['OPENROUTER_API_KEY'] || '';
    if (!this.apiKey) {
      console.error('OPENROUTER_API_KEY not found in environment');
    }
  }

  explainVisualization(imageBase64: string, context?: any): Observable<string> {
    if (!this.apiKey) {
      throw new Error('OpenRouter API key not configured');
    }

    const userPrompt = context
      ? `Explain this visualization. Context: ${JSON.stringify(context)}`
      : 'Explain what this visualization shows.';

    const payload = {
      model: 'google/gemini-3-pro-preview',
      messages: [
        {
          role: 'user',
          content: [
            { type: 'text', text: userPrompt },
            {
              type: 'image_url',
              image_url: { url: `data:image/png;base64,${imageBase64}` },
            },
          ],
        },
      ],
    };

    return this.http
      .post<any>(`${this.baseUrl}/chat/completions`, payload, {
        headers: {
          Authorization: `Bearer ${this.apiKey}`,
          'Content-Type': 'application/json',
        },
      })
      .pipe(
        map((response) => response.choices[0].message.content),
        catchError((error) => {
          console.error('OpenRouter API error:', error);
          throw error;
        }),
      );
  }
}
