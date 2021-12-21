package dev.sample.ssd.das.vos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.json.simple.JSONObject;

import dev.sample.ssd.das.utils.OperationType;

public class LoanAccountVO {

	private String userid;
	private String loanAccountNo;
	private String firstName;
	private String lastName;
	private String mobileNo;
	private String address;
	private String nationalId;
	private String taxId;
	private String emailId;
	private String income;
	private String applyDate;
	private String loanAmount;
	private String status;
	private String loanType;
	private String approverId;
	private String approveOrRejectDate;
	private String rejectReason;
	private String rateOfInterest;
	private String mandatoryValidationMessage;
	private String optionalValidationMessage;
	private String opType;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getLoanAccountNo() {
		return loanAccountNo;
	}

	public void setLoanAccountNo(String loanAccountNo) {
		this.loanAccountNo = loanAccountNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApproverId() {
		return approverId;
	}

	public void setApproverId(String approverId) {
		this.approverId = approverId;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getApproveOrRejectDate() {
		return approveOrRejectDate;
	}

	public void setApproveOrRejectDate(String approveOrRejectDate) {
		this.approveOrRejectDate = approveOrRejectDate;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(String rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
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

	public String getOpType() {
		return opType;
	}

	public void setOpType(String opType) {
		this.opType = opType;
	}

	public LoanAccountVO() {
	}

	public LoanAccountVO(HashMap<String, String> payload, String opType) throws Exception {
		this.mandatoryValidationMessage = "";
		this.opType = opType;

		String key = (String) payload.get("user_id");
		if (key == null) {
			setUserid("");
		} else {
			setUserid(key);
		}

		key = (String) payload.get("loan_account_no");
		if (key == null) {
			setUserid("");
		} else {
			setLoanAccountNo(key);
		}

		key = (String) payload.get("first_name");
		if (key == null) {
			setFirstName("");
		} else {
			setFirstName(key);
		}

		key = (String) payload.get("last_name");
		if (key == null) {
			setLastName("");
		} else {
			setLastName(key);
		}

		key = (String) payload.get("mobile_no");
		if (key == null) {
			setMobileNo("");
		} else {
			setMobileNo(key);
		}

		key = (String) payload.get("address");
		if (key == null) {
			setAddress("");
		} else {
			setAddress(key);
		}

		key = (String) payload.get("national_id");
		if (key == null) {
			setNationalId("");
		} else {
			setNationalId(key);
		}

		key = (String) payload.get("tax_id");
		if (key == null) {
			if (this.opType == OperationType.createLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Tax id is not provided. ";
			}
			setTaxId("");
		} else {
			setTaxId(key);
		}

		key = (String) payload.get("email_id");
		if (key == null) {
			setEmailId("");
		} else {
			setEmailId(key);
		}

		key = (String) payload.get("income");
		if (key == null) {
			if (this.opType == OperationType.createLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Income is not provided. ";
			}
			setIncome("");
		} else {
			setIncome(key);
		}

		key = (String) payload.get("approver_id");
		if (key == null) {
			if (this.opType == OperationType.updateLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Approver id is not provided. ";
			}
			setApproverId("");
		} else {
			setApproverId(key);
		}

		key = (String) payload.get("status");
		if (key == null) {
			if (this.opType == OperationType.updateLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Status is not provided. ";
			}
			setStatus("");
		} else {
			setStatus(key);
		}

		key = (String) payload.get("loan_amount");
		if (key == null) {
			if (this.opType == OperationType.createLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Loan amount is not provided. ";
			}
			setLoanAmount("");
		} else {
			setLoanAmount(key);
		}

		key = (String) payload.get("apply_date");
		if (key == null || key.isEmpty()) {
			setApplyDate("");
		} else {
			setApplyDate(key.substring(0, 10));
		}

		// key = (String)payload.get("approve_or_reject_date");
		// if( key == null ) {
		// setApproveOrRejectDate("");
		// }else {
		// setApproveOrRejectDate(key);
		// }

		key = (String) payload.get("reject_reason");
		if (key == null) {
			setRejectReason("");
		} else {
			setRejectReason(key);
		}

		key = (String) payload.get("loan_type");
		if (key == null) {
			if (this.opType == OperationType.createLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage + "Loan type is not provided. ";
			}
			setLoanType("");
		} else {
			setLoanType(key);
		}

		key = (String) payload.get("rate_of_interest");
		if (key == null) {
			if (this.opType == OperationType.updateLA) {
				this.mandatoryValidationMessage = this.mandatoryValidationMessage
						+ "Rate of interest is not provided. ";
			}
			setRateOfInterest("");
		} else {
			setRateOfInterest(key);
		}

		// if( this.mandatoryValidationMessage.length() > 0 ) {
		// throw new Exception(this.mandatoryValidationMessage);
		// }

	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("user_id", this.userid);
		jsonObject.put("loan_account_no", this.loanAccountNo);
		jsonObject.put("first_name", this.firstName);
		jsonObject.put("last_name", this.lastName);
		jsonObject.put("mobile_no", this.mobileNo);
		jsonObject.put("address", this.address);
		jsonObject.put("national_id", this.nationalId);
		jsonObject.put("tax_id", this.taxId);
		jsonObject.put("email_id", this.emailId);
		jsonObject.put("income", this.income);
		jsonObject.put("approver_id", this.approverId);
		jsonObject.put("status", this.status);
		jsonObject.put("loan_amount", this.loanAmount);
		jsonObject.put("apply_date", this.applyDate != null ? this.applyDate.substring(0, 10) : "");
		jsonObject.put("approve_or_reject_date",
				this.approveOrRejectDate != null ? this.approveOrRejectDate.substring(0, 10)
						: this.approveOrRejectDate);
		jsonObject.put("reject_reason", this.rejectReason);
		jsonObject.put("loan_type", this.loanType);
		jsonObject.put("rate_of_interest", this.rateOfInterest);
		jsonObject.put("validation_message", this.mandatoryValidationMessage);

		return jsonObject;
	}

	public void setValues(ResultSet rs) throws SQLException {
		this.setUserid(rs.getString("user_id"));
		try {
			this.setFirstName(rs.getString("first_name"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setLoanAccountNo(rs.getString("loan_account_no"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setLastName(rs.getString("last_name"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setMobileNo(rs.getLong("mobile_no") + "");
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setAddress(rs.getString("address"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setNationalId(rs.getString("national_id"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setTaxId(rs.getString("tax_id"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setEmailId(rs.getString("email_id"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setIncome(rs.getDouble("income") + "");
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setApproverId(rs.getString("approver_id") + "");
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setStatus(rs.getString("status"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setLoanAmount(rs.getDouble("loan_amount") + "");
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setApplyDate(rs.getString("apply_date") != null ? rs.getString("apply_date").substring(0, 10) : "");
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setApproveOrRejectDate(rs.getString("approve_or_reject_date"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setRejectReason(rs.getString("reject_reason"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setLoanType(rs.getString("loan_type"));
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}
		try {
			this.setRateOfInterest(rs.getFloat("rate_of_interest") + "");
		} catch (SQLException e) {
			// Continue.. this field may not be retrieved from database
		}

	}

}
