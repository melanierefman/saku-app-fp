import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Public pages
  {
    path: '',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'landing-page',
    renderMode: RenderMode.Prerender,
  },
  {
    path: 'login',
    renderMode: RenderMode.Client,
  },
  {
    path: 'forgot-password',
    renderMode: RenderMode.Client,
  },
  // All internal dashboard & master data routes rendered strictly in Client SPA mode
  {
    path: '**',
    renderMode: RenderMode.Client,
  },
];
