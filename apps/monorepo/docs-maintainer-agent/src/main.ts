import { Octokit } from '@octokit/rest';
import * as fs from 'fs';
import * as path from 'path';
import * as YAML from 'yaml';
import { glob } from 'glob';
// Use require for swagger-parser to avoid TypeScript issues
const SwaggerParser = require('swagger-parser');
import simpleGit from 'simple-git';
import yargs from 'yargs';
import { hideBin } from 'yargs/helpers';

interface AgentConfig {
  repoPath: string;
  githubToken?: string;
  baseBranch: string;
  mode: 'analyze-and-propose' | 'dry-run';
}

interface OpenAPISpec {
  product: string;
  specPath: string;
  generatedPath: string;
  title: string;
  version: string;
}

interface ChangeDetection {
  type: 'openapi' | 'nx-project' | 'readme';
  path: string;
  action: string;
  metadata?: any;
}

interface DocumentationUpdate {
  type: string;
  filePath: string;
  content: string;
  description: string;
}

class DocsMaintenanceAgent {
  private octokit?: Octokit;
  private config: AgentConfig;
  private git: any;

  constructor(config: AgentConfig) {
    this.config = config;
    if (config.githubToken) {
      this.octokit = new Octokit({ auth: config.githubToken });
    }
    this.git = simpleGit(config.repoPath);
  }

  async run(): Promise<void> {
    console.log('🤖 Docs Maintainer Agent starting...');
    console.log('🔍 Analyzing workspace for documentation updates...');
    
    try {
      // 1. Detect changes that require doc updates
      const changes = await this.detectChanges();
      
      if (changes.length === 0) {
        console.log('✅ No documentation updates needed');
        return;
      }

      console.log(`📝 Found ${changes.length} changes requiring documentation updates`);
      
      // 2. Generate documentation updates
      const docUpdates = await this.generateDocUpdates(changes);
      
      // 3. Apply updates or create PR
      if (this.config.mode === 'analyze-and-propose') {
        await this.applyDocumentationUpdates(docUpdates);
      } else {
        console.log('🧪 Dry run mode - would apply these updates:');
        docUpdates.forEach(update => {
          console.log(`  📄 ${update.filePath}: ${update.description}`);
        });
      }
    } catch (error) {
      console.error('❌ Error during docs maintenance:', error);
      throw error;
    }
  }

  private async detectChanges(): Promise<ChangeDetection[]> {
    const changes: ChangeDetection[] = [];

    // Check for OpenAPI spec changes
    const openApiSpecs = await this.findOpenApiSpecs();
    console.log(`🔍 Found ${openApiSpecs.length} OpenAPI specifications`);
    
    for (const spec of openApiSpecs) {
      const hasChanged = await this.hasFileChanged(spec.specPath);
      const docExists = fs.existsSync(path.join(this.config.repoPath, spec.generatedPath));
      
      if (hasChanged || !docExists) {
        changes.push({
          type: 'openapi',
          path: spec.specPath,
          action: 'update-api-docs',
          metadata: spec
        });
      }
    }

    // Check for service catalog updates (new/changed projects)
    const serviceCatalogNeedsUpdate = await this.servicesCatalogNeedsUpdate();
    if (serviceCatalogNeedsUpdate) {
      changes.push({
        type: 'nx-project',
        path: 'workspace',
        action: 'update-service-catalog'
      });
    }

    return changes;
  }

  private async findOpenApiSpecs(): Promise<OpenAPISpec[]> {
    const specs: OpenAPISpec[] = [];
    
    // Find all main OpenAPI specs in api-description libraries
    const specPaths = await glob('libs/*/api-description/openapi/openapi.yaml', {
      cwd: this.config.repoPath
    });

    for (const specPath of specPaths) {
      try {
        const fullPath = path.join(this.config.repoPath, specPath);
        const spec = await SwaggerParser.parse(fullPath);
        
        const product = specPath.split('/')[1]; // Extract product name from path
        
        specs.push({
          product,
          specPath,
          generatedPath: `docs/reference/api/${product}.md`,
          title: spec.info?.title || `${product.charAt(0).toUpperCase() + product.slice(1)} API`,
          version: spec.info?.version || '1.0.0'
        });
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';
        console.warn(`⚠️  Could not parse OpenAPI spec at ${specPath}:`, errorMessage);
      }
    }

    return specs;
  }

  private async generateDocUpdates(changes: ChangeDetection[]): Promise<DocumentationUpdate[]> {
    const updates: DocumentationUpdate[] = [];

    for (const change of changes) {
      switch (change.type) {
        case 'openapi': {
          const apiDoc = await this.generateApiDocs(change.metadata);
          if (apiDoc) updates.push(apiDoc);
          break;
        }
        case 'nx-project': {
          const catalogDoc = await this.generateServiceCatalog();
          if (catalogDoc) updates.push(catalogDoc);
          break;
        }
      }
    }

    // Update navigation if we have API docs
    const apiUpdates = updates.filter(u => u.type === 'api-reference');
    if (apiUpdates.length > 0) {
      const navUpdate = await this.updateNavigation(apiUpdates);
      if (navUpdate) updates.push(navUpdate);
    }

    return updates;
  }

  private async generateApiDocs(spec: OpenAPISpec): Promise<DocumentationUpdate | null> {
    try {
      const fullSpecPath = path.join(this.config.repoPath, spec.specPath);
      const parsedSpec = await SwaggerParser.parse(fullSpecPath);
      
      let content = `# ${spec.title}\n\n`;
      content += `**Version:** ${spec.version}\n\n`;
      
      if (parsedSpec.info?.description) {
        content += `${parsedSpec.info.description}\n\n`;
      }

      // Add server information
      if (parsedSpec.servers && parsedSpec.servers.length > 0) {
        content += `## Servers\n\n`;
        parsedSpec.servers.forEach((server: any) => {
          content += `- **${server.description || 'Server'}**: \`${server.url}\`\n`;
        });
        content += '\n';
      }

      // Add paths overview
      if (parsedSpec.paths) {
        content += `## API Endpoints\n\n`;
        content += `This API provides ${Object.keys(parsedSpec.paths).length} endpoints:\n\n`;
        
        Object.entries(parsedSpec.paths).forEach(([pathName, pathInfo]: [string, any]) => {
          const methods = Object.keys(pathInfo).filter(key => 
            ['get', 'post', 'put', 'delete', 'patch'].includes(key.toLowerCase())
          );
          
          methods.forEach(method => {
            const operation = pathInfo[method];
            const summary = operation?.summary || `${method.toUpperCase()} ${pathName}`;
            content += `- **${method.toUpperCase()}** \`${pathName}\` - ${summary}\n`;
          });
        });
        content += '\n';
      }

      // Add link to interactive docs
      content += `## Interactive Documentation\n\n`;
      content += `For detailed API documentation with interactive examples, see:\n\n`;
      content += `- [${spec.product} API Docs](https://sage-bionetworks.github.io/sage-monorepo/apps/${spec.product}/api-docs/)\n\n`;

      // Add OpenAPI spec link
      content += `## OpenAPI Specification\n\n`;
      content += `- [OpenAPI Spec (YAML)](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/${spec.specPath})\n\n`;

      // Add generation timestamp
      content += `---\n*This documentation was automatically generated from the OpenAPI specification.*\n`;
      content += `*Last updated: ${new Date().toISOString()}*\n`;

      return {
        type: 'api-reference',
        filePath: spec.generatedPath,
        content,
        description: `Generated API documentation for ${spec.title}`
      };
    } catch (error) {
      console.error(`❌ Failed to generate API docs for ${spec.product}:`, error);
      return null;
    }
  }

  private async generateServiceCatalog(): Promise<DocumentationUpdate | null> {
    try {
      // Read project configurations from file system
      const projectPaths = await glob('**/project.json', {
        cwd: this.config.repoPath,
        ignore: ['node_modules/**', '**/node_modules/**']
      });
      
      let content = `# Service Catalog\n\n`;
      content += `This page provides an overview of all services and applications in the Sage Monorepo.\n\n`;
      
      // Group projects by scope
      const services = new Map<string, any[]>();
      
      for (const projectPath of projectPaths) {
        try {
          const fullPath = path.join(this.config.repoPath, projectPath);
          const projectConfig = JSON.parse(fs.readFileSync(fullPath, 'utf8'));
          const tags = projectConfig.tags || [];
          const scope = tags.find((tag: string) => tag.startsWith('scope:'))?.replace('scope:', '') || 'shared';
          
          if (!services.has(scope)) {
            services.set(scope, []);
          }
          
          const scopeProjects = services.get(scope);
          if (scopeProjects) {
            scopeProjects.push({
              name: projectConfig.name,
              type: tags.find((tag: string) => tag.startsWith('type:'))?.replace('type:', '') || 'unknown',
              language: tags.find((tag: string) => tag.startsWith('language:'))?.replace('language:', '') || 'unknown',
              root: projectConfig.root || path.dirname(projectPath),
              targets: Object.keys(projectConfig.targets || {})
            });
          }
        } catch (error) {
          console.warn(`Could not read project config at ${projectPath}:`, error);
        }
      }

      // Generate catalog by product
      services.forEach((projectList, scope) => {
        content += `## ${scope.charAt(0).toUpperCase() + scope.slice(1)}\n\n`;
        
        projectList
          .sort((a, b) => a.name.localeCompare(b.name))
          .forEach(project => {
            content += `### ${project.name}\n\n`;
            content += `- **Type**: ${project.type}\n`;
            content += `- **Language**: ${project.language}\n`;
            content += `- **Location**: \`${project.root}\`\n`;
            content += `- **Available Tasks**: ${project.targets.join(', ')}\n\n`;
          });
      });

      content += `---\n*This catalog was automatically generated from the Nx workspace configuration.*\n`;
      content += `*Last updated: ${new Date().toISOString()}*\n`;

      return {
        type: 'service-catalog',
        filePath: 'docs/reference/services.md',
        content,
        description: 'Updated service catalog from Nx workspace'
      };
    } catch (error) {
      console.error('❌ Failed to generate service catalog:', error);
      return null;
    }
  }

  private async updateNavigation(apiUpdates: DocumentationUpdate[]): Promise<DocumentationUpdate | null> {
    try {
      const mkdocsPath = path.join(this.config.repoPath, 'mkdocs.yml');
      const mkdocsContent = fs.readFileSync(mkdocsPath, 'utf8');
      const mkdocsConfig = YAML.parse(mkdocsContent) as any;

      // Find or create the Reference section
      let referenceSection = mkdocsConfig.nav.find((item: any) => item.Reference);
      if (!referenceSection) {
        referenceSection = { Reference: [] };
        mkdocsConfig.nav.push(referenceSection);
      }

      // Add API subsection if it doesn't exist
      let apiSection = referenceSection.Reference.find((item: any) => item.API);
      if (!apiSection) {
        apiSection = { API: [] };
        referenceSection.Reference.push(apiSection);
      }

      // Add new API docs to navigation
      apiUpdates.forEach(update => {
        const product = path.basename(update.filePath, '.md');
        const productTitle = product.charAt(0).toUpperCase() + product.slice(1);
        
        // Remove existing entry if it exists
        apiSection.API = apiSection.API.filter((item: any) => 
          typeof item === 'object' ? !item[productTitle] : true
        );
        
        // Add new entry
        apiSection.API.push({
          [productTitle]: update.filePath.replace('docs/', '')
        });
      });

      const updatedMkdocsContent = YAML.stringify(mkdocsConfig, {
        indent: 2,
        lineWidth: 0
      });

      return {
        type: 'navigation',
        filePath: 'mkdocs.yml',
        content: updatedMkdocsContent,
        description: 'Updated navigation to include new API documentation'
      };
    } catch (error) {
      console.error('❌ Failed to update navigation:', error);
      return null;
    }
  }

  private async applyDocumentationUpdates(updates: DocumentationUpdate[]): Promise<void> {
    for (const update of updates) {
      const fullPath = path.join(this.config.repoPath, update.filePath);
      const dir = path.dirname(fullPath);
      
      // Ensure directory exists
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }
      
      // Write the file
      fs.writeFileSync(fullPath, update.content, 'utf8');
      console.log(`✅ ${update.description} -> ${update.filePath}`);
    }
    
    console.log(`🎉 Applied ${updates.length} documentation updates`);
  }

  private async hasFileChanged(filePath: string): Promise<boolean> {
    try {
      const status = await this.git.status([filePath]);
      return status.modified.includes(filePath) || 
             status.created.includes(filePath) || 
             status.staged.includes(filePath);
    } catch {
      // If we can't determine git status, assume it changed
      return true;
    }
  }

  private async servicesCatalogNeedsUpdate(): Promise<boolean> {
    const catalogPath = path.join(this.config.repoPath, 'docs/reference/services.md');
    
    // If catalog doesn't exist, it needs to be created
    if (!fs.existsSync(catalogPath)) {
      return true;
    }
    
    // Check if any project.json files have changed
    try {
      const status = await this.git.status();
      return status.files.some((file: any) => 
        file.path.includes('project.json') && 
        ['M', 'A', 'D'].includes(file.index)
      );
    } catch {
      return false;
    }
  }
}

// CLI Interface
async function main() {
  const argv = await yargs(hideBin(process.argv))
    .option('repo-path', {
      type: 'string',
      default: process.cwd(),
      description: 'Path to the repository'
    })
    .option('github-token', {
      type: 'string',
      description: 'GitHub token for API access'
    })
    .option('base-branch', {
      type: 'string',
      default: 'main',
      description: 'Base branch to compare against'
    })
    .option('mode', {
      choices: ['analyze-and-propose', 'dry-run'] as const,
      default: 'dry-run' as const,
      description: 'Agent operation mode'
    })
    .help()
    .argv;

  const agent = new DocsMaintenanceAgent({
    repoPath: argv['repo-path'],
    githubToken: argv['github-token'],
    baseBranch: argv['base-branch'],
    mode: argv.mode
  });

  await agent.run();
}

if (require.main === module) {
  main().catch((error) => {
    console.error('💥 Agent failed:', error);
    process.exit(1);
  });
}
