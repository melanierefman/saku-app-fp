import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BranchManagerApprovalService } from './branch-manager-approval.service';

describe('BranchManagerApprovalService', () => {
  let service: BranchManagerApprovalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        BranchManagerApprovalService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(BranchManagerApprovalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
