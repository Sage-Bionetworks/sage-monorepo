import { AboutExecutorSchema } from './schema';

export default async function runExecutor(options: AboutExecutorSchema) {
  console.log('Executor ran for About', options);
  return {
    success: true,
  };
}
