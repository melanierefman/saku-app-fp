import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { MenuService } from './menu.service';

describe('MenuService', () => {
    let service: MenuService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                MenuService,
                provideHttpClient(),
                provideHttpClientTesting(),
            ],
        });
        service = TestBed.inject(MenuService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });
});
