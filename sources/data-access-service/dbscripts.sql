CREATE TABLE <schema_name>.user_details (
    user_id varchar(20) not null,
    first_name varchar(20),
    last_name varchar(20),
    mobile_no bigint,
    address varchar(100),
    national_id varchar(20),
    tax_id varchar(20),
    email_id varchar(40),
    income double,
    PRIMARY KEY (user_id)
);


CREATE TABLE <schema_name>.savings_accounts (
    user_id varchar(20) not null,
    account_no int,
    status varchar(20),
    approver_id varchar(20),
    account_balance double,
    apply_date timestamp,
    approve_or_reject_date timestamp,
    reject_reason varchar(20),
    PRIMARY KEY (user_id)
);


CREATE TABLE <schema_name>.bank_employee (
    user_id varchar(20) not null,
    first_name varchar(20),
    last_name varchar(20),
    mobile_no bigint,
    address varchar(100),
    national_id varchar(20),
    tax_id varchar(20),
    email_id varchar(40),
    department varchar(20),
    PRIMARY KEY (user_id)
);


CREATE TABLE <schema_name>.loan_accounts (
    user_id varchar(20) not null,
    loan_account_no int,
    approver_id varchar(20),
    status varchar(20),
    loan_amount double,
    apply_date timestamp,
    approve_or_reject_date timestamp,
    reject_reason varchar(20),
    loan_type varchar(20),
    rate_of_interest float,
    PRIMARY KEY (user_id)
);
