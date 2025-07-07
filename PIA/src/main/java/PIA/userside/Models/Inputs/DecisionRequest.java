package PIA.userside.Models.Inputs;

public class DecisionRequest {
    private String candidateEmail;
    private String decision;

    // Getters and Setters
    public String getCandidateEmail() { return candidateEmail; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}
