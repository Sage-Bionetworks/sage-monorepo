export class AppError extends Error {
  constructor(
    public override message: string,
    public isUserFacingError = false,
  ) {
    super(message);
  }
}
