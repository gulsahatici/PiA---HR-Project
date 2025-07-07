package PIA.userside.Controllers;


import PIA.userside.Models.Inputs.CandidateInput;
import PIA.userside.Models.Inputs.Filter;
import PIA.userside.Models.Major;
import PIA.userside.Models.Outputs.FilterOutput;
import PIA.userside.Services.UsersideService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/userside")
public class UsersideController {

    private final UsersideService usersideService;

    public UsersideController(UsersideService usersideService) {
        this.usersideService = usersideService;
    }

    @PostMapping(value = "/application/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addApplication(
            @RequestPart("candidate") CandidateInput candidateInput,
            @RequestPart("cv") MultipartFile cvFile) {
        try {
            System.out.println("CONTROLLER::applicationAdd::Executed");

            String result = usersideService.addApplication(candidateInput, cvFile);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("CONTROLLER::application/add::ERROR " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("CONTROLLER::application/add::ERROR:: " + e.getMessage());
        }
    }


    @GetMapping("/get/majors")
    public ResponseEntity<?> getMajors() {
        try{
            System.out.println("CONTROLLER::getmajors::Executed");
            List<String> result = usersideService.getMajors();
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("CONTROLLER::getMajors::ERROR:: " + e.getMessage());

        }
    }

    @GetMapping("/get/universities")
    public ResponseEntity<?> getUniversities() {
        try{
            System.out.println("CONTROLLER::getUniversities::Executed");
            List<String> result = usersideService.getUniversities();
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("CONTROLLER::getUniversities::ERROR:: " + e.getMessage());
        }
    }

    @PostMapping("/download/cv")
    public ResponseEntity<?> downloadCV(@RequestParam String email) {
        try {
            System.out.println("CONTROLLER::downloadCV::Executed");
            byte[] cvFile = usersideService.downloadCV(email);

            if (cvFile == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("CONTROLLER::downloadCV::ERROR:: No CV found for the given email");
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"cv.pdf\"")
                    .body(cvFile);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("CONTROLLER::downloadCV::ERROR:: " + e.getMessage()).getBytes());
        }
    }

    @PostMapping("/filter/candidates")
    public ResponseEntity<?> filterCandidates(@RequestBody List<Filter> filters){
        try{
            System.out.println("CONTROLLER::filterCandidates::Executed");
            List<FilterOutput> result = usersideService.filterCandidates(filters);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("CONTROLLER::filterCandidates::ERROR:" + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("CONTROLLER::filterCandidates::ERROR:: " + e.getMessage());
        }
    }

    // Test API
    @PostMapping(value = "/uploadtest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadTest(
            @RequestPart("candidate") CandidateInput candidateInput,
            @RequestPart("cv") MultipartFile cvFile) {
        try {
            return ResponseEntity.ok("Hello World");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("CONTROLLER::test::Validation failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        try {
            return ResponseEntity.ok("Hello World");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("CONTROLLER::test::Validation failed: " + e.getMessage());
        }
    }

}
