import type { TargetConfiguration } from 'nx/src/config/workspace-json-project-json';

export type MemoizeFnType = (projectPath: string) => Record<string, TargetConfiguration>;

export function memoize(fn: MemoizeFnType): MemoizeFnType {
  const cache: Record<string, Record<string, TargetConfiguration>> = {};

  return (projectPath) => {
    if (!(projectPath in cache)) {
      cache[projectPath] = fn(projectPath);
    }

    return cache[projectPath];
  };
}
