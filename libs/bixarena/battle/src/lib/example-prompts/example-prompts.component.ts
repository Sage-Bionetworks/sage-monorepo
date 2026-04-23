import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  output,
  PLATFORM_ID,
  signal,
  viewChild,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { catchError, finalize, map, Observable, of } from 'rxjs';
import {
  BiomedicalCategory,
  ExamplePrompt,
  ExamplePromptSearchQuery,
  ExamplePromptService,
  ExamplePromptSort,
} from '@sagebionetworks/bixarena/api-client';
import { PromptCardComponent } from '@sagebionetworks/bixarena/ui';
import { buildRungs, buildStrandPath, WAVELEN } from './dna-helix';

const PAGE_SIZE = 3;

const SPIN_MS = 1200;
// 70 ms past the 350 ms `.swap-out` CSS keyframe duration so the browser
// has time to finish the fade-out paint before we swap content in.
const SWAP_MIDPOINT_MS = 420;
const SWAP_IN_MS = 500;

function formatCategory(slug: BiomedicalCategory): string {
  return slug
    .split('-')
    .map((w, i) => (i > 0 && w === 'and' ? w : w.charAt(0).toUpperCase() + w.slice(1)))
    .join(' ');
}

@Component({
  selector: 'bixarena-example-prompts',
  imports: [PromptCardComponent],
  templateUrl: './example-prompts.component.html',
  styleUrl: './example-prompts.component.scss',
})
export class ExamplePromptsComponent implements AfterViewInit, OnDestroy {
  private readonly svc = inject(ExamplePromptService);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly flowGroupRef = viewChild<ElementRef<SVGGElement>>('flowGroup');

  readonly promptSelect = output<string>();

  readonly frontPath = buildStrandPath(true);
  readonly backPath = buildStrandPath(false);
  readonly rungs = buildRungs();

  readonly prompts = signal<ExamplePrompt[]>([]);
  readonly loading = signal(false);
  readonly error = signal(false);
  readonly swapClass = signal<'swap-out' | 'swap-in' | null>(null);

  readonly skeletonSlots = [0, 1, 2];

  private spinRaf: number | null = null;
  private swapTimer: ReturnType<typeof setTimeout> | null = null;
  private endTimer: ReturnType<typeof setTimeout> | null = null;

  ngAfterViewInit(): void {
    this.commitFetch();
  }

  ngOnDestroy(): void {
    if (this.spinRaf !== null) cancelAnimationFrame(this.spinRaf);
    if (this.swapTimer) clearTimeout(this.swapTimer);
    if (this.endTimer) clearTimeout(this.endTimer);
  }

  refresh(): void {
    if (this.loading() || this.swapClass() !== null) return;

    this.animateSpin();
    this.swapClass.set('swap-out');

    // Fetch in parallel with the fade-out, then commit + trigger fade-in
    // only once BOTH the fade-out timing has elapsed AND the fetch has
    // resolved. Otherwise the stale cards fade back in for a frame before
    // being replaced, producing a visible flash.
    const started = performance.now();
    this.fetchPrompts().subscribe((newPrompts) => {
      if (newPrompts === null) {
        // Refresh failed — preserve the existing cards instead of wiping
        // them. A transient blip shouldn't destroy content the user was
        // already reading.
        this.error.set(true);
        if (this.swapTimer) clearTimeout(this.swapTimer);
        this.swapClass.set(null);
        return;
      }

      this.error.set(false);
      const elapsed = performance.now() - started;
      const wait = Math.max(0, SWAP_MIDPOINT_MS - elapsed);
      if (this.swapTimer) clearTimeout(this.swapTimer);
      this.swapTimer = setTimeout(() => {
        this.prompts.set(newPrompts);
        this.swapClass.set('swap-in');
        if (this.endTimer) clearTimeout(this.endTimer);
        this.endTimer = setTimeout(() => this.swapClass.set(null), SWAP_IN_MS);
      }, wait);
    });
  }

  onCardClick(p: ExamplePrompt): void {
    this.promptSelect.emit(p.question);
  }

  categoryLabel(p: ExamplePrompt): string | undefined {
    return p.category ? formatCategory(p.category) : undefined;
  }

  private commitFetch(): void {
    this.fetchPrompts().subscribe((p) => {
      if (p === null) {
        this.error.set(true);
        this.prompts.set([]);
      } else {
        this.error.set(false);
        this.prompts.set(p);
      }
    });
  }

  // Returns null on fetch failure so callers can distinguish a legitimate
  // empty result from a network/API error and decide whether to wipe the
  // current cards.
  private fetchPrompts(): Observable<ExamplePrompt[] | null> {
    this.loading.set(true);
    const query: ExamplePromptSearchQuery = {
      sort: ExamplePromptSort.Random,
      pageSize: PAGE_SIZE,
      active: true,
    };
    return this.svc.listExamplePrompts(query).pipe(
      map((page): ExamplePrompt[] | null => page.examplePrompts ?? []),
      catchError(() => of<ExamplePrompt[] | null>(null)),
      finalize(() => this.loading.set(false)),
    );
  }

  // Translates the strand group by -WAVELEN in SVG user-space. Because the
  // wave is periodic at WAVELEN, snapping back to 0 at the end is visually
  // identical to the final frame — no edge flash.
  private animateSpin(): void {
    if (!this.isBrowser) return;
    const g = this.flowGroupRef()?.nativeElement;
    if (!g) return;
    if (this.spinRaf !== null) cancelAnimationFrame(this.spinRaf);
    const start = performance.now();
    const target = -WAVELEN;
    const step = (now: number) => {
      const t = Math.min((now - start) / SPIN_MS, 1);
      const eased = t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
      const tx = target * eased;
      g.setAttribute('transform', `translate(${tx.toFixed(3)} 0)`);
      if (t < 1) {
        this.spinRaf = requestAnimationFrame(step);
      } else {
        g.setAttribute('transform', 'translate(0 0)');
        this.spinRaf = null;
      }
    };
    this.spinRaf = requestAnimationFrame(step);
  }
}
