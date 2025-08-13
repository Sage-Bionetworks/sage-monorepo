import * as fs from 'fs';
import * as path from 'path';
import { parse } from 'yaml';

interface FileReference {
  file: string;
  referencedBy: string[];
}

class OpenAPICleanup {
  private srcDir: string;
  private fileReferences = new Map<string, FileReference>();
  private processedFiles = new Set<string>();

  constructor(srcDir: string) {
    this.srcDir = path.resolve(srcDir);
  }

  /**
   * Main method to identify unused files
   */
  async findUnusedFiles(): Promise<string[]> {
    console.log(`Analyzing OpenAPI files in: ${this.srcDir}`);

    // Find all YAML files in src directory
    const allFiles = this.getAllYamlFiles(this.srcDir);

    // Initialize file references map
    allFiles.forEach((file) => {
      const relativePath = path.relative(this.srcDir, file);
      this.fileReferences.set(relativePath, {
        file: relativePath,
        referencedBy: [],
      });
    });

    // Find entry points (files that don't follow the typical component/path structure)
    const entryPoints = this.findEntryPoints(allFiles);

    console.log(
      'Entry points found:',
      entryPoints.map((f) => path.relative(this.srcDir, f)),
    );

    // Process each entry point to build reference graph
    for (const entryPoint of entryPoints) {
      await this.processFile(entryPoint, 'ENTRY_POINT');
    }

    // Find unused files
    const unusedFiles: string[] = [];
    this.fileReferences.forEach((ref, filePath) => {
      if (ref.referencedBy.length === 0 && !this.isEntryPoint(filePath)) {
        unusedFiles.push(filePath);
      }
    });

    return unusedFiles;
  }

  /**
   * Get all YAML files recursively
   */
  private getAllYamlFiles(dir: string): string[] {
    const files: string[] = [];

    const entries = fs.readdirSync(dir, { withFileTypes: true });

    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);

      if (entry.isDirectory()) {
        files.push(...this.getAllYamlFiles(fullPath));
      } else if (entry.isFile() && /\.(yaml|yml)$/i.test(entry.name)) {
        files.push(fullPath);
      }
    }

    return files;
  }

  /**
   * Identify entry point files (main OpenAPI spec files)
   */
  private findEntryPoints(allFiles: string[]): string[] {
    return allFiles.filter((file) => {
      const relativePath = path.relative(this.srcDir, file);

      // Entry points are typically at the root level and contain "openapi" info
      if (path.dirname(relativePath) === '.') {
        try {
          const content = fs.readFileSync(file, 'utf8');
          const doc = parse(content) as any;
          return doc && doc.openapi && doc.info;
        } catch {
          return false;
        }
      }
      return false;
    });
  }

  /**
   * Check if a file is an entry point
   */
  private isEntryPoint(filePath: string): boolean {
    const fullPath = path.join(this.srcDir, filePath);
    if (path.dirname(filePath) === '.') {
      try {
        const content = fs.readFileSync(fullPath, 'utf8');
        const doc = parse(content) as any;
        return doc && doc.openapi && doc.info;
      } catch {
        return false;
      }
    }
    return false;
  }

  /**
   * Process a file and extract all its references
   */
  private async processFile(filePath: string, referencedBy: string): Promise<void> {
    const relativePath = path.relative(this.srcDir, filePath);

    if (this.processedFiles.has(relativePath)) {
      return;
    }

    this.processedFiles.add(relativePath);

    try {
      const content = fs.readFileSync(filePath, 'utf8');
      const references = this.extractReferences(content, path.dirname(filePath));

      // Update references
      for (const ref of references) {
        const refPath = path.relative(this.srcDir, ref);
        const fileRef = this.fileReferences.get(refPath);
        if (fileRef && !fileRef.referencedBy.includes(referencedBy)) {
          fileRef.referencedBy.push(referencedBy);
        }

        // Recursively process referenced files
        await this.processFile(ref, relativePath);
      }
    } catch (error) {
      console.warn(`Warning: Could not process file ${relativePath}:`, (error as Error).message);
    }
  }

  /**
   * Extract all $ref references from YAML content
   */
  private extractReferences(content: string, baseDir: string): string[] {
    const references: string[] = [];

    // Find all $ref patterns
    const refPattern = /\$ref:\s*['"]?([^'"#\s]+)(?:#[^'"]*)?['"]?/g;
    let match;

    while ((match = refPattern.exec(content)) !== null) {
      const refPath = match[1];

      // Skip HTTP/HTTPS URLs
      if (refPath.startsWith('http://') || refPath.startsWith('https://')) {
        continue;
      }

      // Resolve relative path
      const resolvedPath = path.resolve(baseDir, refPath);

      // Check if file exists and is within src directory
      if (fs.existsSync(resolvedPath) && resolvedPath.startsWith(this.srcDir)) {
        references.push(resolvedPath);
      }
    }

    return references;
  }

  /**
   * Generate a report of file usage
   */
  generateReport(): void {
    console.log('\n=== OpenAPI File Usage Report ===\n');

    const usedFiles: string[] = [];
    const unusedFiles: string[] = [];

    this.fileReferences.forEach((ref, filePath) => {
      if (ref.referencedBy.length > 0 || this.isEntryPoint(filePath)) {
        usedFiles.push(filePath);
      } else {
        unusedFiles.push(filePath);
      }
    });

    console.log(`Total files: ${this.fileReferences.size}`);
    console.log(`Used files: ${usedFiles.length}`);
    console.log(`Unused files: ${unusedFiles.length}\n`);

    if (unusedFiles.length > 0) {
      console.log('Unused files:');
      unusedFiles.sort().forEach((file) => {
        console.log(`  - ${file}`);
      });
    }

    console.log('\nFile dependencies:');
    Array.from(this.fileReferences.entries())
      .filter(([, ref]) => ref.referencedBy.length > 0)
      .sort()
      .forEach(([filePath, ref]) => {
        console.log(`  ${filePath} (referenced by: ${ref.referencedBy.join(', ')})`);
      });
  }

  /**
   * Delete unused files (with confirmation)
   */
  async cleanupUnusedFiles(dryRun: boolean = true): Promise<void> {
    const unusedFiles = await this.findUnusedFiles();

    if (unusedFiles.length === 0) {
      console.log('No unused files found!');
      return;
    }

    console.log(`\nFound ${unusedFiles.length} unused files:`);
    unusedFiles.forEach((file) => console.log(`  - ${file}`));

    if (dryRun) {
      console.log('\nDry run mode - no files were deleted.');
      console.log('Run with --delete flag to actually delete files.');
      return;
    }

    // Delete files
    for (const file of unusedFiles) {
      const fullPath = path.join(this.srcDir, file);
      try {
        fs.unlinkSync(fullPath);
        console.log(`Deleted: ${file}`);
      } catch (error) {
        console.error(`Failed to delete ${file}:`, (error as Error).message);
      }
    }

    // Clean up empty directories
    this.cleanupEmptyDirectories(this.srcDir);
  }

  /**
   * Remove empty directories
   */
  private cleanupEmptyDirectories(dir: string): void {
    if (!fs.existsSync(dir)) return;

    const entries = fs.readdirSync(dir, { withFileTypes: true });

    // Recursively clean subdirectories first
    for (const entry of entries) {
      if (entry.isDirectory()) {
        const subDir = path.join(dir, entry.name);
        this.cleanupEmptyDirectories(subDir);
      }
    }

    // Check if directory is now empty (excluding the src root)
    const remainingEntries = fs.readdirSync(dir);
    if (remainingEntries.length === 0 && dir !== this.srcDir) {
      try {
        fs.rmdirSync(dir);
        console.log(`Removed empty directory: ${path.relative(this.srcDir, dir)}`);
      } catch (error) {
        console.warn(`Could not remove directory ${dir}:`, (error as Error).message);
      }
    }
  }
}

// CLI interface
async function main() {
  const args = process.argv.slice(2);
  const srcDir = args[0] || 'libs/openchallenges/api-description/src';
  const shouldDelete = args.includes('--delete');
  const showReport = args.includes('--report');

  if (args.includes('--help') || args.includes('-h')) {
    console.log(`
Usage: nx run monorepo-tools:cleanup-openapi [src-dir] [options]

Options:
  --delete    Actually delete unused files (default: dry run)
  --report    Show detailed usage report
  --help, -h  Show this help message

Examples:
  nx run monorepo-tools:cleanup-openapi
  nx run monorepo-tools:cleanup-openapi --report
  nx run monorepo-tools:cleanup-openapi --delete
  nx run monorepo-tools:cleanup-openapi libs/openchallenges/api-description/src --delete
    `);
    return;
  }

  try {
    const cleanup = new OpenAPICleanup(srcDir);

    if (showReport) {
      await cleanup.findUnusedFiles();
      cleanup.generateReport();
    } else {
      await cleanup.cleanupUnusedFiles(!shouldDelete);
    }
  } catch (error) {
    console.error('Error:', (error as Error).message);
    process.exit(1);
  }
}

if (require.main === module) {
  main();
}

export { OpenAPICleanup };
