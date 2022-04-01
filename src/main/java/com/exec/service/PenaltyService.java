package com.exec.service;

import com.exec.repository.PenaltyRepository;

import org.springframework.stereotype.Service;
import com.exec.model.Penalty;
import java.util.*;

@Service
public class PenaltyService {
    
    private final PenaltyRepository penaltyRepository;

    public PenaltyService(PenaltyRepository penaltyRepository){
        this.penaltyRepository = penaltyRepository;
    }

    public void addPenalty(Penalty penalty){
        try {
            penaltyRepository.insert(penalty);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void removePenalty(String Penalty_id){
        try {
            Penalty penalty = getPenaltyById(Penalty_id);
            penaltyRepository.delete(penalty);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Penalty getPenaltyById(String id){
        return penaltyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Penalty found with id: " + id));
    }

    public List<Penalty> getAllPenalties(){
        List<Penalty> penalties = penaltyRepository.findAll();
        return penalties;
    } 


}