import { TestBed } from '@angular/core/testing';
import { BASE_PATH, ModelChatCompletionChunk } from '@sagebionetworks/bixarena/api-client';
import { BattleStreamService } from './battle-stream.service';
import { firstValueFrom, toArray } from 'rxjs';

function createMockReader(chunks: ModelChatCompletionChunk[]) {
  const sseText = chunks.map((c) => `data: ${JSON.stringify(c)}\n\n`).join('');
  const encoded = new TextEncoder().encode(sseText);
  let read = false;
  return {
    read: jest.fn().mockImplementation(() => {
      if (!read) {
        read = true;
        return Promise.resolve({ done: false, value: encoded });
      }
      return Promise.resolve({ done: true, value: undefined });
    }),
    cancel: jest.fn(),
  };
}

describe('BattleStreamService', () => {
  let service: BattleStreamService;
  const originalFetch = globalThis.fetch;

  beforeEach(() => {
    globalThis.fetch = jest.fn();
    TestBed.configureTestingModule({
      providers: [BattleStreamService, { provide: BASE_PATH, useValue: 'http://test/api/v1' }],
    });
    service = TestBed.inject(BattleStreamService);
  });

  afterEach(() => {
    globalThis.fetch = originalFetch;
  });

  const mockFetch = () => globalThis.fetch as jest.Mock;

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  it('should parse SSE chunks and emit ModelChatCompletionChunk', async () => {
    const chunks: ModelChatCompletionChunk[] = [
      { content: 'Hello', status: 'streaming' },
      { content: 'Hello world', status: 'streaming' },
      { status: 'complete', finishReason: 'stop' },
    ];
    const reader = createMockReader(chunks);
    mockFetch().mockResolvedValue({ ok: true, body: { getReader: () => reader } });

    const received = await firstValueFrom(
      service.streamCompletion('battle-1', 'round-1', 'model-1').pipe(toArray()),
    );

    expect(received).toHaveLength(3);
    expect(received[0].content).toBe('Hello');
    expect(received[2].status).toBe('complete');
    expect(reader.cancel).toHaveBeenCalled();
  });

  it('should error on non-ok response', async () => {
    mockFetch().mockResolvedValue({ ok: false, status: 500 });

    await expect(
      firstValueFrom(service.streamCompletion('battle-1', 'round-1', 'model-1')),
    ).rejects.toThrow('500');
  });

  it('should construct correct URL with BASE_PATH', () => {
    const reader = createMockReader([]);
    mockFetch().mockResolvedValue({ ok: true, body: { getReader: () => reader } });
    service.streamCompletion('b-id', 'r-id', 'm-id').subscribe();

    expect(mockFetch()).toHaveBeenCalledWith(
      'http://test/api/v1/battles/b-id/rounds/r-id/stream?modelId=m-id',
      expect.objectContaining({
        method: 'POST',
        credentials: 'include',
      }),
    );
  });

  it('should abort on unsubscribe', () => {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    mockFetch().mockReturnValue(new Promise(() => {}));
    const sub = service.streamCompletion('b-id', 'r-id', 'm-id').subscribe();
    sub.unsubscribe();
    expect(mockFetch()).toHaveBeenCalled();
  });
});
