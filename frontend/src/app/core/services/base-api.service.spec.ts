import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Injectable } from '@angular/core';
import { BaseApiService } from './base-api.service';

interface TestItem {
  id: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
class TestApiService extends BaseApiService<TestItem> {
  protected endpoint = 'test';
}

describe('BaseApiService', () => {
  let service: TestApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TestApiService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(TestApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
