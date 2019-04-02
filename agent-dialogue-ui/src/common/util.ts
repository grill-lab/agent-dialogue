// noinspection JSUnusedGlobalSymbols
export const isKeyPressed = (event: KeyboardEvent, key: string) => {
  if (event.defaultPrevented) {
    return false // Should do nothing if the default action has been cancelled
  }

  let handled = false
  if (event.key !== undefined && event.key === key) {
    handled = true
  }

  if (handled) {
    // Suppress "double action" if event handled
    event.preventDefault()
  }
  return handled
}

export type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>
// noinspection JSUnusedGlobalSymbols
export type PartialBy<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>