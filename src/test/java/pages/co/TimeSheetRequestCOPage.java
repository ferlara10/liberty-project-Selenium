package pages.co;

import com.codeborne.selenide.*;
import org.testng.Assert;
import pages.TimeSheetRequestPage;
import pojo.IColombia;
import suites.utils.CommonTest;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.*;

public class TimeSheetRequestCOPage extends TimeSheetRequestPage {





    public void addTimesheetRequest(IColombia request) throws IOException{
        this.addRequest();
        //fill the form
        CommonTest.enterTime(request.getTime(),getInitialHourInput());
        CommonTest.enterTime(request.getOutTime(),getEndHourInput());

        $(getReasonSelect()).selectOption(request.getReason());
        $(getCostCenterLocator()).selectOptionByValue(request.getCostCenter());
        $(getCommentLocator()).setValue("testing");
        String date = CommonTest.convertDate(request.getDateBeg(),"S");
        $(getDateInputLocator()).setValue(date);
        $(getSendButtonLocator()).click();
        try{
            $(this.getAddButton()).shouldBe(Condition.visible).should(Condition.clickable);
        }catch (AssertionError e){
            String message = CommonTest.clickModal("//div[@class='modal-footer']//button[text()='Cerrar']");
            throw new AssertionError("I found an error message: "+message);
        }
    }

    public boolean verifyRequestExist(IColombia request){
        SelenideElement row = searchRequest(request, "Escalado");
        return row != null;
    }



    public void deleteTimesheetRequest(IColombia request){
        SelenideElement row = searchRequest(request, "Escalado");
        if (row.exists()){
            SelenideElement column = row.$$("td").get(14);
            column.$x(".//a[img[@title='Borrar']]").click();
            //Selenide.sleep(2000);
            CommonTest.clickModal("//*[@id=\"modal_portalconfirm_btn0\"]");
            //$("#modal_portalconfirm_btn0").click();
        }else{
            Assert.fail("Was not possible to find the request and delete it");
        }
    }

    public SelenideElement searchRequest(IColombia request, String requestStatus){
        Selenide.sleep(1000);
        if (!$$(this.getRequestsTableRows()).first().exists())
            return null;
        $$(this.getRequestsTableRows()).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(this.getRequestsTableRows());

        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(1).getText();               //getting text of the date column
            String convertedDate = CommonTest.convertDate(request.getDateBeg(),"S");
            if (dateCell.equals(convertedDate)){

                String interval = cells.get(2).getText();
                String costCenter = cells.get(10).getText();
                String status = cells.get(11).getText();
                String enterDate = cells.get(13).getText();
                String todayDate = CommonTest.getTodayDate();

                if (interval.equals(String.format("%s - %s",request.getTime(),request.getOutTime())) &&
                    costCenter.equals(request.getCostCenter()) && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }


}
