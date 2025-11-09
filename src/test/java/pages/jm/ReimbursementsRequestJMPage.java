package pages.jm;

import com.codeborne.selenide.*;
import org.testng.Assert;
import pages.TimeSheetRequestPage;
import pojo.IReimbursement;
import suites.utils.CommonTest;

import java.util.HashMap;

import static com.codeborne.selenide.Selenide.*;
import static suites.utils.CommonTest.*;

public class ReimbursementsRequestJMPage extends TimeSheetRequestPage {

    private String reasonReiSelect = "#refunddetailstbl select.form-control";
    private String dateReiInput = "#reqformrefundfield0_1";
    private String quantityReiInput = "#refunddetailstbl #refunddetails td.refunddetailstbl_num input.form-control";
    private String amountReiInput = "#refunddetailstbl #refunddetails tr:first-child td:nth-child(4) input.form-control";
    private String tableBodyColumns = "table#refunddetailstbl tbody td";

    public boolean addReimbursementsRequest(IReimbursement request, String language){

        boolean result = false;

        this.addRequest();
        //fill the form
        String date = getDateBaseOnLanguage(language, request.getDateBeg());

        $(reasonReiSelect).selectOptionByValue(request.getReimbursement());
        Selenide.sleep(1000);
        $(dateReiInput).setValue(date);

        $(getReasonSelect()).setValue(request.getReason());

        $(getDateInputLocator()).setValue(date);

        ElementsCollection columns = $$(tableBodyColumns);
        ElementsCollection headerElements = $$("table#refunddetailstbl tbody tr th").filterBy(Condition.visible);
        HashMap<String, Integer> headers = getReimbursementsHeaders(headerElements);

        if (!request.getQuantity().isEmpty())
            setValueJS(columns.get(headers.get("Quantity")).find("input"), convertAmounts(request.getQuantity()));
        if (!request.getAmount().isEmpty())
            setValueJS(columns.get(headers.get("Amount")).find("input"), convertAmounts(request.getAmount()));

        click(getSendButtonLocator(),false);
        try{
            $(this.getAddButton()).shouldBe(Condition.visible).should(Condition.clickable);
            result = true;
        }catch (AssertionError e){
            String message = CommonTest.clickModal("//div[@class='modal-footer']//button[text()='Close'  or text()='Cerrar']");
            throw new AssertionError("I found an error message: "+message);
        }
        return result;

    }

    public boolean verifyReimbursementExist(IReimbursement request, String status, String language){
        Selenide.sleep(1000);
        SelenideElement row = searchReiDynamic(request, status, language);
        return row != null;
    }

    public SelenideElement searchReiDynamic(IReimbursement request, String requestStatus, String language){

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

    public void approveReimbursementRequest(IReimbursement request, String status, String language){
        SelenideElement row = searchReiDynamic(request,status, language);
        if (row != null && row.exists()){
            int index = getHeaderIndex("Actions","Acciones",null,$$(getHeaderTable()));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'icon_Approve.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and approve it");
        }
    }

    public HashMap<String, Integer> getReimbursementsHeaders(ElementsCollection headers){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for(int i=0; i < headers.size() ;i++){
            String name = headers.get(i).getText();
            if (name.equals("Quantity") || name.equals("Monto"))
                result.put("Quantity", i);
            if (name.equals("Amount") || name.equals("Importe"))
                result.put("Amount", i);

        }
        return result;
    }
}
