package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IPanama {

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

    @JsonProperty("Jornal")
    private String Jornal;
    @JsonProperty("Schedule")
    private String Schedule;

    @JsonProperty("Time")
    private String Time;
    @JsonProperty("OutTime")
    private String OutTime;
    @JsonProperty("Time2")
    private String Time2;
    @JsonProperty("OutTime2")
    private String OutTime2;
    @JsonProperty("Time3")
    private String Time3;
    @JsonProperty("OutTime3")
    private String OutTime3;

    @JsonProperty("DayOpt1")
    private String DayOpt1;
    @JsonProperty("DayOpt2")
    private String DayOpt2;
    @JsonProperty("DayOpt3")
    private String DayOpt3;

    @JsonProperty("Lunch")
    private String Lunch;
    @JsonProperty("Reason")
    private String Reason;


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

    public String getJornal() {
        return Jornal;
    }

    public void setJornal(String jornal) {
        Jornal = jornal;
    }

    public String getSchedule() {
        return Schedule;
    }

    public void setSchedule(String schedule) {
        Schedule = schedule;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getOutTime() {
        return OutTime;
    }

    public void setOutTime(String outTime) {
        OutTime = outTime;
    }

    public String getTime2() {
        return Time2;
    }

    public void setTime2(String time2) {
        Time2 = time2;
    }

    public String getOutTime2() {
        return OutTime2;
    }

    public void setOutTime2(String outTime2) {
        OutTime2 = outTime2;
    }

    public String getTime3() {
        return Time3;
    }

    public void setTime3(String time3) {
        Time3 = time3;
    }

    public String getOutTime3() {
        return OutTime3;
    }

    public void setOutTime3(String outTime3) {
        OutTime3 = outTime3;
    }

    public String getDayOpt1() {
        return DayOpt1;
    }

    public void setDayOpt1(String dayOpt1) {
        DayOpt1 = dayOpt1;
    }

    public String getDayOpt2() {
        return DayOpt2;
    }

    public void setDayOpt2(String dayOpt2) {
        DayOpt2 = dayOpt2;
    }

    public String getDayOpt3() {
        return DayOpt3;
    }

    public void setDayOpt3(String dayOpt3) {
        DayOpt3 = dayOpt3;
    }

    public String getLunch() {
        return Lunch;
    }

    public void setLunch(String lunch) {
        Lunch = lunch;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }
}
