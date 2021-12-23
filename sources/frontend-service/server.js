
var express = require('express');
const session = require('express-session');
const path = require('path');
const axios = require('axios');
const FormData = require('form-data');
var multer = require('multer');
// var upload = multer({ dest: 'uploads/'});
// const request = require('request');
const fs = require('fs');


require('dotenv').config();
const config = {
	tenantUrl            : process.env.TENANT_URL,
	clientId             : process.env.CLIENT_ID,
	clientSecret         : process.env.CLIENT_SECRET,
	redirectUri          : process.env.REDIRECT_URI,
	responseType         : process.env.RESPONSE_TYPE,
	flowType             : process.env.FLOW_TYPE,
	scope                : process.env.SCOPE
};

// var bodyParser = require('body-parser');
var {OAuthContext} = require('ibm-verify-sdk');
const { timeEnd } = require('console');
const { nextTick } = require('process');
var authClient = new OAuthContext(config);

const port = process.env.PORT || 3001;

const app = express();
app.use(session({
    secret: 'my-secret',
    resave: true,
    saveUninitialized: false
  }));


//TEST Func for UI - non-secured
app.get("/testAPI", (req, res) => {
    console.log("/testAPI");
    console.log({ message: "Hello from testAPI!" });
    res.json({ message: "Hello from testAPI!" });
});

app.get('/login', (req, res) => {
    console.log("/login");
    res.set('Access-Control-Allow-Origin', '*');
	authClient.authenticate().then((url) => {
		console.log(`("======== Authentication redirect to: \n ${url}`);
		res.redirect(url);
	}).catch(error => {
		console.log(`There was an error with the authentication process:`, error);
		res.send(error);
	})
})

app.get('/redirect', (req, res) => {
    console.log("/redirect");
    res.set('Access-Control-Allow-Origin', '*');
	authClient.getToken(req.url).then(token => {
		token.expiry = new Date().getTime() + (token.expires_in * 1000);
		console.log("======== Token details:");
		console.log(token);
		req.session.token = token;
		authClient.userInfo(req.session.token)
			.then((response) => {
				let username = response.response.preferred_username;
				if ( username === 'loan_official') {
					res.redirect('/loan-branch-official-dashboard');
				} else if ( username === 'savings_official' ) {
					res.redirect('/savings-official-dashboard');
				} else {
					res.redirect('/user');
				}
			}).catch((err) => {
				res.json(err);
			});
		// res.redirect('/savings-official-dashboard');
	}).catch(error => {
			res.send("ERROR: " + error);
	});
});


app.get("/api", (req, res) => {
    console.log("/api");
	console.log(path.join(__dirname+'/ui-react/build/index.html'));
    if(req.session.token){
		console.log('======== Requesting userInfo claims using valid token');
		authClient.userInfo(req.session.token)
			.then((response) => {
                res.set('Access-Control-Allow-Origin', '*');
                res.json({ message: "Hello from redirected /api server!" });
				//res.render('dashboard', {userInfo :response.response});
			}).catch((err) => {
				res.json(err);
			});
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/')
	}
});

//Pending list of Savings Account
app.get('/pendingSavingsAccount', (req, res) => {
	console.log("/pendingSavingsAccount");
	const URL = process.env.APPROVAL_SERVICE_URL + '/approval-flow/account/savings/pendinglist';
	axios.get(URL)
	.then( function (response) {
		console.log(response.data);
		res.send(response.data);
	})
	.catch (function (error) {
		console.log(error);
	});
	// res.json({"message": "hello"});

	// await fetch("http://approval-service-approval-proj.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/approval-flow/account/savings/pendinglist", { method: "get" })
});

//Approve or Reject Savings Account
app.get('/changeSavingsAccountStatus', (req,res) => {
	console.log("/changeSavingsAccountStatus");
	const service = process.env.APPROVAL_SERVICE_URL + '/approval-flow/account/savings/status';
	//const service = "uth.containers.appdomain.cloud/approval-flow/account/savings/status";
	let params = '?user_id=' + req.query.user_id + '&last_name=' + req.query.last_name + '&first_name=' + req.query.first_name + '&mobile_no=' + req.query.mobile_no + '&email_id=' + req.query.email_id + '&status='+ req.query.status;
	let approval_url = service + params;
	// console.log(req.query.status);
	axios.get(approval_url)
	.then( function (response) {
		console.log("Request is completed successfully");
		res.json({"Message":"Request is completed successfully"});
	})
	.catch (function (error) {
		console.log(error);
		res.json({"Error message" :"Request to change Savings Account status - Failed! Internal Server Error."});
	});
});

//Pending list of loan accounts
// http://approval-service-approval-proj.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/approval-flow/account/loan/pendinglist
app.get('/pendingLoanAccounts', (req, res) => {
	console.log("/pendingLoanAccounts");
	const URL = process.env.APPROVAL_SERVICE_URL + '/approval-flow/account/loan/pendinglist';
	axios.get(URL)
	.then( function (response) {
		// console.log(response.data);
		res.send(response.data);
	})
	.catch (function (error) {
		console.log(error);
	});
});

//Approve or Reject Loan Account
app.get('/changeLoanAccountStatus', (req,res) => {
	console.log("/changeLoanAccountStatus");
	const service = process.env.APPROVAL_SERVICE_URL + "/approval-flow/account/loan/status";
	let params = '?user_id=' + req.query.user_id + '&status=' + req.query.status + '&approver_id=loan_official';
	let approval_url = service + params;
	// console.log(req.query.status);
	axios.get(approval_url)
	.then( function (response) {
		console.log("Request is completed successfully");
		res.json({"Message":"Request is completed successfully"});
	})
	.catch (function (error) {
		console.log(error);
		res.json({"Error message" :"Request to change Loan Account status - Failed! Internal Server Error."});
	});
});

//Get User Dashboard Details
app.get('/getUserDashboard', (req, res) => {
	console.log("/getUserDashboard");
	authClient.userInfo(req.session.token)
		.then((response) => {
				let userid = response.response.preferred_username;
				// console.log(userid);
				let url = process.env.SAVINGS_ACCOUNT_SERVICE_URL + '/locker/savings?userid=' + userid;
					axios.get(url)
					.then( function (response) {
						console.log(response.data);
						res.send(response.data);
					})
					.catch (function (error) {
						console.log(error);
					});
		}).catch((err) => {
			res.json(err);
		});
	// let savingsAccUrl="http://savings-acc-svc-savings-project.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/locker/savings";
    // let url = process.env.SAVINGS_ACCOUNT_SERVICE_URL + '?userid=T1U1';
	// axios.get(url)
	// .then( function (response) {
	// 	console.log(response.data);
	// 	res.send(response.data);
	// })
	// .catch (function (error) {
	// 	console.log(error);
	// });
});

//Get Loan Account Details
app.get('/getLoanAccountDetails', (req, res) => {
	console.log("/getLoanAccountDetails");
	authClient.userInfo(req.session.token)
		.then((response) => {
				let userid = response.response.preferred_username;
				console.log(userid);
				let url = process.env.LOAN_ACCOUNT_SERVICE_URL + '/locker/loan?userid=' + userid;
				axios.get(url)
					.then( function (response) {
						console.log(response.data);
						res.send(response.data);
					})
					.catch (function (error) {
						console.log(error);
					});
		}).catch((err) => {
			res.json(err);
		});
	// let loanAccUrl="http://loan-acc-svc-loan-project.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/locker/loan";
    // let url = process.env.LOAN_ACCOUNT_SERVICE_URL + '?userid=T1U1';
	// axios.get(url)
	// .then( function (response) {
	// 	console.log(response.data);
	// 	res.send(response.data);
	// })
	// .catch (function (error) {
	// 	console.log(error);
	// });
});

app.get('/getDocument', (req,res) => {
	console.log("/getDocument");
	// http://doc-access-service-document-access.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/getFile/sm-nationalid.png
	let svcUrl="http://doc-access-service-document-access.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/getFile/";
    let url = svcUrl + req.query.file;
	res.download(url);
	// axios.get(url)
	// .then( function (response) {
	// 	console.log(response.data);
	// 	res.send(response.data);
	// })
	// .catch (function (error) {
	// 	console.log(error);
	// });
});

// serve the react app files
app.use(express.static(`${__dirname}/ui-react/build`));

app.get('*', (req,res) =>{
	// console.log(path.join(__dirname+'/ui-react/build/index.html'));
    res.sendFile(path.join(__dirname+'/ui-react/build/index.html'));
});  

// Serve static resources
// app.use(express.static('./public'));
// app.use(bodyParser.urlencoded({ extended: true }));
// app.use(bodyParser.json());
// app.use(upload.array("files")); 
const multerStorage = multer.diskStorage({
	destination: function(req, file, cb) {
		cb(null, 'uploads/');
	},

	// By default, multer removes file extensions so let's add them back
	filename: function(req, file, cb) {
		cb(null, file.fieldname + path.extname(file.originalname));
	}
});
var upload = multer({ storage: multerStorage});
app.use(upload.any());

// Create Savings Account 
app.post("/createSavingsAccount", async (req, res) => {
    console.log("/createSavingsAccount-POST API");
	// console.log(req.headers);
	// console.log(req.headers['content-type']);
	console.log(req.body);
	// console.log(req.body['userid']);
	console.log(req.files);

	var data = new FormData();
	data.append('firstname', req.body['firstname']);
    data.append('lastname', req.body['lastname']);
    data.append('userid', req.body['userid']);
    data.append('mobilenumber', req.body['mobilenumber']);
    data.append('address', req.body['address']);
	data.append('emailid', req.body['emailid']);
	data.append('taxidfile', fs.createReadStream('./uploads/taxidfile.png'));
	data.append('nationalidfile', fs.createReadStream('./uploads/nationalidfile.png'));
	// data.append('nationalidfile', request('http://nodejs.org/images/logo.png'));
	
	// // req.headers['content-type']
    // // call java service
	// // const headers = {
	// // 	'Content-Type': 'multipart/form-data'
	// //   }
	// // const response = await axios.post(
	// // 	"http://savings-acc-svc-savings-project.cp4i-errortest-dal10-c3-f2c6cdc6801be85fd188b09d006f13e3-0000.us-south.containers.appdomain.cloud/locker/savings", 
	// // 	data, {headers: headers})
	const URL = process.env.SAVINGS_ACCOUNT_SERVICE_URL+'/locker/savings';
	// console.log(URL);
	var config = {
		method: 'post',
		url: URL,
		headers: {  
			...data.getHeaders()
		},
		data : data
	  };
	  
	  axios(config)
	  .then(function (response) {
		console.log(JSON.stringify(response.data));
		res.send("Request created successfully");
	  })
	  .catch(function (error) {
		console.log(error);
		res.send("Error in creating request");
	  });
});

// Create Loan Account 
app.post("/createLoanAccount", async (req, res) => {
    console.log("/createLoanAccount-POST API");
	console.log(req.body);
	console.log(req.files);

	var data = new FormData();
    data.append('userid', req.body['userid']);
    data.append('income', req.body['income']);
    data.append('loan_type', req.body['loan_type']);
	data.append('loan_amount', req.body['loan_amount']);
	data.append('incomeprooffile', fs.createReadStream('./uploads/incomeprooffile.png'));
	
	const URL = process.env.LOAN_ACCOUNT_SERVICE_URL+'/locker/loan';
	var config = {
		method: 'post',
		url: URL,
		headers: {  
			...data.getHeaders()
		},
		data : data
	  };
	  
	  axios(config)
	  .then(function (response) {
		console.log(JSON.stringify(response.data));
		res.json({"Message" : "Loan request has been submitted successfully"});
	  })
	  .catch(function (error) {
		console.log(error);
		res.json({"Message" : "Error in creating Loan request. Internal Server Error."});
	  });
});


// // Serve static resources
// app.use(express.static('./public'));
// app.use(bodyParser.urlencoded({ extended: false }))
// app.use(bodyParser.json())

// Start server
app.listen(port, () => {
	console.log('Listening on http://localhost:' + port);
});