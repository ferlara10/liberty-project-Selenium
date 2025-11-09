package pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.testng.Assert;
import pojo.IColombia;
import suites.utils.CommonTest;

import java.util.HashMap;
import static com.codeborne.selenide.Selenide.$$;

public class TimeSheetRequestPage {

    private String addButton = "#crud_add";
    private String dateInput = "#DateBeg";
    private String costCenterSelect = "#CostCenter";
    private String commentsArea = "#Comments";

    private String sendButton = "#crud_save";
    private String cancelButton = "#crud_cancel";

    private String requestsTableRows = "#crudlistbody table tbody tr";
    private String tableDiv = "#crudlistbody";
    private String attachFileInput = "input[name=\"attach[]\"]";
    private String headerTable = ".table thead th";

    private String initialHourInput = "#Time";
    private String endHourInput = "#OutTime";
    private String reasonSelect = "#Reason";

    //Panama and CostaRica
    private String scheduleInput = "#Schedule";

    private String time1Input = "#Time";
    private String time1endInput = "#OutTime";
    private String time2Input = "#Time2";
    private String time2endInput = "#OutTime2";
    private String time3Input = "#Time3";
    private String time3endInput = "#OutTime3";

    private By option1Checkbox = By.cssSelector( "label[for='DayOpt1']");


    public void addRequest(){
        CommonTest.click(addButton,false);
    }

    public String getAddButton(){
        return this.addButton;
    }

    public String getDateInputLocator(){
        return this.dateInput;
    }

    public String getCostCenterLocator(){
        return this.costCenterSelect;
    }

    public String getCommentLocator(){
        return this.commentsArea;
    }

    public String getSendButtonLocator(){
        return this.sendButton;
    }

    public String getAttachFileLocator(){
        return this.attachFileInput;
    }

    public String getRequestsTableRows() {
        return requestsTableRows;
    }

    public String getTableDiv() {
        return tableDiv;
    }

    public String getHeaderTable (){
        return this.headerTable;
    }

    public String getInitialHourInput() {
        return initialHourInput;
    }

    public String getEndHourInput() {
        return endHourInput;
    }

    public String getReasonSelect() {
        return reasonSelect;
    }

    public String getScheduleInput() {
        return scheduleInput;
    }

    public String getTime1Input() {
        return time1Input;
    }

    public String getTime1endInput() {
        return time1endInput;
    }

    public String getTime2Input() {
        return time2Input;
    }

    public String getTime2endInput() {
        return time2endInput;
    }

    public String getTime3Input() {
        return time3Input;
    }

    public String getTime3endInput() {
        return time3endInput;
    }

    public By getOption1Checkbox() {
        return option1Checkbox;
    }

    public HashMap<String, Integer> getHeadersIndex(ElementsCollection header){
        HashMap<String, Integer> result = new HashMap<String, Integer>();;
        for(int i=0; i < header.size() ;i++){
            String name = header.get(i).getText();
            if (name.equals("OneID") || name.equals("Solicitante"))
                result.put("OneID",i);
            if (name.equals("Date") || name.equals("Fecha"))
                result.put("Date", i);
            if (name.equals("Interval") || name.equals("Intervalo"))
                result.put("Interval", i);
            if (name.equals("Status") || name.equals("Estado"))
                result.put("Status", i);
            if (name.equals("Actions") || name.equals("Acciones") || name.equals("Acción"))
                result.put("Actions", i);
            if (name.equals("Request Date") || name.equals("F. Solicitud") || name.equals("F. de Solicitud"))
                result.put("Request Date", i);
        }
        return result;
    }

    public SelenideElement searchDynamic(IColombia request, String requestStatus, String id){
        Selenide.sleep(1000);
        String table = ".table tbody tr";

        if (!$$(table).first().exists())
            return null;
        $$(table).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(table);
        HashMap<String, Integer> columns = getHeadersIndex($$(this.headerTable));

        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(columns.get("Date")).getText();               //getting text of the date column
            String convertedDate = CommonTest.convertDate(request.getDateBeg(),"S");
            if (dateCell.equals(convertedDate)){

                String oneID = cells.get(columns.get("OneID")).getText();
                String requestDate = cells.get(columns.get("Date")).getText();
                String interval = "";
                if (columns.containsKey("Interval"))
                    interval = cells.get(columns.get("Interval")).getText();
                else
                    interval = String.format("%s - %s",request.getTime(),request.getOutTime());
                String status = cells.get(columns.get("Status")).getText();
                String enterDate = cells.get(columns.get("Request Date")).getText();
                String todayDate = CommonTest.getTodayDate();

                String dateConverted = CommonTest.convertDate(request.getDateBeg(),"S");

                if (dateConverted.equals(requestDate) && interval.equals(String.format("%s - %s",request.getTime(),request.getOutTime()))
                        && enterDate.equals(todayDate) && status.equals(requestStatus)
                        && oneID.equals(id)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }

    public void approveRequest(IColombia request, String status, String id){
        SelenideElement row = searchDynamic(request,status,id);
        if (row.exists()){
            int index = getHeaderIndex("Actions","Acciones", null,$$(this.headerTable));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'icon_Approve.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and approve it");
        }
    }

    public int getHeaderIndex(String english, String spanish, String thirdOption, ElementsCollection header){
        int result = 0;
        for(int i=0; i < header.size() ;i++){
            String name = header.get(i).getText();
            if (name.contains(spanish) || name.equals(english) || name.equals(thirdOption)){
                result = i;
                break;
            }
        }
        return result;
    }

    public void reverseRequest(IColombia request, String status, String id){
        SelenideElement row = searchDynamic(request, status, id);
        ElementsCollection headers = $$(this.headerTable);
        if (row.exists()){
            int index = getHeaderIndex("Revert","Reversar", null, headers);
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//img[contains(@src,'arrow-left.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and reverse it");
        }
    }

    public boolean verifyRequestExist(IColombia request, String status, String id){
        Selenide.sleep(1000);
        SelenideElement row = searchDynamic(request, status, id);
        return row != null;
    }

    public String getCurrentFromDateFilter(){
        return Selenide.executeJavaScript(
                "return document.getElementById('filterdatefrom').value;"
        );
    }

    public void changeFromDateFilter(String from){
        String jsCode = "document.getElementById('filterdatefrom').value = '"+from+"';";
        Selenide.executeJavaScript(jsCode);
        Selenide.executeJavaScript("document.getElementById('filterdatefrom').dispatchEvent(new Event('change'));");
        CommonTest.waitForPageToLoad();
    }
}
