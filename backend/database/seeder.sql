CREATE SCHEMA IF NOT EXISTS mel_saku;
SET search_path TO mel_saku;
--
-- PostgreSQL database dump
--

\restrict Wo4hmctlpADyFDlB4jcfCtaITzeBpxivWfyKTT93HRldLo44wP1molFvaGZxHcZ

-- Dumped from database version 16.14 (Debian 16.14-1.pgdg13+1)
-- Dumped by pg_dump version 16.14 (Debian 16.14-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: mst_customer; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_customer VALUES ('3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '3374011809970004', 'Bagus Wijaya', 'bagus_sby', 'bagus.sby@example.com', '081223344554', '$2a$10$dbHzyXZGqD3U51ubn9QdaOExb/cDlDm9fM/Y6N4EWaxR4wkKOo00W', 'Bagus Wijaya', 'BCA', '1234567804', true, '2026-08-27 08:50:52.65604', '2026-08-27 09:16:22.849663', NULL, NULL);
INSERT INTO mel_saku.mst_customer VALUES ('b073b982-ce00-4562-b488-eaa0599ec19a', '3174025006950002', 'Siti Nurhaliza', 'siti_bandung', 'siti.bandung@example.com', '081223344552', '$2a$10$h8AnMn/tD9P0sEgShcFxvu1dzEF6XOrT53O2hEkgZYnlQkK7DKY/y', 'Siti Nurhaliza', 'BCA', '1234567802', true, '2026-08-27 08:46:28.996618', '2026-08-27 09:28:57.355667', NULL, NULL);
INSERT INTO mel_saku.mst_customer VALUES ('b025e380-21a8-46cf-8825-17a64e0e3cbd', '3171012345670001', 'Melanie Refman', 'melanie', 'melanie@example.com', '081234567899', '$2a$10$FDSkLbO/VPHkvi5PIoZfO.qJe5onwDdR7lUf0bPU4CZiirSj7ATI2', 'Melanie Refman', 'BCA', '1234567860', true, '2026-08-19 15:54:09.956339', '2026-09-17 16:32:52.45177', NULL, 'f6P1I0v6TN2HOfQS9SvHDY:APA91bFalsc812BCQHdr5B9E4RvqvAxrz5v-OzHDCRWb56MD4ftLwgivxKvSMz2Ew106I77QjEqpKE2yVdUOLst7gnuvnSfuiSJMdOUcOpMr6xXHZ3pAQ7o');


--
-- Data for Name: mst_alamat_customer; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_alamat_customer VALUES ('5c92e5ef-f204-48ab-a2ea-87a05c2eca95', 'KTP', 'Jl. Merdeka No. 10', '001', '002', 'Menteng', 'Menteng', 'Jakarta Pusat', 'DKI Jakarta', '10310', '2026-08-19 15:54:38.559499', '2026-08-19 15:54:38.559499', 'b025e380-21a8-46cf-8825-17a64e0e3cbd');
INSERT INTO mel_saku.mst_alamat_customer VALUES ('311d6eb3-6ced-4a87-aee0-1244dea3692c', 'DOMISILI', 'Jl. Sudirman No. 25', '003', '004', 'Karet', 'Setiabudi', 'Jakarta Selatan', 'DKI Jakarta', '12920', '2026-08-19 15:54:38.598181', '2026-08-19 15:54:38.598181', 'b025e380-21a8-46cf-8825-17a64e0e3cbd');
INSERT INTO mel_saku.mst_alamat_customer VALUES ('0d22be32-9588-4bfe-a948-fbaabea3c609', 'KTP', 'Jl. Tebet Barat Dalam No. 45, Tebet', '05', '02', 'Tebet Barat', 'Tebet', 'Kota Jakarta Selatan', 'DKI Jakarta', '12810', '2026-08-27 08:47:38.939047', '2026-08-27 08:47:38.939047', 'b073b982-ce00-4562-b488-eaa0599ec19a');
INSERT INTO mel_saku.mst_alamat_customer VALUES ('9c661bf9-ac17-4b2d-a31b-0a83bad57ea8', 'DOMISILI', 'Jl. Buah Batu No. 120, Lengkong', '04', '09', 'Cijagra', 'Lengkong', 'Kota Bandung', 'Jawa Barat', '40265', '2026-08-27 08:47:38.994192', '2026-08-27 08:47:38.994192', 'b073b982-ce00-4562-b488-eaa0599ec19a');
INSERT INTO mel_saku.mst_alamat_customer VALUES ('93a95204-6bf8-4f1d-8e56-c557a36fa020', 'KTP', 'Jl. Pandanaran No. 50, Semarang Selatan', '03', '01', 'Pleburan', 'Semarang Selatan', 'Kota Semarang', 'Jawa Tengah', '50241', '2026-08-27 08:51:28.54049', '2026-08-27 08:51:28.54049', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0');
INSERT INTO mel_saku.mst_alamat_customer VALUES ('bca95204-b308-4dc7-97ec-00e96568b51c', 'DOMISILI', 'Jl. Rungkut Madya No. 70, Rungkut', '06', '04', 'Rungkut Kidul', 'Rungkut', 'Kota Surabaya', 'Jawa Timur', '60293', '2026-08-27 08:51:28.544189', '2026-08-27 08:51:28.544189', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0');


--
-- Data for Name: mst_cabang; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_cabang VALUES ('3530eedc-0896-4411-9e91-7ec905686560', 'Cabang Bandung', 'Bandung', false, true, '2026-08-14 14:06:26.424142', '2026-08-14 14:06:26.424142');
INSERT INTO mel_saku.mst_cabang VALUES ('634b19a1-269b-461f-842f-bda1ddc9a65d', 'Cabang Surabaya', 'Surabaya', false, true, '2026-08-14 14:06:26.424142', '2026-08-14 14:06:26.424142');
INSERT INTO mel_saku.mst_cabang VALUES ('51332aa8-9b2f-49dc-a7d4-84ae687fe2ce', 'Kantor Pusat (Jakarta)', 'Jakarta', true, true, '2026-08-14 14:06:26.424142', '2026-09-01 09:25:25.498824');


--
-- Data for Name: mst_role; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_role VALUES ('4e2965d9-ee47-47bb-9f42-25c3fae591c6', 'SUPERADMIN', true, '2026-08-14 14:01:29.800939', '2026-08-26 14:26:44.076926');
INSERT INTO mel_saku.mst_role VALUES ('132c88e5-9b2c-49e0-bcb9-7343be2fd72c', 'MARKETING', true, '2026-08-14 14:01:29.800939', '2026-08-26 14:27:47.730323');
INSERT INTO mel_saku.mst_role VALUES ('48192c80-835b-4b93-b5af-9b96add0f372', 'BRANCHMANAGER', true, '2026-08-14 14:01:29.800939', '2026-08-26 14:28:28.880274');
INSERT INTO mel_saku.mst_role VALUES ('2b4f9406-148e-44c4-b56d-32fdb94b0908', 'BACKOFFICE', true, '2026-08-14 14:01:29.800939', '2026-08-26 14:29:04.612998');


--
-- Data for Name: mst_karyawan; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_karyawan VALUES ('3e696e6a-1b63-4386-b24c-c59194723ee7', 'Marketing SAKU', 'marketing@saku.com', 'marketing', '$2a$10$EaGnpF/qAD/1hIFO4INdCuZZ5kLPRK48RFDI0wH4j2n7BFJdOrvOu', true, '2026-08-18 20:45:13.013408', '2026-08-27 08:21:46.344', '132c88e5-9b2c-49e0-bcb9-7343be2fd72c', '51332aa8-9b2f-49dc-a7d4-84ae687fe2ce');
INSERT INTO mel_saku.mst_karyawan VALUES ('fabb76ed-d9c7-488f-b248-63b6e1f677ac', 'Marketing Bandung', 'marketingbdg@example.com', 'marketingbdg', '$2a$10$KRoqig1TTRt0JUzsFEo2qe1tC5Qyp/qNJLz02B7g0O1fUz4nO7h0C', true, '2026-08-27 08:22:00.874', '2026-08-27 08:22:00.885', '132c88e5-9b2c-49e0-bcb9-7343be2fd72c', '3530eedc-0896-4411-9e91-7ec905686560');
INSERT INTO mel_saku.mst_karyawan VALUES ('221d388e-2310-450f-92d4-a34f8bd615cd', 'Marketing Surabaya', 'marketingsby@example.com', 'marketingsby', '$2a$10$aBXUBNnRcGxx3WRAqO4o7.dHsCEjxFcJSma9magY2JUhCLaufvdw6', true, '2026-08-27 08:22:11.134', '2026-08-27 08:22:11.154', '132c88e5-9b2c-49e0-bcb9-7343be2fd72c', '634b19a1-269b-461f-842f-bda1ddc9a65d');
INSERT INTO mel_saku.mst_karyawan VALUES ('18206675-7990-4980-a5a2-726ed3532c68', 'Branch Manager SAKU', 'bm@saku.com', 'bm', '$2a$10$AVqKOWKDstI4fisUxZutm.yQONLC8/RcujOMVdU8LE/Zgk44lH3zG', true, '2026-08-18 20:45:13.013408', '2026-08-27 08:25:18.867', '48192c80-835b-4b93-b5af-9b96add0f372', '51332aa8-9b2f-49dc-a7d4-84ae687fe2ce');
INSERT INTO mel_saku.mst_karyawan VALUES ('436babe9-ba58-465c-8f48-a1945fd83642', 'Branch Manager Bandung', 'bmbdg@example.com', 'bmbdg', '$2a$10$jIsYFWTPtHjcnZrKd7FP8./KuRHWDs0q2IMZ.VgG1LE5YVZubfyBq', true, '2026-08-27 08:25:26.967', '2026-08-27 08:25:26.975', '48192c80-835b-4b93-b5af-9b96add0f372', '3530eedc-0896-4411-9e91-7ec905686560');
INSERT INTO mel_saku.mst_karyawan VALUES ('1ab16242-4251-49ef-bc6f-aaf3195d82fd', 'Branch Manager Surabaya', 'bmsby@example.com', 'bmsby', '$2a$10$7UbNpYEiEM7uij2bQ/r74e9hQ8BJoD5lJWwu9ObMqvo2IiyiABB66', true, '2026-08-27 08:25:34.674', '2026-08-27 08:25:34.683', '48192c80-835b-4b93-b5af-9b96add0f372', '634b19a1-269b-461f-842f-bda1ddc9a65d');
INSERT INTO mel_saku.mst_karyawan VALUES ('2c25e863-163e-474c-bc36-b45fe378ac04', 'Super Admin', 'superadmin@saku.com', 'superadmin', '$2a$12$ApibLrJE81.ztEaQH6MKTOrA6ykKU/VGZhX5DzkBE.eF.cI9b8dYi', true, '2026-08-18 20:45:13.013408', '2026-08-18 20:45:13.013408', '4e2965d9-ee47-47bb-9f42-25c3fae591c6', '51332aa8-9b2f-49dc-a7d4-84ae687fe2ce');
INSERT INTO mel_saku.mst_karyawan VALUES ('417a84b4-192b-43a8-9461-f5b8fc8d4eb6', 'Back Office SAKU', 'backoffice@saku.com', 'backoffice', '$2a$12$pPQoMTi34GNK.uYijYGJD.nN8Y.d84fOy21VNbSGEO7wm2PmK4vei', true, '2026-08-18 20:45:13.013408', '2026-08-18 20:45:13.013408', '2b4f9406-148e-44c4-b56d-32fdb94b0908', '51332aa8-9b2f-49dc-a7d4-84ae687fe2ce');


--
-- Data for Name: mst_menu; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000001', 'Dashboard', '/dashboard', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000002', 'Review Pengajuan Pinjaman', '/pengajuan-pinjaman', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000003', 'Persetujuan Pinjaman', '/persetujuan-pinjaman', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000004', 'Verifikasi KYC Customer', '/verifikasi-customer', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000005', 'Pencairan Dana', '/pencairan', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000006', 'Role & Hak Akses', '/rbac/role-access', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000007', 'Master Role', '/rbac/role', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000008', 'Master Permission', '/rbac/permission', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000009', 'Master Menu Navigasi', '/rbac/menu', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000010', 'Master Karyawan Internal', '/master/karyawan', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000011', 'Master Kantor Cabang', '/master/cabang', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000012', 'Master Produk Plafond', '/master/plafond', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000013', 'Monitoring Pengajuan', '/monitoring/pengajuan', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');
INSERT INTO mel_saku.mst_menu VALUES ('10000000-0000-0000-0000-000000000014', 'Audit Log Aktivitas', '/monitoring/audit-log', true, '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603');


--
-- Data for Name: mst_permission; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000001', 'Lihat Dashboard', 'DASHBOARD', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000001');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000002', 'Lihat Pengajuan Marketing', 'PENGAJUAN', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000002');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000003', 'Review Pengajuan Marketing', 'PENGAJUAN', 'REVIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000002');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000004', 'Lihat Persetujuan BM', 'PERSETUJUAN', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000003');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000005', 'Setujui & Tolak Pengajuan BM', 'PERSETUJUAN', 'APPROVE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000003');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000006', 'Lihat Daftar KYC Customer', 'CUSTOMER', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000004');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000008', 'Lihat Daftar Pencairan', 'PENCAIRAN', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000005');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000010', 'Lihat Role Access', 'ROLE_ACCESS', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000006');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000011', 'Kelola Role Access', 'ROLE_ACCESS', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000006');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000012', 'Lihat Data Role', 'ROLE', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000007');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000013', 'Tambah Data Role', 'ROLE', 'CREATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000007');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000014', 'Ubah Data Role', 'ROLE', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000007');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000015', 'Hapus Data Role', 'ROLE', 'DELETE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000007');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000016', 'Lihat Permission', 'PERMISSION', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000008');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000017', 'Tambah Permission', 'PERMISSION', 'CREATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000008');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000018', 'Ubah Permission', 'PERMISSION', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000008');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000019', 'Hapus Permission', 'PERMISSION', 'DELETE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000008');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000020', 'Lihat Menu', 'MENU', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000009');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000021', 'Tambah Menu', 'MENU', 'CREATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000009');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000022', 'Ubah Menu', 'MENU', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000009');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000023', 'Hapus Menu', 'MENU', 'DELETE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000009');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000024', 'Lihat Karyawan', 'KARYAWAN', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000010');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000025', 'Tambah Karyawan', 'KARYAWAN', 'CREATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000010');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000026', 'Ubah Karyawan', 'KARYAWAN', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000010');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000027', 'Hapus Karyawan', 'KARYAWAN', 'DELETE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000010');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000028', 'Lihat Cabang', 'CABANG', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000011');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000029', 'Tambah Cabang', 'CABANG', 'CREATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000011');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000030', 'Ubah Cabang', 'CABANG', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000011');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000031', 'Hapus Cabang', 'CABANG', 'DELETE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000011');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000032', 'Lihat Plafond', 'PLAFOND', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000012');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000033', 'Tambah Plafond', 'PLAFOND', 'CREATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000012');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000034', 'Ubah Plafond', 'PLAFOND', 'UPDATE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000012');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000035', 'Hapus Plafond', 'PLAFOND', 'DELETE', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000012');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000036', 'Lihat Monitoring Pengajuan', 'MONITORING', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000013');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000037', 'Lihat Audit Log', 'AUDIT_LOG', 'VIEW', '2026-08-31 20:17:27.412603', '2026-08-31 20:17:27.412603', '10000000-0000-0000-0000-000000000014');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000007', 'Verifikasi Dokumen KYC', 'CUSTOMER', 'VERIFY', '2026-08-31 20:17:27.412603', '2026-08-31 20:21:03.25804', '10000000-0000-0000-0000-000000000004');
INSERT INTO mel_saku.mst_permission VALUES ('20000000-0000-0000-0000-000000000009', 'Verifikasi & Eksekusi Pencairan', 'PENCAIRAN', 'VERIFY', '2026-08-31 20:17:27.412603', '2026-08-31 20:21:03.25804', '10000000-0000-0000-0000-000000000005');


--
-- Data for Name: mst_plafond; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_plafond VALUES ('8c1a96c8-0f65-4863-979b-e80ce79e748d', 'Plafond Tier 1 - Starter', 3000000.00, 15000000.00, 60, 69, 0.06, 100000.00, true, '2026-08-19 14:47:31.787807', '2026-08-19 14:47:31.787807', 3000000.00, 15000000.00);
INSERT INTO mel_saku.mst_plafond VALUES ('afa17e37-dbcd-416a-a480-7a58b173053e', 'Plafond Tier 2 - Reguler', 10000000.00, 35000000.00, 70, 79, 0.05, 250000.00, true, '2026-08-19 14:47:40.492711', '2026-08-19 14:47:40.492711', 5000000.00, 35000000.00);
INSERT INTO mel_saku.mst_plafond VALUES ('0ca78ac0-0472-406c-a79a-cb979b642d3f', 'Plafond Tier 3 - Prioritas', 25000000.00, 75000000.00, 80, 89, 0.04, 500000.00, true, '2026-08-19 14:47:49.217146', '2026-08-19 14:47:49.217747', 10000000.00, 75000000.00);
INSERT INTO mel_saku.mst_plafond VALUES ('a6c2bd90-6a76-4cb2-9c34-e1320b0f8a52', 'Plafond Tier 4 - Platinum', 50000000.00, 150000000.00, 90, 100, 0.03, 750000.00, true, '2026-08-19 14:47:59.661578', '2026-08-19 14:47:59.661578', 15000000.00, 150000000.00);


--
-- Data for Name: mst_role_permission; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.mst_role_permission VALUES ('3208fb59-9905-4c68-a5ed-0b143e24c448', '20000000-0000-0000-0000-000000000001', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('b4fb39e0-9595-4245-84f8-e59f544dba8f', '20000000-0000-0000-0000-000000000010', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('2553029b-5669-47c1-bcfa-2b157f94ae5a', '20000000-0000-0000-0000-000000000011', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('81d50359-7768-4d4b-a485-11e34c4021bf', '20000000-0000-0000-0000-000000000012', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('4999a910-d29e-4b00-b90c-f081ef6ee0ca', '20000000-0000-0000-0000-000000000013', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('9d4f9ed8-46ca-4c26-ba88-b329c16fe036', '20000000-0000-0000-0000-000000000014', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('e837c748-2ec3-4eaf-baca-0605c730a45b', '20000000-0000-0000-0000-000000000015', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('fbaa8773-9337-4d85-918d-8b103a9780bd', '20000000-0000-0000-0000-000000000016', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('06615159-a74b-42b5-94e0-ebde21f92831', '20000000-0000-0000-0000-000000000017', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('b66616ca-2d03-4cf9-b2a5-be0f83f0d34a', '20000000-0000-0000-0000-000000000018', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('8b29485b-4110-4650-adab-78e4bf01bd74', '20000000-0000-0000-0000-000000000019', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('7cb77437-3e32-444f-af1e-9e3aa735ea1e', '20000000-0000-0000-0000-000000000020', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('72d7a376-9aaa-4f04-b3e3-f88aea00cddc', '20000000-0000-0000-0000-000000000021', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('27d35cff-8d3a-493b-8b56-9e5095c1469a', '20000000-0000-0000-0000-000000000022', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('251dc8bc-1e02-4b80-a633-6faa125c5332', '20000000-0000-0000-0000-000000000023', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('7249bb80-6cea-456a-a49d-0ae898ed9750', '20000000-0000-0000-0000-000000000024', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('e554543d-e936-4c36-be75-a925dba66ca0', '20000000-0000-0000-0000-000000000025', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('5022408a-e148-4de9-aa24-6b07bf39cb2b', '20000000-0000-0000-0000-000000000026', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('cef5d217-f751-46d8-abea-0516de7df58f', '20000000-0000-0000-0000-000000000027', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('f827aeaf-bb8f-4e98-b936-7f675746c3b0', '20000000-0000-0000-0000-000000000028', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('f08319e9-11a0-4b48-a0b9-36cee8b7bde8', '20000000-0000-0000-0000-000000000029', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('df1ebd3f-5e14-455f-987c-20b43ce545f1', '20000000-0000-0000-0000-000000000030', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('007f62e6-5617-4014-abe5-6d3e30b861dd', '20000000-0000-0000-0000-000000000031', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('4ce57efc-dc2c-4570-a3ad-2a0c9646cc9f', '20000000-0000-0000-0000-000000000032', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('1243d7da-0bea-41a3-8aec-cfcc3c972ca7', '20000000-0000-0000-0000-000000000033', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('bd05b0e6-a8ba-416c-9523-2e8063f8d3d8', '20000000-0000-0000-0000-000000000034', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('798676a9-8051-40d9-92d2-d8df2f60da63', '20000000-0000-0000-0000-000000000035', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('f8f2f17c-a612-4217-85d2-95c8a053d337', '20000000-0000-0000-0000-000000000036', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('bda7c3cc-e9d5-4d94-b8d2-49e8acd9ebf7', '20000000-0000-0000-0000-000000000037', '4e2965d9-ee47-47bb-9f42-25c3fae591c6');
INSERT INTO mel_saku.mst_role_permission VALUES ('d13c09a8-51ac-4208-bd3f-da7c39db5da2', '20000000-0000-0000-0000-000000000001', '132c88e5-9b2c-49e0-bcb9-7343be2fd72c');
INSERT INTO mel_saku.mst_role_permission VALUES ('3b847168-5777-4a3e-a9f0-bf62b7305a72', '20000000-0000-0000-0000-000000000002', '132c88e5-9b2c-49e0-bcb9-7343be2fd72c');
INSERT INTO mel_saku.mst_role_permission VALUES ('d4c16c5e-764c-4137-8e3d-4e5ff80eeb5a', '20000000-0000-0000-0000-000000000003', '132c88e5-9b2c-49e0-bcb9-7343be2fd72c');
INSERT INTO mel_saku.mst_role_permission VALUES ('f72731bb-232e-45a4-99ce-95285860448c', '20000000-0000-0000-0000-000000000001', '48192c80-835b-4b93-b5af-9b96add0f372');
INSERT INTO mel_saku.mst_role_permission VALUES ('f46f1f48-6c30-4b6a-a1eb-d5e49bf8d4f9', '20000000-0000-0000-0000-000000000004', '48192c80-835b-4b93-b5af-9b96add0f372');
INSERT INTO mel_saku.mst_role_permission VALUES ('aedd5ab2-29d8-47a4-9089-d9ef5a8eef91', '20000000-0000-0000-0000-000000000005', '48192c80-835b-4b93-b5af-9b96add0f372');
INSERT INTO mel_saku.mst_role_permission VALUES ('69304f74-62ce-4230-8f5a-ed0cabf9ae3f', '20000000-0000-0000-0000-000000000001', '2b4f9406-148e-44c4-b56d-32fdb94b0908');
INSERT INTO mel_saku.mst_role_permission VALUES ('63062653-e95d-45e4-86d3-6e5ea5401ae2', '20000000-0000-0000-0000-000000000006', '2b4f9406-148e-44c4-b56d-32fdb94b0908');
INSERT INTO mel_saku.mst_role_permission VALUES ('5774a540-11ba-4179-8de4-2200a3c58874', '20000000-0000-0000-0000-000000000007', '2b4f9406-148e-44c4-b56d-32fdb94b0908');
INSERT INTO mel_saku.mst_role_permission VALUES ('59fc3626-744c-4131-b132-a79978ace4fe', '20000000-0000-0000-0000-000000000008', '2b4f9406-148e-44c4-b56d-32fdb94b0908');
INSERT INTO mel_saku.mst_role_permission VALUES ('e9511286-9616-4ee5-808a-5b8fa945c499', '20000000-0000-0000-0000-000000000009', '2b4f9406-148e-44c4-b56d-32fdb94b0908');


--
-- Data for Name: trx_scoring_customer; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_scoring_customer VALUES ('57a1741f-4239-4e0a-b75d-d16a413cd47a', 'Senior Software Engineer', 'KARYAWAN_TETAP', 18000000.00, 48, 1500000.00, 96, 'APPROVED', '2026-08-27 08:47:03.30822', '2026-08-27 09:28:57.355128', 'b073b982-ce00-4562-b488-eaa0599ec19a', 'a6c2bd90-6a76-4cb2-9c34-e1320b0f8a52', 'PT Teknologi Bandung Juara');
INSERT INTO mel_saku.trx_scoring_customer VALUES ('af1f4678-4c39-4542-85c4-798070a60213', 'Software Engineer', 'KARYAWAN_TETAP', 8000000.00, 24, 500000.00, 76, 'APPROVED', '2026-08-19 15:54:29.851887', '2026-08-19 15:56:00.089405', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', 'afa17e37-dbcd-416a-a480-7a58b173053e', 'PT Teknologi Indonesia');
INSERT INTO mel_saku.trx_scoring_customer VALUES ('5c626461-fab5-45d3-8e40-3f3ceb4c987d', 'Staff Pemasaran Properti', 'KARYAWAN_KONTRAK', 6000000.00, 8, 2300000.00, 61, 'REVIEW', '2026-08-27 08:51:20.601024', '2026-08-27 09:16:22.849131', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '8c1a96c8-0f65-4863-979b-e80ce79e748d', 'PT Megah Properti Surabaya');


--
-- Data for Name: trx_pengajuan_pinjaman; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_pengajuan_pinjaman VALUES ('99af64af-18e0-4912-9737-e88d80363add', 'PJ-20260827-733E6E', 15000000.00, 18, 'Renovasi Rumah dan Kebutuhan Keluarga', 0.03, 750000.00, 96, 'SELESAI_DIREVIEW', 'Dokumen lengkap dan valid, pendapatan dan mutasi rekening sesuai kemampuan bayar nasabah', '2026-08-27 09:29:38.770126', '2026-08-27 10:39:35.009675', '3530eedc-0896-4411-9e91-7ec905686560', 'b073b982-ce00-4562-b488-eaa0599ec19a', '57a1741f-4239-4e0a-b75d-d16a413cd47a');
INSERT INTO mel_saku.trx_pengajuan_pinjaman VALUES ('d06d15b3-2d24-48d1-84ee-afa9b4e3c8c6', 'PJ-20260827-0C85AB', 4500000.00, 12, 'Biaya Pengobatan dan Kebutuhan Mendesak', 0.05, 250000.00, 61, 'SELESAI_DIREVIEW', 'Review disetujui', '2026-08-27 09:32:03.740755', '2026-09-13 20:58:56.828183', '634b19a1-269b-461f-842f-bda1ddc9a65d', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '5c626461-fab5-45d3-8e40-3f3ceb4c987d');
INSERT INTO mel_saku.trx_pengajuan_pinjaman VALUES ('f1e5b49d-0e57-44ca-8545-5c96175441d8', 'PJ-20260913-2C655F', 5000000.00, 6, 'Modal Usaha', 0.05, 250000.00, 61, 'PENGAJUAN_DITOLAK', 'Pengajuan uji coba dibatalkan', '2026-09-13 20:48:59.777059', '2026-09-13 21:00:56.043176', '634b19a1-269b-461f-842f-bda1ddc9a65d', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '5c626461-fab5-45d3-8e40-3f3ceb4c987d');
INSERT INTO mel_saku.trx_pengajuan_pinjaman VALUES ('7782fa1c-bc08-460e-9b01-2bcce6c69773', 'PJ-20260911-F2C317', 10000000.00, 12, 'Lainnya', 0.05, 250000.00, 61, 'PENGAJUAN_DITOLAK', 'Pengajuan uji coba dibatalkan', '2026-09-11 17:01:36.874246', '2026-09-13 21:01:10.957791', '634b19a1-269b-461f-842f-bda1ddc9a65d', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '5c626461-fab5-45d3-8e40-3f3ceb4c987d');
INSERT INTO mel_saku.trx_pengajuan_pinjaman VALUES ('4911399d-f9b6-44b3-b65f-bdadb7e557ff', 'PJ-20260916-43E7F4', 10000000.00, 9, 'Pendidikan', 0.05, 250000.00, 76, 'DICAIRKAN', 'Pencairan dana telah berhasil ditransfer ke rekening customer', '2026-09-16 13:52:19.49903', '2026-09-16 14:06:14.344243', '51332aa8-9b2f-49dc-a7d4-84ae687fe2ce', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', 'af1f4678-4c39-4542-85c4-798070a60213');


--
-- Data for Name: trx_angsuran; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_angsuran VALUES ('ffbb0d2a-b4b4-40a0-a906-ec4f067f84a5', 1, 1611111.11, '2026-10-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.346567', '2026-09-16 14:06:14.346567', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('7cef3c75-609b-428e-be1a-3485c24bfde4', 2, 1611111.11, '2026-11-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.350412', '2026-09-16 14:06:14.350412', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('3a6b92d1-b6a2-46e7-b738-eafcebbc6e37', 3, 1611111.11, '2026-12-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.352578', '2026-09-16 14:06:14.352578', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('f8c179b6-9c33-43c8-adbc-8555254a2b5a', 4, 1611111.11, '2027-01-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.354207', '2026-09-16 14:06:14.354207', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('cddcffc3-7962-49f6-91ca-8d384ae52c5c', 5, 1611111.11, '2027-02-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.355926', '2026-09-16 14:06:14.355926', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('d352a04c-9ff7-4696-80b9-07ee68cb9045', 6, 1611111.11, '2027-03-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.357014', '2026-09-16 14:06:14.357014', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('48018c92-9191-479f-a8cf-19eb1e072967', 7, 1611111.11, '2027-04-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.358616', '2026-09-16 14:06:14.358616', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('5ba95a90-eb49-497d-93e5-b85933326bbd', 8, 1611111.11, '2027-05-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.359665', '2026-09-16 14:06:14.359665', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_angsuran VALUES ('f0c63de9-7896-45a7-a747-1d8352a71500', 9, 1611111.11, '2027-06-16', 'BELUM_LUNAS', '2026-09-16 14:06:14.361869', '2026-09-16 14:06:14.361869', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');


--
-- Data for Name: trx_audit_log; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_audit_log VALUES ('12be4d60-46a5-4886-ac74-c7d166f40b68', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-08-26 11:38:45.162897', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('895ddc60-6580-4f9a-9d87-5a44b9cec7b5', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-26 11:42:41.497439', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('98387376-6c38-4001-b2f3-e0afa618f494', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-26 11:48:48.295665', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('23586438-3a45-4b02-8ac5-95381610a6d0', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-26 13:49:26.753239', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('946e145b-25e5-4605-8c47-ee7725037832', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-26 14:53:14.962305', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('5d9eb0bf-b670-4210-b042-8fadf92639f6', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-27 08:18:36.184894', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('0b1130f3-f3b9-4913-abbd-902f2e49e528', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-08-27 09:04:22.206739', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('ddc80035-bb87-435d-bf8a-d7055cdc1644', 'VERIFIKASI_KYC', 'CUSTOMER', 0, 'Backoffice memverifikasi KYC customer Bagus Wijaya (bagus.sby@example.com) dengan status: APPROVED', '2026-08-27 09:16:22.853734', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('47059d39-f9bb-441e-8b49-f0e06f13ac46', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-08-27 09:28:22.286044', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('24fb9a9e-4d8c-4b02-bfc2-f4313624aa90', 'VERIFIKASI_KYC', 'CUSTOMER', 0, 'Backoffice memverifikasi KYC customer Siti Nurhaliza (siti.bandung@example.com) dengan status: APPROVED', '2026-08-27 09:28:57.365834', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('c8b76b3f-8a9e-488a-8726-a2fb552cec11', 'LOGIN', 'AUTH', 0, 'Karyawan marketingbdg (MARKETING) berhasil login ke sistem', '2026-08-27 09:41:23.410329', 'fabb76ed-d9c7-488f-b248-63b6e1f677ac');
INSERT INTO mel_saku.trx_audit_log VALUES ('e1087612-7ebc-4794-8831-92388e67b0bc', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-08-27 09:50:13.578413', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('6f057624-859e-472a-bd76-78580791c8f4', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-08-27 10:05:39.908792', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('dcc4fb20-291a-4bb8-8226-6bc9d97ac0d6', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-08-27 10:09:31.998849', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('a9542802-8ead-4777-a58f-91ac73d2a276', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-08-27 10:32:22.24614', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('6fbd4743-fd01-43cb-8458-c558103ccd8a', 'REVIEW', 'PENGAJUAN', 0, 'Marketing Marketing Surabaya mereview pengajuan no. PJ-20260827-0C85AB dengan hasil: PERLU_REVISI', '2026-08-27 10:33:06.122945', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('33f8c0ab-b6ed-4f70-bd2b-63aa0c9f82ff', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-08-27 10:37:05.016516', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('b40949c8-fb88-4de7-b430-22e64d4a0264', 'LOGIN', 'AUTH', 0, 'Karyawan marketingbdg (MARKETING) berhasil login ke sistem', '2026-08-27 10:38:27.737465', 'fabb76ed-d9c7-488f-b248-63b6e1f677ac');
INSERT INTO mel_saku.trx_audit_log VALUES ('563c8e0b-a4c5-487b-b2a5-d700f220cd04', 'REVIEW', 'PENGAJUAN', 0, 'Marketing Marketing Bandung mereview pengajuan no. PJ-20260827-733E6E dengan hasil: DISETUJUI', '2026-08-27 10:39:35.012661', 'fabb76ed-d9c7-488f-b248-63b6e1f677ac');
INSERT INTO mel_saku.trx_audit_log VALUES ('eded8e2b-c675-4033-8f29-cdc5f151664c', 'LOGIN', 'AUTH', 0, 'Karyawan marketingbdg (MARKETING) berhasil login ke sistem', '2026-08-29 15:12:08.778666', 'fabb76ed-d9c7-488f-b248-63b6e1f677ac');
INSERT INTO mel_saku.trx_audit_log VALUES ('3e51a47c-9aae-4f98-80b8-4892d78453c4', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-29 15:27:14.109136', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('026ee2b2-07eb-448e-9052-1335b8d0c072', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-29 15:35:07.501718', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('97760383-d183-48f4-9217-88c6a3e51471', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-29 15:46:51.248528', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('fe2fa233-a356-48fb-8141-bba7fe3bb1c5', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-08-29 15:51:51.092889', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('f7c83d1b-8ce5-4249-9cbd-3d8f25acda75', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-29 15:52:37.799331', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('d01cd3e3-dd22-44c6-b1bd-ca5f012368e6', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-29 16:04:40.292434', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('ddf60b41-d2ab-424e-b836-2e49f6968ef0', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-29 16:20:52.984681', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('ea581460-66ab-46cc-9334-da16a51a2773', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 09:53:18.569701', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('64bbbf7a-48b3-48ab-a24f-9b6ff0f96935', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 09:54:06.289938', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('dd166cd2-6105-4c08-b584-96b262653ecb', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-30 10:41:24.53829', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('4145ccc9-f504-478b-b261-a21a2e179e12', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-30 10:49:43.611488', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('cb1e1a21-88bd-4148-a36f-7ea65c5b504c', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 10:53:31.311955', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('f3aa74c3-73c4-424a-8b68-fdf9ab47cfe8', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 10:56:08.622921', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('4e0dab94-9724-4b51-9234-d9c65ccac869', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 11:02:43.108067', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('301d70e5-8fef-4235-ad26-eb442259cd3c', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 11:12:42.623134', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('316c7083-de79-43fb-8383-00e04fae1883', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 12:05:42.926469', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('7c78de53-65ae-49df-9505-fa26853d775e', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 14:44:57.705435', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('914bec23-62cb-4f3d-b6f0-10d51f61dccc', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-30 15:00:14.527489', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('62708e4e-5a9f-4ae7-b9d7-02856dd0b668', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 09:38:14.478044', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('bb2d5b06-f0af-4559-ad05-3dc7adfb520e', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:23:06.485112', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('6b64b581-1558-42d0-8b85-4b49e75aaa8e', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:25:41.633759', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('3c80b0a1-a281-4b3c-ba6b-b76e65e2d24b', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:26:15.608759', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('f5f311d2-98e4-42c7-b537-64a365300839', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:28:51.659673', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('622b5746-2f4d-49d3-9004-94bf08caab40', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:29:14.656836', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('0d74a8f1-82eb-4342-87f4-6cb079053dd1', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:36:27.535065', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('1a6cc1a8-f8e3-4dd5-895f-25a6050765e8', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 11:58:16.05543', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('0098e9da-f36e-46bf-a335-38ddb4792d8d', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 13:19:34.890149', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('d452f025-e625-48fb-8231-57868f5ad41e', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-08-31 14:45:07.125559', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('f3b6a89c-9570-44dc-9f32-8bce669ae6d9', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-08-31 14:45:31.536604', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('e0a07e5a-c955-4c4b-be88-8febe1a7f13a', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-01 09:14:52.203596', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('cf070e19-6f8c-44e8-9da6-747bf93542fc', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-01 09:38:50.354292', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('f4208109-627a-4fc5-8147-b44af771ce65', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-01 11:01:20.59691', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('cc5504d4-083e-4c21-a18c-39c1b2fda385', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-01 11:05:15.182015', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('3f63a98e-b1da-497c-847a-c2ca7772a02d', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-01 15:01:21.368942', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('34186922-6fce-4832-af24-4b9d2dc0b583', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-01 16:15:33.413091', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('9b2bc7b7-b318-499d-bf3f-8718b55c9720', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-01 16:52:44.309739', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('40305df5-7b9a-4c83-9fe6-f65a73fe7dc4', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-02 09:12:11.853306', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('1b20de09-8372-495a-a3d1-3b7bf8842061', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-02 10:30:59.195052', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('288e048e-02ef-4ea7-af0f-bafb300ba127', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-02 10:31:53.6769', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('f4639d4b-5172-443c-9808-b4d281d2ae26', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-02 10:45:59.500093', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('c918db4d-a608-4d96-adad-57a8b67cf4ac', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-02 11:29:43.003698', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('255e90a3-b751-485f-a9dd-b8fb1e08520f', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-02 14:14:02.420805', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('a3f8ef06-9b9d-456a-9084-3fd882f3c2e2', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-02 14:14:28.989606', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('dd96af9c-2137-4dd9-8b65-34d534ecbaa2', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-02 14:32:26.480133', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('b40d258f-31d6-4f19-b561-41bc90503ecc', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-02 14:48:30.961795', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('359f8aa3-860e-475e-b38e-586c15173ea6', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-02 16:31:04.605681', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('b5863b0a-2cd4-4402-816f-deb7a9d12760', 'LOGIN', 'AUTH', 0, 'Karyawan bmsby (BRANCHMANAGER) berhasil login ke sistem', '2026-09-02 17:03:18.87888', '1ab16242-4251-49ef-bc6f-aaf3195d82fd');
INSERT INTO mel_saku.trx_audit_log VALUES ('cbce6648-9a20-45c9-bbe5-108b2c303e7e', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-02 17:03:58.271522', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('6813f87a-d056-47b3-a4c1-fb1b9cee5e92', 'LOGIN', 'AUTH', 0, 'Karyawan bmsby (BRANCHMANAGER) berhasil login ke sistem', '2026-09-02 17:04:19.504355', '1ab16242-4251-49ef-bc6f-aaf3195d82fd');
INSERT INTO mel_saku.trx_audit_log VALUES ('82bc258c-0bf4-4df1-b6bc-0a90124bac9b', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-03 09:41:08.182517', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('e7d5f3c3-e481-4b67-bbfa-57b56b5a6562', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-03 11:06:09.154158', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('01984b6c-7afe-445f-84e7-df64fffcc7c1', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-03 11:06:21.596402', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('5a95d0be-10d8-4a51-ac15-f492127d4302', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-03 11:20:11.549809', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('7f1a82eb-cb8e-403f-a532-d5a4b4707051', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-03 11:25:59.130375', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('f3eab822-91f7-43ca-8d9e-f4cf07168ed7', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-03 14:45:01.068851', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('16a0bab0-b916-46ee-b30a-c89d6a1c1e40', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-03 14:46:52.354408', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('c76039c9-3c7a-4a60-9000-e99fa1553567', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-03 15:28:54.517097', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('15368b8e-2751-493a-ba6c-d975072f57ef', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-03 16:17:02.503535', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('b4c3aeb3-f601-4a92-b0ec-50fbeadebe5a', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-03 16:51:40.209419', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('32a313d3-d3d6-4d18-92e9-5d8f9904bd2d', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-04 10:17:46.737485', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('b6cadd7c-7fed-43b7-a996-77673835af74', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-04 10:18:32.115978', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('bfc99c60-e1ca-40a2-8569-a625913f5903', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-04 10:21:02.934576', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('99ae5e24-d462-4a92-9f88-c52446cdc7cb', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-04 10:23:12.798752', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('4c05bf75-e0b4-4d62-bdfa-df99673670d1', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-04 12:04:42.19437', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('3efa58a2-2dd3-4615-a48d-84b3f9ae82e4', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-04 12:05:05.707278', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('e5f5fd9b-d6b4-4adf-9ea5-3fd6521f57a2', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-04 12:05:17.290793', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('a4d16f85-9bf8-4943-844a-b9932a2b9930', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-04 12:55:56.160135', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('b0b5e4ce-3459-480a-94cf-ce9885cfa402', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-04 13:05:11.798998', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('076ee43b-613b-4a92-8837-d02978b8a716', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-04 13:55:25.646563', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('f23b2b8b-2db7-4017-bedf-fd09746cc870', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-04 14:11:28.721369', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('5e4c434f-8541-420a-90dc-7485231d23af', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-04 14:12:46.875169', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('079f8dcd-c89e-4fa7-a13a-4af3addf066a', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-04 14:20:19.537393', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('3e372178-00f1-42d9-86dc-79dd4981477c', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-04 15:03:34.2936', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('45d90b1c-969c-4c45-bff9-bd8546af0efe', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-04 16:14:09.000484', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('cdd2dcd5-bea7-4316-b8f1-d7bda3dbd0de', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-07 11:14:31.859837', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('56078be6-3bf0-435b-9cee-f26261494837', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-08 09:34:11.327655', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('1282d4c5-c8aa-4b58-bda3-ddc5d15e1676', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-08 09:35:48.843276', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('ceb62c1d-3a4a-4606-a0a6-856fda145b4e', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 09:36:19.312185', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('fb8e4649-e907-4a43-bf3c-384107bbcbe2', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-08 09:39:10.88112', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('654fe890-6e1d-438f-9d44-e4ca814b31e5', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-08 09:39:29.445478', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('20079c76-72a9-44f9-8737-0d72d97143db', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 09:41:06.891418', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('55350a68-82c3-4c8e-8367-046663026b72', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 09:42:48.097194', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('a9656f6a-39b1-4e89-94b0-335c9c51f474', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 09:51:01.224729', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('1911d1aa-4ae8-4b86-abea-d53eee61fca1', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-08 10:14:47.150344', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('16bce7ff-11cc-4256-9e5e-b5152718a5da', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-08 10:15:23.93552', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('05d720d8-23e5-497c-85d8-7e82cc0d2c10', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-08 10:54:22.471153', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('dc6a15b9-68f9-4771-a476-5298091bb5c3', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 10:54:56.050094', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('a5192a63-5bd3-445f-9359-c30da6cbd797', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-08 10:56:56.507738', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('a5c11642-3a3b-4e75-abac-bc84ff5c3ff0', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-08 10:58:07.976051', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('cdd7b05c-568d-429e-8a01-169d2ac2392f', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 10:58:35.315896', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('c4b6d451-5cd7-4040-867a-5f3c196491cb', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-08 13:57:33.316332', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('d6cf2e7d-1539-4ae9-a60b-037e47c38500', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-08 13:57:56.63098', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('27bd6852-cf61-40db-a936-80ed6d12cd9e', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-11 12:31:28.031786', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('f2f64538-d90b-40cd-a4e0-66adceee6c3e', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-11 12:32:58.575711', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('81712865-9546-49a5-9652-482228570700', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-11 14:26:49.719619', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('c957c8da-37ca-4069-9527-8b1a36a31a41', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-09-13 20:57:13.153076', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('7e7c48bd-2470-4ce2-8ba2-9a4bedfff278', 'REVIEW', 'PENGAJUAN', 0, 'Marketing Marketing Surabaya mereview pengajuan no. PJ-20260827-0C85AB dengan hasil: DISETUJUI', '2026-09-13 20:58:56.837049', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('204546ae-7527-4050-8229-9366aae2503d', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-09-13 20:59:31.028706', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('4e1cf8b0-e999-4913-99a2-f918e1243c5c', 'REVIEW', 'PENGAJUAN', 0, 'Marketing Marketing Surabaya mereview pengajuan no. PJ-20260913-2C655F dengan hasil: DITOLAK', '2026-09-13 21:00:56.045712', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('41d5d45e-3836-4a56-aff6-ccbdf24ea349', 'REVIEW', 'PENGAJUAN', 0, 'Marketing Marketing Surabaya mereview pengajuan no. PJ-20260911-F2C317 dengan hasil: DITOLAK', '2026-09-13 21:01:10.960006', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('349ac09f-c618-4b99-9cf2-584c989d13b4', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-14 09:54:18.61431', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('7edb8b3a-0872-44d4-89eb-64e0e6cdd7e1', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-14 15:30:03.269046', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('b4d21a61-90c6-40a7-bbaf-e96f1feb268e', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-14 21:33:56.716147', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('598a851c-57f2-46fe-8c63-393fb9850f58', 'LOGIN', 'AUTH', 0, 'Karyawan marketingbdg (MARKETING) berhasil login ke sistem', '2026-09-14 21:35:12.951839', 'fabb76ed-d9c7-488f-b248-63b6e1f677ac');
INSERT INTO mel_saku.trx_audit_log VALUES ('bb518291-5fd0-48e1-a370-3e71196598dd', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-09-14 21:38:28.786241', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('d00fb845-b50f-4c35-be5a-f451016eeae5', 'LOGIN', 'AUTH', 0, 'Karyawan bmsby (BRANCHMANAGER) berhasil login ke sistem', '2026-09-14 21:38:59.967278', '1ab16242-4251-49ef-bc6f-aaf3195d82fd');
INSERT INTO mel_saku.trx_audit_log VALUES ('35b5f59b-c478-4b02-895b-74a274aaba0b', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-14 22:55:45.447056', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('5f4f1840-35b7-4f7a-b4d4-67bf7e34fcff', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-15 09:29:51.525888', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('9c625ec1-cdd6-4a02-8e9e-ed30e611681c', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-15 13:25:53.872928', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('00df78e5-193d-45bc-bac9-dec9cdb3110f', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-15 13:35:19.99255', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('f480f420-7f36-4ea4-8a17-619870c68233', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-15 16:39:07.306257', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('d9cc51a3-9d10-4a29-8e97-7ce2b52b1eac', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-15 16:47:56.774325', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('59c5892c-8643-4869-9e05-097fd62cac94', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-15 16:57:34.709428', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('4e40e4e8-2fc1-4530-b1f6-cf47d119e798', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-16 13:08:49.71467', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('183d8a66-9111-4520-85e3-05cfd54a65dc', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-16 13:09:47.716582', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('c86b28fe-055f-4a31-ac13-de9b10394bb1', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-16 13:13:19.474026', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('8f8de11a-7afd-4fc5-bdc2-c90abe55a590', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-16 13:52:53.094071', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('501babf3-0b1f-482a-89b5-94045a17519a', 'REVIEW', 'PENGAJUAN', 0, 'Marketing Marketing SAKU mereview pengajuan no. PJ-20260916-43E7F4 dengan hasil: DISETUJUI', '2026-09-16 13:53:32.192114', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('0a2acf6f-8bc1-4d90-903d-85973851a4d2', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-16 13:54:02.4059', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('ec3e9db6-22e6-40e3-93e9-5d1c26b8eb85', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-16 13:54:48.572454', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('2f1a803f-236c-4d5e-8d47-21e897fb4648', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-16 13:56:51.762168', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('16724445-0f7a-4838-b6fd-0b07a8897ab3', 'LOGIN', 'AUTH', 0, 'Karyawan marketing (MARKETING) berhasil login ke sistem', '2026-09-16 13:57:23.147921', '3e696e6a-1b63-4386-b24c-c59194723ee7');
INSERT INTO mel_saku.trx_audit_log VALUES ('9d29c884-ed22-43f9-83c5-7139970014b7', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-16 13:57:37.691258', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('52540378-fe4b-43fd-a23f-29cb4460b580', 'APPROVE', 'PENGAJUAN', 0, 'Branch Manager Branch Manager SAKU memproses persetujuan pengajuan no. PJ-20260916-43E7F4 (APPROVE)', '2026-09-16 14:00:11.391449', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('f9b0a78a-825e-42ca-9d0f-b14582ab5a31', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-16 14:04:27.412184', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('5b91190a-8a91-4ffe-b927-a606be47bbc2', 'PENCAIRAN', 'PENCAIRAN', 0, 'Backoffice Back Office SAKU mencairkan pinjaman no. PJ-20260916-43E7F4 sebesar Rp 9750000 ke rekening BCA', '2026-09-16 14:06:14.61471', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('95343d65-cbe3-4642-8d5f-a1ffa01c514b', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-16 14:16:13.678968', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('88ef67a3-340a-448a-9ad5-3d09275c051b', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-17 10:55:40.664456', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('4cee588e-ab52-4f52-8d3d-eca80201bc01', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-17 11:26:54.026794', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('231e1f91-2712-47fe-9617-f51a01f288fa', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-17 13:01:15.652508', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('f378b617-a61b-42ba-933e-babd73328601', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-17 13:40:29.892685', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('8a8ec8a0-3fc8-48be-a715-0ff7882c650a', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-17 14:18:39.152676', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_audit_log VALUES ('9ba5fcd0-146b-411c-ac7d-0182808aa127', 'LOGIN', 'AUTH', 0, 'Karyawan marketingsby (MARKETING) berhasil login ke sistem', '2026-09-17 14:22:03.241215', '221d388e-2310-450f-92d4-a34f8bd615cd');
INSERT INTO mel_saku.trx_audit_log VALUES ('f2e349e9-e989-4b84-a5d9-e64bfaea84ac', 'LOGIN', 'AUTH', 0, 'Karyawan bm (BRANCHMANAGER) berhasil login ke sistem', '2026-09-17 15:24:53.073242', '18206675-7990-4980-a5a2-726ed3532c68');
INSERT INTO mel_saku.trx_audit_log VALUES ('2bad5fc9-9c2e-4d38-803e-9cef1de2076e', 'LOGIN', 'AUTH', 0, 'Karyawan bmsby (BRANCHMANAGER) berhasil login ke sistem', '2026-09-17 15:25:09.910718', '1ab16242-4251-49ef-bc6f-aaf3195d82fd');
INSERT INTO mel_saku.trx_audit_log VALUES ('b13bb6c1-35d2-4a42-81ba-1a5d6bff287e', 'LOGIN', 'AUTH', 0, 'Karyawan superadmin (SUPERADMIN) berhasil login ke sistem', '2026-09-17 15:26:21.818841', '2c25e863-163e-474c-bc36-b45fe378ac04');
INSERT INTO mel_saku.trx_audit_log VALUES ('849502df-0dbe-4254-bb09-cd568cce253e', 'LOGIN', 'AUTH', 0, 'Karyawan backoffice (BACKOFFICE) berhasil login ke sistem', '2026-09-17 15:29:02.327919', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');


--
-- Data for Name: trx_dokumen_customer; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_dokumen_customer VALUES ('de91c558-d5cf-4400-9b59-16d4f1fdeeb5', 'KTP', 'ktp/b025e380-21a8-46cf-8825-17a64e0e3cbd/113cc547-df1b-4fea-89ed-90249daa47ef.jpg', '2026-08-19 15:55:15.786309', '2026-08-19 15:55:15.786309', 'b025e380-21a8-46cf-8825-17a64e0e3cbd');
INSERT INTO mel_saku.trx_dokumen_customer VALUES ('b7001708-db50-4dcd-90ed-6587ea0c3687', 'SELFIE', 'selfie/b025e380-21a8-46cf-8825-17a64e0e3cbd/3ed00da3-253c-495d-9d40-771235c0fd22.jpg', '2026-08-19 15:55:15.816621', '2026-08-19 15:55:15.816621', 'b025e380-21a8-46cf-8825-17a64e0e3cbd');
INSERT INTO mel_saku.trx_dokumen_customer VALUES ('fb14e1ae-ca56-4f1d-9873-a6d44ecac347', 'KTP', 'ktp/b073b982-ce00-4562-b488-eaa0599ec19a/0d13c487-7b3f-4207-8ce8-117ce9cab7b3.jpg', '2026-08-27 08:47:56.985638', '2026-08-27 08:47:56.985638', 'b073b982-ce00-4562-b488-eaa0599ec19a');
INSERT INTO mel_saku.trx_dokumen_customer VALUES ('134c5290-9644-478f-b70e-050e032c96aa', 'SELFIE', 'selfie/b073b982-ce00-4562-b488-eaa0599ec19a/0256233d-a0cd-4b53-b075-b3c33fa78c08.jpg', '2026-08-27 08:47:56.99212', '2026-08-27 08:47:56.99212', 'b073b982-ce00-4562-b488-eaa0599ec19a');
INSERT INTO mel_saku.trx_dokumen_customer VALUES ('c3a25760-5dba-4cfb-9b35-1cb2903f8b8d', 'KTP', 'ktp/3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0/43a9a54e-7ffe-4884-b876-baf44e7bf85b.jpg', '2026-08-27 08:51:35.778833', '2026-08-27 08:51:35.778833', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0');
INSERT INTO mel_saku.trx_dokumen_customer VALUES ('65e7985f-e700-4825-940e-d861f98490f0', 'SELFIE', 'selfie/3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0/d013fa4c-bf72-4b2d-bd26-f13d66f6659d.jpg', '2026-08-27 08:51:35.785523', '2026-08-27 08:51:35.785523', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0');


--
-- Data for Name: trx_dokumen_pinjaman; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('60b2fe7b-7d98-4aa7-8816-69d07baf75c0', 'SLIP_GAJI', 'pinjaman/99af64af-18e0-4912-9737-e88d80363add/f6246dcf-0fc1-473c-9c4e-0eb6264e3373.pdf', '2026-08-27 09:30:01.068405', '2026-08-27 09:30:01.068405', '99af64af-18e0-4912-9737-e88d80363add');
INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('f056c6ef-bf92-4a76-a67b-8a2d49a7df2b', 'REKENING_KORAN', 'pinjaman/99af64af-18e0-4912-9737-e88d80363add/fc7f1584-49e9-4e7f-b54f-c3a0fdec9ad6.pdf', '2026-08-27 09:30:01.089877', '2026-08-27 09:30:01.089877', '99af64af-18e0-4912-9737-e88d80363add');
INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('2102d0f6-acd8-47d5-9adb-49469429434a', 'REKENING_KORAN', 'pinjaman/d06d15b3-2d24-48d1-84ee-afa9b4e3c8c6/706f1f26-03e6-4b4e-bae7-1d81c109cf2a.pdf', '2026-08-27 09:32:24.110826', '2026-08-27 09:32:24.110826', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('682a347e-01ab-4e24-971c-00db0f918ba4', 'SLIP_GAJI', 'pinjaman/d06d15b3-2d24-48d1-84ee-afa9b4e3c8c6/5f88f7a6-3ad7-4101-8abf-65e2d03092e4.pdf', '2026-08-27 09:32:24.093753', '2026-08-27 10:36:48.331897', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('2ba21375-3896-4ec3-a046-1a0b7c75b0de', 'SLIP_GAJI', 'pinjaman/4911399d-f9b6-44b3-b65f-bdadb7e557ff/ce0db407-7e4e-4dc9-b8cc-e61e86f1764f.jpg', '2026-09-16 13:52:20.031099', '2026-09-16 13:52:20.031099', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('cd5da80e-84ce-47b9-8b0f-e4919141178b', 'REKENING_KORAN', 'pinjaman/4911399d-f9b6-44b3-b65f-bdadb7e557ff/9b8e290b-ec64-4b76-96a2-11a40f3a4630.jpg', '2026-09-16 13:52:20.055905', '2026-09-16 13:52:20.055905', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_dokumen_pinjaman VALUES ('9b391ad3-7376-4af0-9637-68c50b0c7bd3', 'NPWP', 'pinjaman/4911399d-f9b6-44b3-b65f-bdadb7e557ff/7412ad54-cd37-400a-af9b-2ec3cc6d71f6.jpg', '2026-09-16 13:52:20.066367', '2026-09-16 13:52:20.066367', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');


--
-- Data for Name: trx_notifikasi; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_notifikasi VALUES ('e80090d1-3595-47af-9645-7efadccb9fcd', 'PENGAJUAN', 'IN_APP', 'Pengajuan Pinjaman Diproses', 'Pengajuan pinjaman no. PJ-20260827-733E6E sedang dalam proses review oleh tim cabang.', 'BELUM_DIBACA', '2026-08-27 09:30:01.107471', '2026-08-27 09:30:01.107471', 'b073b982-ce00-4562-b488-eaa0599ec19a', '99af64af-18e0-4912-9737-e88d80363add');
INSERT INTO mel_saku.trx_notifikasi VALUES ('f17e9a78-f2ab-4f70-b37e-e2a3e50f2e20', 'REVIEW_MARKETING', 'IN_APP', 'Review Pinjaman Disetujui', 'Pengajuan pinjaman no. PJ-20260827-733E6E telah disetujui pada tahap review Marketing dan diteruskan ke Branch Manager.', 'BELUM_DIBACA', '2026-08-27 10:39:35.009675', '2026-08-27 10:39:35.009675', 'b073b982-ce00-4562-b488-eaa0599ec19a', '99af64af-18e0-4912-9737-e88d80363add');
INSERT INTO mel_saku.trx_notifikasi VALUES ('85421882-0ade-4a2c-86bd-5b18efa13a43', 'PENGAJUAN', 'IN_APP', 'Pengajuan Pinjaman Diproses', 'Pengajuan pinjaman no. PJ-20260827-0C85AB sedang dalam proses review oleh tim cabang.', 'SUDAH_DIBACA', '2026-08-27 09:32:24.121749', '2026-09-13 21:04:31.076912', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_notifikasi VALUES ('1aea0b9e-58c4-40ec-86d1-6c9ce38a95d3', 'REVIEW_MARKETING', 'IN_APP', 'Perlu Revisi Dokumen Pinjaman', 'Pengajuan pinjaman no. PJ-20260827-0C85AB memerlukan perbaikan dokumen. Catatan: Slip gaji tidak memiliki stempel/ttd resmi perusahaan. Mohon unggah ulang slip gaji resmi bulan terakhir.', 'SUDAH_DIBACA', '2026-08-27 10:33:06.11769', '2026-09-13 21:04:31.076912', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_notifikasi VALUES ('52a237c3-a504-4825-a0df-6f8eefa43066', 'PENGAJUAN', 'IN_APP', 'Dokumen Revisi Pinjaman Diterima', 'Pengajuan pinjaman no. PJ-20260827-0C85AB sedang dalam proses review oleh tim cabang.', 'SUDAH_DIBACA', '2026-08-27 10:36:48.356948', '2026-09-13 21:04:31.076912', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_notifikasi VALUES ('0e4afed2-779b-4a70-843f-feb36508bba1', 'REVIEW_MARKETING', 'IN_APP', 'Review Pinjaman Disetujui', 'Pengajuan pinjaman no. PJ-20260827-0C85AB telah disetujui pada tahap review Marketing dan diteruskan ke Branch Manager.', 'SUDAH_DIBACA', '2026-09-13 20:58:56.830038', '2026-09-13 21:04:31.076912', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_notifikasi VALUES ('b0532954-dd42-4bc0-bddd-b9ca594f904d', 'REVIEW_MARKETING', 'IN_APP', 'Pengajuan Pinjaman Ditolak', 'Pengajuan pinjaman no. PJ-20260913-2C655F tidak disetujui pada tahap review Marketing. Catatan: Pengajuan uji coba dibatalkan', 'SUDAH_DIBACA', '2026-09-13 21:00:56.043686', '2026-09-14 11:03:28.43492', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', 'f1e5b49d-0e57-44ca-8545-5c96175441d8');
INSERT INTO mel_saku.trx_notifikasi VALUES ('66719b8e-6079-4339-ae6b-6de472187f1a', 'REVIEW_MARKETING', 'IN_APP', 'Pengajuan Pinjaman Ditolak', 'Pengajuan pinjaman no. PJ-20260911-F2C317 tidak disetujui pada tahap review Marketing. Catatan: Pengajuan uji coba dibatalkan', 'SUDAH_DIBACA', '2026-09-13 21:01:10.958348', '2026-09-14 11:13:37.295961', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '7782fa1c-bc08-460e-9b01-2bcce6c69773');
INSERT INTO mel_saku.trx_notifikasi VALUES ('756e8b92-623c-45f8-939e-d1cdb3a03e60', 'PENCAIRAN', 'IN_APP', 'Dana Pinjaman Telah Dicairkan', 'Selamat! Dana pinjaman no. PJ-20260916-43E7F4 sebesar Rp 9750000 telah berhasil dicairkan ke rekening BCA Anda.', 'SUDAH_DIBACA', '2026-09-16 14:06:14.362923', '2026-09-16 14:07:06.903403', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_notifikasi VALUES ('dcac0ca1-89b7-4774-82bd-8ce01f880590', 'APPROVAL_BM', 'IN_APP', 'Pinjaman Disetujui Branch Manager', 'Selamat! Pengajuan pinjaman no. PJ-20260916-43E7F4 telah disetujui oleh Branch Manager dan sedang dalam proses pencairan dana.', 'SUDAH_DIBACA', '2026-09-16 14:00:11.135582', '2026-09-16 14:07:07.891924', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_notifikasi VALUES ('fa4bb53d-f867-4fa1-94aa-0b997e313a81', 'REVIEW_MARKETING', 'IN_APP', 'Review Pinjaman Disetujui', 'Pengajuan pinjaman no. PJ-20260916-43E7F4 telah disetujui pada tahap review Marketing dan diteruskan ke Branch Manager.', 'SUDAH_DIBACA', '2026-09-16 13:53:32.036963', '2026-09-16 14:07:08.310364', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');
INSERT INTO mel_saku.trx_notifikasi VALUES ('3aff254b-c57a-4223-8a67-263da0e78485', 'PENGAJUAN', 'IN_APP', 'Pengajuan Pinjaman Diproses', 'Pengajuan pinjaman no. PJ-20260916-43E7F4 sedang dalam proses review oleh tim cabang.', 'SUDAH_DIBACA', '2026-09-16 13:52:20.074461', '2026-09-16 14:07:08.854757', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');


--
-- Data for Name: trx_otp; Type: TABLE DATA; Schema: public; Owner: user_saku
--


--
-- Data for Name: trx_pencairan; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_pencairan VALUES ('88c640b5-74a2-444a-99df-4a591aa97183', 9750000.00, 'BERHASIL', '2026-09-16 14:06:14.342132', '2026-09-16 14:06:14.342132', '4911399d-f9b6-44b3-b65f-bdadb7e557ff', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');


--
-- Data for Name: trx_persetujuan; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_persetujuan VALUES ('caa450c4-18ae-41df-9fe3-193a85e7f44f', 'DISETUJUI', 'Data keuangan, slip gaji, dan credit scoring memenuhi seluruh kriteria kelayakan pembiayaan.', '2026-09-16 14:00:11.129189', '2026-09-16 14:00:11.129189', '18206675-7990-4980-a5a2-726ed3532c68', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');


--
-- Data for Name: trx_review_pengajuan; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_review_pengajuan VALUES ('5110c60a-7bb4-4c79-a7a6-f3145d680dde', 'PERLU_REVISI', 'Slip gaji tidak memiliki stempel/ttd resmi perusahaan. Mohon unggah ulang slip gaji resmi bulan terakhir.', '2026-08-27 10:33:06.090173', '2026-08-27 10:33:06.090173', '221d388e-2310-450f-92d4-a34f8bd615cd', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_review_pengajuan VALUES ('e3c6bfbe-c004-414a-ad7a-fd3a4a894e84', 'DISETUJUI', 'Dokumen lengkap dan valid, pendapatan dan mutasi rekening sesuai kemampuan bayar nasabah', '2026-08-27 10:39:35.006508', '2026-08-27 10:39:35.006508', 'fabb76ed-d9c7-488f-b248-63b6e1f677ac', '99af64af-18e0-4912-9737-e88d80363add');
INSERT INTO mel_saku.trx_review_pengajuan VALUES ('36d1f3e0-2bcf-4fdd-972d-ef46885566ae', 'DISETUJUI', 'Review disetujui', '2026-09-13 20:58:56.821633', '2026-09-13 20:58:56.821633', '221d388e-2310-450f-92d4-a34f8bd615cd', 'd06d15b3-2d24-48d1-84ee-afa9b4e3c8c6');
INSERT INTO mel_saku.trx_review_pengajuan VALUES ('21428377-2d54-4b0b-b7dd-f0886d3d7a57', 'DITOLAK', 'Pengajuan uji coba dibatalkan', '2026-09-13 21:00:56.040723', '2026-09-13 21:00:56.040723', '221d388e-2310-450f-92d4-a34f8bd615cd', 'f1e5b49d-0e57-44ca-8545-5c96175441d8');
INSERT INTO mel_saku.trx_review_pengajuan VALUES ('46409e9d-0b2c-4935-8f8e-57bf3cce0268', 'DITOLAK', 'Pengajuan uji coba dibatalkan', '2026-09-13 21:01:10.956129', '2026-09-13 21:01:10.956129', '221d388e-2310-450f-92d4-a34f8bd615cd', '7782fa1c-bc08-460e-9b01-2bcce6c69773');
INSERT INTO mel_saku.trx_review_pengajuan VALUES ('2a59ee4b-cad0-4bee-8013-41233def3724', 'DISETUJUI', 'Dokumen lengkap dan valid. Pendapatan, slip gaji, dan mutasi rekening sesuai kemampuan bayar customer.', '2026-09-16 13:53:32.032132', '2026-09-16 13:53:32.032132', '3e696e6a-1b63-4386-b24c-c59194723ee7', '4911399d-f9b6-44b3-b65f-bdadb7e557ff');


--
-- Data for Name: trx_verifikasi_customer; Type: TABLE DATA; Schema: public; Owner: user_saku
--

INSERT INTO mel_saku.trx_verifikasi_customer VALUES ('f109ce96-d4cf-4471-9b26-21286a0cd861', 'APPROVED', 'Dokumen KTP dan selfie valid', '2026-08-19 15:56:00.092398', '2026-08-19 15:56:00.092398', 'b025e380-21a8-46cf-8825-17a64e0e3cbd', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_verifikasi_customer VALUES ('c688c829-8582-423e-a8c2-4a1e21573eb6', 'APPROVED', 'Dokumen KTP dan selfie valid', '2026-08-27 09:16:22.849663', '2026-08-27 09:16:22.849663', '3f249f82-d6e9-4ba7-bcd3-b96cfaa462a0', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');
INSERT INTO mel_saku.trx_verifikasi_customer VALUES ('da75676a-1219-4c44-a39f-1e72e8c2f419', 'APPROVED', 'Dokumen KTP dan selfie valid', '2026-08-27 09:28:57.356909', '2026-08-27 09:28:57.356909', 'b073b982-ce00-4562-b488-eaa0599ec19a', '417a84b4-192b-43a8-9461-f5b8fc8d4eb6');


--
-- PostgreSQL database dump complete
--

\unrestrict Wo4hmctlpADyFDlB4jcfCtaITzeBpxivWfyKTT93HRldLo44wP1molFvaGZxHcZ
