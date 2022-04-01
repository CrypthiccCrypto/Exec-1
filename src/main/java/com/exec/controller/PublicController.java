package com.exec.controller;

// import java.util.Map;
import com.exec.service.CandidateService;
import com.exec.service.PenaltyService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.exec.service.AdminService;
// import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Integral;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.exec.model.*;
@RestController
public class PublicController {
    
    private final CandidateService candidateservice;
    private final AdminService adminservice;
    private final PenaltyService penaltyService;
    // private final GBMService gbmservice;

    public PublicController(CandidateService candidateservice, AdminService adminservice, PenaltyService penaltyService) {
        this.candidateservice = candidateservice;
        this.penaltyService = penaltyService;
        // this.gbmservice = gbmservice;
        this.adminservice = adminservice;
    }

    @GetMapping("/viewCandidates")
    public ResponseEntity<Object> viewCandidates(){
        return new ResponseEntity<Object>(candidateservice.getAllCandidatesBasicInfo(), HttpStatus.OK);
    }

    @GetMapping("/viewCandidate")
    public ResponseEntity<Object> viewCandidate(@RequestParam(value = "roll") String roll){
        Candidate candidate = candidateservice.getCandidateByRoll(roll);
        Map<String, String> candidateInfo = new HashMap<String, String>();

        candidateInfo.put("name", candidate.name);
        candidateInfo.put("post", candidate.post);
        candidateInfo.put("email", candidate.email);
        candidateInfo.put("manifesto", candidate.manifesto_link);
        candidateInfo.put("poster", candidate.poster_link);

        Integer listSize = candidate.Proposers.size();
        candidateInfo.put("num_proposers", Integer.toString(listSize));
        listSize = candidate.Seconders.size();
        candidateInfo.put("num_seconders", Integer.toString(listSize));
        listSize = candidate.video_links.size();
        candidateInfo.put("num_video_links", Integer.toString(listSize));

        String temp;
        // * Adding all the proposers, seconders, links 
        temp = "proposer";
        for (int i = 0; i < candidate.Proposers.size(); i++) {
            candidateInfo.put(temp + Integer.toString(i+1), candidate.Proposers.get(i));
        }

        temp = "seconder";
        for (int i = 0; i < candidate.Seconders.size(); i++) {
            candidateInfo.put(temp + Integer.toString(i+1), candidate.Seconders.get(i));
        }

        temp = "videolink";
        for (int i = 0; i < candidate.video_links.size(); i++) {
            candidateInfo.put(temp + Integer.toString(i+1), candidate.video_links.get(i));
        }
        
        return  new ResponseEntity<Object>(candidateInfo, HttpStatus.OK);
    }
    @GetMapping("/viewAnnouncements")
    public ResponseEntity<Object> viewAnnouncements(){
        Map<String, String> response = new HashMap<String, String>();
        try{
        List<String> announcements = adminservice.view_announcements();
        for (Integer i = 0; i < announcements.size(); ++i){
                response.put("announcement" + i.toString(), announcements.get(i));
            }
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch (Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/getPenaltyCsv")
    public void viewPenalties(HttpServletResponse response){

        try{
            String filename = "Penalties.csv";

            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"" + filename + "\"");

            StatefulBeanToCsv<Penalty> writer = new StatefulBeanToCsvBuilder<Penalty>(response.getWriter())
                                                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                                                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                                                    .withOrderedResults(false).build();

            writer.write(penaltyService.getAllPenalties());
        }
        catch (Exception E){
            
        }
    }

}
