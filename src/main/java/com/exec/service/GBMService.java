package com.exec.service;
import com.exec.repository.GBMRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

import com.exec.model.Candidate;
import com.exec.model.GBM;


@Lazy
@Service
public class GBMService {
    
    private final GBMRepository gbmRepository;
    private final CandidateService candidateService;
    
    public GBMService(GBMRepository gbmRepository, CandidateService candidateService) {
        this.gbmRepository = gbmRepository;
        this.candidateService = candidateService;
    }

    public void addGBM(GBM gbm) {
        gbmRepository.insert(gbm);
    }

    public GBM getGBMByRoll(String roll_no) {
        return gbmRepository.findById(roll_no)
                .orElseThrow(() -> new RuntimeException("No GBM found with roll_no: " + roll_no));
    }

    public void activateGBM(String roll_no, String password) {
        GBM gbm = getGBMByRoll(roll_no);
        gbm.is_activated = true;
        gbm.password = password;
        gbmRepository.save(gbm);
    }

    public void setIsCampaigner(String roll_no){
        GBM gbm = getGBMByRoll(roll_no);
        gbm.is_campaigner = true;
        gbmRepository.save(gbm);
    }

    public void addCampainRequests(String roll_no_gbm, String roll_no_candidate){

        GBM gbm = getGBMByRoll(roll_no_gbm);

        if(!gbm.is_campaigner){
            gbm.campaign_requests.add(roll_no_candidate);
            gbmRepository.save(gbm);
        }else{
            throw new RuntimeException();
        }
    }

    public List<Map<String, String>> viewCampaignRequests(String roll_no_gbm){

        GBM gbm = getGBMByRoll(roll_no_gbm);
        List<Map<String, String>> candidates = new ArrayList<Map<String, String>>();

        for(String roll_no: gbm.campaign_requests){
            Candidate candidate = candidateService.getCandidateByRoll(roll_no);
            Map<String, String> candidate_details = new HashMap<String, String>();
            candidate_details.put("name", candidate.name);
            candidate_details.put("roll_no", candidate.roll_no);
            candidate_details.put("email", candidate.email);
            candidate_details.put("post", candidate.post);
            candidates.add(candidate_details);
        }

        return candidates;
    }

    public void acceptCampaignRequest(String roll_no_gbm, String roll_no_candidate){

        GBM gbm = getGBMByRoll(roll_no_gbm);

        if(!gbm.is_campaigner || gbm.campaign_requests.contains(roll_no_candidate)){
            gbm.campaign_requests.clear();
            setIsCampaigner(roll_no_gbm);
            gbm.campaigner_of = roll_no_candidate;
            gbmRepository.save(gbm);
            candidateService.addCampaigner(roll_no_candidate, roll_no_gbm);
        }else{
            throw new RuntimeException();
        }

    }

    public void rejectCampaignRequest(String roll_no_gbm, String roll_no_candidate){

        GBM gbm = getGBMByRoll(roll_no_gbm);

        if(gbm.campaign_requests.contains(roll_no_candidate)){
            gbm.campaign_requests.remove(roll_no_candidate);
            gbmRepository.save(gbm);
        }else{
            throw new RuntimeException();
        }

    }

    public void setOtp(String roll_no, String otp) {
        GBM gbm = getGBMByRoll(roll_no);
        gbm.otp = otp;
        gbmRepository.save(gbm);
    }

    public void removeOtp(String roll_no) {
        GBM gbm = getGBMByRoll(roll_no);
        gbm.otp = null;
        gbmRepository.save(gbm);
    }
    
    public List<String> get_form_link(String roll_no){
        Candidate candidate = candidateService.getCandidateByRoll(roll_no);
        List<String> forms = new ArrayList<String>();
        for (Map.Entry<String,Integer> entry : candidate.form_link.entrySet()){
            forms.add(entry.getKey());
        }
        return forms;
    }

    public String get_name(String roll_no){
        Candidate candidate = candidateService.getCandidateByRoll(roll_no);
        return candidate.name;
    }

    public String get_post(String roll_no){
        Candidate candidate = candidateService.getCandidateByRoll(roll_no);
        return candidate.post;
    }

    public void set_applied_for_candidature(String roll_no){
        GBM gbm = getGBMByRoll(roll_no);
        if(gbm.applied_for_candidature == false)
            throw new RuntimeException("Already applied for candidature");
        gbm.applied_for_candidature = true;
        gbmRepository.save(gbm);
    }

    public void remove_applied_for_candidature(String roll_no){
        GBM gbm = getGBMByRoll(roll_no);
        if(gbm.applied_for_candidature == false)
            throw new RuntimeException("Did not apply for candidature");
        gbm.applied_for_candidature = false;
        gbmRepository.save(gbm);
    }
   
}
