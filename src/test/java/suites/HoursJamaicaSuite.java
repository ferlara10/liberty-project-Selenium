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
import pages.jm.TimeSheetRequestJMPage;
import pojo.IJamaica;
import suites.utils.CommonTest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.qameta.allure.Allure.step;

public class HoursJamaicaSuite {

    private static Object[][] cachedData;
    private String apiURL;
    private String baseURL;
    private String globalUser;
    private String globalPass;
    private String authToken = "";

    private String employeePassword = "";
    private JSONObject managerInformation = null;
    private String managerPassword = "";

    private String scenariosTable = "TEST_LLA-JM_Hours";
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
            cachedData = commonTest.getScenarios(this.authToken,this.scenariosTable,"","JM");
            System.out.println("finish fetch data...");
        }
    }

    @BeforeClass
    public void setup() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080"; // sets window size
        Configuration.headless = true;
    }

    //for debug
    @DataProvider(name = "jsonData")
    public Object[][] dataProviderJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<IJamaica> dataList = mapper.readValue(new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("dataJamaica.json")
                ).getFile()
        ), new TypeReference<List<IJamaica>>() {});

        Object[][] dataArray = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i][0] = dataList.get(i);
        }
        return dataArray;
    }

    @DataProvider(name = "jamaicaScenarios")
    public Object[][] dataProviderJamaica(){
        return cachedData;
    }

    @Test(dataProvider = "jamaicaScenarios")
    public void hoursFlow(IJamaica scenario) throws IOException {
        System.out.println("-->> Test initialized ");

        step("Send the Request"+" - Class: "+scenario.getCompany()+" - "+scenario.getScenario(), () -> {
            sendRequest(scenario);
        });
        step("Approve Request: " +scenario.getCompany()+", "+scenario.getScenario()+", " +
                scenario.getEmployee()+", "+scenario.getDateBeg()+", "+ scenario.getCostCenter(), () -> {
            approveRequest(scenario.getEmployee(), scenario.getCompany(), scenario);
        });
        step("Report", () -> {
            reportResults(scenario);
        });
        step("Revert Request: " + scenario.getEmployee(), () -> {
            revertRequest(scenario.getCompany(), scenario);
        });
        step("Delete Request", () -> {
            deleteRequest(scenario);
        });

        System.out.println("Finish test case...");
    }

    public void sendRequest(IJamaica scenario){
        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            String language = loginPage.getLanguage();
            employeePassword = loginPage.getUserPassword(this.apiURL, this.globalUser, this.authToken, scenario.getCompany());
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );
            this.oneID = loginPage.getOneID();

            boolean requestExist = false;
            TimeSheetRequestJMPage requestPage =
                    (TimeSheetRequestJMPage) homePage.navigateRequestJM(scenario.getClasse(),true);
            requestPage.addTimesheetRequestJM(scenario);
            String status = language.equals("English") ? "Escalated" : "Escalado";
            requestExist = requestPage.verifyRequestExistJM(scenario, status, language);
            //TODO - add more class
            Assert.assertTrue(requestExist, "Don't able to find the request ");
            homePage.logout();
            System.out.println("Step 1 - send request");
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
    }

    public void approveRequest(String employee, String company, IJamaica request) throws IOException {

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
                //Same company scenario
                TimeSheetRequestJMPage requestPage = (TimeSheetRequestJMPage)
                        homePage.navigateApprovalsJM(request.getClasse(), language, true);

                requestPage.approveRequestJM(request, status, language);
                boolean requestExist = requestPage.verifyRequestExistJM(request, status, language);
                homePage.logout();
                Assert.assertFalse(requestExist, "I found a request. it's not supposed to be there");

            }else {
                //TODO - Multicompany scenario
                System.out.println("->");
            }
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
        System.out.println("Step 2 - approve");

    }

    public void reportResults(IJamaica scenario) throws IOException {
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
            Assert.fail("Data does not matches expected results. Result:" + data.get("match"));
        }
        System.out.println("Step 5 - report");
    }


    public void revertRequest(String company, IJamaica request){

        try{
            LoginPage loginPage = new LoginPage();
            String managerCompany = this.managerInformation.getString("Company");
            String managerEmployee = this.managerInformation.getString("Employee");

            loginPage.navigate(this.baseURL+managerCompany+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(managerEmployee, this.managerPassword, "" );

            String status = language.equals("English") ? "Completed" : "Completado";
            if (company.equals(managerCompany)){
                //Same company scenario
                TimeSheetRequestJMPage requestPage = (TimeSheetRequestJMPage)
                        homePage.navigateApprovalsJM(request.getClasse(), language, false);


                requestPage.reverseRequestJM(request, status, language);
                boolean requestExist = requestPage.verifyRequestExistJM(request,status, language);
                Assert.assertFalse(requestExist, "Don't able to find the request ");
                homePage.logout();

            }else{
                //TODO - Multicompany scenario
                System.out.println("->");
            }
        }catch(AssertionError e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }
        System.out.println("Step 3 - revert");
    }

    public void deleteRequest(IJamaica scenario){

        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );

            boolean requestExist = false;
            TimeSheetRequestJMPage requestPage =
                    (TimeSheetRequestJMPage) homePage.navigateRequestJM(scenario.getClasse(),true);
            String status = language.equals("English") ? "Escalated" : "Escalado";
            requestPage.deleteTimesheetRequestJM(scenario,status,language);
            requestExist = requestPage.verifyRequestExistJM(scenario, status, language);

            Assert.assertFalse(requestExist, "Don't able to find the request ");
            homePage.logout();
            System.out.println("Step 4 - delete");
        }catch (AssertionError e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }

    }




}
