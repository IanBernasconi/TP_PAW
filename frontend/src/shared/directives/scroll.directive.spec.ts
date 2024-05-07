import { ElementRef } from '@angular/core';
import { ScrollDirective } from './scroll.directive';

describe('ScrollDirective', () => {
  it('should create an instance', () => {
    const el = new ElementRef(document.createElement('div'));
    const directive = new ScrollDirective(el);
    expect(directive).toBeTruthy();
  });
});
