package com.exec.service;
import com.exec.repository.AspiringCandidateRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import com.exec.Utils;

import com.exec.model.AspiringCandidate;
import com.exec.model.Candidate;
import com.exec.model.GBM;

@Lazy
@Service
public class AspiringCandidateService{
    
    private final GBMService gbmService;
    private final CandidateService candidateService;
    private final AspiringCandidateRepository aspiringCandidateRepository;

    public AspiringCandidateService(GBMService gbmService, AspiringCandidateRepository aspiringCandidateRepository, CandidateService candidateService) {
        this.gbmService = gbmService;
        this.aspiringCandidateRepository = aspiringCandidateRepository;
        this.candidateService = candidateService;
    }


    public void addAspiringCandidate(AspiringCandidate aspiringcandidate){
        aspiringCandidateRepository.insert(aspiringcandidate);
    }

     public String applyCandidature(String roll_no_gbm, List<String> Seconders, List<String> Proposers, String manifesto, String post){
        GBM gbm = gbmService.getGBMByRoll(roll_no_gbm);

        Seconders = Utils.removeDuplicates(Seconders);
        Proposers = Utils.removeDuplicates(Proposers);

        for(String roll_no : Seconders){
            GBM _gbm = gbmService.getGBMByRoll(roll_no);

            if(_gbm.is_campaigner){
                return "Seconder";
            }
        }

        for(String roll_no : Proposers){
            GBM _gbm = gbmService.getGBMByRoll(roll_no);

            if(_gbm.is_campaigner){
                return "Proposer";
            }
        }

        if(gbm.applied_for_candidature || Seconders.size() < 2 || Proposers.size() < 1 || manifesto == null || post == null){
            throw new RuntimeException();
        }else{
            AspiringCandidate aspiringcandidate = new AspiringCandidate(gbm.roll_no, gbm.name, gbm.email, Seconders, Proposers, manifesto, post);
            addAspiringCandidate(aspiringcandidate);
        }

        return null;
    }

    public List<AspiringCandidate> viewAllAspiringCandidates(){
        List<AspiringCandidate> allAspiringCandidate = aspiringCandidateRepository.findAll();
        return allAspiringCandidate;
    }

    public AspiringCandidate getAspiringCandidateByRoll(String roll){
        return aspiringCandidateRepository.findById(roll)
                .orElseThrow(() -> new RuntimeException("No Aspiring Candidate found with roll_no: " + roll));
    }

    //TODO: applied_for_candidature = False implement this
    public void deleteCandidature(String roll){
        AspiringCandidate aspiringCandidate = getAspiringCandidateByRoll(roll);
        aspiringCandidateRepository.delete(aspiringCandidate);
    }

    public void acceptCandidature(String roll, String name, String email){
        AspiringCandidate aspiringCandidate = getAspiringCandidateByRoll(roll);
        Candidate candidate = new Candidate(roll, name, email, aspiringCandidate.post, aspiringCandidate.Seconders, aspiringCandidate.Proposers, aspiringCandidate.manifesto);
        for(String roll_no : aspiringCandidate.Seconders){
            GBM gbm = gbmService.getGBMByRoll(roll_no);

            if(!gbm.is_campaigner){
                gbmService.setIsCampaigner(roll_no);
            }else{
                throw new RuntimeException();
            }
        }

        for(String roll_no : aspiringCandidate.Proposers){
            GBM gbm = gbmService.getGBMByRoll(roll_no);

            if(!gbm.is_campaigner){
                gbmService.setIsCampaigner(roll_no);
            }else{
                throw new RuntimeException();
            }
        }
        candidateService.addCandidate(candidate);
        aspiringCandidateRepository.delete(aspiringCandidate);        
    }
}
