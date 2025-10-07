package suites;

import com.codeborne.selenide.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.HomePage;
import pages.LoginPage;
import pages.in.TimeSheetRequestInternationalPage;
import pojo.IInternational;
import suites.utils.CommonTest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.qameta.allure.Allure.step;

public class HoursInternationalSuite {

    private static Object[][] cachedData;
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
            cachedData = commonTest.getScenarios(this.authToken,this.scenariosTable,"","XX");
        }
    }

    @BeforeClass
    public void setup() {
        Configuration.browser = "chrome"; // or "firefox"
        Configuration.browserSize = "1920x1080"; // sets window size
        Configuration.headless = true;
    }

    @DataProvider(name = "jsonData")
    public Object[][] dataProviderJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<IInternational> dataList = mapper.readValue(new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("5_dataLNGGT.json")
                ).getFile()
        ), new TypeReference<List<IInternational>>() {});

        Object[][] dataArray = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i][0] = dataList.get(i);
        }
        return dataArray;
    }

    @DataProvider(name = "internationalScenarios")
    public Object[][] dataProviderInternational(){
        return cachedData;
    }

    @Test(dataProvider = "internationalScenarios")
    public void hoursFlow(IInternational scenario) throws IOException {
        System.out.println("-->> Test initialized ");

        step("Send the Request"+" - Scenario: "+scenario.getScenario(), () -> {
            sendRequest(scenario);
        });
        step("Approve Request: " +scenario.getCompany()+", "+scenario.getScenario()+", " +
                scenario.getEmployee()+", "+scenario.getDateBeg()+", "+ scenario.getCostCenter(), () -> {
            approveRequest(scenario.getEmployee(), scenario.getCompany(), scenario);
        });
        step("Report", () -> {
            reportResults(this.scenariosTable,scenario);
        });
        step("Revert Request: " + scenario.getEmployee(), () -> {
            revertRequest(scenario.getCompany(), scenario);
        });
        step("Delete Request", () -> {
            deleteRequest(scenario);
        });

        System.out.println("Finish...");
    }

    public void sendRequest(IInternational scenario){
        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            String language = loginPage.getLanguage();
            employeePassword = loginPage.getUserPassword(this.apiURL, this.globalUser, this.authToken, scenario.getCompany());
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );

            this.oneID = loginPage.getOneID();

            boolean requestExist = false;
            TimeSheetRequestInternationalPage requestPage =
                    (TimeSheetRequestInternationalPage) homePage.navigateRequest(language,"XX");
            requestPage.addTimesheetRequest(scenario, language);
            String status = language.equals("English") ? "Escalated" : "Escalado";
            requestExist = requestPage.verifyRequestIExist(scenario,status,language);

            Assert.assertTrue(requestExist, "Don't able to find the request ");
            homePage.logout();
            System.out.println("Step 1 - send request");
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
    }

    public void approveRequest(String employee, String company, Object scenario) throws IOException {

        try{
            LoginPage loginPage = new LoginPage();
            this.managerInformation = loginPage.getManagerInformation(this.apiURL,this.globalUser,this.authToken,employee,company);
            String managerCompany = this.managerInformation.getString("Company");
            String managerEmployee = this.managerInformation.getString("Employee");
            this.managerPassword = loginPage.getUserPassword(this.apiURL,this.globalUser,this.authToken, managerCompany);


            loginPage.navigate(this.baseURL+managerCompany+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(managerEmployee, this.managerPassword, "" );
            String status = language.equals("English") ? "Pending" : "Pendiente";

            if (company.equals(managerCompany)){
                //TODO - Same company scenario
                TimeSheetRequestInternationalPage requestPage;
                IInternational scene = (IInternational) scenario;

                requestPage = (TimeSheetRequestInternationalPage) homePage.navigateOvertimeApprovalsInternational(language);

                requestPage.approveInternationalRequest(scene, status, this.oneID, language);
                boolean requestExist = requestPage.verifyRequestIExist(scene, status, language);
                homePage.logout();
                Assert.assertFalse(requestExist, "I found a request. it's not supposed to be there");

            }else {
                //TODO - Multicompany scenario
                TimeSheetRequestInternationalPage requestPage;
                IInternational scene = (IInternational) scenario;

                requestPage = (TimeSheetRequestInternationalPage) homePage.navigateIntercompanyOvertimeInternational(language,company,managerCompany);


                requestPage.approveInternationalRequest(scene, status, this.oneID, language);
                boolean requestExist = requestPage.verifyRequestIExist(scene, status, language);
                homePage.logout();
                Assert.assertFalse(requestExist, "I found a request. it's not supposed to be there");
            }
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
        System.out.println("Step 2 - approve");

    }

    public void revertRequest(String company, Object scenario){

        try{
            LoginPage loginPage = new LoginPage();
            String managerCompany = this.managerInformation.getString("Company");
            String managerEmployee = this.managerInformation.getString("Employee");

            loginPage.navigate(this.baseURL+managerCompany+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(managerEmployee, this.managerPassword, "" );

            String status = language.equals("English") ? "Approved" : "Completado";
            if (company.equals(managerCompany)){
                //TODO - Same company scenario
                TimeSheetRequestInternationalPage requestPage;
                IInternational scene = (IInternational) scenario;

                requestPage = (TimeSheetRequestInternationalPage) homePage.navigateOvertimeApprovalsHistoricInternational(language);


                requestPage.reverseIRequest(scene, status, language);
                boolean requestExist = requestPage.verifyRequestIExist(scene,status, language);
                Assert.assertFalse(requestExist, "Don't able to find the request ");
                homePage.logout();

            }else{
                //TODO - Multicompany scenario
                TimeSheetRequestInternationalPage requestPage ;
                IInternational scene = (IInternational) scenario;
                requestPage = (TimeSheetRequestInternationalPage) homePage.navigateIntercompanyHistoricInternational(language);

                requestPage.reverseIRequest(scene, status, language);
                boolean requestExist = requestPage.verifyRequestIExist(scene,status, language);
                Assert.assertFalse(requestExist, "Don't able to find the request ");
                homePage.logout();
            }
        }catch(AssertionError e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
        System.out.println("Step 3 - revert");
    }


    public void deleteRequest(IInternational scenario){

        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );

            boolean requestExist = false;

            TimeSheetRequestInternationalPage requestPage =
                    (TimeSheetRequestInternationalPage) homePage.navigateRequest(language,"XX");
            String status = language.equals("English") ? "Escalated" : "Escalado";
            requestPage.deleteTimesheetRequest(scenario,status, language);
            requestExist = requestPage.verifyRequestIExist(scenario, status, language);

            Assert.assertFalse(requestExist, "Don't able to find the request ");
            homePage.logout();
            System.out.println("Step 4 - delete");
        }catch (AssertionError e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }

    }

    public void reportResults(String table, IInternational scenario) throws IOException {
        JSONObject data = CommonTest.getResult(this.apiURL, this.globalUser, this.authToken, this.scenariosTable,
                scenario.getCompany(), scenario.getScenario(), scenario.getEmployee(),
                scenario.getDateBeg(), scenario.getDateBeg());

        JSONArray result = data.getJSONArray("result");
        JSONArray expected = data.getJSONArray("expected");
        String match = data.getString("match");
        if (match.equals("SUCCESS"))
            Allure.step("Validation passed", Status.PASSED);
        else{
            revertRequest(scenario.getCompany(),scenario);
            deleteRequest(scenario);
            System.out.println("Step 5 - report -> Failed");
            Assert.fail("Data does not matches expected results. Result:" + data.get("match"));
        }
        System.out.println("Step 5 - report");
    }



}
