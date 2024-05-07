import { TestBed } from '@angular/core/testing';

import { EnumService } from './enum.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('EnumService', () => {
  let service: EnumService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(EnumService);

  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
