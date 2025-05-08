export interface Result {
  name: string;
  alias: string[];
  id: string;
}

export interface ResultsList {
  items?: Array<Result>;
}
