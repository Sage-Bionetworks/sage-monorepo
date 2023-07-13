export function isExactType<T extends object>(
  objectToTest: object
): objectToTest is T {
  // const expectedProperties: Array<keyof T> = Object.keys(T);
  return Object.keys(objectToTest).every((prop) => prop in objectToTest);
}
