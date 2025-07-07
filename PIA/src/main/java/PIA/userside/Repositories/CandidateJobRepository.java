package PIA.userside.Repositories;

import PIA.userside.Models.CandidateJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



import java.util.List;

@Repository
public interface CandidateJobRepository extends JpaRepository<CandidateJob, Integer> {
    List<CandidateJob> findByStatus(String status);
}
