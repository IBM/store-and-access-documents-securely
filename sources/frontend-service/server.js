
var express = require('express');
const session = require('express-session');
const path = require('path');
const axios = require('axios');
const FormData = require('form-data');
var multer = require('multer');
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
// const { timeEnd } = require('console');
// const { nextTick } = require('process');
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


//Pending list of Savings Account
app.get('/pendingSavingsAccount', (req, res) => {
	console.log("/pendingSavingsAccount");
	const URL = process.env.APPROVAL_SERVICE_URL + '/approval-flow/account/savings/pendinglist';
	
	if(req.session.token) {	
		var token = 'Bearer ' + req.session.token["access_token"];
		var config = {
			method: 'get',
			url: URL,
			headers: { 
			  'Authorization': token
			}
		  };
		axios(config)
		.then( function (response) {
			console.log(response.data);
			res.send(response.data);
		})
		.catch (function (error) {
			console.log(error);
		});
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/login')
	}
	
});

//Approve or Reject Savings Account
app.get('/changeSavingsAccountStatus', (req,res) => {
	console.log("/changeSavingsAccountStatus");
	const service = process.env.APPROVAL_SERVICE_URL + '/approval-flow/account/savings/status';
	let params = '?user_id=' + req.query.user_id + '&last_name=' + req.query.last_name + '&first_name=' + req.query.first_name + '&mobile_no=' + req.query.mobile_no + '&email_id=' + req.query.email_id + '&status='+ req.query.status;
	let approval_url = service + params;
	
	if(req.session.token) {	
		var token = 'Bearer ' + req.session.token["access_token"];
		var config = {
			method: 'get',
			url: approval_url,
			headers: { 
			  'Authorization': token
			}
		  };
		axios(config)
		.then( function (response) {
			console.log("Request is completed successfully");
			res.json({"Message":"Request is completed successfully"});
		})
		.catch (function (error) {
			console.log(error);
			res.json({"Error message" :"Request to change Savings Account status - Failed! Internal Server Error."});
		});
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/login')
	}
});

//Pending list of loan accounts
app.get('/pendingLoanAccounts', (req, res) => {
	console.log("/pendingLoanAccounts");
	const URL = process.env.APPROVAL_SERVICE_URL + '/approval-flow/account/loan/pendinglist';
	if(req.session.token) {	
		var token = 'Bearer ' + req.session.token["access_token"];
		var config = {
			method: 'get',
			url: URL,
			headers: { 
			  'Authorization': token
			}
		  };
		axios(config)
		.then( function (response) {
			// console.log(response.data);
			res.send(response.data);
		})
		.catch (function (error) {
			console.log(error);
		});
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/login')
	}
});

//Approve or Reject Loan Account
app.get('/changeLoanAccountStatus', (req,res) => {
	console.log("/changeLoanAccountStatus");
	const service = process.env.APPROVAL_SERVICE_URL + "/approval-flow/account/loan/status";
	let params = '?user_id=' + req.query.user_id + '&status=' + req.query.status + '&approver_id=loan_official';
	let approval_url = service + params;
	
	if(req.session.token) {	
		var token = 'Bearer ' + req.session.token["access_token"];
		var config = {
			method: 'get',
			url: approval_url,
			headers: { 
			  'Authorization': token
			}
		  };
		axios(config)
		.then( function (response) {
			console.log("Request is completed successfully");
			res.json({"Message":"Request is completed successfully"});
		})
		.catch (function (error) {
			console.log(error);
			res.json({"Error message" :"Request to change Loan Account status - Failed! Internal Server Error."});
		});
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/login')
	}
});

//Get User Dashboard Details
app.get('/getUserDashboard', (req, res) => {
	console.log("/getUserDashboard");
	if(req.session.token) {
		//  var token = 'Bearer ' + "teststring";
		var token = 'Bearer ' + req.session.token["access_token"];
		// .toString('base64');
			
		authClient.userInfo(req.session.token)
		.then((response) => {
				let userid = response.response.preferred_username;
				// console.log(userid);
				let url = process.env.SAVINGS_ACCOUNT_SERVICE_URL + '/locker/savings?userid=' + userid;
				var config = {
					method: 'get',
					url: url,
					headers: { 
					  'Authorization': token
					}
				  };
				
				axios(config)
					.then( function (response) {
						console.log(response.data);
						res.send(response.data);
					})
					.catch (function (error) {
						console.log(error);
						res.redirect('/');
					});
		}).catch((err) => {
			res.json(err);
		});
	} else {
		console.log('======== Current session had no token available.');
		res.redirect('/');
	}
});

//Get Loan Account Details
app.get('/getLoanAccountDetails', (req, res) => {
	console.log("/getLoanAccountDetails");
	if(req.session.token) {
		var token = 'Bearer ' + req.session.token["access_token"];
		
		authClient.userInfo(req.session.token)
		.then((response) => {
				let userid = response.response.preferred_username;
				
				let url = process.env.LOAN_ACCOUNT_SERVICE_URL + '/locker/loan?userid=' + userid;
				var config = {
					method: 'get',
					url: url,
					headers: { 
					  'Authorization': token
					}
				  };
				axios(config)
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
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/login')
	}
});

// app.get('/getDocument', (req,res) => {
// 	console.log("/getDocument");
// 	let svcUrl="http://doc-access-service-xxxx/getFile/";
//     let url = svcUrl + req.query.file;
// 	res.download(url);
// 	// axios.get(url)
// 	// .then( function (response) {
// 	// 	console.log(response.data);
// 	// 	res.send(response.data);
// 	// })
// 	// .catch (function (error) {
// 	// 	console.log(error);
// 	// });
// });

// serve the react app files
app.use(express.static(`${__dirname}/ui-react/build`));

app.get('*', (req,res) =>{
	// console.log(path.join(__dirname+'/ui-react/build/index.html'));
    res.sendFile(path.join(__dirname+'/ui-react/build/index.html'));
});  

// Multer configuration
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
	console.log(req.body);
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
	
	const URL = process.env.SAVINGS_ACCOUNT_SERVICE_URL+'/locker/savings';
	
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
	
	if (req.session.token) {
		var token = 'Bearer ' + req.session.token["access_token"];
	
		const URL = process.env.LOAN_ACCOUNT_SERVICE_URL+'/locker/loan';
		var config = {
			method: 'post',
			url: URL,
			headers: {  
				'Authorization': token,
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
	} else {
		console.log('======== Current session had no token available.')
		res.redirect('/login')
	}
});


// // Serve static resources
// app.use(express.static('./public'));
// app.use(bodyParser.urlencoded({ extended: false }))
// app.use(bodyParser.json())

// Start server
app.listen(port, () => {
	console.log('Listening on http://localhost:' + port);
});