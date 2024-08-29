
export class Completer<T> {
  public readonly promise: Promise<T>;
  private _complete: ((value: (PromiseLike<T> | T)) => void) | undefined;
  private _reject: ((reason?: any) => void) | undefined;

  get complete(): (value: (PromiseLike<T> | T)) => void {
    return this._complete!;
  }

  get reject(): (reason?: any) => void {
    return this._reject!;
  }

  public constructor() {
    this.promise = new Promise<T>((resolve, reject) => {
      this._complete = resolve;
      this._reject = reject;
    });
  }
}

export function isPageHidden() {
  const doc = document as any;
  return doc.hidden || doc.msHidden || doc.webkitHidden || doc.mozHidden;
}


