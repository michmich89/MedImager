// Constants
const hostname = '127.0.0.1';
const port = 3000;

// Require (import) libaries.
var express = require('express');
var router = require('./routers/router');
var path = require('path');
var passport = require('passport');
var https = require('https');
var bodyParser = require( 'body-parser' );
var cookieParser = require( 'cookie-parser' );
var session = require( 'express-session' );
var mongoose = require('mongoose');


// Init Express
var app = express();


app.use('/*', function(req, res, next) {

    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	res.header("Access-Control-Allow-Methods", "*");
    next();

});



app.use('/', router);

// ----------------------------------------------------
// Server functionality

app.listen(3000, function () {
	console.log('App listening on port 3000!');
});