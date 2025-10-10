package pages.pa;

import com.codeborne.selenide.*;
import pages.TimeSheetRequestPage;
import pojo.IColombia;
import pojo.IInternational;
import pojo.IPanama;
import suites.utils.CommonTest;

import java.io.IOException;
import java.util.HashMap;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TimeSheetRequestPAPage extends TimeSheetRequestPage {

    private String jornalInput = "#Jornal";
    private String scheduleInput = "#Schedule";

    private String time1Input = "#Time";
    private String time1endInput = "#OutTime";
    private String time2Input = "#Time2";
    private String time2endInput = "#OutTime2";
    private String time3Input = "#Tim3";
    private String time3endInput = "#OutTime3";

    private String option1Checkbox = "#DayOpt1";
    private String option2Checkbox = "#DayOpt2";
    private String option3Checkbox = "#DayOpt3";

    private String reasonSelect = "#Reason";


    public void addTimesheetRequest(IPanama request) throws IOException {
        this.addRequest();
        //fill the form
        //$(jornalInput).selectOptionByValue(request.getJornal());
        String date = CommonTest.convertDate(request.getDateBeg(),"S");
        $(getDateInputLocator()).setValue(date);
        //$(scheduleInput).selectOptionByValue(request.getSchedule());

        if (!request.getTime().isEmpty())
            CommonTest.enterTime(request.getTime(),time1Input);
        if (!request.getOutTime().isEmpty())
            CommonTest.enterTime(request.getOutTime(),time1endInput);

        if (!request.getTime2().isEmpty())
            CommonTest.enterTime(request.getTime2(),time2Input);
        if (!request.getOutTime2().isEmpty())
            CommonTest.enterTime(request.getOutTime2(),time2endInput);

        if (!request.getTime3().isEmpty())
            CommonTest.enterTime(request.getTime3(),time3Input);
        if (!request.getOutTime3().isEmpty())
            CommonTest.enterTime(request.getOutTime3(),time3endInput);

        if (request.getDayOpt1().equals("1"))
            $(option1Checkbox).click();

        if (request.getDayOpt2().equals("1"))
            $(option2Checkbox).click();

        if (request.getDayOpt3().equals("1"))
            $(option3Checkbox).click();

        $(reasonSelect).selectOption(request.getReason());
        $(getCommentLocator()).setValue("testing");
        CommonTest.uploadDummyFile(getAttachFileLocator());

        $(getSendButtonLocator()).click();
        try{
            $(this.getAddButton()).shouldBe(Condition.visible).should(Condition.clickable);
        }catch (AssertionError e){
            String message = CommonTest.clickModal("//div[@class='modal-footer']//button[text()='Cerrar']");
            throw new AssertionError("I found an error message: "+message);
        }
    }

    public boolean verifyRequestPAExist(IPanama request, String status, String language, String oneId){
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

                String intervalUI = CommonTest.getInterval(request.getTime(), request.getOutTime(), request.getOutTime2(), request.getOutTime3());
                String dateConverted = CommonTest.getDateBaseOnLanguage(language,request.getDateBeg());

                String oneID = cells.get(columns.get("OneID")).getText();
                String requestDate = cells.get(columns.get("Date")).getText();
                String interval = cells.get(columns.get("Interval")).getText();
                String status = cells.get(columns.get("Status")).getText();
                String enterDate = cells.get(columns.get("Request Date")).getText();
                String todayDate = "";
                if (language.equals("Spanish") || language.equals("Español"))
                    todayDate = CommonTest.getTodayDate();
                else
                    todayDate = CommonTest.getTodayDateEnglish();

                if (requestDate.equals(dateConverted) && oneID.contains(oneId) && interval.equals(intervalUI)
                        && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }

}
