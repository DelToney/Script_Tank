const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.grabAllUsers = functions.https.onCall((data, context) => {

    var names = [];
    console.log("L:/", "CALLED_USERS_FUNCTION", context.auth.uid);
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                names.push(ds.child("name").val())
        });
        return {name: names};
        });
    });

exports.grabUsersFiles = functions.https.onCall((data, context) => {

    var users = [];
    var file_names = [];
    var db_ids = [];
    console.log("L:/", "CALLED_USERS_FILES_FUNCTION", context.auth.uid);
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                 users.push(ds.child("name").val())
                 file_names.push(ds.child("Files").val())
                 db_ids.push(ds.key)
        });
        return {usernames: users,
                    files: file_names,
                    ids: db_ids};
        });
    });

exports.grabAllWriters = functions.https.onCall((data, context) => {

    var writers = [];
    var keys = [];
    console.log("L:/", "CALLED_GRAB_ALL_WRITERS", context.auth.uid);
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                var type = ds.child("type").val();
                if (type === "Writer") {
                    writers.push(ds.child("name").val())
                    keys.push(ds.key);
                }
        });

        return {names: writers,
                db_ids: keys};
        });
    });

exports.sendWriterRequest = functions.https.onCall((data, context) => {

    const dest_key = data.dest_key;
    console.log("L:/", "CALLED_SEND_REQUEST_TO_WRITER", dest_key);
    const path = "/Users/" + dest_key + "/token"
    const receiver = admin.database().ref(path).once('value');
    return Promise.all([receiver]).then(result_data => {
        const dest_id = result_data[0].val();
        console.log("L:/", "THE DEST_ID IS", dest_id);
        const payload = {
                notification: {
                    title: "New Editor Request",
                    body: data.body,
                }
        };

        return admin.messaging().sendToDevice(dest_id, payload)
        .then(function (response) {
            console.log("Successfully sent a message", response);
            return response;
        }).catch(function (error) {
            console.log("Error sending message", error);
        });
    });

});

/*exports.changePushId = functions.database.ref('/Users/{pushId}/').onCreate((snapshot, context) => {
    console.log("L:/", "CHANGE_PUSH_ID_CALLED");
    const uid = context.auth.uid;
    const curr_val = snapshot.ref.key;
    console.log("L:/ ", curr_val);
    return snapshot.ref.key.set(uid);

}); */

exports.loadUserProfile = functions.https.onCall((data, context) => {

    console.log("L:/", "CALLED_LOAD_USER_PROFILE", context.auth.uid);
    const submit_email = data.email;
    var profile = [];
    var r_key = "";
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                if (ds.child("email").val() === submit_email) {
                     profile.push(ds.val())
                     r_key = ds.key;
                }


        });
        return {data: profile,
            key: r_key};
        });
    });



