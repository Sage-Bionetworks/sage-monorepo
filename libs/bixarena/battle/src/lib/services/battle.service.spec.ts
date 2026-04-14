import { TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { of, EMPTY, throwError } from 'rxjs';
import {
  BattleService as BattleApiService,
  BattleEvaluationOutcome,
} from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleStateService } from './battle.service';
import { BattleStreamService } from './battle-stream.service';

const mockConfig = {
  config: {
    battle: { promptLengthLimit: 5000, roundLimit: 20, promptUseLimit: 5 },
  },
};

const mockBattleResponse = {
  id: 'battle-1',
  model1: { id: 'model-1', name: 'GPT-4' },
  model2: { id: 'model-2', name: 'Claude' },
};

const mockRoundResponse = {
  id: 'round-1',
  battleId: 'battle-1',
  roundNumber: 1,
};

describe('BattleStateService', () => {
  let service: BattleStateService;
  let battleApi: {
    createBattle: jest.Mock;
    createBattleRound: jest.Mock;
    createBattleEvaluation: jest.Mock;
    updateBattle: jest.Mock;
  };
  let streamService: { streamCompletion: jest.Mock };

  beforeEach(() => {
    battleApi = {
      createBattle: jest.fn().mockReturnValue(of(mockBattleResponse)),
      createBattleRound: jest.fn().mockReturnValue(of(mockRoundResponse)),
      createBattleEvaluation: jest.fn().mockReturnValue(of({})),
      updateBattle: jest.fn().mockReturnValue(of({})),
    };

    streamService = {
      streamCompletion: jest.fn().mockReturnValue(EMPTY),
    };

    TestBed.configureTestingModule({
      providers: [
        BattleStateService,
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: BattleApiService, useValue: battleApi },
        { provide: BattleStreamService, useValue: streamService },
        { provide: ConfigService, useValue: mockConfig },
      ],
    });
    service = TestBed.inject(BattleStateService);
  });

  it('should create with landing phase', () => {
    expect(service).toBeTruthy();
    expect(service.phase()).toBe('landing');
  });

  describe('submitPrompt', () => {
    it('should transition to streaming phase', async () => {
      await service.submitPrompt('test prompt');
      expect(service.phase()).not.toBe('landing');
      expect(service.battleId()).toBe('battle-1');
    });

    it('should create battle and round via API', async () => {
      await service.submitPrompt('test prompt');
      expect(battleApi.createBattle).toHaveBeenCalledWith({ title: 'test prompt' });
      expect(battleApi.createBattleRound).toHaveBeenCalledWith('battle-1', {
        promptMessage: { role: 'user', content: 'test prompt' },
      });
    });

    it('should set prompt use limit for new prompts', async () => {
      await service.submitPrompt('new prompt');
      expect(service.promptUsesRemaining()).toBe(5);
      expect(service.lastPrompt()).toBe('new prompt');
    });

    it('should transition to error on API failure', async () => {
      battleApi.createBattle.mockReturnValue(throwError(() => new Error('fail')));
      await service.submitPrompt('test');
      expect(service.phase()).toBe('error');
    });

    it('should start two parallel streams', async () => {
      await service.submitPrompt('test');
      expect(streamService.streamCompletion).toHaveBeenCalledTimes(2);
      expect(streamService.streamCompletion).toHaveBeenCalledWith('battle-1', 'round-1', 'model-1');
      expect(streamService.streamCompletion).toHaveBeenCalledWith('battle-1', 'round-1', 'model-2');
    });
  });

  describe('submitVote', () => {
    beforeEach(async () => {
      await service.submitPrompt('test');
      service.model1Stream.set({
        messages: [],
        text: 'done',
        status: 'complete',
        finishReason: 'stop',
        errorMessage: null,
        isSlowHint: false,
        retryable: false,
      });
      service.model2Stream.set({
        messages: [],
        text: 'done',
        status: 'complete',
        finishReason: 'stop',
        errorMessage: null,
        isSlowHint: false,
        retryable: false,
      });
    });

    it('should submit evaluation and transition to reveal', async () => {
      await service.submitVote(BattleEvaluationOutcome.Model1);
      expect(battleApi.createBattleEvaluation).toHaveBeenCalledWith('battle-1', {
        outcome: 'model1',
      });
      expect(service.phase()).toBe('reveal');
      expect(service.selectedOutcome()).toBe('model1');
    });

    it('should decrement prompt uses remaining', async () => {
      const before = service.promptUsesRemaining();
      await service.submitVote(BattleEvaluationOutcome.Tie);
      expect(service.promptUsesRemaining()).toBe(before - 1);
    });
  });

  describe('computed signals', () => {
    it('bothComplete should be true when both streams are done', () => {
      service.model1Stream.set({
        messages: [],
        text: '',
        status: 'complete',
        finishReason: 'stop',
        errorMessage: null,
        isSlowHint: false,
        retryable: false,
      });
      service.model2Stream.set({
        messages: [],
        text: '',
        status: 'error',
        finishReason: null,
        errorMessage: 'err',
        isSlowHint: false,
        retryable: false,
      });
      expect(service.bothComplete()).toBe(true);
    });

    it('bothComplete should be false when one stream is still going', () => {
      service.model1Stream.set({
        messages: [],
        text: '',
        status: 'complete',
        finishReason: 'stop',
        errorMessage: null,
        isSlowHint: false,
        retryable: false,
      });
      service.model2Stream.set({
        messages: [],
        text: '',
        status: 'streaming',
        finishReason: null,
        errorMessage: null,
        isSlowHint: false,
        retryable: false,
      });
      expect(service.bothComplete()).toBe(false);
    });

    it('should expose promptLengthLimit and promptUseDots from config', () => {
      expect(service.promptLengthLimit).toBeGreaterThan(0);
      expect(service.promptUseDots.length).toBe(service.promptUseLimit);
    });
  });

  describe('reset', () => {
    it('should return to landing phase', async () => {
      await service.submitPrompt('test');
      service.reset();
      expect(service.phase()).toBe('landing');
      expect(service.battleId()).toBeNull();
      expect(service.model1()).toBeNull();
    });
  });
});
