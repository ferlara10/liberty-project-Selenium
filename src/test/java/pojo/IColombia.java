package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IColombia {

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
    @JsonProperty("Class")
    private String Classe;
    @JsonProperty("Time")
    private String Time;
    @JsonProperty("OutTime")
    private String OutTime;
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
        this.Company = company;
    }

    public String getScenario() {
        return Scenario;
    }

    public void setScenario(String scenario) {
        this.Scenario = scenario;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getEmployee() {
        return Employee;
    }

    public void setEmployee(String employee) {
        this.Employee = employee;
    }

    public String getDateBeg() {
        return DateBeg;
    }

    public void setDateBeg(String dateBeg) {
        this.DateBeg = dateBeg;
    }

    public String getClasse() {
        return Classe;
    }

    public void setClass(String aClass) {
        this.Classe = aClass;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getOutTime() {
        return OutTime;
    }

    public void setOutTime(String outTime) {
        this.OutTime = outTime;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        this.Reason = reason;
    }

    public String getCostCenter() {
        return CostCenter;
    }

    public void setCostCenter(String costCenter) {
        this.CostCenter = costCenter;
    }
}
