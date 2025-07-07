package PIA.userside.Controllers;


import PIA.userside.Models.Inputs.DecisionRequest;
import PIA.userside.Models.CandidateJob;
import PIA.userside.Repositories.CandidateJobRepository;
import PIA.userside.Services.MailService;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private CandidateJobRepository candidateJobRepository;

    @PostMapping("/decision")
    public ResponseEntity<String> sendDecisionEmail(@RequestBody DecisionRequest request) {
        try {
            // Mail gönder
            MailService.sendDecisionMail(request.getCandidateEmail(), request.getDecision());

            // (örnek id ile) kayıt oluştur/veritabanında güncelle
            CandidateJob record = new CandidateJob();
            record.setCandidateId(1); // frontend'den alınmalı
            record.setJobId(1);       // frontend'den alınmalı
            record.setCityId(1);      // dummy
            record.setApplicationDate(LocalDate.now());
            record.setStatus(request.getDecision());

            candidateJobRepository.save(record);

            return ResponseEntity.ok("Mail gönderildi ve başvuru durumu kaydedildi.");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Mail hatası: " + e.getMessage());
        }
    }

    @GetMapping("/applications")
    public List<CandidateJob> getAllApplications() {
        return candidateJobRepository.findAll();
    }

    @GetMapping("/applications/accepted")
    public List<CandidateJob> getAccepted() {
        return candidateJobRepository.findByStatus("accept");
    }

    @GetMapping("/applications/rejected")
    public List<CandidateJob> getRejected() {
        return candidateJobRepository.findByStatus("reject");
    }
}