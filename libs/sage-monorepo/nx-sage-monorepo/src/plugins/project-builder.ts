import { readdirSync, existsSync } from 'fs';
import { join } from 'path';

export type ProjectBuilder = 'gradle' | 'maven';

const BUILD_FILES: Record<ProjectBuilder, string> = {
  gradle: 'build.gradle',
  maven: 'pom.xml',
};

export function inferProjectBuilder(
  workspaceRoot: string,
  projectRoot: string,
): ProjectBuilder | undefined {
  const projectPath = join(workspaceRoot, projectRoot);

  // Check if the project directory exists
  if (!existsSync(projectPath)) {
    return undefined;
  }

  try {
    const siblingFiles = readdirSync(projectPath);

    // Check for known build files
    for (const [builder, buildFile] of Object.entries(BUILD_FILES)) {
      if (siblingFiles.includes(buildFile)) {
        return builder as ProjectBuilder;
      }
    }
  } catch (error) {
    console.error(`Failed to read directory: ${projectPath}`, error);
    return undefined;
  }

  return undefined;
}
