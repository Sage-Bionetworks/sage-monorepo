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
  type: 'openapi' | 'nx-project' | 'readme' | 'project-status' | 'getting-started';
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

interface ProjectInfo {
  name: string;
  type: 'application' | 'library';
  language: string;
  scope: string;
  description?: string;
  status: 'active' | 'experimental' | 'deprecated';
  tags: string[];
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
        docUpdates.forEach((update) => {
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
          metadata: spec,
        });
      }
    }

    // Check for service catalog updates (new/changed projects)
    const serviceCatalogNeedsUpdate = await this.servicesCatalogNeedsUpdate();
    if (serviceCatalogNeedsUpdate) {
      changes.push({
        type: 'nx-project',
        path: 'services-catalog',
        action: 'update-services-catalog',
      });
    }

    // Check for project status updates
    const projectStatusNeedsUpdate = await this.projectStatusNeedsUpdate();
    if (projectStatusNeedsUpdate) {
      changes.push({
        type: 'project-status',
        path: 'docs/index.md',
        action: 'update-project-status',
      });
    }

    // Check for README changes that might affect documentation
    const readmeChanges = await this.detectReadmeChanges();
    changes.push(...readmeChanges);

    return changes;
  }

  private async findOpenApiSpecs(): Promise<OpenAPISpec[]> {
    const specs: OpenAPISpec[] = [];

    // Find all main OpenAPI specs in api-description libraries
    const specPaths = await glob('libs/*/api-description/openapi/openapi.yaml', {
      cwd: this.config.repoPath,
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
          version: spec.info?.version || '1.0.0',
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
        case 'project-status': {
          const statusDoc = await this.generateProjectStatusUpdate();
          if (statusDoc) updates.push(statusDoc);
          break;
        }
        case 'readme': {
          const readmeDoc = await this.syncReadmeTooDocs(change);
          if (readmeDoc) updates.push(readmeDoc);
          break;
        }
      }
    }

    // Update navigation if we have API docs
    const apiUpdates = updates.filter((u) => u.type === 'api-reference');
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

      // Add paths overview with more details
      if (parsedSpec.paths) {
        content += `## API Endpoints\n\n`;
        content += `This API provides ${Object.keys(parsedSpec.paths).length} endpoints:\n\n`;

        // Group endpoints by tags if available
        const endpointsByTag: Record<string, any[]> = {};
        const untaggedEndpoints: any[] = [];

        Object.entries(parsedSpec.paths).forEach(([pathName, pathInfo]: [string, any]) => {
          const methods = Object.keys(pathInfo).filter((key) =>
            ['get', 'post', 'put', 'delete', 'patch'].includes(key.toLowerCase()),
          );

          methods.forEach((method) => {
            const operation = pathInfo[method];
            const tags = operation?.tags || [];
            const endpoint = {
              method: method.toUpperCase(),
              path: pathName,
              summary: operation?.summary || `${method.toUpperCase()} ${pathName}`,
              description: operation?.description,
              deprecated: operation?.deprecated || false,
            };

            if (tags.length > 0) {
              tags.forEach((tag: string) => {
                if (!endpointsByTag[tag]) endpointsByTag[tag] = [];
                endpointsByTag[tag].push(endpoint);
              });
            } else {
              untaggedEndpoints.push(endpoint);
            }
          });
        });

        // Render endpoints by tag
        Object.entries(endpointsByTag).forEach(([tag, endpoints]) => {
          content += `### ${tag}\n\n`;
          endpoints.forEach((endpoint) => {
            const deprecatedFlag = endpoint.deprecated ? ' ⚠️ *Deprecated*' : '';
            content += `- **${endpoint.method}** \`${endpoint.path}\`${deprecatedFlag}\n`;
            content += `  ${endpoint.summary}\n`;
            if (endpoint.description && endpoint.description !== endpoint.summary) {
              content += `  \n  ${endpoint.description}\n`;
            }
            content += '\n';
          });
        });

        // Render untagged endpoints
        if (untaggedEndpoints.length > 0) {
          content += `### Other Endpoints\n\n`;
          untaggedEndpoints.forEach((endpoint) => {
            const deprecatedFlag = endpoint.deprecated ? ' ⚠️ *Deprecated*' : '';
            content += `- **${endpoint.method}** \`${endpoint.path}\`${deprecatedFlag}\n`;
            content += `  ${endpoint.summary}\n`;
            if (endpoint.description && endpoint.description !== endpoint.summary) {
              content += `  \n  ${endpoint.description}\n`;
            }
            content += '\n';
          });
        }
      }

      // Add authentication info if available
      if (parsedSpec.components?.securitySchemes) {
        content += `## Authentication\n\n`;
        Object.entries(parsedSpec.components.securitySchemes).forEach(
          ([name, scheme]: [string, any]) => {
            content += `- **${name}**: ${scheme.type}`;
            if (scheme.description) {
              content += ` - ${scheme.description}`;
            }
            content += '\n';
          },
        );
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
        description: `Generated API documentation for ${spec.title}`,
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
        ignore: ['node_modules/**', '**/node_modules/**'],
      });

      let content = `# Service Catalog\n\n`;
      content += `This page provides an overview of all services and applications in the Sage Monorepo.\n\n`;
      content += `*Last updated: ${new Date().toISOString()}*\n\n`;

      // Group projects by scope
      const services = new Map<string, any[]>();
      const stats = {
        totalProjects: 0,
        applications: 0,
        libraries: 0,
        byLanguage: {} as Record<string, number>,
        byScope: {} as Record<string, number>,
      };

      for (const projectPath of projectPaths) {
        try {
          const fullPath = path.join(this.config.repoPath, projectPath);
          const projectConfig = JSON.parse(fs.readFileSync(fullPath, 'utf8'));
          const tags = projectConfig.tags || [];
          const scope =
            tags.find((tag: string) => tag.startsWith('scope:'))?.replace('scope:', '') || 'shared';
          const projectType =
            tags.find((tag: string) => tag.startsWith('type:'))?.replace('type:', '') || 'unknown';
          const language =
            tags.find((tag: string) => tag.startsWith('language:'))?.replace('language:', '') ||
            'unknown';

          // Update statistics
          stats.totalProjects++;
          if (projectConfig.projectType === 'application') stats.applications++;
          if (projectConfig.projectType === 'library') stats.libraries++;
          stats.byLanguage[language] = (stats.byLanguage[language] || 0) + 1;
          stats.byScope[scope] = (stats.byScope[scope] || 0) + 1;

          if (!services.has(scope)) {
            services.set(scope, []);
          }

          // Get README path if it exists
          const readmePath = path.join(path.dirname(projectPath), 'README.md');
          const hasReadme = fs.existsSync(path.join(this.config.repoPath, readmePath));

          const scopeProjects = services.get(scope);
          if (scopeProjects) {
            scopeProjects.push({
              name: projectConfig.name,
              type: projectType,
              projectType: projectConfig.projectType || 'library',
              language,
              root: projectConfig.root || path.dirname(projectPath),
              targets: Object.keys(projectConfig.targets || {}),
              hasReadme,
              description: this.extractProjectDescription(
                projectConfig,
                path.join(this.config.repoPath, readmePath),
              ),
            });
          }
        } catch (error) {
          console.warn(`Could not read project config at ${projectPath}:`, error);
        }
      }

      // Add summary statistics
      content += `## Overview\n\n`;
      content += `- **Total Projects**: ${stats.totalProjects}\n`;
      content += `- **Applications**: ${stats.applications}\n`;
      content += `- **Libraries**: ${stats.libraries}\n\n`;

      content += `### By Language\n\n`;
      Object.entries(stats.byLanguage)
        .sort(([, a], [, b]) => b - a)
        .forEach(([language, count]) => {
          content += `- **${language}**: ${count} projects\n`;
        });
      content += '\n';

      content += `### By Scope\n\n`;
      Object.entries(stats.byScope)
        .sort(([, a], [, b]) => b - a)
        .forEach(([scope, count]) => {
          content += `- **${scope}**: ${count} projects\n`;
        });
      content += '\n';

      // Generate catalog by product/scope
      const sortedScopes = Array.from(services.keys()).sort();
      sortedScopes.forEach((scope) => {
        const projectList = services.get(scope);
        if (!projectList) return;

        content += `## ${scope.charAt(0).toUpperCase() + scope.slice(1)} Projects\n\n`;

        // Group by project type within scope
        const apps = projectList.filter((p) => p.projectType === 'application');
        const libs = projectList.filter((p) => p.projectType === 'library');

        if (apps.length > 0) {
          content += `### Applications\n\n`;
          apps
            .sort((a, b) => a.name.localeCompare(b.name))
            .forEach((project) => {
              content += `#### ${project.name}\n\n`;
              if (project.description) {
                content += `${project.description}\n\n`;
              }
              content += `- **Language**: ${project.language}\n`;
              content += `- **Location**: \`${project.root}\`\n`;
              content += `- **Available Tasks**: ${project.targets.join(', ')}\n`;
              if (project.hasReadme) {
                content += `- **Documentation**: [README](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/${project.root}/README.md)\n`;
              }
              content += '\n';
            });
        }

        if (libs.length > 0) {
          content += `### Libraries\n\n`;
          libs
            .sort((a, b) => a.name.localeCompare(b.name))
            .forEach((project) => {
              content += `#### ${project.name}\n\n`;
              if (project.description) {
                content += `${project.description}\n\n`;
              }
              content += `- **Type**: ${project.type}\n`;
              content += `- **Language**: ${project.language}\n`;
              content += `- **Location**: \`${project.root}\`\n`;
              content += `- **Available Tasks**: ${project.targets.join(', ')}\n`;
              if (project.hasReadme) {
                content += `- **Documentation**: [README](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/${project.root}/README.md)\n`;
              }
              content += '\n';
            });
        }
      });

      content += `---\n*This catalog was automatically generated from the Nx workspace configuration.*\n`;
      content += `*Last updated: ${new Date().toISOString()}*\n`;

      return {
        type: 'service-catalog',
        filePath: 'docs/reference/services.md',
        content,
        description: 'Updated service catalog from Nx workspace',
      };
    } catch (error) {
      console.error('❌ Failed to generate service catalog:', error);
      return null;
    }
  }

  private async updateNavigation(
    apiUpdates: DocumentationUpdate[],
  ): Promise<DocumentationUpdate | null> {
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
      apiUpdates.forEach((update) => {
        const product = path.basename(update.filePath, '.md');
        const productTitle = product.charAt(0).toUpperCase() + product.slice(1);

        // Remove existing entry if it exists
        apiSection.API = apiSection.API.filter((item: any) =>
          typeof item === 'object' ? !item[productTitle] : true,
        );

        // Add new entry
        apiSection.API.push({
          [productTitle]: update.filePath.replace('docs/', ''),
        });
      });

      const updatedMkdocsContent = YAML.stringify(mkdocsConfig, {
        indent: 2,
        lineWidth: 0,
      });

      return {
        type: 'navigation',
        filePath: 'mkdocs.yml',
        content: updatedMkdocsContent,
        description: 'Updated navigation to include new API documentation',
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
      return (
        status.modified.includes(filePath) ||
        status.created.includes(filePath) ||
        status.staged.includes(filePath)
      );
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
      return status.files.some(
        (file: any) => file.path.includes('project.json') && ['M', 'A', 'D'].includes(file.index),
      );
    } catch {
      return false;
    }
  }

  private async projectStatusNeedsUpdate(): Promise<boolean> {
    // Check if the main index.md needs project status updates
    // This could be triggered by new projects, changed README files, etc.
    const indexPath = path.join(this.config.repoPath, 'docs/index.md');

    if (!fs.existsSync(indexPath)) {
      return true;
    }

    // For now, check if any README.md files have changed
    try {
      const status = await this.git.status();
      return status.files.some(
        (file: any) => file.path.includes('README.md') && ['M', 'A'].includes(file.index),
      );
    } catch {
      return false;
    }
  }

  private async detectReadmeChanges(): Promise<ChangeDetection[]> {
    const changes: ChangeDetection[] = [];

    try {
      // Find README files in app and lib directories that might need doc updates
      const readmePaths = await glob('{apps,libs}/**/README.md', {
        cwd: this.config.repoPath,
        ignore: ['node_modules/**', '**/node_modules/**'],
      });

      for (const readmePath of readmePaths) {
        const hasChanged = await this.hasFileChanged(readmePath);
        if (hasChanged) {
          changes.push({
            type: 'readme',
            path: readmePath,
            action: 'sync-readme-to-docs',
            metadata: { readmePath },
          });
        }
      }
    } catch (error) {
      console.warn('⚠️  Could not detect README changes:', error);
    }

    return changes;
  }

  private async generateProjectStatusUpdate(): Promise<DocumentationUpdate | null> {
    try {
      // Get all projects from the workspace
      const projects = await this.getAllProjects();

      // Read current index.md
      const indexPath = 'docs/index.md';
      const fullIndexPath = path.join(this.config.repoPath, indexPath);
      let content = '';

      if (fs.existsSync(fullIndexPath)) {
        content = fs.readFileSync(fullIndexPath, 'utf-8');
      }

      // Generate new project table
      const projectTable = this.generateProjectTable(projects);

      // Replace or add the project table in index.md
      const updatedContent = this.updateProjectTableInIndex(content, projectTable);

      return {
        type: 'project-status',
        filePath: indexPath,
        content: updatedContent,
        description: 'Updated project status table in main documentation',
      };
    } catch (error) {
      console.error('❌ Failed to generate project status update:', error);
      return null;
    }
  }

  private async syncReadmeTooDocs(change: ChangeDetection): Promise<DocumentationUpdate | null> {
    try {
      const readmePath = change.metadata.readmePath;
      const fullReadmePath = path.join(this.config.repoPath, readmePath);

      if (!fs.existsSync(fullReadmePath)) {
        return null;
      }

      const readmeContent = fs.readFileSync(fullReadmePath, 'utf-8');

      // Extract project name from path (apps/project-name/README.md or libs/project-name/README.md)
      const pathParts = readmePath.split('/');
      const projectType = pathParts[0]; // 'apps' or 'libs'
      const projectName = pathParts[1];

      // Generate docs path
      const docsPath = `docs/reference/${projectType}/${projectName}.md`;

      // Create documentation content with header and processed README content
      let docContent = `# ${projectName}\n\n`;
      docContent += `*This documentation is automatically synced from [${readmePath}](https://github.com/Sage-Bionetworks/sage-monorepo/blob/main/${readmePath})*\n\n`;
      docContent += '---\n\n';
      docContent += readmeContent;

      return {
        type: 'readme-sync',
        filePath: docsPath,
        content: docContent,
        description: `Synced README from ${readmePath}`,
      };
    } catch (error) {
      console.error(`❌ Failed to sync README ${change.path}:`, error);
      return null;
    }
  }

  private async getAllProjects(): Promise<ProjectInfo[]> {
    const projects: ProjectInfo[] = [];

    try {
      const projectPaths = await glob('**/project.json', {
        cwd: this.config.repoPath,
        ignore: ['node_modules/**', '**/node_modules/**'],
      });

      for (const projectPath of projectPaths) {
        try {
          const fullPath = path.join(this.config.repoPath, projectPath);
          const projectConfig = JSON.parse(fs.readFileSync(fullPath, 'utf-8'));

          const projectName = projectConfig.name || path.basename(path.dirname(projectPath));
          const tags = projectConfig.tags || [];

          // Extract metadata from tags
          const scopeTag =
            tags.find((tag: string) => tag.startsWith('scope:'))?.split(':')[1] || 'unknown';
          const languageTag =
            tags.find((tag: string) => tag.startsWith('language:'))?.split(':')[1] || 'unknown';

          // Determine project type
          const projectType =
            projectConfig.projectType === 'application' ? 'application' : 'library';

          // Determine status (could be enhanced with more logic)
          let status: 'active' | 'experimental' | 'deprecated' = 'active';
          if (tags.includes('status:experimental')) status = 'experimental';
          if (tags.includes('status:deprecated')) status = 'deprecated';

          projects.push({
            name: projectName,
            type: projectType,
            language: languageTag,
            scope: scopeTag,
            status,
            tags,
          });
        } catch {
          console.warn(`⚠️  Could not parse project config at ${projectPath}`);
        }
      }
    } catch (error) {
      console.error('❌ Failed to get all projects:', error);
    }

    return projects.sort((a, b) => a.name.localeCompare(b.name));
  }

  private generateProjectTable(projects: ProjectInfo[]): string {
    if (projects.length === 0) {
      return '| Name | Type | Language | Scope | Status |\n| --- | --- | --- | --- | --- |\n| No projects found | - | - | - | - |\n';
    }

    let table = '| Name | Type | Language | Scope | Status |\n';
    table += '| --- | --- | --- | --- | --- |\n';

    // Group by scope for better organization
    const projectsByScope = projects.reduce(
      (acc, project) => {
        if (!acc[project.scope]) acc[project.scope] = [];
        acc[project.scope].push(project);
        return acc;
      },
      {} as Record<string, ProjectInfo[]>,
    );

    // Sort scopes and add projects
    Object.keys(projectsByScope)
      .sort()
      .forEach((scope) => {
        projectsByScope[scope].forEach((project) => {
          const statusIcon =
            project.status === 'active' ? '✅' : project.status === 'experimental' ? '🧪' : '⚠️';

          table += `| **${project.name}** | ${project.type} | ${project.language} | ${project.scope} | ${statusIcon} ${project.status} |\n`;
        });
      });

    return table;
  }

  private updateProjectTableInIndex(content: string, newTable: string): string {
    // Look for existing project table section
    const tableStartMarker = '## Current Projects';
    const tableStartIndex = content.indexOf(tableStartMarker);

    if (tableStartIndex === -1) {
      // No existing table, add it at the end
      return content + '\n\n' + tableStartMarker + '\n\n' + newTable + '\n';
    }

    // Find the end of the current table (next ## section or end of file)
    const afterTableStart = tableStartIndex + tableStartMarker.length;
    const nextSectionIndex = content.indexOf('\n## ', afterTableStart);
    const endIndex = nextSectionIndex === -1 ? content.length : nextSectionIndex;

    // Replace the section
    const beforeTable = content.substring(0, tableStartIndex);
    const afterTable = content.substring(endIndex);

    return beforeTable + tableStartMarker + '\n\n' + newTable + '\n' + afterTable;
  }

  private extractProjectDescription(projectConfig: any, readmePath: string): string {
    // Try to get description from project config first
    if (projectConfig.description) {
      return projectConfig.description;
    }

    // If no description in config, try to extract from README
    try {
      if (fs.existsSync(readmePath)) {
        const readmeContent = fs.readFileSync(readmePath, 'utf-8');
        // Look for first paragraph after title
        const lines = readmeContent.split('\n');
        let inFirstSection = false;

        for (const line of lines) {
          const trimmed = line.trim();

          // Skip empty lines and markdown headers
          if (!trimmed || trimmed.startsWith('#')) {
            if (trimmed.startsWith('#')) inFirstSection = true;
            continue;
          }

          // If we're in the first section and find a non-empty line, use it
          if (inFirstSection && trimmed.length > 10) {
            // Clean up the description
            return (
              trimmed
                .replace(/\[([^\]]+)\]\([^)]+\)/g, '$1') // Remove markdown links
                .replace(/`([^`]+)`/g, '$1') // Remove code blocks
                .substring(0, 200) + (trimmed.length > 200 ? '...' : '')
            );
          }
        }
      }
    } catch {
      // Ignore errors reading README
    }

    return '';
  }
}

// CLI Interface
async function main() {
  const argv = await yargs(hideBin(process.argv))
    .option('repo-path', {
      type: 'string',
      default: process.cwd(),
      description: 'Path to the repository',
    })
    .option('github-token', {
      type: 'string',
      description: 'GitHub token for API access',
    })
    .option('base-branch', {
      type: 'string',
      default: 'main',
      description: 'Base branch to compare against',
    })
    .option('mode', {
      choices: ['analyze-and-propose', 'dry-run'] as const,
      default: 'dry-run' as const,
      description: 'Agent operation mode',
    })
    .help().argv;

  const agent = new DocsMaintenanceAgent({
    repoPath: argv['repo-path'],
    githubToken: argv['github-token'],
    baseBranch: argv['base-branch'],
    mode: argv.mode,
  });

  await agent.run();
}

if (require.main === module) {
  main().catch((error) => {
    console.error('💥 Agent failed:', error);
    process.exit(1);
  });
}
