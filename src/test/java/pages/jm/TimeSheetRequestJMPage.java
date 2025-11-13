package pages.jm;

import com.codeborne.selenide.*;
import org.testng.Assert;
import pages.TimeSheetRequestPage;
import pojo.IJamaica;
import suites.utils.CommonTest;

import java.util.HashMap;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static suites.utils.CommonTest.getDateBaseOnLanguage;
import static suites.utils.CommonTest.getHeadersIIndex;

public class TimeSheetRequestJMPage extends TimeSheetRequestPage {

    private String callOutDayCheck = "input[name='calloutopt']";
    private String holidayCheck = "input[name='holidayopt']";

    public boolean addTimesheetRequestJM(IJamaica request){

        boolean result = false;

        this.addRequest();

        CommonTest.enterTime(request.getTime(),getInitialHourInput());
        CommonTest.enterTime(request.getOutTime(),getEndHourInput());

        if (request.getCallOut().equals("1"))
            $(callOutDayCheck).click();

        if (request.getHoliday().equals("1"))
            $(holidayCheck).click();

        //$(getReasonSelect()).setValue(request.getReason());
        $(getCostCenterLocator()).selectOptionByValue(request.getCostCenter());
        $(getCommentLocator()).setValue("testing");
        String date = CommonTest.convertDate(request.getDateBeg(),"E");
        $(getDateInputLocator()).setValue(date);
        CommonTest.click(getSendButtonLocator(),false);
        try{
            $(this.getAddButton()).shouldBe(Condition.visible).should(Condition.clickable);
            result = true;
        }catch (AssertionError e){
            String message = CommonTest.clickModal("//div[@class='modal-footer']//button[text()='Cerrar' or text()='Close']");
            throw new AssertionError("I found an error message: "+message);
        }
        return result;
    }

    public void deleteTimesheetRequestJM(IJamaica request, String status, String language){
        SelenideElement row = searchJDynamic(request, status, language);
        if (row.exists()){
            int index = getHeaderIndex("Action", "Acción",null,$$(getHeaderTable()));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//a[img[@title='Delete' or @title='Borrar']]").click();
            CommonTest.clickModal("//*[@id=\"modal_portalconfirm_btn0\"]");
        }else{
            Assert.fail("Was not possible to find the request and delete it");
        }
    }

    public void approveRequestJM(IJamaica request, String status, String language){
        SelenideElement row = searchJDynamic(request,status, language);
        if (row != null && row.exists()){
            int index = getHeaderIndex("Actions","Acciones",null,$$(getHeaderTable()));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'icon_Approve.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and approve it");
        }
    }

    public void reverseRequestJM(IJamaica request, String status, String language){
        SelenideElement row = searchJDynamic(request, status, language);
        ElementsCollection headers = $$(getHeaderTable());
        if (row.exists()){
            int index = getHeaderIndex("Reverse","Reversar",null,headers);
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'arrow-left.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and reverse it");
        }
    }

    public boolean verifyRequestExistJM(IJamaica request, String status, String language){
        Selenide.sleep(1000);
        SelenideElement row = searchJDynamic(request, status, language);
        return row != null;
    }

    public SelenideElement searchJDynamic(IJamaica request, String requestStatus, String language){

        //changeFromDateFilter(CommonTest.getDateBaseOnLanguage(language,"2022-01-01"));
        Selenide.sleep(1000);
        String table = ".table tbody tr";
        String header = ".table thead th";

        if (!$$(table).first().exists())
            return null;
        $$(table).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(table);
        HashMap<String, Integer> columns = getHeadersIIndex($$(header));

        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(columns.get("Date")).getText();
            String convertedDate = getDateBaseOnLanguage(language,request.getDateBeg());

            if (dateCell.equals(convertedDate)){

                String dateConverted = getDateBaseOnLanguage(language,request.getDateBeg());

                String requestDate = cells.get(columns.get("Date")).getText();
                //String costCenter = cells.get(columns.get("Cost Center")).getText();
                String status = cells.get(columns.get("Status")).getText();
                String enterDate = cells.get(columns.get("Request Date")).getText();

                String todayDate = "";
                if (language.equals("Spanish") || language.equals("Español"))
                    todayDate = CommonTest.getTodayDate();
                else
                    todayDate = CommonTest.getTodayDateEnglish();

                if (dateConverted.equals(requestDate) && todayDate.equals(enterDate) && requestStatus.equals(status)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }


}
