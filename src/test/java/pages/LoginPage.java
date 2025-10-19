package pages;

import com.codeborne.selenide.Selenide;
import org.json.JSONException;
import org.json.JSONObject;
import suites.utils.CommonTest;

import java.io.IOException;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {
    //Locators
    private String emailInput = "#email";
    private String passwordInput = "#password";
    private String languageSelect = "#langselected";
    private String loginButton = "#loginbtn";

    //Actions
    public HomePage login(String user, String pass, String language){
        $(emailInput).setValue(user);
        $(passwordInput).setValue(pass);
        //TODO - handle the language because when you change it, refresh the page
        if (!language.isEmpty())
            $(languageSelect).selectOption(language);
        CommonTest.click(loginButton,false);
        return new HomePage();
    }

    public void navigate(String url){
        open(url);
        CommonTest.waitForPageToLoad();
    }

    public String getUserPassword(String baseURL, String user, String token, String company) throws IOException {
        String passURL = String.format("%s/dt_testerpassword/?user=%s&token=%s&company=%s",
                baseURL, user, token, company);

        JSONObject data = CommonTest.getJsonObject(passURL).getJSONObject("data").getJSONObject("Password");
        return data.getString("PortalTestPwd");
    }


    public JSONObject getManagerInformation(String baseURL, String user, String token, String employee, String company) throws IOException {
        String passURL = String.format("%s/dt_testermanager/?user=%s&token=%s&company=%s&employee=%s",
                baseURL, user, token, company, employee);

        JSONObject response = CommonTest.getJsonObject(passURL);
        try{
            return response.getJSONArray("data").getJSONObject(0);
        }catch (JSONException e){
            System.out.println("I found an error in the API: "+response.get("status_msg"));
            throw new IOException("I found an error in the API: "+response.get("status_msg"));
        }

    }

    public String getOneID(){
        String [] name = $("#user-status").getText().split("\n");
        String [] oneid = name[1].split(":");
        return oneid[1].trim()+" - "+name[0];
    }

    public String getLanguage(){
        Selenide.sleep(1000);
        return $(languageSelect).getText();
    }

}
