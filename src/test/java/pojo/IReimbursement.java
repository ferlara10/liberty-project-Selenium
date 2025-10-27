package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IReimbursement {

    @JsonProperty("Company")
    private String Company;
    @JsonProperty("Scenario")
    private String Scenario;
    @JsonProperty("Description")
    private String Description;
    @JsonProperty("Employee")
    private String Employee;
    @JsonProperty("DateBeg")
    private String DateBeg;

    @JsonProperty("Reason")
    private String Reason;
    @JsonProperty("Reimbursement")
    private String Reimbursement;
    @JsonProperty("Quantity")
    private String Quantity;
    @JsonProperty("Amount")
    private String Amount;

    @JsonProperty("_inactive")
    private String inactive;
    @JsonProperty("_user")
    private String user;
    @JsonProperty("_ts")
    private String ts;

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getScenario() {
        return Scenario;
    }

    public void setScenario(String scenario) {
        Scenario = scenario;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getEmployee() {
        return Employee;
    }

    public void setEmployee(String employee) {
        Employee = employee;
    }

    public String getDateBeg() {
        return DateBeg;
    }

    public void setDateBeg(String dateBeg) {
        DateBeg = dateBeg;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getReimbursement() {
        return Reimbursement;
    }

    public void setReimbursement(String reimbursement) {
        Reimbursement = reimbursement;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }
}
