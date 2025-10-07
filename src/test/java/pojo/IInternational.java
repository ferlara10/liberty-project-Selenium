package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IInternational {

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
    @JsonProperty("WageType")
    private String WageType;
    @JsonProperty("Number")
    private String Number;

    @JsonProperty("Reason")
    private String Reason;
    @JsonProperty("CostCenter")
    private String CostCenter;

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

    public String getWageType() {
        return WageType;
    }

    public void setWageType(String wageType) {
        WageType = wageType;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getCostCenter() {
        return CostCenter;
    }

    public void setCostCenter(String costCenter) {
        CostCenter = costCenter;
    }
}
