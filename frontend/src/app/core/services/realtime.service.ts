import { Injectable, inject, PLATFORM_ID, NgZone } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Subject, Observable, filter } from 'rxjs';
import { environment } from '../../../environments/environment';
import { TokenService } from './auth/token.service';

export interface RealTimeEvent {
  eventType: string;
  referenceId?: string;
  title?: string;
  message?: string;
  customerName?: string;
  nomorPengajuan?: string;
  targetRoles?: string[];
  timestamp?: string;
}

@Injectable({
  providedIn: 'root',
})
export class RealTimeService {
  private platformId = inject(PLATFORM_ID);
  private tokenService = inject(TokenService);
  private ngZone = inject(NgZone);

  private eventSource: EventSource | null = null;
  private eventSubject = new Subject<RealTimeEvent>();
  private isConnecting = false;
  private reconnectTimer: any = null;

  /** Stream of all raw real-time events */
  public readonly events$: Observable<RealTimeEvent> = this.eventSubject.asObservable();

  /** Stream filtered for KYC events (Pendaftaran Baru & Revisi Dokumen) */
  public readonly kycUpdates$: Observable<RealTimeEvent> = this.events$.pipe(
    filter((e) => e.eventType === 'KYC_SUBMITTED' || e.eventType === 'KYC_REVISED')
  );

  /** Stream filtered for Marketing loan events (Pengajuan Baru, Revisi Berkas, Pencairan Selesai) */
  public readonly loanMarketingUpdates$: Observable<RealTimeEvent> = this.events$.pipe(
    filter((e) =>
      e.eventType === 'LOAN_SUBMITTED' ||
      e.eventType === 'LOAN_REVISED' ||
      e.eventType === 'LOAN_DISBURSED' ||
      e.eventType === 'LOAN_REVIEWED'
    )
  );

  /** Stream filtered for Branch Manager loan events (Lolos Review Marketing, Pencairan Selesai) */
  public readonly loanBmUpdates$: Observable<RealTimeEvent> = this.events$.pipe(
    filter((e) => e.eventType === 'LOAN_READY_FOR_BM' || e.eventType === 'LOAN_DISBURSED')
  );

  /** Stream filtered for Backoffice Pencairan events (Disetujui BM & Siap Dicairkan) */
  public readonly loanPencairanUpdates$: Observable<RealTimeEvent> = this.events$.pipe(
    filter((e) => e.eventType === 'LOAN_READY_FOR_DISBURSEMENT')
  );

  constructor() {
    if (isPlatformBrowser(this.platformId)) {
      this.connect();
    }
  }

  /**
   * Connect to the Server-Sent Events stream
   */
  public connect(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    if (this.eventSource || this.isConnecting) return;

    const token = this.tokenService.getAccessToken();
    if (!token) {
      // User is not logged in yet; retry when session is available
      this.scheduleReconnect(5000);
      return;
    }

    this.isConnecting = true;
    const base = environment.apiUrl.replace(/\/+$/, '');
    const streamUrl = `${base}/realtime/stream?token=${encodeURIComponent(token)}`;

    try {
      this.eventSource = new EventSource(streamUrl);

      const eventTypes = [
        'init',
        'KYC_SUBMITTED',
        'KYC_REVISED',
        'LOAN_SUBMITTED',
        'LOAN_REVISED',
        'LOAN_READY_FOR_BM',
        'LOAN_READY_FOR_DISBURSEMENT',
        'LOAN_DISBURSED',
        'LOAN_REVIEWED',
        'LOAN_REJECTED_BY_BM',
      ];

      eventTypes.forEach((type) => {
        this.eventSource?.addEventListener(type, (evt: MessageEvent) => {
          this.ngZone.run(() => {
            try {
              const data: RealTimeEvent = JSON.parse(evt.data);
              if (data && data.eventType !== 'CONNECTED') {
                this.eventSubject.next(data);
              }
            } catch (err) {
              console.warn('[RealTimeService] Error parsing SSE payload:', err);
            }
          });
        });
      });

      this.eventSource.onopen = () => {
        this.isConnecting = false;
      };

      this.eventSource.onerror = (err) => {
        this.isConnecting = false;
        this.disconnect();
        // Browser or custom reconnect
        this.scheduleReconnect(8000);
      };
    } catch (e) {
      this.isConnecting = false;
      this.scheduleReconnect(10000);
    }
  }

  /**
   * Close the active SSE stream
   */
  public disconnect(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
    this.isConnecting = false;
  }

  private scheduleReconnect(delayMs: number): void {
    if (this.reconnectTimer) clearTimeout(this.reconnectTimer);
    this.reconnectTimer = setTimeout(() => {
      this.connect();
    }, delayMs);
  }
}
