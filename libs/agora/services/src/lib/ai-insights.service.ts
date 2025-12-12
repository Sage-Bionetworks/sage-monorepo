import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { OpenRouterApiService } from './openrouter-api.service';

export interface AIInsightsState {
  visible: boolean;
  isCapturing: boolean;
  isStreaming: boolean;
  isComplete: boolean;
  streamingResponse: string;
}

@Injectable({
  providedIn: 'root',
})
export class AiInsightsService {
  private readonly openRouterService = inject(OpenRouterApiService);

  private stateSubject = new BehaviorSubject<AIInsightsState>({
    visible: false,
    isCapturing: false,
    isStreaming: false,
    isComplete: false,
    streamingResponse: '',
  });

  private currentSubscription: Subscription | null = null;

  state$: Observable<AIInsightsState> = this.stateSubject.asObservable();

  get state(): AIInsightsState {
    return this.stateSubject.value;
  }

  /**
   * Start the capture process - opens panel and sets capturing state
   */
  startCapture() {
    this.setComplete(false);
    this.openPanel();
    this.setCapturing(true);
  }

  /**
   * Analyze an image with the given prompt using OpenRouter API
   * Manages all streaming state internally
   */
  analyzeImage(
    imageDataUrl: string,
    prompt: string,
    apiKey: string,
    modelId = 'anthropic/claude-sonnet-4.5',
  ): Observable<void> {
    return new Observable((observer) => {
      this.setCapturing(false);
      this.setStreaming(true, '');

      this.currentSubscription = this.openRouterService
        .explainVisualizationStream(imageDataUrl, prompt, apiKey, modelId)
        .subscribe({
          next: (textDelta) => {
            const currentResponse = this.state.streamingResponse;
            this.updateStreamingResponse(currentResponse + textDelta);
          },
          error: (error) => {
            console.error('OpenRouter API call failed:', error);
            this.setStreaming(false, '');
            observer.error(error);
          },
          complete: () => {
            this.setStreaming(false, this.state.streamingResponse);
            this.setComplete(true);
            observer.next();
            observer.complete();
          },
        });

      return () => {
        this.currentSubscription?.unsubscribe();
      };
    });
  }

  openPanel() {
    this.stateSubject.next({
      ...this.state,
      visible: true,
    });
  }

  closePanel() {
    this.stateSubject.next({
      ...this.state,
      visible: false,
    });
  }

  setCapturing(isCapturing: boolean) {
    this.stateSubject.next({
      ...this.state,
      isCapturing,
    });
  }

  setStreaming(isStreaming: boolean, streamingResponse = '') {
    this.stateSubject.next({
      ...this.state,
      isStreaming,
      streamingResponse,
    });
  }

  updateStreamingResponse(response: string) {
    this.stateSubject.next({
      ...this.state,
      streamingResponse: response,
    });
  }

  reset() {
    this.currentSubscription?.unsubscribe();
    this.stateSubject.next({
      visible: false,
      isCapturing: false,
      isStreaming: false,
      isComplete: false,
      streamingResponse: '',
    });
  }

  setComplete(isComplete: boolean) {
    this.stateSubject.next({
      ...this.state,
      isComplete,
    });
  }
}
