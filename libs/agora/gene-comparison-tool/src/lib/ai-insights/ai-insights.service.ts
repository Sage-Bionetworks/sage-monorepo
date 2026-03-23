import { Injectable, inject } from '@angular/core';
import { Observable, forkJoin } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GeneService } from '@sagebionetworks/agora/api-client';
import { AiInsightsMessage, AiInsightsStreamEvent, AiModel, AI_MODELS } from './ai-insights.models';
import { buildGeneContext, buildMessages } from './ai-insights.prompts';

const OPENROUTER_URL = 'https://openrouter.ai/api/v1/chat/completions';

@Injectable({ providedIn: 'root' })
export class AiInsightsService {
  private readonly geneService = inject(GeneService);
  private abortController: AbortController | null = null;

  streamGeneInsights(
    geneIds: string[],
    userMessage: string,
    model: AiModel,
    conversationHistory?: AiInsightsMessage[],
  ): Observable<AiInsightsStreamEvent> {
    const geneRequests = geneIds.map((id) => this.geneService.getGene(id));

    return forkJoin(geneRequests).pipe(
      switchMap((genes) => {
        const geneContext = buildGeneContext(genes);
        const messages = buildMessages(geneContext, userMessage, conversationHistory);
        return this.streamFromOpenRouter(messages, model);
      }),
    );
  }

  cancelStream(): void {
    this.abortController?.abort();
    this.abortController = null;
  }

  getAvailableModels() {
    return AI_MODELS;
  }

  private streamFromOpenRouter(
    messages: AiInsightsMessage[],
    model: AiModel,
  ): Observable<AiInsightsStreamEvent> {
    return new Observable<AiInsightsStreamEvent>((subscriber) => {
      const abortController = new AbortController();
      this.abortController = abortController;

      // TODO: replace with a valid OpenRouter API key
      const apiKey = '';

      fetch(OPENROUTER_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${apiKey}`,
          'HTTP-Referer': globalThis.location?.origin || 'https://agora.adknowledgeportal.org',
        },
        body: JSON.stringify({
          model,
          messages: messages.map((m) => ({ role: m.role, content: m.content })),
          stream: true,
        }),
        signal: abortController.signal,
      })
        .then(async (response) => {
          if (!response.ok) {
            const errorText = await response.text();
            subscriber.next({
              type: 'error',
              error: `OpenRouter API error (${response.status}): ${errorText}`,
            });
            subscriber.complete();
            return;
          }

          const reader = response.body?.getReader();
          if (!reader) {
            subscriber.next({ type: 'error', error: 'No response body' });
            subscriber.complete();
            return;
          }

          const decoder = new TextDecoder();
          let fullContent = '';
          let buffer = '';

          let readResult = await reader.read();
          while (!readResult.done) {
            const value = readResult.value;

            buffer += decoder.decode(value, { stream: true });
            const lines = buffer.split('\n');
            buffer = lines.pop() || '';

            for (const line of lines) {
              const trimmed = line.trim();
              if (!trimmed || !trimmed.startsWith('data: ')) continue;

              const data = trimmed.slice(6);
              if (data === '[DONE]') continue;

              try {
                const parsed = JSON.parse(data);
                const delta = parsed.choices?.[0]?.delta?.content;
                if (delta) {
                  fullContent += delta;
                  subscriber.next({
                    type: 'content',
                    content: delta,
                    fullContent,
                  });
                }
              } catch {
                // skip malformed SSE chunks
              }
            }
            readResult = await reader.read();
          }

          subscriber.next({ type: 'done', fullContent });
          subscriber.complete();
        })
        .catch((err) => {
          if (err.name === 'AbortError') {
            subscriber.complete();
          } else {
            subscriber.next({ type: 'error', error: err.message });
            subscriber.complete();
          }
        });

      return () => {
        abortController.abort();
      };
    });
  }
}
