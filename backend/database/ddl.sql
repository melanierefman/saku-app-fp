-- DROP SCHEMA mel_saku;

CREATE SCHEMA mel_saku AUTHORIZATION user_saku;
-- mel_saku.mst_cabang definition

-- Drop table

-- DROP TABLE mel_saku.mst_cabang;

CREATE TABLE mel_saku.mst_cabang ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, is_default bool NOT NULL, kota varchar(50) NOT NULL, nama varchar(50) NOT NULL, status bool NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT mst_cabang_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_cabang OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_cabang TO user_saku;


-- mel_saku.mst_customer definition

-- Drop table

-- DROP TABLE mel_saku.mst_customer;

CREATE TABLE mel_saku.mst_customer ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, email varchar(50) NOT NULL, fcm_token varchar(255) NULL, nama varchar(150) NOT NULL, nama_bank varchar(50) NULL, nama_ibu_kandung varchar(150) NULL, nama_rekening varchar(150) NULL, nik varchar(20) NOT NULL, no_hp varchar(16) NOT NULL, no_rekening varchar(20) NULL, "password" varchar(255) NOT NULL, status bool NOT NULL, updated_date timestamp(6) NOT NULL, username varchar(50) NOT NULL, CONSTRAINT mst_customer_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_customer OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_customer TO user_saku;


-- mel_saku.mst_menu definition

-- Drop table

-- DROP TABLE mel_saku.mst_menu;

CREATE TABLE mel_saku.mst_menu ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, nama varchar(50) NOT NULL, "path" varchar(150) NOT NULL, status bool NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT mst_menu_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_menu OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_menu TO user_saku;


-- mel_saku.mst_permission definition

-- Drop table

-- DROP TABLE mel_saku.mst_permission;

CREATE TABLE mel_saku.mst_permission ( id uuid NOT NULL, "action" varchar(50) NOT NULL, created_date timestamp(6) NOT NULL, mst_menu_id uuid NOT NULL, nama varchar(50) NOT NULL, resource varchar(50) NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT mst_permission_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_permission OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_permission TO user_saku;


-- mel_saku.mst_plafond definition

-- Drop table

-- DROP TABLE mel_saku.mst_plafond;

CREATE TABLE mel_saku.mst_plafond ( id uuid NOT NULL, biaya_admin numeric(15, 2) NOT NULL, bunga numeric(6, 4) NOT NULL, created_date timestamp(6) NOT NULL, max_plafond numeric(15, 2) NOT NULL, max_skor int4 NOT NULL, min_pendapatan numeric(15, 2) NOT NULL, min_plafond numeric(15, 2) NOT NULL, min_skor int4 NOT NULL, nama varchar(50) NOT NULL, plafond_maksimal numeric(15, 2) NOT NULL, status bool NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT mst_plafond_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_plafond OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_plafond TO user_saku;


-- mel_saku.mst_role definition

-- Drop table

-- DROP TABLE mel_saku.mst_role;

CREATE TABLE mel_saku.mst_role ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, nama varchar(50) NOT NULL, status bool NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT mst_role_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_role OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_role TO user_saku;


-- mel_saku.mst_role_permission definition

-- Drop table

-- DROP TABLE mel_saku.mst_role_permission;

CREATE TABLE mel_saku.mst_role_permission ( id uuid NOT NULL, mst_permission_id uuid NOT NULL, mst_role_id uuid NOT NULL, CONSTRAINT mst_role_permission_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.mst_role_permission OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_role_permission TO user_saku;


-- mel_saku.trx_angsuran definition

-- Drop table

-- DROP TABLE mel_saku.trx_angsuran;

CREATE TABLE mel_saku.trx_angsuran ( id uuid NOT NULL, cicilan_ke int4 NOT NULL, created_date timestamp(6) NOT NULL, jatuh_tempo date NOT NULL, jumlah_angsuran numeric(15, 2) NOT NULL, status_bayar varchar(20) NOT NULL, trx_pengajuan_pinjaman_id uuid NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_angsuran_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_angsuran OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_angsuran TO user_saku;


-- mel_saku.trx_audit_log definition

-- Drop table

-- DROP TABLE mel_saku.trx_audit_log;

CREATE TABLE mel_saku.trx_audit_log ( id uuid NOT NULL, "action" varchar(20) NOT NULL, created_date timestamp(6) NOT NULL, description varchar(225) NOT NULL, entity varchar(20) NOT NULL, entity_id int4 NOT NULL, mst_karyawan_id uuid NOT NULL, CONSTRAINT trx_audit_log_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_audit_log OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_audit_log TO user_saku;


-- mel_saku.trx_dokumen_pinjaman definition

-- Drop table

-- DROP TABLE mel_saku.trx_dokumen_pinjaman;

CREATE TABLE mel_saku.trx_dokumen_pinjaman ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, doc_type varchar(20) NOT NULL, file_url varchar(100) NOT NULL, trx_pengajuan_pinjaman_id uuid NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_dokumen_pinjaman_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_dokumen_pinjaman OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_dokumen_pinjaman TO user_saku;


-- mel_saku.trx_notifikasi definition

-- Drop table

-- DROP TABLE mel_saku.trx_notifikasi;

CREATE TABLE mel_saku.trx_notifikasi ( id uuid NOT NULL, channel varchar(50) NOT NULL, created_date timestamp(6) NOT NULL, judul varchar(50) NOT NULL, mst_customer_id uuid NOT NULL, pesan varchar(500) NOT NULL, status varchar(50) NOT NULL, trx_pengajuan_pinjaman_id uuid NULL, "type" varchar(50) NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_notifikasi_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_notifikasi OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_notifikasi TO user_saku;


-- mel_saku.trx_otp definition

-- Drop table

-- DROP TABLE mel_saku.trx_otp;

CREATE TABLE mel_saku.trx_otp ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, expired_at timestamp(6) NOT NULL, is_used bool NOT NULL, mst_customer_id uuid NOT NULL, otp_code varchar(6) NOT NULL, purpose varchar(50) NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_otp_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_otp OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_otp TO user_saku;


-- mel_saku.trx_pencairan definition

-- Drop table

-- DROP TABLE mel_saku.trx_pencairan;

CREATE TABLE mel_saku.trx_pencairan ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, jumlah_pencairan numeric(15, 2) NOT NULL, mst_karyawan_id uuid NOT NULL, status_pencairan varchar(20) NOT NULL, trx_pengajuan_pinjaman_id uuid NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_pencairan_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_pencairan OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_pencairan TO user_saku;


-- mel_saku.trx_pengajuan_pinjaman definition

-- Drop table

-- DROP TABLE mel_saku.trx_pengajuan_pinjaman;

CREATE TABLE mel_saku.trx_pengajuan_pinjaman ( id uuid NOT NULL, biaya_admin numeric(15, 2) NOT NULL, bunga numeric(6, 4) NOT NULL, catatan_review varchar(1000) NOT NULL, created_date timestamp(6) NOT NULL, jumlah_pinjaman numeric(15, 2) NOT NULL, mst_branch_id uuid NOT NULL, mst_customer_id uuid NOT NULL, nomor_pengajuan varchar(100) NOT NULL, skor_kesehatan int4 NOT NULL, status_pengajuan varchar(20) NOT NULL, tenor_bulan int4 NOT NULL, trx_scoring_customer_id uuid NOT NULL, tujuan_pinjaman varchar(50) NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_pengajuan_pinjaman_pkey PRIMARY KEY (id), CONSTRAINT uk2qpribhmta23ssjj1yw33so6g UNIQUE (nomor_pengajuan));

-- Permissions

ALTER TABLE mel_saku.trx_pengajuan_pinjaman OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_pengajuan_pinjaman TO user_saku;


-- mel_saku.trx_persetujuan definition

-- Drop table

-- DROP TABLE mel_saku.trx_persetujuan;

CREATE TABLE mel_saku.trx_persetujuan ( id uuid NOT NULL, catatan varchar(1000) NOT NULL, created_date timestamp(6) NOT NULL, hasil_persetujuan varchar(20) NOT NULL, mst_karyawan_id uuid NOT NULL, trx_pengajuan_pinjaman_id uuid NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_persetujuan_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_persetujuan OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_persetujuan TO user_saku;


-- mel_saku.trx_review_pengajuan definition

-- Drop table

-- DROP TABLE mel_saku.trx_review_pengajuan;

CREATE TABLE mel_saku.trx_review_pengajuan ( id uuid NOT NULL, catatan varchar(1000) NOT NULL, created_date timestamp(6) NOT NULL, hasil_review varchar(20) NOT NULL, mst_karyawan_id uuid NOT NULL, trx_pengajuan_pinjaman_id uuid NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_review_pengajuan_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_review_pengajuan OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_review_pengajuan TO user_saku;


-- mel_saku.trx_scoring_customer definition

-- Drop table

-- DROP TABLE mel_saku.trx_scoring_customer;

CREATE TABLE mel_saku.trx_scoring_customer ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, lama_bekerja_bulan int4 NOT NULL, mst_customer_id uuid NOT NULL, mst_plafond_id uuid NULL, pekerjaan varchar(100) NOT NULL, penghasilan_bulanan numeric(15, 2) NOT NULL, skor int4 NOT NULL, status_pekerjaan varchar(50) NOT NULL, status_scoring varchar(20) NOT NULL, tempat_kerja varchar(150) NOT NULL, total_cicilan_lain_bulanan numeric(15, 2) NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_scoring_customer_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_scoring_customer OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_scoring_customer TO user_saku;


-- mel_saku.trx_verifikasi_customer definition

-- Drop table

-- DROP TABLE mel_saku.trx_verifikasi_customer;

CREATE TABLE mel_saku.trx_verifikasi_customer ( id uuid NOT NULL, catatan_verifikasi varchar(1000) NOT NULL, created_date timestamp(6) NOT NULL, mst_customer_id uuid NOT NULL, mst_karyawan_id uuid NOT NULL, status_verifikasi varchar(20) NOT NULL, updated_date timestamp(6) NOT NULL, CONSTRAINT trx_verifikasi_customer_pkey PRIMARY KEY (id));

-- Permissions

ALTER TABLE mel_saku.trx_verifikasi_customer OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_verifikasi_customer TO user_saku;


-- mel_saku.mst_alamat_customer definition

-- Drop table

-- DROP TABLE mel_saku.mst_alamat_customer;

CREATE TABLE mel_saku.mst_alamat_customer ( id uuid NOT NULL, alamat_lengkap varchar(100) NOT NULL, created_date timestamp(6) NOT NULL, jenis_alamat varchar(50) NOT NULL, kecamatan varchar(50) NOT NULL, kelurahan varchar(50) NOT NULL, kode_pos varchar(10) NOT NULL, kota_kabupaten varchar(50) NOT NULL, provinsi varchar(50) NOT NULL, rt varchar(20) NOT NULL, rw varchar(20) NOT NULL, updated_date timestamp(6) NOT NULL, mst_customer_id uuid NOT NULL, CONSTRAINT mst_alamat_customer_pkey PRIMARY KEY (id), CONSTRAINT fkl0sn6rhuludsia9esfgigefvn FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id));

-- Permissions

ALTER TABLE mel_saku.mst_alamat_customer OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_alamat_customer TO user_saku;


-- mel_saku.mst_karyawan definition

-- Drop table

-- DROP TABLE mel_saku.mst_karyawan;

CREATE TABLE mel_saku.mst_karyawan ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, email varchar(50) NOT NULL, nama varchar(100) NOT NULL, "password" varchar(255) NOT NULL, status bool NOT NULL, updated_date timestamp(6) NOT NULL, username varchar(20) NOT NULL, mst_branch_id uuid NOT NULL, mst_role_id uuid NOT NULL, CONSTRAINT mst_karyawan_pkey PRIMARY KEY (id), CONSTRAINT fk25y69kj0f937dvpylt38pk6bl FOREIGN KEY (mst_branch_id) REFERENCES mel_saku.mst_cabang(id), CONSTRAINT fk3j1skme0j8rcm479rvdl7qi6e FOREIGN KEY (mst_role_id) REFERENCES mel_saku.mst_role(id));

-- Permissions

ALTER TABLE mel_saku.mst_karyawan OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.mst_karyawan TO user_saku;


-- mel_saku.trx_dokumen_customer definition

-- Drop table

-- DROP TABLE mel_saku.trx_dokumen_customer;

CREATE TABLE mel_saku.trx_dokumen_customer ( id uuid NOT NULL, created_date timestamp(6) NOT NULL, doc_type varchar(20) NOT NULL, file_url varchar(100) NOT NULL, updated_date timestamp(6) NOT NULL, mst_customer_id uuid NOT NULL, CONSTRAINT trx_dokumen_customer_pkey PRIMARY KEY (id), CONSTRAINT fk90agki5encrj9v3qn9d9hmmeh FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id));

-- Permissions

ALTER TABLE mel_saku.trx_dokumen_customer OWNER TO user_saku;
GRANT ALL ON TABLE mel_saku.trx_dokumen_customer TO user_saku;


-- Foreign Key Constraints for ERD Relationships

ALTER TABLE mel_saku.mst_permission 
    ADD CONSTRAINT fk_permission_menu FOREIGN KEY (mst_menu_id) REFERENCES mel_saku.mst_menu(id);

ALTER TABLE mel_saku.mst_role_permission 
    ADD CONSTRAINT fk_role_perm_permission FOREIGN KEY (mst_permission_id) REFERENCES mel_saku.mst_permission(id),
    ADD CONSTRAINT fk_role_perm_role FOREIGN KEY (mst_role_id) REFERENCES mel_saku.mst_role(id);

ALTER TABLE mel_saku.trx_scoring_customer 
    ADD CONSTRAINT fk_scoring_customer FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id),
    ADD CONSTRAINT fk_scoring_plafond FOREIGN KEY (mst_plafond_id) REFERENCES mel_saku.mst_plafond(id);

ALTER TABLE mel_saku.trx_verifikasi_customer 
    ADD CONSTRAINT fk_verifikasi_customer FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id),
    ADD CONSTRAINT fk_verifikasi_karyawan FOREIGN KEY (mst_karyawan_id) REFERENCES mel_saku.mst_karyawan(id);

ALTER TABLE mel_saku.trx_otp 
    ADD CONSTRAINT fk_otp_customer FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id);

ALTER TABLE mel_saku.trx_pengajuan_pinjaman 
    ADD CONSTRAINT fk_pengajuan_cabang FOREIGN KEY (mst_branch_id) REFERENCES mel_saku.mst_cabang(id),
    ADD CONSTRAINT fk_pengajuan_customer FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id),
    ADD CONSTRAINT fk_pengajuan_scoring FOREIGN KEY (trx_scoring_customer_id) REFERENCES mel_saku.trx_scoring_customer(id);

ALTER TABLE mel_saku.trx_dokumen_pinjaman 
    ADD CONSTRAINT fk_dokumen_pengajuan FOREIGN KEY (trx_pengajuan_pinjaman_id) REFERENCES mel_saku.trx_pengajuan_pinjaman(id);

ALTER TABLE mel_saku.trx_review_pengajuan 
    ADD CONSTRAINT fk_review_karyawan FOREIGN KEY (mst_karyawan_id) REFERENCES mel_saku.mst_karyawan(id),
    ADD CONSTRAINT fk_review_pengajuan FOREIGN KEY (trx_pengajuan_pinjaman_id) REFERENCES mel_saku.trx_pengajuan_pinjaman(id);

ALTER TABLE mel_saku.trx_persetujuan 
    ADD CONSTRAINT fk_persetujuan_karyawan FOREIGN KEY (mst_karyawan_id) REFERENCES mel_saku.mst_karyawan(id),
    ADD CONSTRAINT fk_persetujuan_pengajuan FOREIGN KEY (trx_pengajuan_pinjaman_id) REFERENCES mel_saku.trx_pengajuan_pinjaman(id);

ALTER TABLE mel_saku.trx_pencairan 
    ADD CONSTRAINT fk_pencairan_karyawan FOREIGN KEY (mst_karyawan_id) REFERENCES mel_saku.mst_karyawan(id),
    ADD CONSTRAINT fk_pencairan_pengajuan FOREIGN KEY (trx_pengajuan_pinjaman_id) REFERENCES mel_saku.trx_pengajuan_pinjaman(id);

ALTER TABLE mel_saku.trx_angsuran 
    ADD CONSTRAINT fk_angsuran_pengajuan FOREIGN KEY (trx_pengajuan_pinjaman_id) REFERENCES mel_saku.trx_pengajuan_pinjaman(id);

ALTER TABLE mel_saku.trx_notifikasi 
    ADD CONSTRAINT fk_notifikasi_customer FOREIGN KEY (mst_customer_id) REFERENCES mel_saku.mst_customer(id),
    ADD CONSTRAINT fk_notifikasi_pengajuan FOREIGN KEY (trx_pengajuan_pinjaman_id) REFERENCES mel_saku.trx_pengajuan_pinjaman(id);

ALTER TABLE mel_saku.trx_audit_log 
    ADD CONSTRAINT fk_audit_karyawan FOREIGN KEY (mst_karyawan_id) REFERENCES mel_saku.mst_karyawan(id);

-- Permissions

GRANT ALL ON SCHEMA mel_saku TO user_saku;