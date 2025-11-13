package suites;

import com.codeborne.selenide.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.HomePage;
import pages.LoginPage;
import pages.jm.ReimbursementsRequestJMPage;
import pojo.IReimbursement;
import suites.utils.CommonTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static io.qameta.allure.Allure.step;
import static suites.utils.CommonTest.*;

public class ReimbursementsSuite {

    private static Object[][] cachedData;
    private String apiURL;
    private String baseURL;
    private String globalUser;
    private String globalPass;
    private String authToken = "";

    private String employeePassword = "";
    private JSONObject managerInformation = null;
    private String managerPassword = "";

    private String scenariosTable = "TEST_Reimbursement";
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
            cachedData = commonTest.getScenarios(this.authToken,this.scenariosTable,"","Reimbursements");
            System.out.println("finish fetch data...");
        }
    }

    @BeforeClass
    public void setup() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080"; // sets window size
        Configuration.headless = true;
    }

    @BeforeMethod
    public void beforeMethod(Method method, Object[] testData) {
        IReimbursement scenario = (IReimbursement) testData[0];

        String URL = String.format("%sdt_testerdeleterequest/?user=%s&token=%s&company=%s&employee=%s&dateBeg=%s&dateEnd=%s&type=%s",
                this.apiURL, this.globalUser, this.authToken, scenario.getCompany(), scenario.getEmployee(),scenario.getDateBeg(),scenario.getDateBeg(),"RE");
        try{
            JSONObject object = getJsonObject(URL,"DELETE");
            int status = object.getInt("status");
            System.out.println("         -> [BEFORE] Deleting a request executed correctly "+status);
        }catch (IOException e){
            System.out.println("         -> [BEFORE] I got an issue trying to consume DELETE API: "+e);
        }
    }

    @AfterMethod
    public void afterMethod(Method method, Object[] testData) {
        IReimbursement scenario = (IReimbursement) testData[0];

        String URL = String.format("%sdt_testerdeleterequest/?user=%s&token=%s&company=%s&employee=%s&dateBeg=%s&dateEnd=%s&type=%s",
                this.apiURL, this.globalUser, this.authToken, scenario.getCompany(), scenario.getEmployee(),scenario.getDateBeg(),scenario.getDateBeg(),"RE");
        try{
            JSONObject object = getJsonObject(URL,"DELETE");
            int status = object.getInt("status");
            System.out.println("        -> [AFTER] Deleting a request executed correctly "+status);
        }catch (IOException e){
            System.out.println("        -> [AFTER] I got an issue trying to consume DELETE API: "+e);
        }
    }

    //for debug
    @DataProvider(name = "jsonData")
    public Object[][] dataProviderJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<IReimbursement> dataList = mapper.readValue(new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("errors.json")
                ).getFile()
        ), new TypeReference<List<IReimbursement>>() {});

        Object[][] dataArray = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i][0] = dataList.get(i);
        }
        return dataArray;
    }

    @DataProvider(name = "reimbursementScenarios")
    public Object[][] dataProviderJamaica(){
        return cachedData;
    }

    @Test(dataProvider = "reimbursementScenarios")
    public void hoursFlow(IReimbursement scenario) throws IOException {
        System.out.println("-->> Test initialized ");

        step("Send the Request"+" - Company: "+scenario.getCompany()+" - Employee: "+scenario.getEmployee(), () -> {
            sendRequest(scenario);
        });
        step("Approve Request", () -> {
            approveRequest(scenario.getEmployee(), scenario.getCompany(), scenario);
        });
        step("Report", () -> {
            reportResults(scenario);
        });
        step("Revert Request: " + scenario.getEmployee(), () -> {
            //revertRequest(scenario.getCompany(), scenario);
        });
        step("Delete Request", () -> {
            //deleteRequest(scenario);
        });
        System.out.println("Finish test case...");
    }

    public void sendRequest(IReimbursement scenario) {

        boolean isRequestSent = false;
        boolean requestExist = false;

        try {
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL + scenario.getCompany() + "/");
            String language = loginPage.getLanguage();
            employeePassword = loginPage.getUserPassword(this.apiURL, this.globalUser, this.authToken, scenario.getCompany());
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "");
            this.oneID = loginPage.getOneID();

            ReimbursementsRequestJMPage requestPage =
                    (ReimbursementsRequestJMPage) homePage.navigateReimbursements();
            isRequestSent = requestPage.addReimbursementsRequest(scenario, language);
            String status = language.equals("English") ? "Escalated" : "Escalado";
            requestExist = requestPage.verifyReimbursementExist(scenario, status, language);
            if (isRequestSent && !requestExist) {
                String currentDate = requestPage.getCurrentFromDateFilter();
                int difference = getDifferenceByMonths(currentDate, language);
                System.out.println("         -> Retrying to find the request Current: " + currentDate + "_Difference: " + difference);
                if (difference < 6) {
                    requestPage.changeFromDateFilter("01/01/2023");
                    requestExist = requestPage.verifyReimbursementExist(scenario, status, language);
                    if (!requestExist)
                        throw new IOException("Apparently y sent the request but i didn't find it in the table");
                }
            }
            //TODO - add more class
            homePage.logout();
            System.out.println("* Step 1 - Request Sent");
            Assert.assertTrue(requestExist, "Don't able to find the request ");
        } catch (AssertionError | IOException e) {
            HomePage homePage = new HomePage();
            homePage.logout();
            System.out.println("* Step 1 - ERROR - Company: " + scenario.getCompany() + " - Employee: " + scenario.getEmployee() + "_" + e);
            Assert.fail(e.getMessage());
        }
    }

    public void approveRequest(String employee, String company, IReimbursement scenario) throws IOException {

        boolean requestExist = false;
        String managerCompany = "";
        String managerEmployee = "";

        try{
            LoginPage loginPage = new LoginPage();
            this.managerInformation = loginPage.getManagerInformation(this.apiURL,this.globalUser,this.authToken,employee,company);
            managerCompany = this.managerInformation.getString("Company");
            managerEmployee = this.managerInformation.getString("Employee");
            this.managerPassword = loginPage.getUserPassword(this.apiURL,this.globalUser,this.authToken, managerCompany);

            loginPage.navigate(this.baseURL+managerCompany+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(managerEmployee, this.managerPassword, "" );
            String status = language.equals("English") ? "Pending" : "Pendiente";

            if (company.equals(managerCompany)){
                //Same company scenario
                ReimbursementsRequestJMPage requestPage = (ReimbursementsRequestJMPage)
                        homePage.navigateReimbursementApprovals();

                requestExist = requestPage.verifyReimbursementExist(scenario, status, language);
                if (!requestExist){
                    System.out.println("         -> Retrying to find the request");
                    String currentDate = requestPage.getCurrentFromDateFilter();
                    int difference = getDifferenceByMonths(currentDate, language);
                    if (difference < 6){
                        requestPage.changeFromDateFilter("01/01/2023");
                        requestExist = requestPage.verifyReimbursementExist(scenario, status, language);
                        if (!requestExist)
                            throw new IOException("Apparently y sent the request but i didn't find it in the table");
                    }
                }
                requestPage.approveReimbursementRequest(scenario, status, language);
                requestExist = requestPage.verifyReimbursementExist(scenario, status, language);
                homePage.logout();
                System.out.println("* Step 2 - Request Approved");
                Assert.assertFalse(requestExist, "I found a request. it's not supposed to be there");

            }else {
                //TODO - Multicompany scenario
                System.out.println("-> MULTICOMPANY IS NOT SUPPORTED YET");
                Assert.fail("-> MULTICOMPANY IS NOT SUPPORTED YET");
            }
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            System.out.println("* Step 2 - ERROR - Manager Company: "+managerCompany+" - Employee: "+managerEmployee + "- "+e);
            Assert.fail(e.getMessage());
        }
    }

    public void reportResults(IReimbursement scenario) throws IOException {
        JSONObject data = CommonTest.getResult(this.apiURL, this.globalUser, this.authToken, this.scenariosTable,
                scenario.getCompany(), scenario.getScenario(), scenario.getEmployee(),
                scenario.getDateBeg(), scenario.getDateBeg());

        String match = data.getString("match");
        if (match.equals("SUCCESS")) {
            System.out.println("* Step 5 - API query - report -> Passed");
            Allure.step("Validation passed", Status.PASSED);
        } else {
            //revertRequest(scenario.getCompany(),scenario);
            //deleteRequest(scenario);
            System.out.println("* Step 5 - API query - report -> Failed");
            Assert.fail("Data does not matches expected results. Result:" + data.get("match"));
        }
    }

}
