package com.bcafinance.backend_saku.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenAPIConfig {

    // Konfigurasi metadata OpenAPI, otentikasi JWT Bearer, dan pengelompokan modul (Tag Groups) untuk Scalar
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        // 1. Pengelompokan Modul (Tag Groups) untuk Sidebar Scalar Docs
        List<Map<String, Object>> tagGroups = List.of(
                Map.of(
                        "name", "Autentikasi & Public",
                        "tags", List.of("Auth Customer", "Auth Karyawan", "Auth OTP", "Public API")
                ),
                Map.of(
                        "name", "Customer / Nasabah",
                        "tags", List.of("Customer - Registrasi", "Customer - Profil", "Customer - Pengajuan Pinjaman", "Customer - Notifikasi")
                ),
                Map.of(
                        "name", "Marketing",
                        "tags", List.of("Marketing - Dashboard", "Marketing - Pengajuan Pinjaman")
                ),
                Map.of(
                        "name", "Branch Manager",
                        "tags", List.of("Branch Manager - Dashboard", "Branch Manager - Persetujuan")
                ),
                Map.of(
                        "name", "Backoffice",
                        "tags", List.of("Backoffice - Dashboard", "Backoffice - Verifikasi Customer", "Backoffice - Pencairan Pinjaman")
                ),
                Map.of(
                        "name", "Superadmin",
                        "tags", List.of(
                                "Superadmin - Dashboard",
                                "Superadmin - Monitoring Pengajuan",
                                "Superadmin - Kelola Karyawan",
                                "Superadmin - Master Cabang",
                                "Superadmin - Konfigurasi Plafond",
                                "Superadmin - Kelola Menu",
                                "Superadmin - Kelola Permission",
                                "Superadmin - Role & Hak Akses",
                                "Superadmin - Audit Log"
                        )
                ),
                Map.of(
                        "name", "Scoring Engine",
                        "tags", List.of("Scoring Engine")
                )
        );

        // 2. Definisi Urutan Tag Terstruktur
        List<Tag> orderedTags = List.of(
                // Autentikasi & Public
                new Tag().name("Auth Customer").description("Autentikasi nasabah SAKU App (Login, Lupa Password, Refresh Token, Logout)"),
                new Tag().name("Auth Karyawan").description("Autentikasi staf internal SAKU App (Superadmin, Backoffice, Marketing, Branch Manager)"),
                new Tag().name("Auth OTP").description("Pengiriman dan verifikasi One-Time Password (OTP) via Email / SMS"),
                new Tag().name("Public API").description("Informasi publik dan kalkulator simulasi kredit SAKU App tanpa login"),

                // Customer
                new Tag().name("Customer - Registrasi").description("Alur pendaftaran nasabah baru SAKU App (Cek NIK/HP, Step 1-5 Registrasi)"),
                new Tag().name("Customer - Profil").description("Pengelolaan profil nasabah SAKU App (Profil, Rekening, Domisili, Pekerjaan, Ganti Password, KYC)"),
                new Tag().name("Customer - Pengajuan Pinjaman").description("Alur pengajuan kredit nasabah SAKU App (Step 1-2, Riwayat, Detail, Jadwal Angsuran)"),
                new Tag().name("Customer - Notifikasi").description("Layanan notifikasi nasabah SAKU App (List, Unread Count, Detail, Mark as Read)"),

                // Marketing
                new Tag().name("Marketing - Dashboard").description("Statistik performa, antrean review, dan volume pinjaman staf Marketing"),
                new Tag().name("Marketing - Pengajuan Pinjaman").description("Evaluasi dan review pengajuan pinjaman nasabah oleh staf Marketing"),

                // Branch Manager
                new Tag().name("Branch Manager - Dashboard").description("Statistik metrik keputusan approval, volume kredit, dan performa cabang Branch Manager"),
                new Tag().name("Branch Manager - Persetujuan").description("Persetujuan akhir pengajuan pinjaman kredit oleh Branch Manager"),

                // Backoffice
                new Tag().name("Backoffice - Dashboard").description("Statistik antrean verifikasi KYC dan rekapitulasi pencairan pinjaman Backoffice"),
                new Tag().name("Backoffice - Verifikasi Customer").description("Verifikasi identitas dan berkas KYC nasabah baru oleh tim Backoffice"),
                new Tag().name("Backoffice - Pencairan Pinjaman").description("Pencairan dana kredit yang telah disetujui BM ke rekening nasabah"),

                // Superadmin
                new Tag().name("Superadmin - Dashboard").description("Statistik keseluruhan metrik sistem, pengguna, cabang, dan volume pinjaman SAKU App"),
                new Tag().name("Superadmin - Monitoring Pengajuan").description("Pemantauan komprehensif seluruh siklus hidup pengajuan pinjaman nasabah"),
                new Tag().name("Superadmin - Kelola Karyawan").description("Manajemen data akun staf internal, role, dan cabang penempatan SAKU App"),
                new Tag().name("Superadmin - Master Cabang").description("Manajemen data master kantor cabang SAKU App (CRUD & Status Aktif)"),
                new Tag().name("Superadmin - Konfigurasi Plafond").description("Pengaturan batas pinjaman, tenor, suku bunga, dan biaya admin produk pinjaman"),
                new Tag().name("Superadmin - Kelola Menu").description("Manajemen struktur hierarki dan navigasi menu sistem SAKU App"),
                new Tag().name("Superadmin - Kelola Permission").description("Manajemen hak akses granular permission pada fitur-fitur SAKU App"),
                new Tag().name("Superadmin - Role & Hak Akses").description("Manajemen data role dan penugasan izin (permissions) pada SAKU App"),
                new Tag().name("Superadmin - Audit Log").description("Pemantauan jejak audit aktivitas dan perubahan data dalam sistem SAKU App"),

                // Scoring
                new Tag().name("Scoring Engine").description("Engine perhitungan skor kredit dan batas kelayakan plafond nasabah")
        );

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("SAKU App API")
                        .version("1.0.0")
                        .description("Dokumentasi REST API Sistem Pengajuan & Pengelolaan Kredit SAKU App")
                        .contact(new Contact()
                                .name("SAKU Team")
                                .email("support@sakuapp.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://sakuapp.com")))
                .servers(List.of(
                        new Server().url("/").description("Current Server URL")
                ))
                .tags(orderedTags)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Masukkan Token JWT (tanpa kata 'Bearer ')")));

        openAPI.addExtension("x-tagGroups", tagGroups);

        return openAPI;
    }

    // Injeksi otomatis response codes standar (400, 401, 403, 404, 500) dengan statusCode yang tepat
    @Bean
    public OpenApiCustomizer globalResponsesCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }

            // Buat Content & Schema terpisah untuk masing-masing HTTP Status Code
            Content content400 = createErrorContent(openApi, 400, "ErrorResponse400", "Format input tidak valid atau validasi gagal");
            Content content401 = createErrorContent(openApi, 401, "ErrorResponse401", "Autentikasi diperlukan: Token JWT tidak valid atau sudah kedaluwarsa");
            Content content403 = createErrorContent(openApi, 403, "ErrorResponse403", "Akses ditolak: Anda tidak memiliki izin untuk mengakses resource ini");
            Content content404 = createErrorContent(openApi, 404, "ErrorResponse404", "Data atau resource yang diminta tidak ditemukan");
            Content content500 = createErrorContent(openApi, 500, "ErrorResponse500", "Terjadi kesalahan internal pada server");

            // Terapkan ke setiap operasi API di seluruh path
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((pathString, pathItem) -> {
                    pathItem.readOperations().forEach(operation -> {
                        ApiResponses responses = operation.getResponses();
                        if (responses == null) {
                            responses = new ApiResponses();
                            operation.setResponses(responses);
                        }

                        // 400 Bad Request
                        if (!responses.containsKey("400")) {
                            responses.addApiResponse("400", new ApiResponse()
                                    .description("Bad Request - Validasi payload gagal atau pelanggaran aturan bisnis")
                                    .content(content400));
                        }

                        // 401 Unauthorized
                        if (!responses.containsKey("401")) {
                            responses.addApiResponse("401", new ApiResponse()
                                    .description("Unauthorized - Token JWT tidak disertakan, tidak valid, atau kedaluwarsa")
                                    .content(content401));
                        }

                        // 403 Forbidden
                        if (!responses.containsKey("403")) {
                            responses.addApiResponse("403", new ApiResponse()
                                    .description("Forbidden - Hak akses atau role pengguna tidak mencukupi")
                                    .content(content403));
                        }

                        // 404 Not Found (Untuk endpoint dengan path variable ID)
                        if (pathString.contains("{") && !responses.containsKey("404")) {
                            responses.addApiResponse("404", new ApiResponse()
                                    .description("Not Found - Resource dengan ID yang ditentukan tidak ditemukan")
                                    .content(content404));
                        }

                        // 500 Internal Server Error
                        if (!responses.containsKey("500")) {
                            responses.addApiResponse("500", new ApiResponse()
                                    .description("Internal Server Error - Terjadi kesalahan internal pada server")
                                    .content(content500));
                        }
                    });
                });
            }
        };
    }

    private Content createErrorContent(OpenAPI openApi, int statusCode, String schemaName, String message) {
        Schema<?> schema = new ObjectSchema()
                .name(schemaName)
                .description("Format response error " + statusCode + " SAKU App")
                .addProperty("statusCode", new IntegerSchema().example(statusCode))
                .addProperty("message", new StringSchema().example(message))
                .addProperty("data", new ObjectSchema().nullable(true).example(null))
                .addProperty("errors", new ObjectSchema().nullable(true).example(null));

        openApi.getComponents().addSchemas(schemaName, schema);

        Map<String, Object> exampleValue = new LinkedHashMap<>();
        exampleValue.put("statusCode", statusCode);
        exampleValue.put("message", message);
        exampleValue.put("data", null);
        exampleValue.put("errors", null);

        MediaType mediaType = new MediaType()
                .schema(new Schema<>().$ref("#/components/schemas/" + schemaName))
                .example(exampleValue);

        return new Content().addMediaType("application/json", mediaType);
    }
}
