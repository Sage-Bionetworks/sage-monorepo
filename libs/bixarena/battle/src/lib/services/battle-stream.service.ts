import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BASE_PATH, ModelChatCompletionChunk } from '@sagebionetworks/bixarena/api-client';

@Injectable()
export class BattleStreamService {
  private readonly basePath = inject(BASE_PATH);

  streamCompletion(
    battleId: string,
    roundId: string,
    modelId: string,
  ): Observable<ModelChatCompletionChunk> {
    return new Observable((subscriber) => {
      const controller = new AbortController();
      const url = `${this.basePath}/battles/${battleId}/rounds/${roundId}/stream?modelId=${modelId}`;

      fetch(url, {
        method: 'POST',
        credentials: 'include',
        headers: { Accept: 'text/event-stream' },
        signal: controller.signal,
      })
        .then(async (response) => {
          if (!response.ok) {
            subscriber.error(new Error(`Stream request failed: ${response.status}`));
            return;
          }

          if (!response.body) {
            subscriber.error(new Error('Response body is null'));
            return;
          }
          const reader = response.body.getReader();
          const decoder = new TextDecoder();
          let buffer = '';

          try {
            // eslint-disable-next-line no-constant-condition
            while (true) {
              const { done, value } = await reader.read();
              if (done) break;

              buffer += decoder.decode(value, { stream: true });

              const events = buffer.split('\n\n');
              buffer = events.pop() ?? '';

              for (const event of events) {
                for (const line of event.split('\n')) {
                  if (line.startsWith('data: ')) {
                    const chunk = JSON.parse(line.slice(6)) as ModelChatCompletionChunk;
                    subscriber.next(chunk);
                  }
                }
              }
            }
          } finally {
            reader.cancel();
          }

          subscriber.complete();
        })
        .catch((err) => {
          if (err.name !== 'AbortError') {
            subscriber.error(err);
          }
        });

      return () => controller.abort();
    });
  }
}
