package dev.sample.ssd.das.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dev.sample.ssd.das.vos.LoanAccountVO;
import dev.sample.ssd.das.vos.SavingsAccountVO;
import dev.sample.ssd.das.vos.UserVO;

public class DBUtils {
	private static String schemaName = null;

	public static Connection getConnection() throws Exception {
		Class.forName("com.ibm.db2.jcc.DB2Driver");

		InputStream inputStream = null;
		JSONParser jsonParser = new JSONParser();
		inputStream = DBUtils.class.getClassLoader().getResourceAsStream("config/credentials-db2.json");
		JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));

		JSONObject conObject = (JSONObject) jsonObject.get("connection");
		JSONObject db2Object = (JSONObject) conObject.get("db2");

		JSONArray hostsArray = (JSONArray) db2Object.get("hosts");
		JSONObject hostNameObject = (JSONObject) hostsArray.get(0);
		String hostName = (String) hostNameObject.get("hostname");
		String port = (String) (hostNameObject.get("port") + "");

		String db = (String) db2Object.get("database");

		JSONObject authObject = (JSONObject) db2Object.get("authentication");
		String username = (String) authObject.get("username");
		String password = (String) authObject.get("password");
		schemaName = username;

		String jdbcurl = "jdbc:db2://" + hostName + ":" + port + "/" + db + ":user=" +
				username + ";password=" + password + ";sslConnection=true;";

		Connection con = DriverManager.getConnection(jdbcurl);

		return con;

	}

	public static String getUserQuery(String userid) {
		return "SELECT * " +
				"from " + schemaName + ".user_details where user_id = '" + userid + "'";
	}

	public static String getUsersQuery() {
		return "SELECT * from " + schemaName + ".user_details";
	}

	public static String getEmployeeQuery(String employeeId) {
		return "SELECT * " +
				"from " + schemaName + ".bank_employee where user_id = '" + employeeId + "'";
	}

	public static String getSAQuery(String userid) {
		String query = "SELECT * " +
				"from " + schemaName + ".savings_accounts where user_id = '" + userid + "' and status='approved'";
		System.out.println("getSAQuery = " + query);
		return query;
	}

	public static String getPendingSavingsAccounts() {
		String query = "SELECT * " +
				"FROM " + schemaName + ".user_details as users, " + schemaName + ".savings_accounts as sa " +
				"WHERE users.user_id = sa.user_id and UPPER(sa.status) = 'PENDING'";
		System.out.println("getPendingSavingsAccounts: " + query);

		return query;
	}

	public static String updateUserForLoanQuery(String userid, LoanAccountVO updateSAData) throws Exception {
		String query = "UPDATE " + schemaName + ".user_details SET tax_id = '" +
				updateSAData.getTaxId() + "', income = " + updateSAData.getIncome() + " where user_id = '" + userid
				+ "'";
		System.out.println("updateSAQuery = " + query);

		return query;
	}

	public static String updateLAQuery(String userid, LoanAccountVO updateLAData) throws Exception {
		String updateLAQuery = "UPDATE " + schemaName + ".loan_accounts SET status = '" + updateLAData.getStatus()
				+ "', approve_or_reject_date = '" + Utils.getCurrentDate() +
				"', reject_reason = '" + updateLAData.getRejectReason() + "', rate_of_interest = 7.5" +
				" WHERE user_id = '" + userid + "'";

		System.out.println("updateLAQuery = " + updateLAQuery);

		return updateLAQuery;

	}

	public static String getLAQuery(String userid) {
		String laQuery = "SELECT * " +
				"from " + schemaName + ".loan_accounts where user_id = '" + userid + "'";
		System.out.println("LA query = " + laQuery);
		return laQuery;
	}

	public static String getPendingLAQuery() {
		String query = "SELECT * " +
				"FROM " + schemaName + ".user_details as users, " + schemaName + ".loan_accounts as la " +
				"WHERE users.user_id = la.user_id and UPPER(la.status) = 'PENDING'";
		System.out.println("getPendingLAQuery: " + query);

		return query;
	}

	public static String getUpdateSAStatusQuery(String userid, SavingsAccountVO saData) {
		String saUpdateStatusQuery = "UPDATE " + schemaName + ".savings_accounts SET status = '" + saData.getStatus()
				+ "', " +
				"approve_or_reject_date = '" + Utils.getCurrentDate() + "', " + " reject_reason = '"
				+ saData.getRejectReason() + "'" +
				" WHERE user_id = '" + userid + "'";

		System.out.println("getUpdateSAStatusQuery = " + saUpdateStatusQuery);
		return saUpdateStatusQuery;
	}

	public static String createUserQuery(UserVO userData) throws Exception {

		if (userData.getMandatoryValidationMessage().length() > 0) {
			throw new Exception(userData.getMandatoryValidationMessage());
		}

		StringBuffer querySB = new StringBuffer("");
		querySB.append("INSERT INTO ");
		querySB.append(schemaName);
		querySB.append(".user_details (user_id, first_name, last_name, mobile_no, " +
				"address, national_id, tax_id, email_id, income) ");
		querySB.append("VALUES ('");
		querySB.append(userData.getUserid()).append("', '");
		querySB.append(userData.getFirstName()).append("', '");
		querySB.append(userData.getLastName()).append("', ");
		querySB.append(userData.getMobileNo()).append(", '");
		querySB.append(userData.getAddress()).append("', '");
		querySB.append(userData.getNationalId()).append("', '");
		querySB.append(userData.getTaxId()).append("', '");
		querySB.append(userData.getEmailId()).append("', ");
		querySB.append(userData.getIncome()).append(")");

		System.out.println("POST user query: " + querySB.toString());

		return querySB.toString();
	}

	public static String createSAQuery(String userid, SavingsAccountVO saData) throws Exception {

		StringBuffer querySB = new StringBuffer("");
		querySB.append("INSERT INTO ").append(schemaName).append(".savings_accounts ");
		querySB.append("(user_id, status, savings_account_no, account_balance, apply_date) ");
		querySB.append("VALUES ('");
		querySB.append(userid).append("', '");
		querySB.append(Constants.status_pending).append("', ");
		querySB.append(saData.getSavingsAccountNo()).append(", ");
		querySB.append(1000).append(", '"); // Account balance is 1000 when SA is created
		querySB.append(Utils.getCurrentDate()).append("')");

		System.out.println("create SA Query = " + querySB.toString());

		return querySB.toString();
	}

	public static String createLAQuery(String userid, LoanAccountVO laData) throws Exception {
		StringBuffer querySB = new StringBuffer("");
		querySB.append("INSERT INTO ").append(schemaName).append(".loan_accounts ");
		querySB.append("(user_id, loan_account_no, status, apply_date, loan_type, loan_amount) ");
		querySB.append("VALUES ('");
		querySB.append(userid).append("', ");
		querySB.append(laData.getLoanAccountNo()).append(", '");
		querySB.append(Constants.status_pending).append("', '");
		querySB.append(Utils.getCurrentDate()).append("', '");
		querySB.append(laData.getLoanType()).append("', ");
		querySB.append(laData.getLoanAmount());
		querySB.append(")");

		System.out.println("create LA Query = " + querySB.toString());

		return querySB.toString();
	}

	public static String createEmployeeQuery(UserVO empData) throws Exception {
		StringBuffer querySB = new StringBuffer("");
		querySB.append("INSERT INTO ");
		querySB.append(schemaName);
		querySB.append(".bank_employee (user_id, first_name, last_name, mobile_no, " +
				"address, national_id, tax_id, email_id, department) ");
		querySB.append("VALUES ('");
		querySB.append(empData.getUserid()).append("', '");
		querySB.append(empData.getFirstName()).append("', '");
		querySB.append(empData.getLastName()).append("', ");
		querySB.append(empData.getMobileNo()).append(", '");
		querySB.append(empData.getAddress()).append("', '");
		querySB.append(empData.getNationalId()).append("', '");
		querySB.append(empData.getTaxId()).append("', '");
		querySB.append(empData.getEmailId()).append("', '");
		querySB.append(empData.getDepartment()).append("')");

		return querySB.toString();
	}

	public static String getLastLoanAccountNumberQuery() {
		String query = "SELECT MAX(loan_account_no) as loan_account_no from " + schemaName + ".loan_accounts";
		return query;
	}

	public static String getLastSavingsAccountNumberQuery() {
		String query = "SELECT MAX(savings_account_no) as savings_account_no from " + schemaName + ".savings_accounts";
		return query;
	}

	public static String getDeleteUserFromUserQuery(String userid) {
		String query = "DELETE FROM " + schemaName + ".user_details WHERE user_id = '" + userid + "'";
		return query;
	}

	public static String getDeleteUserFromSavingsAccountQuery(String userid) {
		String query = "DELETE FROM " + schemaName + ".savings_accounts WHERE user_id = '" + userid + "'";
		return query;
	}

	public static String getDeleteUserFromLoanAccountQuery(String userid) {
		String query = "DELETE FROM " + schemaName + ".loan_accounts WHERE user_id = '" + userid + "'";
		return query;
	}

}
