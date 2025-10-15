package pages.in;

import com.codeborne.selenide.*;
import org.testng.Assert;
import pages.TimeSheetRequestPage;
import pojo.IInternational;
import suites.utils.CommonTest;

import java.io.IOException;
import java.util.HashMap;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static suites.utils.CommonTest.*;

public class TimeSheetRequestInternationalPage extends TimeSheetRequestPage {

    private String wageTypeSelect = "#WageType";
    private String quantityInput = "#Number";
    private String reasonInput = "#Reason";

    public void reverseIRequest(IInternational request, String status, String language){
        SelenideElement row = searchIDynamic(request, status, language);
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


    public void approveInternationalRequest(IInternational request, String status, String id, String language){
        SelenideElement row = searchIDynamic(request,status, language);
        if (row.exists()){
            int index = getHeaderIndex("Actions","Acciones",null,$$(getHeaderTable()));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'icon_Approve.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and approve it");
        }
    }

    public void addTimesheetRequest(IInternational request, String language) throws IOException {
        this.addRequest();
        //fill the form
        String date = getDateBaseOnLanguage(language, request.getDateBeg());
        $(getDateInputLocator()).setValue(date);
        $(wageTypeSelect).selectOptionByValue(request.getWageType());
        $(quantityInput).setValue(request.getNumber());
        $(getCostCenterLocator()).selectOptionByValue(request.getCostCenter());
        $(getCommentLocator()).setValue(request.getReason());
        if(request.getCompany().equals("CNDDO") || request.getCompany().equals("LNGGT") ||
        request.getCompany().equals("LNHHN") || request.getCompany().equals("LNSSV")){
            uploadDummyFile(getAttachFileLocator());
            $(reasonInput).setValue(request.getReason());
        }

        $(getSendButtonLocator()).click();
        try{
            $(this.getAddButton()).shouldBe(Condition.visible).should(Condition.clickable);
        }catch (AssertionError e){
            String message = CommonTest.clickModal("//div[@class='modal-footer']//button[text()='Close'  or text()='Cerrar']");
            throw new AssertionError("I found an error message: "+message);
        }
    }

    public boolean verifyRequestIExist(IInternational request, String status, String language){
        SelenideElement row = searchIDynamic(request, status, language);
        return row != null;
    }

    public SelenideElement searchIDynamic(IInternational request, String requestStatus, String language){
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
                //String wageType = cells.get(columns.get("Wage Type")).getText();
                //String costCenter = cells.get(columns.get("Cost Center")).getText();
                String status = cells.get(columns.get("Status")).getText();
                String enterDate = cells.get(columns.get("Request Date")).getText();
                String todayDate = "";
                if (language.equals("Spanish") || language.equals("Español"))
                    todayDate = CommonTest.getTodayDate();
                else
                    todayDate = CommonTest.getTodayDateEnglish();


                if (dateConverted.equals(requestDate)
                        && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }



    public void deleteTimesheetRequest(IInternational request, String status, String language){
        SelenideElement row = searchIDynamic(request, status, language);
        if (row.exists()){
            int index = getHeaderIndex("Action", "Acción",null,$$(getHeaderTable()));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//a[img[@title='Delete' or @title='Borrar']]").click();
            CommonTest.clickModal("//*[@id=\"modal_portalconfirm_btn0\"]");
        }else{
            Assert.fail("Was not possible to find the request and delete it");
        }
    }


}
