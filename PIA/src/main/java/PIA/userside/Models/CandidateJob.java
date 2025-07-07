package PIA.userside.Models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "CandidateJob")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int candidateId;
    private int jobId;
    private int cityId;
    private LocalDate applicationDate;
    private String status;
}

