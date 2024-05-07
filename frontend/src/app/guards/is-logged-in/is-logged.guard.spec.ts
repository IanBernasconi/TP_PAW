import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { IsLoggedGuard } from './is-logged.guard';

describe('isLoggedInGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => IsLoggedGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
