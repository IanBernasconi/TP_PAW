import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { IsProviderGuard } from './is-provider.guard';

describe('IsProviderGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => IsProviderGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
