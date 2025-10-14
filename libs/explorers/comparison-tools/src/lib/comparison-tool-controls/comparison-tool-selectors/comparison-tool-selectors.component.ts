import {
  Component,
  computed,
  effect,
  input,
  output,
  signal,
  ViewEncapsulation,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ComparisonToolConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { SelectItem } from 'primeng/api';
import { Select } from 'primeng/select';

interface DropdownTree {
  [key: string]: DropdownTree;
}

@Component({
  selector: 'explorers-comparison-tool-selectors',
  imports: [FormsModule, Select],
  templateUrl: './comparison-tool-selectors.component.html',
  styleUrls: ['./comparison-tool-selectors.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolSelectorsComponent {
  pageConfigs = input<ComparisonToolConfig[]>([]);
  popoverWikis = input<{ [key: string]: SynapseWikiParams }>({});
  initialSelection = input<string[]>([]);

  selectionChanged = output<string[]>();

  selectedValues = signal<string[]>([]);

  dropdownTree = computed(() => {
    const configs = this.pageConfigs();
    if (!configs || configs.length === 0) {
      return {};
    }
    return this.buildDropdownTree(configs);
  });
  dropdownLevels = computed(() => {
    const tree = this.dropdownTree();
    return this.getDropdownOptionsFromTree(tree);
  });

  constructor() {
    effect(() => {
      this.initializeSelection();
    });
  }

  private buildDropdownTree(configs: ComparisonToolConfig[]): DropdownTree {
    const tree: DropdownTree = {};

    configs.forEach((config) => {
      let currentLevel = tree;
      config.dropdowns.forEach((value) => {
        if (!currentLevel[value]) {
          currentLevel[value] = {};
        }
        currentLevel = currentLevel[value];
      });
    });

    return tree;
  }

  private getDropdownOptionsFromTree(tree: DropdownTree): SelectItem[][] {
    const levels: SelectItem[][] = [];
    let currentTree = tree;

    let levelIndex = 0;
    while (this.hasKeys(currentTree)) {
      const options = Object.keys(currentTree).map((key) => ({
        label: key,
        value: key,
      }));

      levels.push(options);

      if (levelIndex < this.selectedValues().length) {
        const selectedValue = this.selectedValues()[levelIndex];
        if (currentTree[selectedValue]) {
          currentTree = currentTree[selectedValue];
        } else {
          // Selection is invalid
          break;
        }
      } else {
        // No more selections
        break;
      }

      levelIndex++;
    }

    return levels;
  }

  private initializeSelection(): void {
    const tree = this.dropdownTree();
    const initialSelection = this.initialSelection();

    if (!this.hasKeys(tree)) {
      this.selectedValues.set([]);
      return;
    }

    const newSelection =
      initialSelection &&
      initialSelection.length > 0 &&
      this.isValidSelection(initialSelection, tree)
        ? [...initialSelection]
        : this.getDefaultSelection(tree);

    this.updateSelection(newSelection);
  }

  private isValidSelection(selection: string[], tree: DropdownTree): boolean {
    let currentLevel = tree;

    for (const value of selection) {
      if (!currentLevel[value]) {
        return false;
      }
      currentLevel = currentLevel[value];
    }

    return true;
  }

  private getDefaultSelection(tree: DropdownTree): string[] {
    const selection: string[] = [];
    this.autoCompleteSelection(tree, selection);
    return selection;
  }

  private autoCompleteSelection(currentLevel: DropdownTree, selection: string[]): void {
    while (this.hasKeys(currentLevel)) {
      const firstKey = Object.keys(currentLevel)[0];
      selection.push(firstKey);
      currentLevel = currentLevel[firstKey];
    }
  }

  onDropdownChange(levelIndex: number, selectedValue: string): void {
    const newSelection = this.selectedValues().slice(0, levelIndex + 1);
    newSelection[levelIndex] = selectedValue;

    const tree = this.dropdownTree();
    let currentLevel = tree;

    for (const selection of newSelection) {
      if (currentLevel[selection]) {
        currentLevel = currentLevel[selection];
      } else {
        break;
      }
    }

    this.autoCompleteSelection(currentLevel, newSelection);

    this.updateSelection(newSelection);
  }

  getSelectedValueForLevel(levelIndex: number): string | undefined {
    return this.selectedValues()[levelIndex];
  }

  private hasKeys(tree: DropdownTree): boolean {
    return Object.keys(tree).length > 0;
  }

  private updateSelection(newSelection: string[]): void {
    this.selectedValues.set(newSelection);
    if (newSelection.length > 0) {
      this.selectionChanged.emit([...newSelection]);
    }
  }
}
