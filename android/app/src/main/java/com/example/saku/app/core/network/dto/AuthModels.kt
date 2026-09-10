package com.example.saku.app.core.network.dto

import com.google.gson.annotations.SerializedName

// Login & Token DTOs
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("tokenType") val tokenType: String? = "Bearer",
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("expiresIn") val expiresIn: Long? = null,
    @SerializedName("user") val user: UserLoginProfileDto? = null
)

data class UserLoginProfileDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("nama") val nama: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("noHp") val noHp: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("tipe") val tipe: String? = null,
    @SerializedName("status") val status: Boolean? = null,
    @SerializedName("isKycVerified") val isKycVerified: Boolean? = null,
    @SerializedName("cabang") val cabang: String? = null,
    @SerializedName("permissions") val permissions: List<String>? = null
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

// OTP DTOs
data class SendOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("purpose") val purpose: String = "REGISTRATION"
)

data class VerifyOtpRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otpCode") val otpCode: String,
    @SerializedName("purpose") val purpose: String = "REGISTRATION"
)

data class VerifyOtpResponse(
    @SerializedName("valid") val valid: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("email") val email: String? = null
)

data class ForgotPasswordRequest(
    @SerializedName("email") val email: String
)

data class SendOtpResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("expiryMinutes") val expiryMinutes: Int? = null
)

data class ResetPasswordRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otpCode") val otpCode: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmNewPassword") val confirmNewPassword: String
)

// Address DTO
data class AlamatCustomerDto(
    @SerializedName("alamatLengkap") val alamatLengkap: String = "",
    @SerializedName("rt") val rt: String = "",
    @SerializedName("rw") val rw: String = "",
    @SerializedName("kelurahan") val kelurahan: String = "",
    @SerializedName("kecamatan") val kecamatan: String = "",
    @SerializedName("kotaKabupaten") val kotaKabupaten: String = "",
    @SerializedName("provinsi") val provinsi: String = "",
    @SerializedName("kodePos") val kodePos: String = ""
)

// 5-Step KYC Registration DTOs
data class RegisterStep1KtpRequestDto(
    @SerializedName("nik") val nik: String,
    @SerializedName("namaLengkap") val namaLengkap: String,
    @SerializedName("alamatKtp") val alamatKtp: AlamatCustomerDto
)

data class RegisterStep2PersonalRequestDto(
    @SerializedName("noHp") val noHp: String,
    @SerializedName("namaIbuKandung") val namaIbuKandung: String? = null,
    @SerializedName("namaBank") val namaBank: String,
    @SerializedName("noRekening") val noRekening: String,
    @SerializedName("namaRekening") val namaRekening: String,
    @SerializedName("pekerjaan") val pekerjaan: String,
    @SerializedName("tempatKerja") val tempatKerja: String,
    @SerializedName("statusPekerjaan") val statusPekerjaan: String,
    @SerializedName("pendapatan") val pendapatan: Double,
    @SerializedName("lamaBekerjaBulan") val lamaBekerjaBulan: Int,
    @SerializedName("totalCicilanLainnya") val totalCicilanLainnya: Double = 0.0,
    @SerializedName("sameAsKtp") val sameAsKtp: Boolean = true,
    @SerializedName("alamatDomisili") val alamatDomisili: AlamatCustomerDto? = null
)

data class RegisterStep4TncRequest(
    @SerializedName("isAgreed") val isAgreed: Boolean = true
)

data class RegisterStep5CompleteRequest(
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class RegisterStepResponse(
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("step") val step: Int? = null,
    @SerializedName("message") val message: String? = null
)

// Legacy (4-step) DTOs
data class RegisterStep1Request(
    @SerializedName("email") val email: String,
    @SerializedName("username") val username: String,
    @SerializedName("noHp") val noHp: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("otpCode") val otpCode: String? = null
)

data class RegisterStep2Request(
    @SerializedName("nik") val nik: String,
    @SerializedName("namaLengkap") val namaLengkap: String,
    @SerializedName("namaRekening") val namaRekening: String,
    @SerializedName("namaBank") val namaBank: String,
    @SerializedName("noRekening") val noRekening: String,
    @SerializedName("pekerjaan") val pekerjaan: String,
    @SerializedName("tempatKerja") val tempatKerja: String,
    @SerializedName("statusPekerjaan") val statusPekerjaan: String,
    @SerializedName("pendapatan") val pendapatan: Double,
    @SerializedName("lamaBekerjaBulan") val lamaBekerjaBulan: Int,
    @SerializedName("totalCicilanLainnya") val totalCicilanLainnya: Double = 0.0
)

data class RegisterStep3Request(
    @SerializedName("alamatKtp") val alamatKtp: AlamatCustomerDto,
    @SerializedName("alamatDomisili") val alamatDomisili: AlamatCustomerDto
)
