var moment=require('moment');
var now=moment();

function callThis (dataFromClient){

console.log("Call back fired: " + dataFromClient);
}

module.exports = function(app,io){

	app.post('/status', function(req, res){
		var doc_name;
		var status =req.body;
		var clients_in_the_room = io.sockets.adapter.rooms["room"].sockets;

		console.log("post on status with "+status);



		var doc_id=req.body.doctor_id;
		var room_id =req.body.room_id;
		var clean  =req.body.clean;
		var device_token=req.body.device_token;
		var timestamp = req.body.timestamp;

		console.log("TIMESTAMP RECEIVED:: "+timestamp);

		IS.Status.addStatus(status, (err,status) => {if(err){ throw err;}});
		IS.Info.getInfo((err,info) => {
			if(err){
				throw err;
			}
			//console.log("In demo with id "+id);
			var doc_name;
			for(var i=0;i<info.length;i++)
			{
				var object = info[i];
				if(object.doctor_id==doc_id)
				{
					console.log("Doctor "+object.doctor_name+" has entered room "+room_id);
					var tosend="Doctor "+object.doctor_name+" has entered room "+room_id;
					doc_name=object.doctor_name;
				}
			}

			var tsmoment=moment.utc(now.valueOf());
			var time=tsmoment.local().format('h:mm a');
     console.log("Time "+time);
			for (var clientId in clients_in_the_room ) {
				//console.log("Roomid  inside"+room_id);

	 		 console.log('client in room from post: %s', clientId); //Seeing is believing
	 		 var client_socket = io.sockets.connected[clientId];
	 		    client_socket.emit('eventafterpost',{value:tosend,
					                                      timestamp:time},callThis);

	 		}


		});


		// IS.Status.findOneAndUpdate({doctor_id:9},{$set:{clean:"yes"}},{sort: {'doctor_id':1}},function(err,doc){
		//
		// 			if(err)
		// 				 console.log(doc);
		// });

		res.send("SENT to demo");

	});
};


function dbupdateYES(id)
{

	IS.Status.findOneAndUpdate({doctor_id:id},{$set:{clean:"yes"}},{sort: {'doctor_id':-1}},function(err,doc){

	 			if(err){ throw err;}
	 				 //console.log(doc);
	 });

}

function dbupdateNO(id)
{
	IS.Status.findOneAndUpdate({doctor_id:id},{$set:{clean:"no"}},{sort: {'timestamp':-1}},function(err,doc){

				if(err){ throw err;}
					 //console.log(doc);
	 });

	 IS.Info.findOneAndUpdate({doctor_id:id},{$inc:{count:1}},{sort: {'timestamp':-1}},function(err,doc){

 				if(err){ throw err;}
 					 //console.log(doc);
 	 });


}
