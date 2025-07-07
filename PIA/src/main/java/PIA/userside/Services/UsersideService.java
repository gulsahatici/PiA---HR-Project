package PIA.userside.Services;


import PIA.userside.Models.*;
import PIA.userside.Models.Inputs.CandidateInput;
import PIA.userside.Models.Inputs.Filter;
import PIA.userside.Models.Outputs.FilterOutput;
import PIA.userside.Repositories.UsersideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import static PIA.userside.GlobalData.Constants.*;

@Service
public class UsersideService {

    private final UsersideRepository usersideRepository;

    public UsersideService(UsersideRepository usersideRepository) {
        this.usersideRepository = usersideRepository;
    }


    // Helper Function
    public String capitalizeName(String name) {
        if (name == null || name.isBlank()) return name;

        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    public String normalizeSex(String sex) {
        if (sex == null) return null;

        String normalized = sex.trim().toLowerCase();
        if (sexes.contains(normalized)) {
            System.out.println("Normalized: " +  normalized);
            return normalized;
        } else {
            return null;
        }
    }

    public String normalizeEnglishLevel(String level) {
        if (level == null) return null;

        String normalized = level.trim().toLowerCase();
        if (englishLevels.contains(normalized)) {
            return normalized;
        } else {
            return null;
        }
    }

    public String normalizeCurrentYear(String year) {
        if (year == null) return null;

        String normalized = year.trim().toLowerCase();
        if (years.contains(normalized)) {
            return normalized;
        } else {
            return null;
        }
    }


    @Transactional
    // Add Multipart Later::DONE
    public String addApplication(CandidateInput candidate, MultipartFile cvFile) {
        try{
            List<String> nullErrors = new ArrayList<>();
            List<String> logicErrors = new ArrayList<>();

            // <editor-fold desc="Null Checks">
            // CHECKS
            String name = candidate.getCandidateName();
            if (name == null || name.isBlank()) {
                nullErrors.add("candidateName");
            }
            else{
                name = capitalizeName(name);
                candidate.setCandidateName(name);
            }

            String surname = candidate.getCandidateSurname();
            if (surname == null || surname.isBlank()) {
                nullErrors.add("candidateSurname");
            }
            else {
                surname = capitalizeName(surname);
                candidate.setCandidateSurname(surname);
            }

            LocalDate birthDay = candidate.getCandidateBirthDay();
            if (birthDay == null) {
                nullErrors.add("candidateBirthDay");
            } else if (birthDay.isAfter(LocalDate.now())) {
                logicErrors.add("candidateBirthDay can not be in the future.");
            }

            String email = candidate.getCandidateEmail();
            if (email == null || email.isBlank()) {
                nullErrors.add("candidateEmail");
            }

            String phone = candidate.getCandidatePhone();
            if (phone == null || phone.isBlank()) {
                nullErrors.add("candidatePhone");
            }

            String university = candidate.getCandidateUniversity();
            if (university == null || university.isBlank()) {
                nullErrors.add("candidateUniversity");
            }

            String major = candidate.getCandidateMajor();
            if (major == null || major.isBlank()) {
                nullErrors.add("candidateMajor");
            }

            BigDecimal gpa = candidate.getCandidateGPA();
            if (gpa == null) {
                nullErrors.add("candidateGPA");
            } else if (gpa.compareTo(BigDecimal.ZERO) < 0 || gpa.compareTo(new BigDecimal("4.0")) > 0) {
                logicErrors.add("candidateGPA must be between 0 and 4");
            }

            String sex = candidate.getCandidateSex();
            if (sex == null || sex.isBlank()) {
                nullErrors.add("candidateSex");
            }
            else {
                sex = normalizeSex(sex);
                if (sex == null) {
                    logicErrors.add("candidateSex must be either 'male' or 'female'");
                }
            }

            String currentYear = candidate.getCandidateCurrentYear();
            if (currentYear == null || currentYear.isBlank()) {
                nullErrors.add("candidateCurrentYear");
            }
            else {
                currentYear = normalizeCurrentYear(currentYear);
                if (currentYear == null) {
                    logicErrors.add("candidateCurrentYear must be in [freshman, sophomore, junior, senior, graduate]");
                }
            }
            
            Integer expectedGraduateYear = candidate.getCandidateExpectedGraduateYear();
            if (expectedGraduateYear == null) {
                nullErrors.add("expectedGraduateYear");
            }

            String englishLevel = candidate.getCandidateEnglishLevel();
            if (englishLevel == null || englishLevel.isBlank()) {
                nullErrors.add("candidateEnglishLevel");
            }
            else {
                englishLevel = normalizeEnglishLevel(englishLevel);
                if (englishLevel == null) {
                    logicErrors.add("candidateEnglishLevel must be in [a1, a2, b1, b2, c1, c2]");
                }
            }

            // Additional fields (job, city, applicationDate)
            String job = candidate.getJobName();
            if (job == null || job.isBlank()) {
                nullErrors.add("jobName");
            }

            String city = candidate.getCityName();
            if (city == null || city.isBlank()) {
                nullErrors.add("cityName");
            }

            if (cvFile == null || cvFile.isEmpty()) {
                nullErrors.add("cvFile");
            }
            // </editor-fold>

            // <editor-fold desc="Error Message Set">
            // Null Error Set
            if (!nullErrors.isEmpty()) {
                String errorMessage = String.join(", ", nullErrors) + " can not be null";
                throw new IllegalArgumentException(errorMessage);
            }

            // Logic Error Set
            if (!logicErrors.isEmpty()) {
                String errorMessage = String.join("\n", logicErrors);
                throw new IllegalArgumentException(errorMessage);
            }
            // </editor-fold>

            // <editor-fold desc="Foreign Key Setting For University, Major, Job and City. Also get candidateId">
            List<String> notPresent = new ArrayList<>();
            Integer majorId = usersideRepository.getMajor(major);
            Integer jobId = usersideRepository.getJob(job);
            Integer cityId = usersideRepository.getCity(city);
            if (majorId == null){
                notPresent.add("Major: "  + major);
            }
            if (jobId == null){
                notPresent.add("Job: "  + job);
            }
            if (cityId == null){
                notPresent.add("City: "  + city);
            }
            if (!notPresent.isEmpty()) {
                String errorMessage = String.join(", ", notPresent) + " is not present in the database";
                throw new IllegalArgumentException(errorMessage);
            }
            // Add university if university is not present in the database
            Integer universityId = usersideRepository.getUniversity(university);
            if (universityId == null){
                University u = new  University();
                u.setUniversityName(university);
                addUniversity(u);
            }
            universityId = usersideRepository.getUniversity(university);
            // </editor-fold>

            // <editor-fold desc="Create Candidate">
            Candidate c = new Candidate();
            c.setCandidateName(name);
            c.setCandidateSurname(surname);
            c.setCandidateBirthDay(birthDay);
            c.setCandidateEmail(email);
            c.setCandidatePhone(phone);
            c.setCandidateUniversity(universityId);
            c.setCandidateMajor(majorId);
            c.setCandidateGPA(gpa);
            c.setCandidateSex(sex);
            c.setCandidateCurrentYear(currentYear);
            c.setCandidateExpectedGraduateYear(expectedGraduateYear);
            c.setCandidateEnglishLevel(englishLevel);
            addCandidate(c);
            // </editor-fold>

            // <editor-fold desc="Create CandidateJob">
            Integer candidateId = usersideRepository.getCandidateId(email);
            if (candidateId == null){
                throw new IllegalArgumentException("SERVICE::addApplication::Can not found Candidate with email: " + email);
            }

            CandidateJob cj = new CandidateJob();
            cj.setApplicationDate(LocalDate.now());
            cj.setStatus("pending");
            cj.setCandidateId(candidateId);
            cj.setCityId(cityId);
            cj.setJobId(jobId);
            addCandidateJob(cj);
            // </editor-fold>

            // <editor-fold desc="Save CV">

            CandidateCV cv = new CandidateCV();
            cv.setCandidateId(candidateId);
            cv.setCvFile(cvFile.getBytes());
            cv.setCvFileName(cvFile.getOriginalFilename());
            addCandidateCV(cv);
            // </editor-fold>

            return "SERVICE::addApplication::Success";
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CV file", e);
        } catch (RuntimeException e) {
            throw new RuntimeException("SERVICE::addApplication::Error" + e.getMessage(), e);
        }
    }

    // SERVICE DB SAVE
    public String addCandidate(Candidate candidate) {
        try{
            usersideRepository.addCandidate(candidate);
            return "SERVICE::addCandidate::Success";
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addCandidate::Error::" + e.getMessage(), e);
        }
    }

    public String addCity(City city) {
        try{
            usersideRepository.addCity(city);
            return "SERVICE::addCity::Success";
        }  catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addCity::Error::" + e.getMessage(), e);
        }
    }

    public String addJob(Job job) {
        try{
            usersideRepository.addJob(job);
            return "SERVICE::addJob::Success";
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addJob::Error::" + e.getMessage(), e);
        }
    }

    public String addMajor(Major major) {
        try{
            usersideRepository.addMajor(major);
            return "SERVICE::addMajor::Success";
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addMajor::Error::" + e.getMessage(), e);
        }
    }

    public String addUniversity(University university) {
        try{
            usersideRepository.addUniversity(university);
            return "SERVICE::addUniversity::Success";
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addUniversity::Error::" + e.getMessage(), e);
        }
    }

    // Relations
    public String addJobCity(JobCity jobCity) {
        try{
            usersideRepository.addJobCity(jobCity);
            return "SERVICE::addJobCity::Success";
        }  catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addJobCity::Error::" + e.getMessage(), e);
        }
    }

    public String addCandidateJob(CandidateJob candidateJob) {
        try{
            usersideRepository.addCandidateJob(candidateJob);
            return "SERVICE::addCandidateJob::Success";
        }  catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addCandidateJob::Error::" + e.getMessage(), e);
        }
    }

    public String addCandidateCV(CandidateCV candidateCV) {
        try {
            usersideRepository.addCandidateCV(candidateCV);
            return "SERVICE::addCandidateCV::Success";
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::addCandidateCV::Error::" + e.getMessage(), e);
        }
    }


    // SERVICE DB GET
    public List<String> getMajors(){
        try{
            return usersideRepository.getMajors();
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::getMajors::Error::" + e.getMessage(), e);
        }
    }

    public List<String> getUniversities(){
        try{
            return usersideRepository.getUniversities();
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::getUniversities::Error::" + e.getMessage(), e);
        }
    }

    public byte[] downloadCV(String email){
        try {
            return usersideRepository.getCandidateCV(email);
        } catch (RuntimeException e){
            throw new RuntimeException("SERVICE::downloadCV::Error::" + e.getMessage(), e);
        }
    }




    // Filter
    // CHECK LATER, MOST PARTS WRITTEN BY CHAT GPT
    public List<FilterOutput> filterCandidates(List<Filter> filters) {
        try {
            for (Filter filter : filters) {

                if (filter.getCandidateUniversities() != null) {
                    if (filter.getCandidateUniversities().isEmpty() ||
                            filter.getCandidateUniversities().stream().anyMatch(s -> s == null || s.trim().isEmpty())) {
                        throw new IllegalArgumentException("candidateUniversities cannot be empty or contain blank values.");
                    }
                }

                if (filter.getCandidateMajors() != null) {
                    if (filter.getCandidateMajors().isEmpty() ||
                            filter.getCandidateMajors().stream().anyMatch(s -> s == null || s.trim().isEmpty())) {
                        throw new IllegalArgumentException("candidateMajors cannot be empty or contain blank values.");
                    }
                }

                if (filter.getJobNames() != null) {
                    if (filter.getJobNames().isEmpty() ||
                            filter.getJobNames().stream().anyMatch(s -> s == null || s.trim().isEmpty())) {
                        throw new IllegalArgumentException("jobNames cannot be empty or contain blank values.");
                    }
                }

                if (filter.getCityNames() != null) {
                    if (filter.getCityNames().isEmpty() ||
                            filter.getCityNames().stream().anyMatch(s -> s == null || s.trim().isEmpty())) {
                        throw new IllegalArgumentException("cityNames cannot be empty or contain blank values.");
                    }
                }

                if (filter.getCandidateSexes() != null && filter.getCandidateSexes().isEmpty()) {
                    throw new IllegalArgumentException("candidateSexes cannot be empty if provided.");
                }

                if (filter.getCandidateCurrentYears() != null && filter.getCandidateCurrentYears().isEmpty()) {
                    throw new IllegalArgumentException("candidateCurrentYears cannot be empty if provided.");
                }

                if (filter.getCandidateEnglishLevels() != null && filter.getCandidateEnglishLevels().isEmpty()) {
                    throw new IllegalArgumentException("candidateEnglishLevels cannot be empty if provided.");
                }

                if (filter.getCandidateApplicationStatuses() != null && filter.getCandidateApplicationStatuses().isEmpty()) {
                    throw new IllegalArgumentException("candidateApplicationStatuses cannot be empty if provided.");
                }

                if (filter.getCandidateExpectedGraduateYears() != null && filter.getCandidateExpectedGraduateYears().isEmpty()) {
                    throw new IllegalArgumentException("candidateExpectedGraduateYears cannot be empty if provided.");
                }

                if (filter.getApplicationDates() != null && filter.getApplicationDates().isEmpty()) {
                    throw new IllegalArgumentException("applicationDates cannot be empty if provided.");
                }

                if (filter.getCandidateMinGPA() != null && filter.getCandidateMaxGPA() != null) {
                    if (filter.getCandidateMinGPA().compareTo(filter.getCandidateMaxGPA()) > 0) {
                        throw new IllegalArgumentException("candidateMinGPA cannot be greater than candidateMaxGPA.");
                    }
                }

                if (filter.getCandidateMinAge() != null && filter.getCandidateMaxAge() != null) {
                    if (filter.getCandidateMinAge() > filter.getCandidateMaxAge()) {
                        throw new IllegalArgumentException("candidateMinAge cannot be greater than candidateMaxAge.");
                    }
                }

                if (filter.getApplicationMinDate() != null && filter.getApplicationMaxDate() != null) {
                    if (filter.getApplicationMinDate().isAfter(filter.getApplicationMaxDate())) {
                        throw new IllegalArgumentException("applicationMinDate cannot be after applicationMaxDate.");
                    }
                }
            }

            return usersideRepository.filterCandidates(filters);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("SERVICE::filterCandidates::Invalid input: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new RuntimeException("SERVICE::filterCandidates::Error: " + e.getMessage(), e);
        }
    }
}
