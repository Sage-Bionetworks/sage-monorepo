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
  private readonly baseUrl = 'https://openrouter.ai/api/v1';

  explainVisualization(
    imageUrl: string,
    template: string,
    apiKey: string,
    modelId: string,
  ): Observable<string> {
    if (!apiKey) {
      throw new Error('OpenRouter API key not configured');
    }

    const payload = {
      model: modelId,
      messages: [
        {
          role: 'system',
          content: template,
        },
        {
          role: 'user',
          content: [
            {
              type: 'image_url',
              image_url: { url: imageUrl },
            },
          ],
        },
      ],
    };

    return this.http
      .post<any>(`${this.baseUrl}/chat/completions`, payload, {
        headers: {
          Authorization: `Bearer ${apiKey}`,
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
