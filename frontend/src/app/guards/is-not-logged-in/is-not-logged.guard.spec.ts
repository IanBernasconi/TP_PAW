import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { IsNotLoggedGuard } from './is-not-logged.guard';

describe('isNotLoggedGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => IsNotLoggedGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
