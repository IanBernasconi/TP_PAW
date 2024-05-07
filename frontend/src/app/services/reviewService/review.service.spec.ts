import { TestBed } from '@angular/core/testing';

import { ReviewService } from './review.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ReviewService', () => {
  let service: ReviewService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ReviewService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
