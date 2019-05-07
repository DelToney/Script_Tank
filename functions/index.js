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

exports.searchForWriters = functions.https.onCall((data, context) => {

    const query = data.query.toLowerCase();
    var writers = [];
    var keys = [];
    console.log("L:/", "CALLED_SEARCH_FOR_WRITERS", context.auth.uid);
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                var type = ds.child("type").val();
                var name = ds.child("name").val();
                var search_name = name.toLowerCase();
                if ((type === "Writer") && ((search_name.startsWith(query)) || search_name.includes(query)))  {
                    writers.push(name)
                    keys.push(ds.key);
                }
        });

        return {names: writers,
                db_ids: keys};
        });
    });



exports.sendEditorRequest = functions.https.onCall((data, context) => {

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

exports.loadUserProfileByEmail = functions.https.onCall((data, context) => {

    console.log("L:/", "CALLED_LOAD_USER_PROFILE_EMAIL", context.auth.uid);
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

exports.loadUserProfileByKey = functions.https.onCall((data, context) => {

    console.log("L:/", "CALLED_LOAD_USER_PROFILE_KEY", context.auth.uid);
    const key = data.key;
    const fb = admin.database().ref("/Users/" + key);
    return fb.once('value').then(dataSnapshot => {
        return dataSnapshot.val();
        });
    });

exports.createRequest = functions.https.onCall((data, context) => {

    console.log("L:/", "CALLED_CREATE_REQUEST", context.auth.uid);
    const user_key = data.user_key;
    const recv_key = data.receiver_key;
    const inital_status = "STATUS_PENDING";
    const request_obj = {uid: user_key, rid: recv_key, status: inital_status};
    return admin.database().ref("/Requests/").push(request_obj).then((push_request) => {

        const req_key = push_request.key;
        var recv_deliverable = {};
        var user_deliverable = {};
        recv_deliverable[user_key] = req_key;
        user_deliverable[recv_key] = req_key;
        const user_update = admin.database().ref("/Users/" + user_key + "/Requests/").update(
        user_deliverable);
        const recv_update = admin.database().ref("/Users/" + recv_key + "/Requests/").update(
        recv_deliverable);
        return Promise.all([user_update, recv_update]);
        });
    });



exports.getUserIdeas = functions.https.onCall((data, context) => {
    let ideas = [];
    let ideaID = [];
    const userID = data.userID;
    const dbref = admin.database().ref("/Ideas/" + userID);
    console.log("getting ", context.auth.uid, "'s files");
    

    return dbref.once('value').then(datasnapshot => {
        datasnapshot.forEach(ds => {
            ideas.push(ds.child('IdeaName').val());
            ideaID.push(ds.key);
        });
        return {IdeaIDs: ideaID,
                IdeaNames: ideas};
    });

});

exports.searchForIdeas = functions.https.onCall((data, context) => {

    const query = data.query;
    var ideas = [];
    var writers = [];
    console.log("L:/", "CALLED_SEARCH_FOR_IDEAS", query);
    const fb = admin.database().ref("/Ideas/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
            ds.forEach(ds1 =>{
                var idea = ds1.child("IdeaName").val()
                var writer = ds1.child("WriterName").val();
                if (idea.includes(query))  {
                    ideas.push(idea);
                    writers.push(writer);
                }
        });



        });
                return {Ideas: ideas,
                        Writers: writers};
      });
    });

exports.getPublisherIdeas = functions.https.onCall((data, context) => {
    const query = data.query;
    var ideas = [];
    var writers = [];
    console.log("L:/", "CALLED_GET_PUBLISHER_IDEAS", query);
    const fb = admin.database().ref("/Ideas/");

});