import { Directive, ElementRef } from '@angular/core';

// https://www.neoito.com/blog/how-to-maintain-scroll-position-in-angular-chat-app/

@Directive({
  selector: '[scrollDirective]'
})
export class ScrollDirective {
  previousScrollHeightMinusTop: number;
  readyFor: string;
  toReset = false;

  constructor(public elementRef: ElementRef) {
    this.previousScrollHeightMinusTop = 0;
    this.readyFor = 'up';
    this.restore();
  }

  reset() {
    this.previousScrollHeightMinusTop = 0;
    this.readyFor = 'up';

    this.elementRef.nativeElement.scrollTop = this.elementRef.nativeElement.scrollHeight;
  }

  restore() {
    if (this.toReset) {
      if (this.readyFor === 'up') {
        this.elementRef.nativeElement.scrollTop =
          this.elementRef.nativeElement.scrollHeight -
          this.previousScrollHeightMinusTop;
      }
      this.toReset = false;
    }
  }

  prepareFor(direction: string) {
    this.toReset = true;
    this.readyFor = direction || 'up';
    this.elementRef.nativeElement.scrollTop = !this.elementRef.nativeElement.scrollTop // check for scrollTop is zero or not
      ? this.elementRef.nativeElement.scrollTop + 1
      : this.elementRef.nativeElement.scrollTop;
    this.previousScrollHeightMinusTop =
      this.elementRef.nativeElement.scrollHeight - this.elementRef.nativeElement.scrollTop;
    // the current position is stored before new messages are loaded
  }
}