package pages.co;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.testng.Assert;
import pojo.IColombia;
import suites.utils.CommonTest;

import static com.codeborne.selenide.Selenide.$$;

public class SurchargeRequestCOPage extends TimeSheetRequestCOPage{

    public SelenideElement searchSurchargeRequest(IColombia request, String requestStatus){
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
                String costCenter = cells.get(7).getText();
                String status = cells.get(8).getText();
                String enterDate = cells.get(10).getText();
                String todayDate = CommonTest.getTodayDate();

                if (interval.equals(String.format("%s - %s",request.getTime(),request.getOutTime())) &&
                        costCenter.equals(request.getCostCenter()) && enterDate.equals(todayDate) && status.equals(requestStatus)){
                    return rows.get(i);
                }
            }
        }
        return null;
    }

    public boolean verifySurchargeRequestExist(IColombia request){
        SelenideElement row = searchSurchargeRequest(request, "Escalado");
        return row != null;
    }

    public void deleteSurchargeRequest(IColombia request){
        SelenideElement row = searchSurchargeRequest(request, "Escalado");
        if (row.exists()){
            int index = getHeaderIndex("Actions", "Acción",null,$$(".table thead th"));
            SelenideElement column = row.$$("td").get(index);
            column.$x(".//a[img[@title='Borrar']]").click();
            //Selenide.sleep(2000);
            CommonTest.clickModal("//*[@id=\"modal_portalconfirm_btn0\"]");
            //$("#modal_portalconfirm_btn0").click();
        }else{
            Assert.fail("Was not possible to find the request and delete it");
        }
    }
}
