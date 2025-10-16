package pages;

import com.codeborne.selenide.Selenide;
import pages.co.SurchargeRequestCOPage;
import pages.co.TimeSheetRequestCOPage;
import pages.cr.TimeSheetRequestCRPage;
import pages.in.TimeSheetRequestInternationalPage;
import pages.jm.TimeSheetRequestJMPage;
import pages.pa.TimeSheetRequestPAPage;

import static com.codeborne.selenide.Selenide.*;
import static suites.utils.CommonTest.click;

public class HomePage {

    private String logoutButton = ".btn-default.margin-bottom-20";
    private String timesheetRequestLink = "a[title=\"Timesheets Requests\"]";
    private String timesheetHistoryLink = "";

    //Manager Locators
    private String tsApprovalsHistoryLink = "a[title=\"TS Approvals History\"]";

    //Colombia Locators
    private String extraHoursRequestMainLink = "a[title=\"Solicitudes de Horas Extras\"]";
    private String extraHoursEnglishRequestMainLink = "a[title=\"Timesheets Requests\"]";
    private String extraHoursRequestLink = "a[title=\"Solicitudes\"]";
    private String extraHoursHistoricLink = "a[title=\"Historial\"]";

    private String surchargeRequestsMainLink = "a[title=\"Solicitudes de Recargos\"]";
    private String overtimeApprovalsLink = "a[title=\"Aprobaciones de Horas Extras\"]";
    private String surchargeApprovalsLink = "a[title=\"Aprobaciones de Recargos\"]";

    private String overtimeApprovalsEnglishLink = "a[title=\"TS Approvals Requests\"]";

    //Multicompany
    private String intercompanyApprovesLink = "a[title=\"Aprobaciones Intercompany\"]";
    private String intercompanyApprovalsEnglishLink = "a[title=\"Intercompany Approvals\"]";
    private String LNCExtraHoursLink = "a[title=\"LNC Horas Extras\"]";
    private String approveHistoricLink = "a[title=\"Aprobaciones Historial\"]";
    private String approveHistoricEnglishLink = "a[title=\"Approvals History\"]";


    public TimeSheetRequestPage navigateSurchargeApprovalsHistoric(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(surchargeApprovalsLink).click();
            $$("a[title=\"Historial\"]").get(1).click();
            return new TimeSheetRequestPage();
        } else
            System.out.println("falta en ingles");
        return null;
    }

    public TimeSheetRequestPage navigateOvertimeApprovalsHistoricInternational(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(overtimeApprovalsLink).click();
            $$("a[title=\"Historial\"]").get(0).click();
        } else
            $(tsApprovalsHistoryLink).click();
        return new TimeSheetRequestInternationalPage();
    }

    //This navigation is when you are in the same company
    public TimeSheetRequestPage navigateOvertimeApprovalsHistoric(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(overtimeApprovalsLink).click();
            $$("a[title=\"Historial\"]").get(0).click();
            return new TimeSheetRequestPage();
        } else{
            $(tsApprovalsHistoryLink).click();
            return new TimeSheetRequestInternationalPage();
        }
    }

    public TimeSheetRequestPage navigateSurchargeApprovals(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(surchargeApprovalsLink).click();
            $$("a[title=\"Aprobaciones\"]").get(1).click();
            return new TimeSheetRequestPage();
        } else
            System.out.println("falta en ingles");
        return null;
    }

    public TimeSheetRequestPage navigateOvertimeApprovalsInternational(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(overtimeApprovalsLink).click();
            $$("a[title=\"Aprobaciones\"]").get(0).click();
        } else
            $(overtimeApprovalsEnglishLink).click();
        return new TimeSheetRequestInternationalPage();
    }

    public TimeSheetRequestPage navigateOvertimeApprovals(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(overtimeApprovalsLink).click();
            $$("a[title=\"Aprobaciones\"]").get(0).click();
            return new TimeSheetRequestPage();
        } else{
            $(overtimeApprovalsEnglishLink).click();
            return new TimeSheetRequestInternationalPage();
        }
    }

    public TimeSheetRequestPage navigateIntercompanyOvertimeInternational
            (String language, String company, String managerCompany){
        String complement = "";
        if (managerCompany.equals("LNSSV") || managerCompany.equals("LLACO") ||
            managerCompany.equals("LNGGT") || managerCompany.equals("CNDDO") ||
            managerCompany.equals("TTCCR") || managerCompany.equals("LNHHN"))                                         //TODO - Could be necessary to add more companies
            complement = " Horas";
        else
            complement = " Horas Extras";

        String locator1 = language.equals("Spanish") || language.equals("Español")
                ? intercompanyApprovesLink : intercompanyApprovalsEnglishLink;
        String locator2 = language.equals("Spanish") || language.equals("Español")
                ? "a[title=\""+company.substring(0,3)+complement+"\"]" : "a[title=\""+company.substring(0,3)+" TS Requests\"]";

        $(locator1).click();
        click(locator2,false);

        if (company.equals("CWPPA") || company.equals("LNPPA"))
            return new TimeSheetRequestPAPage();
        else
            return new TimeSheetRequestInternationalPage();
    }

    public TimeSheetRequestPage navigateIntercompanyOvertime(String language, String company){
        String locator = "a[title=\""+company.substring(0,3)+" Horas Extras\"]";
        String locatorE = "a[title=\""+company.substring(0,3)+" TS Requests\"]";
        //TODO - check if the menu language
        if (language.equals("Spanish") || language.equals("Español")){
            $(intercompanyApprovesLink).click();
            $(locator).click();
        }else{
            $(intercompanyApprovalsEnglishLink).click();
            $(locatorE).click();
            return new TimeSheetRequestInternationalPage();
        }
        return new TimeSheetRequestPage();
    }

    public TimeSheetRequestPage navigateIntercompanySurcharge(String language, String company){
        String locator = "a[title=\""+company.substring(0,3)+" Recargos\"]";
        $(intercompanyApprovesLink).click();
        $(locator).click();
        return new TimeSheetRequestPage();
    }

    public TimeSheetRequestCRPage navigateLNCHistoricRequestLink(){
        $(intercompanyApprovesLink).click();
        $(approveHistoricLink).click();
        return new TimeSheetRequestCRPage();
    }

    public void navigateTimesheetsRequest(){
        $(timesheetRequestLink).click();
    }

    public void navigateTimesheetHistory(){
        $(timesheetHistoryLink).click();
    }


    //Colombia Menu
    public TimeSheetRequestPage navigateRequest(String language, String company){

        if (language.equals("Spanish") || language.equals("Español")) {
            $(extraHoursRequestMainLink).click();
            $$(extraHoursRequestLink).get(0).click();
            if (company.equals("CO"))
                return new TimeSheetRequestCOPage();
            else
                return new TimeSheetRequestInternationalPage();
        }else{
            $(extraHoursEnglishRequestMainLink).click();
            return new TimeSheetRequestInternationalPage();
        }
    }

    public SurchargeRequestCOPage navigateSurchargeRequest(String company){
        $(surchargeRequestsMainLink).click();
        $$(extraHoursRequestLink).get(1).click();
        if (company.equals("CO"))
            return new SurchargeRequestCOPage();
        return null;
    }

    public TimeSheetRequestPage navigateIntercompanyHistoricInternational(String language, String company){
        if (language.equals("Spanish") || language.equals("Español")){
            $(intercompanyApprovesLink).click();
            $(approveHistoricLink).click();
        }else{
            $(intercompanyApprovalsEnglishLink).click();
            int amount = $$(approveHistoricEnglishLink).size();
            if (amount > 1)
                $$(approveHistoricEnglishLink).get(1).click();
            else
                $$(approveHistoricEnglishLink).get(0).click();
        }

        if (company.equals("CWPPA") || company.equals("LNPPA"))
            return new TimeSheetRequestPAPage();
        else
            return new TimeSheetRequestInternationalPage();

    }

    public TimeSheetRequestPage navigateIntercompanyHistoric(String language){
        if (language.equals("Spanish") || language.equals("Español")){
            $(intercompanyApprovesLink).click();
            $(approveHistoricLink).click();
            return new TimeSheetRequestPage();
        }else{
            $(intercompanyApprovalsEnglishLink).click();
            $$(approveHistoricEnglishLink).get(1).click();
            return new TimeSheetRequestInternationalPage();
        }

    }

    //Panama Menu
    public TimeSheetRequestPAPage navigateRequestPA(String language, String company){
        $("a[title=\"Solicitud de Horas Extras\"]").click();
        return new TimeSheetRequestPAPage();
    }

    public TimeSheetRequestPAPage navigateApprovalsPA(String language, boolean isNewRequest){
        String locator1 = language.equals("Spanish") || language.equals("Español")
                ? "a[title=\"Aprobaciones\"]" : "a[title=\"Approvals\"]";
        String locator2 = language.equals("Spanish") || language.equals("Español")
                ? "a[title=\"Aprobación de Horas Extras\"]" : "a[title=\"Overtime Approvals\"]";
        String locator3 = language.equals("Spanish") || language.equals("Español")
                ? "a[title=\"Historial Aprobación de Horas Extras\"]" : "a[title=\"Overtime Approvals History\"]";

        $(locator1).click();
        if (isNewRequest)
            click(locator2,false);
        else
            click(locator3,false);

        return new TimeSheetRequestPAPage();
    }


    //Jamaica Menu
    public TimeSheetRequestJMPage navigateRequestJM(String requestType, boolean isNewRequest){
        String subMenu = "History";
        String locator = "";
        if (isNewRequest)
            subMenu = "Requests";

        if (requestType.equals("OR")){
            $("a[title=\"Overtime Requests\"]").click();
            locator = "//a[@title='Overtime Requests']/following-sibling::ul//a[@title='"+subMenu+"']";
            click(locator,true);
        }

        if (requestType.equals("SBR")){
            $("a[title=\"Standby Requests\"]").click();
            locator = "//a[@title='Standby Requests']/following-sibling::ul//a[@title='"+subMenu+"']";
            click(locator,true);
        }

        if (requestType.equals("COR")){
            $("a[title=\"Call Out Requests\"]").click();
            locator = "//a[@title='Call Out Requests']/following-sibling::ul//a[@title='"+subMenu+"']";
            click(locator,true);
        }

        if (requestType.equals("SR")){
            $("a[title=\"Shift Requests\"]").click();
            locator = "//a[@title='Shift Requests']/following-sibling::ul//a[@title='"+subMenu+"']";
            click(locator,true);
        }

        return new TimeSheetRequestJMPage();
    }

    public TimeSheetRequestJMPage navigateApprovalsJM(String requestType,String language, boolean isNewRequest){

        String subMenu1 = language.equals("Español") ? "Aprobaciones" : "Approvals";
        String subMenu2 = language.equals("Español") ? "Historial" : "History";

        if (requestType.equals("OR")){
            String menu = language.equals("Español") ? "Aprobaciones Horas Extras" : "Overtime Approvals";

            String locator1 = "a[title=\""+menu+"\"]";
            $(locator1).click();
            String subMenuResult = isNewRequest ? subMenu1 : subMenu2;
            String locator2 = "//a[@title='"+menu+"']/following-sibling::ul//a[@title='"+subMenuResult+"']";
            click(locator2,true);
        }
        if (requestType.equals("SBR")){
            String menu = language.equals("Español") ? "Aprobaciones Standby" : "Standby Approvals";

            String locator1 = "a[title=\""+menu+"\"]";
            $(locator1).click();
            String subMenuResult = isNewRequest ? subMenu1 : subMenu2;
            String locator2 = "//a[@title='"+menu+"']/following-sibling::ul//a[@title='"+subMenuResult+"']";
            click(locator2,true);
        }
        if (requestType.equals("COR")){
            String menu = language.equals("Español") ? "Aprobaciones Call Out" : "Call Out Approvals";
            String locator1 = "a[title=\""+menu+"\"]";
            $(locator1).click();
            String subMenuResult = isNewRequest ? subMenu1 : subMenu2;
            String locator2 = "//a[@title='"+menu+"']/following-sibling::ul//a[@title='"+subMenuResult+"']";
            click(locator2,true);
        }
        if (requestType.equals("SR")){
            String menu = language.equals("Español") ? "Aprobaciones Shift" : "Shift Approvals";
            String locator1 = "a[title=\""+menu+"\"]";
            $(locator1).click();
            String subMenuResult = isNewRequest ? subMenu1 : subMenu2;
            String locator2 = "//a[@title='"+menu+"']/following-sibling::ul//a[@title='"+subMenuResult+"']";
            click(locator2,true);
        }

        return new TimeSheetRequestJMPage();
    }

    public void logout(){
        Selenide.sleep(1000);
        click(logoutButton,false);
    }


}
