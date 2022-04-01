package com.exec.controller;

import java.util.*;

import com.exec.EmailServiceImpl;
import com.exec.Utils;

import com.exec.repository.AdminRepository;
import com.exec.model.Admin;
import com.exec.model.AspiringCandidate;
import com.exec.model.GBM;
import com.exec.model.Penalty;
import com.exec.service.AdminService;
import com.exec.service.AspiringCandidateService;
import com.exec.service.GBMService;
import com.exec.service.PenaltyService;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@RequestMapping("/api/admin")
@RestController
public class AdminController {

    private final AdminService adminService;
    private final GBMService gbmService;
    private final AspiringCandidateService aspiringCandidateService;
    private final AdminRepository  adminRepository;
    private final PenaltyService penaltyService;
    private Utils utils=new Utils();
    private EmailServiceImpl emailSender= new EmailServiceImpl();

    public AdminController(AdminService adminService,AdminRepository adminRepository, AspiringCandidateService aspiringCandidateService, GBMService gbmService, PenaltyService penaltyService) {
        this.adminService=adminService;
        this.adminRepository=adminRepository;
        this.aspiringCandidateService = aspiringCandidateService;
        this.gbmService = gbmService;
        this.penaltyService = penaltyService;
    }


    //a temporary function for testing. Will be removed later
    @PostMapping("/addAdmin")
    public ResponseEntity<Object> addAdmin(@RequestBody Map<String,String> body) {
        
        Map<String, String> response = new HashMap<String, String>();
        try{
            PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
            String encoded_password=passwordEncoder.encode(body.get("password"));
            Admin admin;
            try{
                admin=new Admin("0",body.get("name"),body.get("email"));
                admin.password=encoded_password;
                adminRepository.insert(admin);
            }
            catch(Exception E)
            {
                response.put("message","Admin account already exists");
                return new ResponseEntity<Object>(response,HttpStatus.BAD_REQUEST);
            }
        
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> body, HttpSession session) {

        Map<String,String> response = new HashMap<>();

        try{

            String roll_no = utils.isLoggedIn(session);
            if(roll_no != null)
            {
                response.put("message", "Already logged in");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            
            Admin admin;
            try {
                admin = adminRepository.findAll().get(0);
            } 
            catch (Exception e) {
                response.put("message", "Admin account not found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if(! passwordEncoder.matches(body.get("password"), admin.password)){
                response.put("message", "Invalid credentials");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }

            session.setAttribute("roll_no", admin.roll_no);
            session.setAttribute("access_level", "Admin");
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpSession session) {

        Map<String,String> response = new HashMap<>();

        try{
            String roll_no = utils.isLoggedIn(session);
            if( roll_no==null || !session.getAttribute("access_level").equals("Admin"))
            {
                response.put("message", "Admin not logged in");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }

            session.removeAttribute("roll_no");
            session.removeAttribute("access_level");
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody Map<String, String> body, HttpSession session) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        try{
            String roll_no = utils.isLoggedIn(session);
            Map<String,String> response = new HashMap<>();

            if( roll_no==null || !session.getAttribute("access_level").equals("Admin"))
            {
                response.put("message", "Invalid password change request");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }

            Admin admin=adminRepository.findAll().get(0);
            if(! passwordEncoder.matches(body.get("password"), admin.password)){
                response.put("message", "Invalid Password");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            
            String password = passwordEncoder.encode(body.get("new_password"));
            admin.password=password;
            adminRepository.save(admin);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PostMapping("/addAnnouncement")
    public ResponseEntity<Object> addAnnouncement(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "Invalid add announcement request");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            response.put("pk",Integer.toString(adminService.add_announcement(body.get("announcement"))));
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/removeAnnouncement")
    public ResponseEntity<Object> remove_form_link(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "Invalid remove announcement request");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            adminService.remove_announcement(body.get("announcement"));
    
           return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/viewMyAnnouncements")
    public ResponseEntity<Object> view_my_forms(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "Invalid view request");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            List <String> announcements = adminService.view_announcements(); 
            for (Integer i = 0; i < announcements.size(); ++i){
                response.put("announcement" + i.toString(), announcements.get(i));
            }
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/viewAllNominations")
    public ResponseEntity<Object> view_all_nominations(HttpSession session){

        Map<String, String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "No Admin login found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            List<AspiringCandidate> nominations = aspiringCandidateService.viewAllAspiringCandidates();
            return new ResponseEntity<Object>(nominations, HttpStatus.OK);
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/rejectNomination")
    public ResponseEntity<Object> reject_nomination(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            GBM gbm;
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "No Admin login found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }

            try{
                gbm = gbmService.getGBMByRoll(body.get("roll_no"));
                aspiringCandidateService.deleteCandidature(body.get("roll_no"));
                gbmService.remove_applied_for_candidature(roll_no);
            }
            catch(Exception E)
            {
                response.put("message", "No such nomination found");
                return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
            }
            emailSender.sendCandidatureRejectionMessage(gbm.email, gbm.name);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/acceptNomination")
    public ResponseEntity<Object> accept_nomination(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            GBM gbm;
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "No Admin login found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }

            try{
                gbm = gbmService.getGBMByRoll(body.get("roll_no"));
                aspiringCandidateService.acceptCandidature(body.get("roll_no"), gbm.name, gbm.email);
            }
            catch(Exception E)
            {
                response.put("message", "No such nomination found");
                return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
            }
            emailSender.sendCandidatureAcceptanceMessage(gbm.email, gbm.name);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/addPenalty")
    public ResponseEntity<Object> add_penalty(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "No Admin login found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            try{
                Penalty penalty;
                if(body.containsKey("remark"))
                    penalty = new Penalty(body.get("role"), body.get("name"), body.get("roll_no"), body.get("fine"), body.get("level"), body.get("part"), body.get("remark"));
                else
                    penalty = new Penalty(body.get("role"), body.get("name"), body.get("roll_no"), body.get("fine"), body.get("level"), body.get("part"), "");
                penaltyService.addPenalty(penalty);
            }
            catch(Exception E)
            {
                response.put("message", "Not all parameters are present");
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/removePenalty")
    public ResponseEntity<Object> remove_penalty(@RequestBody Map<String, String> body, HttpSession session){
        Map<String,String> response = new HashMap<>();
        try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "No Admin login found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            try{
                penaltyService.removePenalty(body.get("penalty_id"));
            }
            catch(Exception E)
            {
                response.put("message", "No penalty with this penalty_id found");
                return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception E){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/viewAllPenalties")
    public ResponseEntity<Object> view_all_penalties(HttpSession session){
        Map<String, String> response = new HashMap<>();
        // try{
            String roll_no = utils.isLoggedIn(session);
            if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
                response.put("message", "No Admin login found");
                return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            List<Penalty> penalties = penaltyService.getAllPenalties();
            return new ResponseEntity<Object>(penalties, HttpStatus.OK);
        // }
        // catch(Exception E){
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        // }
    }
    //WARNING: DO NOT UNCOMMENT THE UNDERLYING PORTION

    // @PostMapping("/populate")
    // public ResponseEntity<Object> populate(HttpSession session){
    //     Map<String,String> response = new HashMap<>();
    //     try{
    //         String roll_no = utils.isLoggedIn(session);
    //         if(roll_no == null || !session.getAttribute("access_level").equals("Admin")){
    //             response.put("message", "No Admin login found");
    //             return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
    //         }
    //         adminService.populate();
    //         return ResponseEntity.status(HttpStatus.OK).build();
    //     }
    //     catch(Exception E){
    //         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    //     }
    // }

}
