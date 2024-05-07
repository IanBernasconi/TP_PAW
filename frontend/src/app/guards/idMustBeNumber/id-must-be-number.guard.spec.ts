import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { IdMustBeNumberGuard } from './id-must-be-number.guard';

describe('idMustBeNumberGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => IdMustBeNumberGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
