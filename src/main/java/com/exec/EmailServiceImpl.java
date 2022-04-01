package com.exec;

import java.util.*;

import com.exec.model.Penalty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailServiceImpl extends EmailSettings {

    @Lazy
    @Autowired
    private JavaMailSender emailSender = getJavaMailSender();
    private Map<String, String> template_emails = new HashMap<String, String>();
    private Map<String, String> template_subjects = new HashMap<String, String>();

    public EmailServiceImpl() {
        template_emails.put( "OTP", "Hey %s,\n\n"
                + "Please enter the following otp to complete the sign up process\n"
                + "OTP: %s\n\n"
                + "Thank you,\n"
                + "ExeC Team");
        template_emails.put( "Campign Request", "Hey %s,\n\n"
                + "You have been requested to be a campaigner for %s\n"
                + "Please log into your ExeC account to accept or reject the request\n\n"
                + "Thank you,\n"
                + "ExeC Team");
        template_emails.put("Campaigner Acceptance", "Hey %s,\n\n"
                + "%s has accepted your campaigning request\n"
                + "Please log into your account to view the changes\n\n"
                + "Thank you,\n"
                + "ExeC Team");
        template_emails.put("Penalty Imposition", "Hey %s,\n\n"
                + "A penalty of Rs. %s and Level %s has been imposed on you on the account of violation of\n"
                + " %s of the Code of Conduct.\n"
                + "Remark- %s.\n"
                + "Penalty Id: %s\n\n"
                + "Thank you,\n"
                + "Exec Team");
        template_emails.put("Candidature Acceptance","Hey %s,\n\n"
                + "The admin has accepted your candidature request\n"
                + "You can now signup as candidate.\n\n"
                + "Thank you,\n"
                + "Exec Team");
        template_emails.put("Candidature Rejection","Hey %s,\n\n"
                + "The admin has rejected your candidature request. Please contact the Election Commission for more details.\n\n"
                + "Thank you,\n"
                + "Exec Team");

        template_subjects.put( "OTP", "Exec - Sign Up OTP");
        template_subjects.put( "Campign Request", "Exec - Campaign Request");
        template_subjects.put( "Campaigner Acceptance", "Exec - Campaigner Acceptance");
        template_subjects.put( "Campaigner Rejection", "Exec - Campaigner Rejection");
        template_subjects.put( "Candidature Acceptance", "Exec - Candidature Acceptance");
        template_subjects.put( "Candidature Rejection", "Exec - Candidature Rejection");
        template_subjects.put( "Penalty Imposition", "Exec - Penalty Imposed");
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        emailSender.send(message);
    }

    public void sendOTPMessage(String to, String name, String otp) {
        String text = String.format(template_emails.get("OTP"), name, otp);
        String subject = template_subjects.get("OTP");
        sendSimpleMessage(to, subject, text);
    }

    public void sendCampaignRequestMessage(String to, String campaigner, String campaignee) {
        String text = String.format(template_emails.get("Campign Request"), campaigner, campaignee);
        String subject = template_subjects.get("Campign Request");
        sendSimpleMessage(to, subject, text);
    }

    public void sendCampaignerAcceptanceMessage(String to, String campaigner, String campaignee) {
        String text = String.format(template_emails.get("Campaigner Acceptance"), campaignee, campaigner);
        String subject = template_subjects.get("Campaigner Acceptance");
        sendSimpleMessage(to, subject, text);
    }

    public void sendPenaltyImpositionMessage(String to, Penalty pen) {
        String text = String.format(template_emails.get("Penalty Imposition"), pen.name, pen.fine, pen.level, pen.part, pen.remark, pen.id);
        String subject = template_subjects.get("Penalty Imposition");
        sendSimpleMessage(to, subject, text);
    }

    public void sendCandidatureAcceptanceMessage(String to, String name) {
        String text = String.format(template_emails.get("Candidature Acceptance"), name);
        String subject = template_subjects.get("Candidature Acceptance");
        sendSimpleMessage(to, subject, text);
    }
    
    public void sendCandidatureRejectionMessage(String to, String name) {
        String text = String.format(template_emails.get("Candidature Rejection"), name);
        String subject = template_subjects.get("Candidature Rejection");
        sendSimpleMessage(to, subject, text);
    }
}
