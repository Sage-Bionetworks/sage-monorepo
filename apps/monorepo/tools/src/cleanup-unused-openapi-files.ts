// Handle EPIPE errors gracefully (e.g., when output is piped to head or grep)
process.stdout.on('error', (error) => {
  if (error.code === 'EPIPE') {
    process.exit(0);
  }
});

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
      const entryPointName = path.relative(this.srcDir, entryPoint);
      await this.processFile(entryPoint, `ENTRY:${entryPointName}`);
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

    console.log(`📊 Summary:`);
    console.log(`   Total files: ${this.fileReferences.size}`);
    console.log(`   Used files: ${usedFiles.length}`);
    console.log(`   Unused files: ${unusedFiles.length}\n`);

    // Show emoji legend
    this.printEmojiLegend();

    if (unusedFiles.length > 0) {
      console.log('🗑️  Unused files:');
      unusedFiles.sort().forEach((file) => {
        console.log(`   ${file}`);
      });
      console.log('');
    }

    // Generate dependency graph
    this.generateDependencyGraph();
  }

  /**
   * Print legend for emojis used in the report
   */
  private printEmojiLegend(): void {
    console.log('📖 Legend:');
    console.log('   📄 Entry Point (main OpenAPI spec files)');
    console.log('   🛤️ Path (API endpoint definitions)');
    console.log('   🧬 Schema (data model definitions)');
    console.log('   📤 Response (HTTP response definitions)');
    console.log('   🔼 Parameter (request parameter definitions)');
    console.log('   🔐 Security (authentication/authorization schemes)');
    console.log('   🔗 Link (OpenAPI link definitions)');
    console.log('   🔗 → Dependency chain (shows reference flow)');
    console.log('');
  }

  /**
   * Generate a graph view of dependencies
   */
  private generateDependencyGraph(): void {
    console.log('� Dependency Graph:\n');

    // Group files by category for better organization
    const categories = this.groupFilesByCategory();

    // Show entry points first
    console.log('📋 Entry Points:');
    categories.entryPoints.forEach((file) => {
      console.log(`   📄 ${file}`);
    });
    console.log('');

    // Show files grouped by type with their dependencies
    this.printCategoryDependencies('🛤️ Paths', categories.paths);
    this.printCategoryDependencies('🧬 Schemas', categories.schemas);
    this.printCategoryDependencies('📤 Responses', categories.responses);
    this.printCategoryDependencies('🔼 Parameters', categories.parameters);
    this.printCategoryDependencies('🔐 Security', categories.security);
    this.printCategoryDependencies('🔗 Links', categories.links);
    this.printCategoryDependencies('🌈 Other', categories.other);
  }

  /**
   * Group files by their category/type
   */
  private groupFilesByCategory() {
    const categories = {
      entryPoints: [] as string[],
      paths: [] as string[],
      schemas: [] as string[],
      responses: [] as string[],
      parameters: [] as string[],
      security: [] as string[],
      links: [] as string[],
      other: [] as string[],
    };

    Array.from(this.fileReferences.entries())
      .filter(([, ref]) => ref.referencedBy.length > 0)
      .sort()
      .forEach(([filePath]) => {
        if (this.isEntryPoint(filePath)) {
          categories.entryPoints.push(filePath);
        } else if (filePath.includes('/paths/')) {
          categories.paths.push(filePath);
        } else if (filePath.includes('/schemas/')) {
          categories.schemas.push(filePath);
        } else if (filePath.includes('/responses/')) {
          categories.responses.push(filePath);
        } else if (filePath.includes('/parameters/')) {
          categories.parameters.push(filePath);
        } else if (filePath.includes('/securitySchemes/')) {
          categories.security.push(filePath);
        } else if (filePath.includes('/links/')) {
          categories.links.push(filePath);
        } else {
          categories.other.push(filePath);
        }
      });

    return categories;
  }

  /**
   * Print dependencies for a specific category with complete chains
   */
  private printCategoryDependencies(categoryName: string, files: string[]): void {
    if (files.length === 0) return;

    console.log(`${categoryName}:`);
    files.forEach((file) => {
      const ref = this.fileReferences.get(file);
      if (ref && ref.referencedBy.length > 0) {
        const fileName = path.basename(file, path.extname(file));
        console.log(`\n   ${fileName}`);

        // Find all paths from entry points to this file
        const chains = this.findDependencyChains(file);

        if (chains.length > 0) {
          console.log(`     🔗 Dependency chains:`);
          chains.forEach((chain, index) => {
            const chainDisplay = chain
              .map((step) => {
                if (step.startsWith('ENTRY:')) {
                  return `📄 ${step.substring(6)}`;
                }
                const stepName = path.basename(step, path.extname(step));
                if (step.includes('/schemas/')) return `🧬 ${stepName}`;
                if (step.includes('/responses/')) return `📤 ${stepName}`;
                if (step.includes('/parameters/')) return `🔼  ${stepName}`;
                if (step.includes('/paths/')) return `🛤️ ${stepName}`;
                if (step.includes('/securitySchemes/')) return `🔐 ${stepName}`;
                if (step.includes('/links/')) return `🔗 ${stepName}`;
                return `📄 ${stepName}`;
              })
              .join(' → ');

            console.log(`       ${index + 1}. ${chainDisplay}`);
          });
        }
      }
    });
    console.log('');
  }

  /**
   * Find all dependency chains from entry points to a target file
   */
  private findDependencyChains(targetFile: string): string[][] {
    const chains: string[][] = [];

    // Find all paths from entry points to the target
    const entryPoints = Array.from(this.fileReferences.entries())
      .filter(([filePath]) => this.isEntryPoint(filePath))
      .map(([filePath]) => filePath);

    for (const entryPoint of entryPoints) {
      this.findChainsRecursive(
        `ENTRY:${entryPoint}`,
        targetFile,
        [`ENTRY:${entryPoint}`],
        chains,
        new Set(),
      );
    }

    // Remove duplicate chains and sort by length (shorter chains first)
    const uniqueChains = chains
      .filter((chain, index) => {
        const chainStr = chain.join('→');
        return chains.findIndex((c) => c.join('→') === chainStr) === index;
      })
      .sort((a, b) => a.length - b.length);

    return uniqueChains.slice(0, 5); // Limit to 5 chains to avoid overwhelming output
  }

  /**
   * Recursively find dependency chains
   */
  private findChainsRecursive(
    currentFile: string,
    targetFile: string,
    currentChain: string[],
    allChains: string[][],
    visited: Set<string>,
  ): void {
    if (visited.has(currentFile)) return;
    visited.add(currentFile);

    // Get the actual file path
    const actualFile = currentFile.startsWith('ENTRY:') ? currentFile.substring(6) : currentFile;

    // If we found the target, add this chain
    if (actualFile === targetFile) {
      allChains.push([...currentChain]);
      return;
    }

    // Get references from this file
    const content = this.getFileContent(actualFile);
    if (!content) return;

    const baseDir = path.dirname(path.join(this.srcDir, actualFile));
    const references = this.extractReferences(content, baseDir);

    for (const ref of references) {
      const refPath = path.relative(this.srcDir, ref);
      if (refPath === targetFile) {
        // Found direct reference to target
        allChains.push([...currentChain, refPath]);
      } else if (!currentChain.includes(refPath)) {
        // Continue searching through this reference
        this.findChainsRecursive(
          refPath,
          targetFile,
          [...currentChain, refPath],
          allChains,
          new Set(visited),
        );
      }
    }
  }

  /**
   * Get file content safely
   */
  private getFileContent(filePath: string): string | null {
    try {
      const fullPath = path.join(this.srcDir, filePath);
      return fs.readFileSync(fullPath, 'utf8');
    } catch {
      return null;
    }
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

  // Handle EPIPE errors gracefully (when output is piped to commands like head/grep)
  process.stdout.on('error', (err: NodeJS.ErrnoException) => {
    if (err.code === 'EPIPE') {
      process.exit(0); // Exit gracefully on broken pipe
    }
    throw err;
  });

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
  main().catch((error) => {
    // Handle EPIPE errors gracefully
    if (error.code === 'EPIPE') {
      process.exit(0);
    } else {
      console.error('Error:', error.message);
      process.exit(1);
    }
  });
}

export { OpenAPICleanup };
