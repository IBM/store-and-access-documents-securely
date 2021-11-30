package com.example.approval.vos;

import java.sql.ResultSet;
import java.util.HashMap;

import org.json.JSONObject;

import com.example.approval.utils.UserType;

public class UserVO {
	
	private String userid;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String address;
	private String nationalId;
	private String taxId;
	private String emailId;
	private String income;
	private String department;
	private String mandatoryValidationMessage;
	private String optionalValidationMessage;
	private String userType;
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	public String getTaxId() {
		return taxId;
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getMandatoryValidationMessage() {
		return mandatoryValidationMessage;
	}
	public void setMandatoryValidationMessage(String mandatoryValidationMessage) {
		this.mandatoryValidationMessage = mandatoryValidationMessage;
	}
	public String getOptionalValidationMessage() {
		return optionalValidationMessage;
	}
	public void setOptionalValidationMessage(String optionalValidationMessage) {
		this.optionalValidationMessage = optionalValidationMessage;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

	
	public UserVO(){
	}
	
	public UserVO(String userType){
		this.setUserType(userType);
	}
	
	public UserVO(HashMap<String, String> payload, String userType) throws Exception{
		this.mandatoryValidationMessage = "";
		this.optionalValidationMessage = "";
		this.setUserType(userType);
		
    	String key = (String)payload.get("user_id");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "User id is not provided. ";
    		this.setUserid("");
    	}else {
    		this.setUserid(key);
    	}

    	key = (String)payload.get("first_name");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "First name is not provided. ";
    		this.setFirstName("");
    	}else {
    		this.setFirstName(key);
    	}

    	key = (String)payload.get("last_name");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Last name is not provided. ";
    		this.setLastName("");
    	}else {
    		this.setLastName(key);
    	}

    	key = (String)payload.get("mobile_no");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Mobile number is not provided. ";
    		this.setMobileNo("");
    	}else {
    		this.setMobileNo(key);
    	}

    	key = (String)payload.get("address");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Address is not provided. ";
    		this.setAddress("");
    	}else {
    		this.setAddress(key);
    	}
    	
    	key = (String)payload.get("national_id");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "National id is not provided. ";
    		this.setNationalId("");
    	}else {
    		this.setNationalId(key);
    	}
    	
    	key = (String)payload.get("tax_id");
    	if( key == null ) {
    		this.optionalValidationMessage = this.optionalValidationMessage + "Tax id is not provided. ";
    		this.setTaxId("");
    	}else {
    		this.setTaxId(key);
    	}
    	
    	key = (String)payload.get("email_id");
    	if( key == null ) {
    		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Email id is not provided. ";
    		this.setEmailId("");
    	}else {
    		this.setEmailId(key);
    	}
    	
    	if(this.userType == UserType.customerType) {
        	key = (String)payload.get("income");
        	if( key == null ) {
        		this.optionalValidationMessage = this.optionalValidationMessage + "Income is not provided. ";
        		this.setIncome("NULL"); // optional in user
        	}else {
        		this.setIncome(key);
        	}
    	}
    	
    	if(this.userType == UserType.employeeType) {
        	key = (String)payload.get("department");
        	if( key == null ) {
        		this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Department is not provided. ";
        		this.setDepartment("");
        	}else {
        		this.setDepartment(key);
        	}
    	}
    	
    	if( this.mandatoryValidationMessage.length() > 0 ) {
    		throw new Exception(this.mandatoryValidationMessage);
    	}

	}
	
	
	public JSONObject toJSON(){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user_id", userid);
		jsonObject.put("first_name", firstName);
		jsonObject.put("last_name", lastName);
		jsonObject.put("mobile_no", mobileNo);
		jsonObject.put("address", address);
		jsonObject.put("national_id", nationalId);
		jsonObject.put("tax_id", taxId);
		jsonObject.put("email_id", emailId);
		if( this.userType == UserType.customerType ) {
			jsonObject.put("income", income);
		}
		if( this.userType == UserType.customerType ) {
			jsonObject.put("department", department);
		}
		jsonObject.put("mandatory_validation_message", mandatoryValidationMessage);
		jsonObject.put("optional_validation_message", optionalValidationMessage);
		
		return jsonObject;
	}
	
	public void setValues(ResultSet rs) throws Exception{
		this.setUserid(rs.getString("user_id"));
		this.setFirstName(rs.getString("first_name"));
		this.setLastName(rs.getString("last_name"));
		this.setMobileNo(rs.getLong("mobile_no")+"");
		this.setAddress(rs.getString("address"));
		this.setNationalId(rs.getString("national_id"));
		this.setTaxId(rs.getString("tax_id"));
		this.setEmailId(rs.getString("email_id"));
		if( this.userType == UserType.customerType ) {
			this.setIncome(rs.getDouble("income")+"");
		}
		if( this.userType == UserType.employeeType ) {
			this.setEmailId(rs.getString("department"));
		}
	}
	
	
}
