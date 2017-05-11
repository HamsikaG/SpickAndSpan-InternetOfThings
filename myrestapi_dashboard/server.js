var express=require('express');
var mongoose=require('mongoose');
var bodyParser=require('body-parser');
var jsonparser=bodyParser.json();
var urlencodedParser=bodyParser.urlencoded({extended:true});

mongoose.connect('mongodb://dbuser:undercover22@ds019876.mlab.com:19876/nodeandroiddb')
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
	console.log('Database connection established!');
});
var app=require('./app');
IS=require('./status');

function Demo(id)
{
	IS.Info.getInfo((err,info) => {
		if(err){
			throw err;
		}
		console.log("In demo with id "+id);
		var doc_name;

		for(var i=0;i<info.length;i++)
		{
			var object = info[i];
			if(object.doctor_id==id)
			{
				console.log("Found doc with id "+id+" with name "+object.doctor_name);
				doc_name=object.doctor_name;
			}
		}
		console.log("Doc name"+doc_name);

	});

}

			app.get('/status', (req, res) => {
				console.log("GET HIT ON STATUS with body ");
				IS.Info.getInfo((err, status) => {
					if(err){
						throw err;
					}
					console.log("GET HIT ON STATUS with body "+status);


			for(var i=0;i<status.length;i++)
			 {
				 var object = status[i];
				 res.write("Doctor "+object.doctor_name+" with id "+object.doctor_id+" his count "+object.count+"\n");

			 }
			 res.end();
				 });

			});
