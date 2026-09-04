import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { PlafondService } from './plafond.service';

describe('PlafondService', () => {
    let service: PlafondService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                PlafondService,
                provideHttpClient(),
                provideHttpClientTesting(),
            ],
        });
        service = TestBed.inject(PlafondService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});

