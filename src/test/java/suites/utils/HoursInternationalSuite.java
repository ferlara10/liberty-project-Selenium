package suites.utils;

import com.codeborne.selenide.Configuration;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.HomePage;
import pages.LoginPage;
import pages.co.TimeSheetRequestCOPage;
import pojo.IColombia;
import pojo.IInternational;

import java.io.IOException;

import static io.qameta.allure.Allure.step;

public class HoursInternationalSuite {

    private static Object[][] cachedData;  //cache here
    private String apiURL;
    private String baseURL;
    private String globalUser;
    private String globalPass;
    private String authToken = "";

    private String employeePassword = "";
    private JSONObject managerInformation = null;
    private String managerPassword = "";

    private String scenariosTable = "TEST_LLA-XX_Hours";
    private String oneID = "";

    @BeforeSuite
    @Parameters({"baseUrlParam", "apiEnvParam", "globalUserParam", "globalPassParam"})
    public void fetchData(String baseUrlParam, String apiEnvParam, String globalUserParam, String globalPassParam) throws Exception{

        String apiEnv = System.getProperty("apiEnv", apiEnvParam != null ? apiEnvParam : "");

        this.globalUser = System.getProperty("user", globalUserParam != null ? globalUserParam : "");
        this.globalPass = System.getProperty("pass", globalPassParam != null ? globalPassParam : "");
        this.baseURL = System.getProperty("baseURL", baseUrlParam != null ? baseUrlParam : "");

        if(cachedData == null){
            CommonTest commonTest = new CommonTest(apiEnv,this.globalUser,this.globalPass);
            this.apiURL = commonTest.getAPIURL();
            authToken = commonTest.getToken();
            cachedData = commonTest.getScenarios(this.authToken,this.scenariosTable,"", "XX");
            System.out.println("finish...");
        }
    }

    @BeforeClass
    public void setup() {
        Configuration.browser = "chrome"; // or "firefox"
        Configuration.browserSize = "1920x1080"; // sets window size
    }

    @DataProvider(name = "internationalScenarios")
    public Object[][] dataProviderColombia(){
        return cachedData;
    }


    @Test(dataProvider = "internationalScenarios")
    public void hoursFlow(IInternational scenario) throws IOException {
        System.out.println("-->> Test initialized ");
        step("Send the Request", () -> {
            //sendRequest(scenario);
        });
        /*
        step("Approve Request: " + scenario.getEmployee()+", "+scenario.getDateBeg()+", "+
                scenario.getTime()+", "+scenario.getOutTime()+", "+scenario.getCostCenter(), () -> {
            approveRequest(scenario.getEmployee(), scenario.getCompany(), scenario);
        });
        step("Revert Request: " + scenario.getEmployee(), () -> {
            revertRequest(scenario.getCompany(), scenario);
        });
        step("Delete Request", () -> {
            deleteRequest(scenario.getEmployee(), scenario);
        });
        step("Report", () -> {
            reportResults(this.scenariosTable,scenario.getCompany(), scenario.getScenario(),
                    scenario.getEmployee(), scenario.getDateBeg(), scenario.getDateBeg());
        });
         */
        //System.out.println("Finish...");
    }
    /*
    public void sendRequest(IInternational scenario){

        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            employeePassword = loginPage.getUserPassword(this.apiURL, this.globalUser, this.authToken, scenario.getCompany());
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );
            this.oneID = loginPage.getOneID();

            TimeSheetRequestCOPage requestPage = (TimeSheetRequestCOPage) homePage.navigateRequest("CO");
            requestPage.addTimesheetRequest(scenario);
            boolean requestExist = requestPage.verifyRequestExist(scenario);
            Assert.assertTrue(requestExist, "Don't able to find the request ");
            homePage.logout();
            System.out.println("Step 1 - send request");
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
    }
    */


}
