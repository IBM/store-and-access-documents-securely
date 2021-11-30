
var express = require('express');
const session = require('express-session');
const path = require('path');

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

var bodyParser = require('body-parser');
var {OAuthContext} = require('ibm-verify-sdk');
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

// serve the react app files
app.use(express.static(`${__dirname}/ui-react/build`));

app.get('*', (req,res) =>{
	// console.log(path.join(__dirname+'/ui-react/build/index.html'));
    res.sendFile(path.join(__dirname+'/ui-react/build/index.html'));
});  

// Create Savings Account 
app.post("/createSavingsAccount", (req, res) => {
    console.log("/api-post");
    console.log(req);
    // call java service
    console.log({ message: "Hello from POST API!" });
});

// Serve static resources
app.use(express.static('./public'));
app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())

// Start server
app.listen(port, () => {
	console.log('Listening on http://localhost:' + port);
});