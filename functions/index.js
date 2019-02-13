const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.grabAllUsers = functions.https.onCall((data, context) => {

    var names = [];
    console.log("L:/", "CALLED_USER_FUNCTION", context.auth.uid);
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                names.push(ds.child("name").val())
        });
        return names;
        });
    });
