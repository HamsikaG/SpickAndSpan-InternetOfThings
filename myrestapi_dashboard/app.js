var path = require('path'); // Parse directory paths
var http = require('http'); // Basic HTTP functionality
var bodyParser=require('body-parser');
var jsonparser=bodyParser.json();

var url = require('url');

var conf      = require(path.join(__dirname, 'config'));
var internals = require(path.join(__dirname, 'internals'));

var urlencodedParser=bodyParser.urlencoded({extended:true});

var websockets = require('socket.io'); // Use WebSockets
var express=require('express');

var port = process.env.PORT || 8080;
var app = express();
var server = http.Server(app);
var io = websockets(server);
app.set('socketio', io);


var clients=[];

app.use(urlencodedParser);
app.use(jsonparser);
app.use(express.static('public'));

var viewsDir = path.join(__dirname, 'views');
var publicDir = path.join(__dirname, 'public');

app.use(express.static(publicDir));

app.get('/', function(req, res)
{
  res.sendFile('views/dashboard.html', { root: '.' });
});

app.use(function(err, req, res, next)
{
  console.log(err.stack);
  res.status(err.status || 500);
  res.sendFile('/views/error.html', { root: '.' });
});


app.get('/', function(req, res)
			{
				res.sendFile('views/dashboard.html', { root: '.' });
			});
app.get('/dashboard', function(req, res)
			{
					res.sendFile('views/dashboard.html', { root: '.' });
			});
app.get('/messaging', function(req, res)
 			{
					res.sendFile('views/typography.html', { root: '.' });
			});
app.post('/messaging', function(req, res)
			{
					var iocp = req.app.get('socketio');
					iocp.broadcast.emit("message_client",{doc_id: req.body.doctorid});
			});
app.get('/user', function(req, res)
			{
				res.sendFile('views/user.html', { root: '.' });
			});



require('./test')(app,io);
require('./poststatus')(app,io);

function callback(dataFromClient){
  console.log("Callback from client with data "+dataFromClient);
}


internals.start(mqtt => {

io.on('connection', socket => {


      console.log("Inside io socket function");

      socket_handler(socket, mqtt);

	});

});

io.attach(server);


server.listen(port, function()
{
    var host = server.address().address;
    var port = server.address().port;
    console.log('App listening at http://%s:%s', host, port);
});

function socket_handler(socket, mqtt) {
    // Called when a client connects
    mqtt.on('clientConnected', client => {
        console.log("client connected");
        socket.emit('debug', {
            type: 'CLIENT', msg: 'New client connected: ' + client.id
        });
    });

    // Called when a client disconnects
    mqtt.on('clientDisconnected', client => {
        console.log("client disconnected");
        socket.emit('debug', {
            type: 'CLIENT', msg: 'Client "' + client.id + '" has disconnected'
        });
    });

    // Called when a client publishes data
    mqtt.on('published', (data, client) => {


          if(data.topic == "pressure"){

            console.log("Message published to node: -" + JSON.stringify(data));

            socket.emit('debug', {
              type: 'PUBLISH',
              msg: 'Client "' + client.id + '" published "' + JSON.stringify(data) + '"'
            });
          }


    });

    // Called when a client subscribes
    mqtt.on('subscribed', (topic, client) => {
        if (!client) return;

        socket.emit('debug', {
            type: 'SUBSCRIBE',
            msg: 'Client "' + client.id + '" subscribed to "' + topic + '"'
        });
    });

    // Called when a client unsubscribes
    mqtt.on('unsubscribed', (topic, client) => {
        if (!client) return;

        socket.emit('debug', {
            type: 'SUBSCRIBE',
            msg: 'Client "' + client.id + '" unsubscribed from "' + topic + '"'
        });
    });


    console.log("Connected to server at port 1000 with id "+socket.id+" joined room");
   socket.join("room");  //Keep this to work!


    socket.emit('hey',{});
    socket.on('hey2',function(data){
      console.log("Woodyyyyy from app.js "+data.value);
    });

    socket.on('refresh', function()
  {
              var t=" ";
              IS.Info.getInfo((err, status) => {if(err){throw err;}

              for(var i=0;i<status.length;i++)
              {
               var object = status[i];
               console.log("object details"+object.doctor_id);
               var id=object.doctor_id;

                IS.Status.findOne({doctor_id:object.doctor_id},function(err,doc){
                   var id=doc.toObject().doctor_id;
                    var name;
                      IS.Info.findOne({doctor_id:id},function(err,data){
                        name=data.toObject().doctor_name;
                        t="Doctor "+ name+" with id "+doc.toObject().doctor_id+" entered room "+doc.toObject().room_id+ " at time "+doc.toObject().timestamp+"\n";
                        socket.emit('UpdateOnPage', { value: t});
                       }).sort({ timestamp: -1 });

                  });

    }

     });

  });

                 socket.on('testrun', function(data)
                 {
                   //console.log("got test run :"+ data.id);
                         setTimeout(func2, 3000, data.id);

                 });

                 function func2(arg)
                 {
                         socket.broadcast.emit('message_to',{staff_id: arg});
                 }



                 socket.on('initusers', function()
                 {

                   IS.Info.getInfo((err, info) => {if(err){throw err;}

                       for(var i=0;i<info.length;i++)
                       {

                         socket.emit('user_list',{userlist: info[i].doctor_name});
                       }
                   });

                 });

                 socket.on('hospital_rate', function()
                 {

                   var yays=0, total=0;

                   IS.Status.count({clean: "yes"}, function(err, c) {

                              yays= c;
                              console.log('Yes Count is ' + yays);

                              IS.Status.count({}, function(err, c) {

                                               total= c;
                                               console.log('Total Count is ' + total);
                                               socket.emit("hosresult",{yes: yays,total :total});                                          });


                         });

                 });

                 socket.on('cleancount', function(data)
                 {

                     var name = data.doctor_name;
                     var id =2;
                     IS.Info.getInfo((err, info) => {if(err){throw err;}

                         for(var i=0;i<info.length;i++)
                         {
                           if(name == info[i].doctor_name)
                           {
                             id = info[i].doctor_id;

                             console.log("ID for doc" + name + " is "+id);

                           }
                         }

                         var yays=0, total=0;

                         IS.Status.count({doctor_id: id,clean: "yes"}, function(err, c) {

                                    yays= c;
                                    console.log('Yes Count is ' + yays);

                                    IS.Status.count({doctor_id: id}, function(err, c) {

                                                     total= c;
                                                     console.log('Total Count is ' + total);
                                                     socket.emit("cleanresult",{yes: yays,total :total});                                          });

                               });

                     });

                   });

}

module.exports=app;
