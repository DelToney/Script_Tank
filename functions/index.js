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


exports.sendMessageFCM = functions.https.onCall((data, context) => {

    const msg_content = data.content;
    const rid = data.recv_id;
    const sid = data.sender_id;
    console.log("L:/", "CALLED_SEND_MESSAGE", rid);
    const msg_path = "/Messages/sampleThread/";
    const recv_path = "/Users/" + rid + "/token"
    const db_data = {
        content: msg_content,
        recv_id: rid,
        sender_id: sid,
    };
    const message = {
                        data: {
                            content: msg_content,
                        }
     };

    const get_key = admin.database().ref(recv_path).once('value');
    const write_db_data = admin.database().ref(msg_path).push(db_data);

    return Promise.all([write_db_data, get_key]).then(result_data => {
        const dest_token = result_data[1].val();
        console.log(dest_token, dest_token);
        return  admin.messaging().sendToDevice(dest_token, message)
                                       .then(function (response) {
                                           console.log("Successfully sent a message", response);
                                           return response;
                                       }).catch(function (error) {
                                           console.log("Error sending message", error);
                                       });
        //return ;

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
    console.log(data.key);
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

    // console.log("L:/", "CALLED_CREATE_REQUEST", context.auth.uid);
    // const user_key = data.user_key;
    // const recv_key = data.receiver_key;
    // const inital_status = "STATUS_PENDING";
    // const request_obj = {uid: user_key, rid: recv_key, status: inital_status};
    // return admin.database().ref("/Requests/").push(request_obj).then((push_request) => {

    //     const req_key = push_request.key;
    //     var recv_deliverable = {};
    //     var user_deliverable = {};
    //     recv_deliverable[user_key] = req_key;
    //     user_deliverable[recv_key] = req_key;
    //     const user_update = admin.database().ref("/Users/" + user_key + "/Requests/").update(
    //     user_deliverable);
    //     const recv_update = admin.database().ref("/Users/" + recv_key + "/Requests/").update(
    //     recv_deliverable);
    //     return Promise.all([user_update, recv_update]);
    //     });
    // });

exports.grabUserFriends = functions.https.onCall((data, context) => {

    const user_id = data.user_id;
    var names = [];
    var thread_ids = [];
    var keys = [];
    console.log("L:/", "CALLED_GRAB_USER_FRIENDS", context.auth.uid);
    const fb = admin.database().ref("/Users/" + user_id + "/Messages/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
              var friend_id = ds.key;
              var thread_id = ds.val();
              var friend_name = "";
              var id = "";
              admin.database().ref("/Users/" + friend_id).on('value', function(value) {
                id = value.key;
                friend_name = value.child("name").val();
                keys.push(id)
                names.push(friend_name);
                thread_ids.push(thread_id)

              }, function(errorObject) {
                console.log("Failure: " + errorObject.code);
              });



        });
            return {friends: names,
                                     threads: thread_ids,
                                     keys: keys};
        });




    });

exports.retrieveMessages = functions.https.onCall((data, context) => {

    const thread_id = data.thread_id;
    var messages = [];
    console.log("L:/", "CALLED_RETRIEVE_MESSAGES", context.auth.uid);
    const fb = admin.database().ref("/Messages/" + thread_id);
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
              var content = ds.child("content").val();
              var sender_id = ds.child("sender_id").val();
              messages.push({content: content, id: sender_id});

        });
            return {messages: messages};
        });




    });





