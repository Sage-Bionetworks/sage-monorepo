import { CheckFormatExecutorSchema } from './schema';

export default async function runExecutor(options: CheckFormatExecutorSchema) {
  console.log('Executor ran for CheckFormat', options);
  return {
    success: true,
  };
}
