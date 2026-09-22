package com.bcafinance.backend_saku.features.public_api.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@Hidden // Sembunyikan controller internal docs dari daftar API
public class ScalarDocsController {

    // Pengalihan otomatis dari rute swagger-ui ke Scalar Docs
    @GetMapping(value = {"/swagger-ui.html", "/swagger-ui", "/swagger-ui/index.html"})
    public RedirectView redirectToScalarDocs() {
        return new RedirectView("/docs");
    }

    // Tampilkan dokumentasi interaktif Scalar API Reference untuk SAKU App
    @GetMapping(value = {"/docs", "/scalar"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getDocs() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <title>SAKU App - API Reference</title>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <link rel="icon" type="image/svg+xml" href="https://scalar.com/favicon.svg" />
                    <style>
                      body {
                        margin: 0;
                        padding: 0;
                      }
                    </style>
                  </head>
                  <body>
                    <script
                      id="api-reference"
                      data-url="/v3/api-docs"
                      data-configuration='{
                        "theme": "purple",
                        "darkMode": true,
                        "layout": "modern",
                        "showSidebar": true,
                        "hideModels": false,
                        "defaultHttpClient": {
                          "targetKey": "javascript",
                          "clientKey": "fetch"
                        }
                      }'></script>
                    <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
                  </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }
}
