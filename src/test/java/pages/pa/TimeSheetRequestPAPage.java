package pages.pa;

import com.codeborne.selenide.*;
import org.openqa.selenium.By;
import org.testng.Assert;
import pages.TimeSheetRequestPage;
import pojo.IPanama;
import suites.utils.CommonTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static suites.utils.CommonTest.*;

public class TimeSheetRequestPAPage extends TimeSheetRequestPage {

    private String jornalInput = "#Jornal";

    private By option2Checkbox = By.cssSelector("label[for='DayOpt2']");
    private By option3Checkbox = By.cssSelector("label[for='DayOpt3']");

    private String lunchSelect = "#Lunch";


    public boolean addTimesheetRequest(IPanama request) throws IOException {
        boolean result = false;

        //$(getAddButton()).shouldBe(visible).shouldBe(clickable).click();
        //$(getSendButtonLocator()).shouldBe(visible).shouldBe(clickable);
        this.addRequest();
        //fill the form

        String journal = request.getJornal();
        $(jornalInput).shouldBe(visible,enabled).selectOptionByValue(journal);

        Selenide.sleep(2000);
        $(getScheduleInput()).selectOptionByValue(request.getSchedule());

        if (journal.equals("3") || journal.equals("4"))
            $(lunchSelect).selectOptionByValue(request.getLunch());

        if (!request.getTime().isEmpty())
            CommonTest.enterTime(request.getTime(),getTime1Input());

        if (!request.getOutTime().isEmpty())
            CommonTest.enterTime(request.getOutTime(),getTime1endInput());

        if (!request.getTime2().isEmpty())
            CommonTest.enterTime(request.getTime2(),getTime2Input());
        if (!request.getOutTime2().isEmpty())
            CommonTest.enterTime(request.getOutTime2(),getTime2endInput());

        if (!request.getTime3().isEmpty())
            CommonTest.enterTime(request.getTime3(),getTime3Input());
        if (!request.getOutTime3().isEmpty())
            CommonTest.enterTime(request.getOutTime3(),getTime3endInput());

        if (request.getDayOpt1().equals("1"))
            clickNoWait(getOption1Checkbox());

        if (request.getDayOpt2().equals("1"))
            clickNoWait(option2Checkbox);

        if (request.getDayOpt3().equals("1"))
            clickNoWait(option3Checkbox);

        String reason =  getReasonByValue(getReasonSelect(), request.getReason());
        if (reason.equals("null"))
            throw new AssertionError("Reason: " +request.getReason()+" is not present in this company.");
        $(getReasonSelect()).selectOptionByValue(reason);
        $(getCommentLocator()).setValue("testing");
        CommonTest.uploadDummyFile(getAttachFileLocator());

        //needs to be there because is hidden other elements
        String date = CommonTest.convertDate(request.getDateBeg(),"S");
        $(getDateInputLocator()).setValue(date);

        $(getSendButtonLocator()).click();
        //click(getSendButtonLocator(),false);
        try{
            $(this.getAddButton()).shouldBe(visible).should(Condition.clickable);
            result = true;
        }catch (AssertionError e){
            String message = CommonTest.clickModal("//div[@class='modal-footer']//button[text()='Cerrar']");
            throw new AssertionError("I found an error message: "+message);
        }
        return result;
    }

    public void deleteTimesheetRequest(IPanama request, String status, String language, String oneId){
        SelenideElement row = searchPADynamic(request, status, language, oneId);
        if (row.exists()){
            //int index = getHeaderIndex("Action", "Acción",null,$$(getHeaderTable()));
            //SelenideElement column = row.$$("td").get(index);
            //column.$x(".//a[img[@title='Delete' or @title='Borrar']]").click();
            click(".//a[img[@title='Delete' or @title='Borrar']]", true);
            CommonTest.clickModal("//*[@id=\"modal_portalconfirm_btn0\"]");
        }else{
            Assert.fail("Was not possible to find the request and delete it");
        }
    }

    public boolean approvePARequest(IPanama request, String status, String id, String language){
        boolean result = false;
        SelenideElement row = searchPADynamic(request,status, language, id);
        if (row.exists()){
            int index = getHeaderIndex("Actions","Acciones",null,$$(getHeaderTable()));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'icon_Approve.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
            result = true;
        }else{
            Assert.fail("Was not possible to find the request and approve it");
        }
        return result;
    }

    public void reversePARequest(IPanama request, String status, String oneId, String language){
        SelenideElement row = searchPADynamic(request, status, language, oneId);
        ElementsCollection headers = $$(getHeaderTable());
        if (row.exists()){
            int index = getHeaderIndex("Reverse","Reversar","Rev.",headers);
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'arrow-left.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and reverse it");
        }
    }

    public boolean verifyRequestPAExist(IPanama request, String status, String language, String oneId){
        Selenide.sleep(1000);
        SelenideElement row = searchPADynamic(request, status, language, oneId);
        return row != null;
    }

    public SelenideElement searchPADynamic(IPanama request, String requestStatus, String language, String oneId){
        Selenide.sleep(1000);
        String table = ".table tbody tr";
        String header = ".table thead th";

        if (!$$(table).first().exists())
            return null;
        $$(table).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(table);
        HashMap<String, Integer> columns = this.getHeadersIndex($$(header));

        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(columns.get("Date")).getText();
            String convertedDate = CommonTest.getDateBaseOnLanguage(language,request.getDateBeg());

            if (dateCell.equals(convertedDate)){

                //String intervalUI = CommonTest.getInterval(request.getTime(), request.getOutTime(), request.getOutTime2(), request.getOutTime3());
                String dateConverted = CommonTest.getDateBaseOnLanguage(language,request.getDateBeg());

                String oneID = cells.get(columns.get("OneID")).getText();
                String requestDate = cells.get(columns.get("Date")).getText();
                //String interval = cells.get(columns.get("Interval")).getText();
                String status = cells.get(columns.get("Status")).getText();
                String enterDate = cells.get(columns.get("Request Date")).getText();
                String todayDate = "";
                if (language.equals("Spanish") || language.equals("Español"))
                    todayDate = CommonTest.getTodayDate();
                else
                    todayDate = CommonTest.getTodayDateEnglish();

                if (requestDate.equals(dateConverted) && oneID.contains(oneId)
                        && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }

}
