
//var app=require('./app');

module.exports = function(app, io){
  app.get('/test', function(req, res){
    console.log("Get on test");

    io.sockets.emit('event',{
         value: "testdata"
    });
    res.send("Testing..")
  });
};
