package suites.utils;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import pojo.IColombia;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.IInternational;
import pojo.IJamaica;
import pojo.IPanama;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
            case "PA": dataArray = mapper.readValue(a, IPanama[].class);      break;
            case "XX": dataArray = mapper.readValue(a, IInternational[].class); break;
            case "JM": dataArray = mapper.readValue(a, IJamaica[].class); break;
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
        waitForPageToLoad();
        return message;
    }

    public static JSONObject getResult(String apiURL, String user, String authToken, String table, String company, String scenarioName,
                                String employee, String dateBeg, String dateEnd) throws IOException {
        String url = String.format("%s/dt_testervariablepayment/?user=%s&token=%s&table=%s&company=%s&scenario=%s&employee=%s&dateBeg=%s&dateEnd=%s",
                apiURL, user, authToken, table, company, scenarioName, employee, dateBeg, dateBeg);

        return getJsonObject(url).getJSONObject("data");
    }

    public static void uploadDummyFile(String locator){
        $(locator).uploadFile(new File(
                Objects.requireNonNull(
                        CommonTest.class.getClassLoader().getResource("dummy.txt")
                ).getFile()));
    }

    public static String getDateBaseOnLanguage(String language, String requestDate){
        String convertedDate = "";
        if (language.equals("English"))
            convertedDate = CommonTest.convertDate(requestDate,"E");
        else
            convertedDate = CommonTest.convertDate(requestDate,"S");
        return convertedDate;
    }

    public static String getInterval(String initialHour1, String finalHour1, String finalHour2, String finalHour3){
        String result = initialHour1+" - ";
        if (finalHour2.isEmpty() && finalHour3.isEmpty())
            return result+finalHour1;
        else{
            if (finalHour3.isEmpty())
                return result+finalHour2;
            else
                return result+finalHour3;
        }

    }

    public static void waitForPageToLoad() {
        Wait().withTimeout(Duration.ofSeconds(20)).until(webDriver ->
                Objects.equals(executeJavaScript("return document.readyState"), "complete") &&
                        Boolean.TRUE.equals(executeJavaScript("return jQuery.active == 0"))
        );
    }

    public static void click(String locator, boolean isXPath){
        if (isXPath)
            $x(locator).click();
        else
            $(locator).click();
        waitForPageToLoad();
    }

    public static void clickNoWait(By locator) throws IOException{
        try{
            $(locator).click();
        }catch (AssertionError e){
            throw new AssertionError("Was not possible to click the element: "+locator);
        }
    }

    public static HashMap<String, Integer> getHeadersIIndex(ElementsCollection header){
        HashMap<String, Integer> result = new HashMap<String, Integer>();;
        for(int i=0; i < header.size() ;i++){
            String name = header.get(i).getText();
            if (name.equals("Date") || name.equals("Fecha"))
                result.put("Date", i);
            if (name.equals("Wage Type") || name.equals("Concepto"))
                result.put("Wage Type", i);
            if (name.equals("Cost Center") || name.equals("Centro de Costo"))
                result.put("Cost Center", i);
            if (name.equals("Status") || name.equals("Estado"))
                result.put("Status", i);
            if (name.equals("Request Date") || name.equals("F. Solicitud") || name.equals("F. de Solicitud"))
                result.put("Request Date", i);
            if(name.equals("Action"))
                result.put("Action", i);
        }
        return result;
    }

}
