import { Component, inject, OnDestroy, output, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

interface HelixNode {
  label: string;
  icon: string;
  top: number;
  left: number;
}

interface CategoryPrompt {
  category: string;
  prompts: string[];
}

const HELIX_NODES: HelixNode[] = [
  {
    label: 'Cardiology',
    icon: 'M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z',
    top: 32,
    left: 55,
  },
  {
    label: 'Oncology',
    icon: 'M12 12m-10 0a10 10 0 1 0 20 0a10 10 0 1 0-20 0M16 12l-4-4-4 4M12 16V8',
    top: 58,
    left: 130,
  },
  {
    label: 'Genetics',
    icon: 'M2 15c6.667-6 13.333 0 20-6M9 22c1.798-1.998 2.518-3.995 2.807-5.993M15 2c-1.798 1.998-2.518 3.995-2.807 5.993',
    top: 28,
    left: 205,
  },
  {
    label: 'Neuro',
    icon: 'M12 2a8 8 0 0 0-8 8c0 6 8 12 8 12s8-6 8-12a8 8 0 0 0-8-8ZM12 10m-3 0a3 3 0 1 0 6 0a3 3 0 1 0-6 0',
    top: 58,
    left: 280,
  },
  {
    label: 'Immuno',
    icon: 'm10 20-1.25-2.5L6 18l1-3-2.5-1.5L6 12 4.5 10.5 7 9l-1-3 2.75.5L10 4l2 2 2-2 1.25 2.5L18 6l-1 3 2.5 1.5L18 12l1.5 1.5L17 15l1 3-2.75-.5L14 20l-2-2Z',
    top: 32,
    left: 355,
  },
];

// Mock data — will be replaced by API with category support
const MOCK_PROMPTS: CategoryPrompt[] = [
  {
    category: 'Cardiology',
    prompts: [
      'What are the latest guidelines for managing atrial fibrillation?',
      'How does SGLT2 inhibitor therapy benefit heart failure patients?',
      'What is the role of cardiac MRI in diagnosing myocarditis?',
    ],
  },
  {
    category: 'Oncology',
    prompts: [
      'What are the current immunotherapy options for non-small cell lung cancer?',
      'How do checkpoint inhibitors work in cancer treatment?',
      'What is the role of liquid biopsy in cancer diagnosis?',
    ],
  },
  {
    category: 'Genetics',
    prompts: [
      'How does CRISPR-Cas9 gene editing work and what are its clinical applications?',
      'What are the ethical considerations of germline gene therapy?',
      'How do polygenic risk scores help predict disease susceptibility?',
    ],
  },
  {
    category: 'Neuro',
    prompts: [
      "What are the emerging biomarkers for early detection of Alzheimer's disease?",
      "How does deep brain stimulation work for Parkinson's disease?",
      'What is the current understanding of long COVID neurological effects?',
    ],
  },
  {
    category: 'Immuno',
    prompts: [
      'How do mRNA vaccines trigger an immune response?',
      'What is the role of T-cell exhaustion in chronic infections?',
      'How do autoimmune diseases develop and what are common treatment approaches?',
    ],
  },
];

@Component({
  selector: 'bixarena-example-prompts',
  templateUrl: './example-prompts.component.html',
  styleUrl: './example-prompts.component.scss',
})
export class ExamplePromptsComponent implements OnDestroy {
  readonly promptSelect = output<string>();
  readonly nodes = HELIX_NODES;
  readonly selectedPrompt = signal<string | null>(null);
  readonly selectedCategory = signal<string | null>(null);

  private readonly onDocClick = (e: MouseEvent) => {
    const target = e.target as HTMLElement;
    if (!target.closest('.helix-node') && !target.closest('.dna-prompt')) {
      this.selectedPrompt.set(null);
      this.selectedCategory.set(null);
    }
  };

  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  constructor() {
    if (this.isBrowser) {
      document.addEventListener('click', this.onDocClick);
    }
  }

  ngOnDestroy(): void {
    if (this.isBrowser) {
      document.removeEventListener('click', this.onDocClick);
    }
  }

  onNodeClick(node: HelixNode): void {
    const category = MOCK_PROMPTS.find((c) => c.category === node.label);
    if (!category) return;
    const randomPrompt = category.prompts[Math.floor(Math.random() * category.prompts.length)];
    this.selectedPrompt.set(randomPrompt);
    this.selectedCategory.set(node.label);
  }

  usePrompt(): void {
    const prompt = this.selectedPrompt();
    if (prompt) {
      this.promptSelect.emit(prompt);
      this.selectedPrompt.set(null);
      this.selectedCategory.set(null);
    }
  }
}
