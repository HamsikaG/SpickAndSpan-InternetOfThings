
var mongoose = require('mongoose');
var express=require('express');
var router=express.Router();


var statusSchema=new mongoose.Schema({
          doctor_id:Number,
          room_id:Number,
          clean:String,
          device_token:String,
          timestamp:Date,
});

var infoSchema=new mongoose.Schema({
          doctor_id:Number,
          doctor_name:String,
          count:Number,
          device_token:String,

});

//var Status=module.exports=mongoose.model('SpickAndSpan',statusSchema);



var IS=module.exports={
 Status:mongoose.model('DoctorActivity',statusSchema),
 Info:mongoose.model('docotorsInformation',infoSchema,'docotorsInformation')
}


IS.Status.getStatus= (callback, limit) => {
 IS.Status.find(callback).limit(limit);
}


IS.Status.addStatus= (status, callback) => {
 IS.Status.create(status, callback);
}

IS.Info.getInfo= (callback, limit) => {
 IS.Info.find(callback).limit(limit);
}


IS.Info.addInfo= (info, callback) => {
 IS.Info.create(info, callback);
}
