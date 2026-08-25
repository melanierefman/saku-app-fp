package com.bcafinance.backend_saku.core.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    public void sendOtpEmail(String toEmail, String otpCode, String purpose, int expiryMinutes) {
        String purposeTitle = formatPurpose(purpose);
        String subject = "Kode OTP SAKU: " + otpCode + " - " + purposeTitle;
        String expiryText = expiryMinutes + " menit";

        log.info("=================================================");
        log.info("📧 [EMAIL OTP DISPATCHER]");
        log.info("To      : {}", toEmail);
        log.info("Subject : {}", subject);
        log.info("Purpose : {}", purpose);
        log.info("OTP Code: {}", otpCode);
        log.info("Expiry  : {} menit", expiryMinutes);
        log.info("=================================================");

        if (mailSender != null && senderEmail != null && !senderEmail.isBlank()) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                helper.setFrom(senderEmail, "SAKU");
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(buildHtmlEmail(otpCode, purposeTitle, expiryText), true);

                mailSender.send(mimeMessage);
                log.info("✅ Email OTP berhasil dikirimkan secara nyata ke {}", toEmail);
            } catch (Exception e) {
                log.error("❌ Gagal mengirim email via SMTP: {}. Kode OTP tetap tercatat di log.", e.getMessage());
            }
        } else {
            log.warn("⚠️ SMTP Credentials belum disetel di .env (MAIL_USERNAME/MAIL_PASSWORD). Email disimulasikan di console.");
        }
    }

    private String buildHtmlEmail(String otpCode, String purposeTitle, String expiryText) {
        String template = """
            <div style="font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 540px; margin: 0 auto; padding: 24px; background-color: #f8fafc; border-radius: 16px;">
                <div style="background: linear-gradient(135deg, #FF792E 0%, #FF5B00 100%); padding: 24px; text-align: center; border-radius: 12px 12px 0 0;">
                    <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: 800; letter-spacing: 3px;">SAKU</h1>
                    <p style="color: #fff2ea; margin: 4px 0 0; font-size: 13px; font-weight: 500;">Aplikasi Finansial SAKU</p>
                </div>
                <div style="background-color: #ffffff; padding: 32px 28px; border-radius: 0 0 12px 12px; border: 1px solid #fed7aa; border-top: none; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);">
                    <h2 style="color: #1e293b; font-size: 18px; margin-top: 0; font-weight: 700;">Verifikasi {{PURPOSE_TITLE}}</h2>
                    <p style="color: #475569; font-size: 14px; line-height: 1.6;">
                        Gunakan kode OTP berikut untuk melanjutkan proses <strong>{{PURPOSE_TITLE}}</strong> di akun SAKU Anda:
                    </p>
                    <div style="text-align: center; margin: 28px 0;">
                        <div style="display: inline-block; background-color: #fff7ed; padding: 14px 28px; border-radius: 12px; font-size: 34px; font-weight: 800; letter-spacing: 8px; color: #FF792E; border: 2px dashed #FF792E;">
                            {{OTP_CODE}}
                        </div>
                    </div>
                    <p style="color: #ea580c; font-size: 13px; text-align: center; margin-bottom: 24px; font-weight: 500;">
                        ⏰ Kode ini berlaku selama <strong>{{EXPIRY_TEXT}}</strong>.
                    </p>
                    <hr style="border: none; border-top: 1px solid #f1f5f9; margin: 20px 0;" />
                    <p style="color: #64748b; font-size: 12px; line-height: 1.5; margin-bottom: 0;">
                        <strong>Penting:</strong> Jangan berikan kode OTP ini kepada siapa pun, termasuk pihak yang mengatasnamakan tim SAKU.
                    </p>
                </div>
                <div style="text-align: center; margin-top: 20px;">
                    <p style="color: #94a3b8; font-size: 11px; margin: 0;">&copy; 2026 SAKU. All rights reserved.</p>
                </div>
            </div>
            """;

        return template
                .replace("{{PURPOSE_TITLE}}", purposeTitle)
                .replace("{{OTP_CODE}}", otpCode)
                .replace("{{EXPIRY_TEXT}}", expiryText);
    }


    private String formatPurpose(String purpose) {
        if (purpose == null) return "Verifikasi";
        return switch (purpose.toUpperCase()) {
            case "REGISTRATION" -> "Registrasi Akun";
            case "LOGIN", "LOGIN_2FA" -> "Login Akun";
            case "RESET_PASSWORD" -> "Reset Password";
            case "PENCAIRAN" -> "Pencairan Pinjaman";
            case "VERIFIKASI_TRANSAKSI" -> "Verifikasi Transaksi";
            default -> purpose;
        };
    }
}

