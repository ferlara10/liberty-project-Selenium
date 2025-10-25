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
import pages.TimeSheetRequestPage;
import pages.co.SurchargeRequestCOPage;
import pages.co.TimeSheetRequestCOPage;
import pojo.IColombia;
import suites.utils.CommonTest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static io.qameta.allure.Allure.step;
import static suites.utils.CommonTest.*;


public class HoursColombiaSuite {

    private static Object[][] cachedData;
    private String apiURL;
    private String baseURL;
    private String globalUser;
    private String globalPass;
    private String authToken = "";

    private String employeePassword = "";
    private JSONObject managerInformation = null;
    private String managerPassword = "";

    private String scenariosTable = "TEST_LLA-CO_Hours";
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
            cachedData = commonTest.getScenarios(this.authToken,this.scenariosTable,"","CO");
            System.out.println("finish...");
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
        IColombia scenario = (IColombia) testData[0];

        String URL = String.format("%sdt_testerdeleterequest/?user=%s&token=%s&company=%s&employee=%s&dateBeg=%s&dateEnd=%s&type=%s",
                this.apiURL, this.globalUser, this.authToken, scenario.getCompany(), scenario.getEmployee(),scenario.getDateBeg(),scenario.getDateBeg(),"HR");
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
        IColombia scenario = (IColombia) testData[0];

        String URL = String.format("%sdt_testerdeleterequest/?user=%s&token=%s&company=%s&employee=%s&dateBeg=%s&dateEnd=%s&type=%s",
                this.apiURL, this.globalUser, this.authToken, scenario.getCompany(), scenario.getEmployee(),scenario.getDateBeg(),scenario.getDateBeg(),"HR");
        try{
            JSONObject object = getJsonObject(URL,"DELETE");
            int status = object.getInt("status");
            System.out.println("        -> [AFTER] Deleting a request executed correctly "+status);
        }catch (IOException e){
            System.out.println("        -> [AFTER] I got an issue trying to consume DELETE API: "+e);
        }
    }

    @DataProvider(name = "jsonData")
    public Object[][] dataProviderJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<IColombia> dataList = mapper.readValue(new File(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource("errors.json")
                ).getFile()
        ), new TypeReference<List<IColombia>>() {});

        Object[][] dataArray = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            dataArray[i][0] = dataList.get(i);
        }
        return dataArray;
    }


    @DataProvider(name = "colombiaScenarios")
    public Object[][] dataProviderColombia(){
        return cachedData;
    }

    @Test(dataProvider = "colombiaScenarios")
    public void hoursFlow(IColombia scenario) throws IOException {
        System.out.println("-->> Test initialized ");

        step("Send the Request"+" - Class: "+scenario.getClasse(), () -> {
            sendRequest(scenario);
        });
        step("Approve Request: " +scenario.getCompany()+", "+scenario.getScenario()+", " +
                scenario.getEmployee()+", "+scenario.getDateBeg()+", "+
                scenario.getTime()+", "+scenario.getOutTime()+", "+scenario.getCostCenter(), () -> {
            approveRequest(scenario.getEmployee(), scenario.getCompany(), scenario);
        });
        step("Report", () -> {
            reportResults(this.scenariosTable,scenario);
        });
        step("Revert Request: " + scenario.getEmployee(), () -> {
            //revertRequest(scenario.getCompany(), scenario);
        });
        step("Delete Request", () -> {
            //deleteRequest(scenario.getEmployee(), scenario);
        });

        System.out.println("Finish...");
    }

    public void sendRequest(IColombia scenario){

        boolean isRequestSent = false;
        boolean requestExist = false;

        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            String language = loginPage.getLanguage();
            employeePassword = loginPage.getUserPassword(this.apiURL, this.globalUser, this.authToken, scenario.getCompany());
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );
            this.oneID = loginPage.getOneID();

            if (scenario.getClasse().equals("HE")){
                TimeSheetRequestCOPage requestPage =
                        (TimeSheetRequestCOPage) homePage.navigateRequest(language,"CO");
                isRequestSent = requestPage.addTimesheetRequest(scenario);
                requestExist = requestPage.verifyRequestExist(scenario);
                if (isRequestSent && !requestExist){
                    String currentDate = requestPage.getCurrentFromDateFilter();
                    int difference = getDifferenceByMonths(currentDate,getTodayDate());
                    System.out.println("         -> Retrying to find the request Current: "+currentDate+"_Difference: "+difference);
                    if (difference < 6){
                        requestPage.changeFromDateFilter("01/01/2023");
                        requestExist = requestPage.verifyRequestExist(scenario);
                        if (!requestExist)
                            throw new IOException("Apparently y sent the request but i didn't find it in the table");
                    }
                }
            }else{
                SurchargeRequestCOPage requestPage = (SurchargeRequestCOPage) homePage.navigateSurchargeRequest("CO");
                isRequestSent = requestPage.addTimesheetRequest(scenario);
                requestExist = requestPage.verifySurchargeRequestExist(scenario);
                if (isRequestSent && !requestExist){
                    String currentDate = requestPage.getCurrentFromDateFilter();
                    int difference = getDifferenceByMonths(currentDate,getTodayDate());
                    System.out.println("         -> Retrying to find the request Current: "+currentDate+"_Difference: "+difference);
                    if (difference < 6){
                        requestPage.changeFromDateFilter("01/01/2023");
                        requestExist = requestPage.verifySurchargeRequestExist(scenario);
                        if (!requestExist)
                            throw new IOException("Apparently y sent the request but i didn't find it in the table");
                    }
                }
            }
            homePage.logout();
            System.out.println("* Step 1 - Request Sent");
            Assert.assertTrue(requestExist, "Don't able to find the request ");
        }catch(AssertionError | IOException e){
            HomePage homePage = new HomePage();
            homePage.logout();
            System.out.println("* Step 1 - ERROR - Company: "+scenario.getCompany()+" - Employee: "+scenario.getEmployee()+"_"+e);
            Assert.fail(e.getMessage());
        }
    }

    public void approveRequest(String employee, String company, Object scenario) throws IOException {

        String managerCompany = "";
        String managerEmployee = "";

        boolean isLoggedin = false;
        boolean isRequestApproved = false;
        boolean requestExist = false;

        try{
            LoginPage loginPage = new LoginPage();
            this.managerInformation = loginPage.getManagerInformation(this.apiURL,this.globalUser,this.authToken,employee,company);
            managerCompany = this.managerInformation.getString("Company");
            managerEmployee = this.managerInformation.getString("Employee");
            this.managerPassword = loginPage.getUserPassword(this.apiURL,this.globalUser,this.authToken, managerCompany);

            loginPage.navigate(this.baseURL+managerCompany+"/");
            HomePage homePage = loginPage.login(managerEmployee, this.managerPassword, "" );
            if (company.equals(managerCompany)){
                //TODO - Same company scenario
                TimeSheetRequestPage requestPage;
                IColombia scene = (IColombia) scenario;
                if (scene.getClasse().equals("HE")){
                    requestPage = homePage.navigateOvertimeApprovals("Spanish");
                }else{
                    requestPage = homePage.navigateSurchargeApprovals("Spanish");
                }

                requestExist = requestPage.verifyRequestExist(scene, "Pendiente", this.oneID);
                if (!requestExist){
                    String currentDate = requestPage.getCurrentFromDateFilter();
                    int difference = getDifferenceByMonths(currentDate,getTodayDate());
                    System.out.println("         -> Retrying to find the request Current: "+currentDate+"_Difference: "+difference);
                    if (difference < 6){
                        requestPage.changeFromDateFilter("01/01/2023");
                        requestExist = requestPage.verifyRequestExist(scene, "Pendiente", this.oneID);
                        if (!requestExist)
                            throw new IOException("Apparently y sent the request but i didn't find it in the table");
                    }
                }
                requestPage.approveRequest(scene, "Pendiente", this.oneID);
                requestExist = requestPage.verifyRequestExist(scene, "Pendiente", this.oneID);
                homePage.logout();
                System.out.println("* Step 2 - Request Approved");
                Assert.assertFalse(requestExist, "I found a request. it's not supposed to be there");

            }else {
                //TODO - Multicompany scenario
                TimeSheetRequestPage requestPage;
                IColombia scene = (IColombia) scenario;
                if (scene.getClasse().equals("HE")){
                    requestPage = homePage.navigateIntercompanyOvertime("Spanish",company);
                }else{
                    requestPage = homePage.navigateIntercompanySurcharge("Spanish",company);
                }
                requestExist = requestPage.verifyRequestExist(scene, "Pendiente", this.oneID);
                if (!requestExist){
                    String currentDate = requestPage.getCurrentFromDateFilter();
                    int difference = getDifferenceByMonths(currentDate,getTodayDate());
                    System.out.println("         -> Retrying to find the request Current: "+currentDate+"_Difference: "+difference);
                    if (difference < 6){
                        requestPage.changeFromDateFilter("01/01/2023");
                        requestExist = requestPage.verifyRequestExist(scene, "Pendiente", this.oneID);
                        if (!requestExist)
                            throw new IOException("Apparently y sent the request but i didn't find it in the table");
                    }
                }
                requestPage.approveRequest(scene, "Pendiente", this.oneID);
                requestExist = requestPage.verifyRequestExist(scene, "Pendiente", this.oneID);
                homePage.logout();
                System.out.println("* Step 2 - Request Approved");
                Assert.assertFalse(requestExist, "I found a request. it's not supposed to be there");
            }
        }catch(AssertionError | IOException e){
            System.out.println("* Step 2 - ERROR - Manager Company: "+managerCompany+" - Employee: "+managerEmployee + "- "+e);
            Assert.fail(e.getMessage());
        }
    }


    public void revertRequest(String company, Object scenario){

        try{
            LoginPage loginPage = new LoginPage();
            String managerCompany = this.managerInformation.getString("Company");
            String managerEmployee = this.managerInformation.getString("Employee");

            loginPage.navigate(this.baseURL+managerCompany+"/");
            HomePage homePage = loginPage.login(managerEmployee, this.managerPassword, "" );
            if (company.equals(managerCompany)){
                //TODO - Same company scenario
                TimeSheetRequestPage requestPage;
                IColombia scene = (IColombia) scenario;
                if (scene.getClasse().equals("HE")){
                    requestPage = homePage.navigateOvertimeApprovalsHistoric("Spanish");
                }else{
                    requestPage = homePage.navigateSurchargeApprovalsHistoric("Spanish");
                }
                requestPage.reverseRequest(scene, "Completado", this.oneID);
                boolean requestExist = requestPage.verifyRequestExist(scene,"Completado",this.oneID);
                Assert.assertFalse(requestExist, "Don't able to find the request ");
                homePage.logout();

            }else{
                //TODO - Multicompany scenario
                TimeSheetRequestPage requestPage ;
                IColombia scene = (IColombia) scenario;
                requestPage = homePage.navigateIntercompanyHistoric("Spanish");
                requestPage.reverseRequest(scene, "Completado", this.oneID);
                boolean requestExist = requestPage.verifyRequestExist(scene,"Completado",this.oneID);
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

    public void deleteRequest(String username, IColombia scenario){

        try{
            LoginPage loginPage = new LoginPage();
            loginPage.navigate(this.baseURL+scenario.getCompany()+"/");
            String language = loginPage.getLanguage();
            HomePage homePage = loginPage.login(scenario.getEmployee(), this.employeePassword, "" );

            boolean requestExist = false;
            if (scenario.getClasse().equals("HE")){
                TimeSheetRequestCOPage requestPage =
                        (TimeSheetRequestCOPage) homePage.navigateRequest(language,"CO");
                requestPage.deleteTimesheetRequest(scenario);
                requestExist = requestPage.verifyRequestExist(scenario);
            }else{
                SurchargeRequestCOPage requestPage = (SurchargeRequestCOPage) homePage.navigateSurchargeRequest("CO");
                requestPage.deleteSurchargeRequest(scenario);
                requestExist = requestPage.verifySurchargeRequestExist(scenario);
            }
            Assert.assertFalse(requestExist, "Don't able to find the request ");
            homePage.logout();
            System.out.println("Step 4 - delete");
        }catch (AssertionError e){
            HomePage homePage = new HomePage();
            homePage.logout();
            Assert.fail(e.getMessage());
        }

    }


    public void reportResults(String table, IColombia scenario) throws IOException {
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
