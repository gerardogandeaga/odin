const functions = require('firebase-functions');

/*
* Created by: Shreya Jain
* Created on: November 13, 2020
* Description: Callable function that returns current time of Firebase
*/
exports.getTime = functions.https.onCall((data,context)=>{
    return Date.now()
    })