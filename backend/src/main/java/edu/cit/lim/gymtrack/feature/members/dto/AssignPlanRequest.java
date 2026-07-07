package edu.cit.lim.gymtrack.feature.members.dto;

public class AssignPlanRequest {

    private Long memberId;
    private Long planId;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
}
