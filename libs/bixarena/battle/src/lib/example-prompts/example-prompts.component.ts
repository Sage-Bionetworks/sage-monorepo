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

type CategoryFilter = 'all' | BiomedicalCategory;

interface RungSpec {
  x: number;
  y1: number;
  y2: number;
}

const PAGE_SIZE = 3;

// DNA helix geometry.
// Each strand is a chain of S-cubics — one cubic per full wavelength with
// control points on OPPOSITE sides of the midline. Y(t) peaks at
// t = 0.5 ∓ √3/6 and reaches MID ± 0.2887·CTRL_AMP, so the visible amplitude
// is VIS_AMP when CTRL_AMP = VIS_AMP / 0.2887.
const VB_W = 900;
const MID_Y = 90;
const WAVELEN = 600;
const VIS_AMP = 65;
const CTRL_AMP = VIS_AMP / 0.2887;
// RUNG_STEP must divide WAVELEN evenly so the rung lattice is also
// WAVELEN-periodic — otherwise rungs shift relative to the strands on spin
// reset and flash at the viewBox edges.
const RUNG_STEP = 20;
const LEAD_WAVES = 1;
const TRAIL_WAVES = 3;
// Quarter-wavelength phase shift places midline X-crossings inside the
// viewBox (at 150 / 450 / 750) instead of at the edges, so the leftmost
// and rightmost ovals read symmetric with the middle one.
const PHASE_OFFSET = WAVELEN / 4;

const SPIN_MS = 1200;
const SWAP_MIDPOINT_MS = 420;
const SWAP_IN_MS = 500;

function cubicFactor(t: number): number {
  // 3·t·(1-t)·(1-2t) ∈ [-0.2887, +0.2887]
  return 3 * t * (1 - t) * (1 - 2 * t);
}

function buildStrandPath(firstUp: boolean): string {
  const startX = -LEAD_WAVES * WAVELEN - PHASE_OFFSET;
  const endX = VB_W + TRAIL_WAVES * WAVELEN;
  const count = Math.ceil((endX - startX) / WAVELEN);
  let d = `M ${startX} ${MID_Y}`;
  for (let i = 0; i < count; i++) {
    const x0 = startX + i * WAVELEN;
    const x1 = x0 + WAVELEN;
    const c1x = x0 + WAVELEN / 3;
    const c2x = x0 + (2 * WAVELEN) / 3;
    const c1y = firstUp ? MID_Y - CTRL_AMP : MID_Y + CTRL_AMP;
    const c2y = firstUp ? MID_Y + CTRL_AMP : MID_Y - CTRL_AMP;
    d += ` C ${c1x} ${c1y} ${c2x} ${c2y} ${x1} ${MID_Y}`;
  }
  return d;
}

function buildRungs(): RungSpec[] {
  const strandStartX = -LEAD_WAVES * WAVELEN - PHASE_OFFSET;
  const rungEnd = VB_W + TRAIL_WAVES * WAVELEN;
  const out: RungSpec[] = [];
  for (let x = strandStartX; x <= rungEnd; x += RUNG_STEP) {
    const offset = x - strandStartX;
    const local = ((offset % WAVELEN) + WAVELEN) % WAVELEN;
    const factor = cubicFactor(local / WAVELEN);
    const yF = MID_Y - CTRL_AMP * factor;
    const yB = MID_Y + CTRL_AMP * factor;
    if (Math.abs(yF - yB) < 14) continue;
    out.push({ x, y1: yF, y2: yB });
  }
  return out;
}

function shuffle<T>(arr: readonly T[]): T[] {
  const a = [...arr];
  for (let i = a.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [a[i], a[j]] = [a[j], a[i]];
  }
  return a;
}

function formatCategory(slug: BiomedicalCategory): string {
  return slug
    .split('-')
    .map((w, i) => (i > 0 && w === 'and' ? w : w.charAt(0).toUpperCase() + w.slice(1)))
    .join(' ');
}

@Component({
  selector: 'bixarena-example-prompts',
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

  readonly category = signal<CategoryFilter>('all');
  readonly prompts = signal<ExamplePrompt[]>([]);
  readonly loading = signal(false);
  readonly error = signal(false);
  readonly swapClass = signal<'swap-out' | 'swap-in' | null>(null);

  readonly skeletonSlots = [0, 1, 2];
  readonly formatCategory = formatCategory;

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

  selectCategory(cat: CategoryFilter): void {
    if (this.category() === cat && !this.error()) return;
    this.category.set(cat);
    this.commitFetch();
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

  private commitFetch(): void {
    this.fetchPrompts().subscribe((p) => this.prompts.set(p));
  }

  private fetchPrompts(): Observable<ExamplePrompt[]> {
    this.loading.set(true);
    this.error.set(false);
    const cat = this.category();
    const query: ExamplePromptSearchQuery = {
      sort: ExamplePromptSort.Random,
      pageSize: PAGE_SIZE,
      active: true,
      ...(cat !== 'all' ? { categories: [cat] } : {}),
    };
    // Shuffle client-side because the server's RANDOM sort can return the
    // same order for small pools.
    return this.svc.listExamplePrompts(query).pipe(
      map((page) => shuffle(page.examplePrompts ?? [])),
      catchError(() => {
        this.error.set(true);
        return of<ExamplePrompt[]>([]);
      }),
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
