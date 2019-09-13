var express = require('express');
var router = express.Router();
var bodyParser = require('body-parser');
var urlencodeParser = bodyParser.urlencoded({ extended: false });
var path = require('path')
var forEach = require('async-foreach').forEach;

const sqlite3 = require('sqlite3').verbose();
const stripe = require("stripe")("sk_test_lECSh8Nxklh7fkOKZv1p7NoX");
let infors = "";
let payment_type = "";

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('crawl/reserve', { title: 'Crawl Service' });
});

router.post('/reserve', urlencodeParser, function(req, res){

	if(!req.body) return res.sendStatus(400);

	// open the database
	let db = new sqlite3.Database(path.join(__dirname, '..', 'database', 'crawlservice.db'), (err) => {
		if(err) return res.send(err.message);
		// res.send("connect to database sqlite");
	});

	//parse variable
	let urls = req.body.urls.split(',');
	let keys = req.body.keys;

	let requests = urls.map((url, index) => {
	    return new Promise((resolve) => {
	    	asyncFunction(db, url, keys, index, resolve);
	    });
	});

	Promise.all(requests).then(() => {
		db.close((err) => {
			if (err) {
				return console.error(err.message);
			}
			console.log('Close the database connection.');
			// res.send('enterinformation');
			res.send(keys);
		});
	});
});

router.post('/information', urlencodeParser, function(req, res){
	infors = req.body.urls + "#" + req.body.keys;
	
	if(!req.body) return res.sendStatus(400);
	
	res.send('enterinformation');
});

router.get('/enterinformation', function(req, res, next) {
  res.render('crawl/enterinformation', { title: 'Crawl Service', infor: infors.toString() });
});

router.post('/confirm', urlencodeParser, async function(req, res){
	// res.send(req.body.companyname);
	if(!req.body) return res.sendStatus(400);
	// open the database
	let db = new sqlite3.Database(path.join(__dirname, '..', 'database', 'crawlservice.db'), (err) => {
		if(err) return res.send(err.message);
		// res.send("connect to database sqlite");
	});

	//parse variable
	let str_comp = req.body.companyname;
	let str_email = req.body.email;
	let str_username = req.body.username;
	let str_phone = req.body.phone;
	let str_address = req.body.address;
	let str_urls = req.body.urls;
	let str_keys = req.body.keys;
	payment_type = req.body.pay;

	let result = await asyncInsertDBCompany(db, str_urls, str_keys, str_comp, str_email, str_username, str_phone, str_address);
	res.send('reservecomfirm');
});

router.get('/reservecomfirm', urlencodeParser, function(req, res){
	res.render('crawl/reservecomfirm', { title: 'Crawl Service', pay: payment_type });
}); 

router.post('/backtop', urlencodeParser, function(req, res){
	res.send('reserve');
});

router.get('/reserve', function(req, res, next) {
  res.render('crawl/reserve', { title: 'Crawl Service' });
});

router.post('/finish', urlencodeParser, function(req, res){
	let key = "pk_test_Ztdn7ROKq9iHWxQH8UxxoArH";
	let secret_key = "sk_test_lECSh8Nxklh7fkOKZv1p7NoX";

	let amount = req.body.amount*100;
 	// return res.send(req.body.amount);
	// create a customer
	stripe.customers.create({
		email: req.body.email,
		source: req.body.stripeToken
	})
	.then(customer =>
		stripe.charges.create({ 
		amount,
		description: "Sample Charge",
		currency: "usd",
		customer: customer.id
	}))
	.then(charge => {
		res.send("reservefinished");
	});
});

router.post('/finished', urlencodeParser, function(req, res){
	res.send("reservefinished");
});

router.get('/reservefinished', function(req, res, next) {
	res.render('crawl/reservefinished', { title: 'Crawl Service' });
	// run bat file
	"use strict";
	// The path to the .bat file
	var myBatFilePath = path.join(__dirname, '..', 'bat', 'main.tpr.bat');

	const spawn = require('child_process').spawn;
	const bat = spawn('cmd.exe', ['/c', myBatFilePath]);

	// Handle normal output
	bat.stdout.on('data', (data) => {
		// As said before, convert the Uint8Array to a readable string.
		var str = String.fromCharCode.apply(null, data);
		console.info(str);
	});

	// Handle error output
	bat.stderr.on('data', (data) => {
		// As said before, convert the Uint8Array to a readable string.
		var str = String.fromCharCode.apply(null, data);
		console.error(str);
	});

	// Handle on exit event
	bat.on('exit', (code) => {
		var preText = `Child exited with code ${code} : `;

		switch(code){
		    case 0:
		        console.info(preText+"Something unknown happened executing the batch.");
		        break;
		    case 1:
		        console.info(preText+"The file already exists");
		        break;
		    case 2:
		        console.info(preText+"The file doesn't exists and now is created");
		        break;
		    case 3:
		        console.info(preText+"An error ocurred while creating the file");
		        break;
		}
	});
});

function asyncFunction (db, url, keyword, index, cb) {
	setTimeout(() => {

		//not need use id
		let data_row = [url, keyword];
		db.run("INSERT INTO urlandkeyword VALUES (?,?)", data_row, function(err) {
			if (err) {
				return console.error(err.message);
			}
			console.log(`Rows inserted ${this.changes}`);
		});

	    cb();
	}, 100);
}

function asyncInsertDBCompany(db, urls, keywords, name, email, username, phone, address){
	setTimeout(() => {

		//not need use id
		let data_row = [urls, keywords, name, email, username, phone, address];

		// output the INSERT statement
		db.run("INSERT INTO company VALUES (?,?,?,?,?,?,?)", data_row, function(err) {
			if (err) {
				return console.error(err.message);
			}
			console.log(`Rows inserted ${this.changes}`);
		});

	}, 100);
}

function asyncStripePayment(){
	let key = "pk_test_Ztdn7ROKq9iHWxQH8UxxoArH";
	let secret_key = "sk_test_lECSh8Nxklh7fkOKZv1p7NoX";

	let amount = 10*100;
 
	// create a customer
	stripe.customers.create({
		email: req.body.stripeEmail, // customer email
		source: req.body.stripeToken // token for the card
	})
	.then(customer =>
		stripe.charges.create({ // charge the customer
		amount,
		description: "Sample Charge",
		currency: "usd",
		customer: customer.id
	}))
	.then(charge => res.render("pay")); // render the payment successful alter page after payment
}

module.exports = router;
