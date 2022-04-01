package com.exec.service;

import java.util.ArrayList;

// import javax.management.RuntimeErrorException;

import com.exec.model.Candidate;
import com.exec.model.GBM;
import com.exec.repository.CandidateRepository;
import com.exec.repository.GBMRepository;
import java.util.*;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final GBMRepository gbmRepository;

    public CandidateService(CandidateRepository candidateRepository, GBMRepository gbmRepository){
        this.candidateRepository = candidateRepository;
        this.gbmRepository = gbmRepository;
    }

    public void addCandidate(Candidate candidate){
        candidateRepository.insert(candidate);
    }

    public Candidate getCandidateByRoll(String roll_no) {
        return candidateRepository.findById(roll_no)
                .orElseThrow(() -> new RuntimeException("No Candidate found with roll_no: " + roll_no));
    }

    public void activateCandidate(String roll_no, String password) {
        Candidate candidate = getCandidateByRoll(roll_no);
        candidate.is_activated = true;
        candidate.password = password;
        candidateRepository.save(candidate);
    }

    public void setOtp(String roll_no, String otp) {
        Candidate candidate = getCandidateByRoll(roll_no);
        candidate.otp = otp;
        candidateRepository.save(candidate);
    }

    public void removeOtp(String roll_no) {
        Candidate candidate = getCandidateByRoll(roll_no);
        candidate.otp = null;
        candidateRepository.save(candidate);
    }

    public void addCampaigner(String roll_no_candidate, String roll_no_gbm){
        
        Candidate candidate = getCandidateByRoll(roll_no_candidate);
        GBM gbm = getGBMByRoll(roll_no_gbm);

        if(!gbm.is_campaigner){
            candidate.Campaigners.add(roll_no_gbm);
            candidateRepository.save(candidate);
        }else{
            throw new RuntimeException();
        }
    }

    public GBM getGBMByRoll(String roll_no) {
        return gbmRepository.findById(roll_no)
                .orElseThrow(() -> new RuntimeException("No GBM found with roll_no: " + roll_no));
    }

    public Integer add_form(String roll_no_candidate, String form_link){
        Candidate candidate = getCandidateByRoll(roll_no_candidate);
        if(candidate.is_activated == true){
            List<Integer> value_set = new ArrayList<Integer>();
            for (Map.Entry<String,Integer> entry : candidate.form_link.entrySet()){
                value_set.add(entry.getValue());
            }
            Integer new_pk = 0;
            for ( int i =0; i<value_set.size();++i){
                if(value_set.get(i)>new_pk){
                    new_pk = value_set.get(i);
                }
            }
            new_pk = new_pk +1;
            candidate.form_link.put(form_link,new_pk);
            candidateRepository.save(candidate);
            return new_pk;
            // return 1;
        }
        else{
            throw new RuntimeException();
        }
    }
    public void remove_form(String roll_no, String link){
        Candidate candidate = getCandidateByRoll(roll_no);
        if(candidate.is_activated == true){
            candidate.form_link.remove(link);
            candidateRepository.save(candidate);
        }
        else{
            throw new RuntimeException();
        }
    }

    public ArrayList<String> view_forms(String roll_no){
        Candidate candidate = getCandidateByRoll(roll_no);
        if(candidate.is_activated == true){
            Set<String> key_set = candidate.form_link.keySet();
            return new ArrayList<String>(key_set);
        }
        else{
            throw new RuntimeException();
        }
    }

    public List< Map<String, String> > getAllCandidatesBasicInfo(){
        List<Candidate> allCandidates = candidateRepository.findAll();
        List< Map<String, String> > BasicInfo = new ArrayList< Map<String, String> >();
        
        for(Candidate candidate: allCandidates){
            Map<String, String> candidateInfo = new HashMap<String, String>();
            candidateInfo.put("name", candidate.name);
            candidateInfo.put("post", candidate.post);
            candidateInfo.put("email", candidate.email);

            BasicInfo.add(candidateInfo);
        }
        return BasicInfo;
    }

    public void add_video(String roll_no, String link){
        Candidate candidate = getCandidateByRoll(roll_no);
        if(candidate.is_activated == true){
            candidate.video_links.add(link);
            candidateRepository.save(candidate);
        }
        else{
            throw new RuntimeException();
        }
    }

    public void add_poster(String roll_no, String link){
        Candidate candidate = getCandidateByRoll(roll_no);
        if(candidate.is_activated == true){
            candidate.poster_link = link;
            candidateRepository.save(candidate);
        }
        else{
            throw new RuntimeException();
        }
    }
}
