package pages.cr;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.testng.Assert;
import pages.TimeSheetRequestPage;
import pojo.IColombia;
import suites.utils.CommonTest;

import java.util.HashMap;

import static com.codeborne.selenide.Selenide.$$;

public class TimeSheetRequestCRPage extends TimeSheetRequestPage {

    public void denyRequest(IColombia request){
        SelenideElement row = searchRequestCO(request, "Pendiente");
        if (row.exists()){
            SelenideElement column = row.$$("td").get(14);
            column.$x(".//img[contains(@src,'icon_Reject.gif')]").click();
            //Selenide.sleep(2000);
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and delete it");
        }
    }

    public void reverseRequest(IColombia request){
        SelenideElement row = searchHistoric(request, "Completado");
        if (row.exists()){
            SelenideElement column = row.$$("td").get(8);
            column.$x(".//img[contains(@src,'arrow-left.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and reverse it");
        }
    }

    public void approveRequest(IColombia request, String id){
        SelenideElement row2 = searchDynamic(request,"Pendiente",id);
        SelenideElement row = searchRequestCO(request, "Pendiente");
        if (row.exists()){
            SelenideElement column = row.$$("td").get(14);
            column.$x(".//img[contains(@src,'icon_Approve.gif')]").click();
            CommonTest.clickModal(".//button[@id='modal_portalconfirm_btn0']");
        }else{
            Assert.fail("Was not possible to find the request and approve it");
        }
    }

    public boolean verifyRequestExist(IColombia request){
        SelenideElement row = searchRequestCO(request, "Pendiente");
        return row != null;
    }

    public boolean verifyHistoric(IColombia request){
        SelenideElement row = searchHistoric(request, "Completado");
        return row != null;
    }

    public HashMap<String, Integer> getHeadersIndexSpanish(ElementsCollection header){
        HashMap<String, Integer> result = new HashMap<String, Integer>();;
        for(int i=0; i < header.size() ;i++){
            String name = header.get(i).getText();
            if (name.equals("OneID"))
                result.put("OneID",i);
            if (name.equals("Fecha"))
                result.put("Fecha", i);
            if (name.equals("Intervalo"))
                result.put("Intervalo", i);
            if (name.equals("Estado"))
                result.put("Estado", i);
            if (name.equals("Acciones"))
                result.put("Acciones", i);
            if (name.equals("F. Solicitud"))
                result.put("F. Solicitud", i);
        }
        return result;
    }

    public HashMap<String, Integer> getHeadersIndexEnglish(ElementsCollection header){
        HashMap<String, Integer> result = new HashMap<String, Integer>();;
        for(int i=0; i < header.size() ;i++){
            String name = header.get(i).getText();
            if (name.equals("OneID"))
                result.put("OneID",i);
            if (name.equals("Date"))
                result.put("Date", i);
            if (name.equals("Interval"))
                result.put("Interval", i);
            if (name.equals("Status"))
                result.put("Status", i);
            if (name.equals("Actions"))
                result.put("Actions", i);
            if (name.equals("Request Date"))
                result.put("Request Date", i);
        }
        return result;
    }

    public SelenideElement searchDynamic(IColombia request, String requestStatus, String id){
        Selenide.sleep(1000);
        String table = ".table tbody tr";
        String header = ".table thead th";

        if (!$$(table).first().exists())
            return null;
        $$(table).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(table);
        HashMap<String, Integer> columns = getHeadersIndexSpanish($$(header));
        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(columns.get("Fecha")).getText();               //getting text of the date column
            String convertedDate = CommonTest.convertDate(request.getDateBeg(),"S");
            if (dateCell.equals(convertedDate)){

                String oneID = cells.get(columns.get("OneID")).getText();

                String requestDate = cells.get(columns.get("Fecha")).getText();
                String interval = cells.get(columns.get("Intervalo")).getText();
                String status = cells.get(columns.get("Estado")).getText();
                String enterDate = cells.get(columns.get("F. Solicitud")).getText();
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

    public SelenideElement searchHistoric(IColombia request, String requestStatus){
        Selenide.sleep(1000);
        String table = ".table tbody tr";
        if (!$$(table).first().exists())
            return null;
        $$(table).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(table);

        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(3).getText();               //getting text of the date column
            String convertedDate = CommonTest.convertDate(request.getDateBeg(),"S");
            if (dateCell.equals(convertedDate)){

                String requestDate = cells.get(3).getText();
                String interval = cells.get(4).getText();
                String status = cells.get(7).getText();
                String enterDate = cells.get(10).getText();
                String todayDate = CommonTest.getTodayDate();

                String dateConverted = CommonTest.convertDate(request.getDateBeg(),"S");

                if (dateConverted.equals(requestDate) && interval.equals(String.format("%s - %s",request.getTime(),request.getOutTime()))
                        && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }

    public SelenideElement searchRequestCO(IColombia request, String requestStatus){
        Selenide.sleep(1000);
        String table = ".table tbody tr";
        if (!$$(table).first().exists())
            return null;
        $$(table).shouldHave(CollectionCondition.sizeGreaterThan(0));
        ElementsCollection rows = $$(table);

        for(int i=0; i < rows.size() ;i++){
            ElementsCollection cells = rows.get(i).$$("td");
            String dateCell = cells.get(3).getText();               //getting text of the date column
            String convertedDate = CommonTest.convertDate(request.getDateBeg(),"S");
            if (dateCell.equals(convertedDate)){

                String requestDate = cells.get(3).getText();
                String interval = cells.get(4).getText();
                String status = cells.get(13).getText();
                String enterDate = cells.get(17).getText();
                String todayDate = CommonTest.getTodayDate();

                String dateConverted = CommonTest.convertDate(request.getDateBeg(),"S");

                if (dateConverted.equals(requestDate) && interval.equals(String.format("%s - %s",request.getTime(),request.getOutTime()))
                        && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }
}
