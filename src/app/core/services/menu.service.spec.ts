import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { MenuService } from './menu.service';
import { Menu, MenuRequest } from '../models/menu.model';
import { environment } from '../../../environments/environment';

describe('MenuService', () => {
    let service: MenuService;
    let httpMock: HttpTestingController;

    const mockMenuRequest: MenuRequest = {
        nama: 'Menu Test',
        path: 'menu-test',
        status: true,
    };

    const mockMenu: Menu = {
        id: '1',
        nama: 'Menu Test',
        path: 'menu-test',
        status: true,
        createdDate: '2022-01-01',
        updatedDate: '2022-01-01',
    };

    const mockApiResponse = {
        data: mockMenu,
        message: 'Data berhasil ditambahkan',
        statusCode: 200,
    };

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                MenuService,
                provideHttpClient(),
                provideHttpClientTesting(),
            ],
        });

        service = TestBed.inject(MenuService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        // Memastikan tidak ada request HTTP yang belum ter-handle
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    // Unit Test: Create Menu (Positif)
    it('should send POST request to create menu and return created data', () => {
        service.create(mockMenuRequest).subscribe((result) => {
            expect(result).toEqual(mockMenu);
            expect(result.id).toBe('1');
            expect(result.nama).toBe('Menu Test');
            expect(result.status).toBe(true);
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/master/menu`);

        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(mockMenuRequest);
        req.flush(mockApiResponse);
    });

    // Unit Test: Create Menu (Negatif - Nama tidak ada)
    it('should return error when trying to create menu with no name', () => {
        const invalidMenuRequest: MenuRequest = {
            ...mockMenuRequest,
            nama: '',
        };

        const mockErrorResponse = {
            message: 'Nama menu wajib diisi',
            statusCode: 400,
        };

        service.create(invalidMenuRequest).subscribe({
            next: () => {
                expect.fail('Should not have been called');
            },
            error: (err) => {
                expect(err.status).toBe(400);
                expect(err.error.message).toBe('Nama menu wajib diisi');
            },
        });

        const req = httpMock.expectOne(`${environment.apiUrl}/master/menu`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual(invalidMenuRequest);

        req.flush(mockErrorResponse, {
            status: 400,
            statusText: 'Bad Request',
        });
    });
});
