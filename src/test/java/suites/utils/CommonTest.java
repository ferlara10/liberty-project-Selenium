package suites.utils;

import com.codeborne.selenide.Selenide;
import org.json.JSONArray;
import org.json.JSONObject;
import pojo.IColombia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.IInternational;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.*;

public class CommonTest {

    String user = "api-portal";
    String pass = "Temp123$";
    String apiURL = "https://qas.payroll123.net/dev/api/";

    public CommonTest(String env, String user, String pass){
        this.user = user;
        this.pass = pass;
        if (env.equals("qa"))
            apiURL = "https://payroll123.net/dev/api/";
        if (env.equals("dev"))
            apiURL = "https://qas.payroll123.net/dev/api/";
    }

    public String getAPIURL(){
        return this.apiURL;
    }

    public String getToken() throws IOException {
        String loginURL = String.format("%slogin/?user=%s&password=%s&_bodyPayload=1&f=login",apiURL,user,pass);
        // Parse JSON
        JSONObject jsonObject = getJsonObject(loginURL);

        return jsonObject.getString("token");
    }

    public Object [] [] getScenarios(String token, String table, String filter, String branch) throws IOException {
        String scenariosURL = String.format("%s/md_customtable/?user=%s&token=%s&table=%s&filter=%s",
                apiURL,user,token,table,filter);

        // Parse JSON
        JSONObject jsonObject = getJsonObject(scenariosURL);
        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("data"));

        ObjectMapper mapper = new ObjectMapper();
        String a = jsonArray.toString();
        Object [] dataArray;
        switch (branch){
            case "CO": dataArray = mapper.readValue(a, IColombia[].class);      break;
            case "XX": dataArray = mapper.readValue(a, IInternational[].class); break;
            default: dataArray = mapper.readValue(a, IColombia[].class);
        }
        Object[][] testData = new Object[dataArray.length][1];

        for (int i = 0; i < dataArray.length; i++) {
            testData[i][0] = dataArray[i];
        }
        return testData;
    }

    public static JSONObject getJsonObject(String URL) throws IOException {
        URL url = new URL(URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // Parse JSON
        return  new JSONObject(response.toString());
    }

    public static String convertDate(String dateInput, String language){
        // Parse input
        LocalDate date = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // Format to MM/dd/yyyy
        if (language.equals("S"))
            return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        else
            return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    public static String getTodayDate(){
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String getTodayDateEnglish(){
        LocalDate today = LocalDate.now();
        return today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    public static void enterTime(String time, String locator){
        executeJavaScript(
                "arguments[0].value='"+time+"'; arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                $(locator)
        );
    }

    public static String clickModal(String buttonOKLocator){
        Selenide.sleep(2000);
        String message = $("#modal_bdy_portalconfirm").getText();
        $x(buttonOKLocator).click();
        Selenide.sleep(1000);
        return message;
    }

    public static JSONObject getResult(String apiURL, String user, String authToken, String table, String company, String scenarioName,
                                String employee, String dateBeg, String dateEnd) throws IOException {
        String url = String.format("%s/dt_testervariablepayment/?user=%s&token=%s&table=%s&company=%s&scenario=%s&employee=%s&dateBeg=%s&dateEnd=%s",
                apiURL, user, authToken, table, company, scenarioName, employee, dateBeg, dateBeg);

        return getJsonObject(url).getJSONObject("data");
    }

    public static void waitForPageToLoad() {
        Wait().until(webDriver ->
                Objects.equals(executeJavaScript("return document.readyState"), "complete") &&
                        Boolean.TRUE.equals(executeJavaScript("return jQuery.active == 0"))
        );
    }

}
