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

exports.grabAllPublishers = functions.https.onCall((data, context) => {

    var writers = [];
    var keys = [];
    console.log("L:/", "CALLED_GRAB_ALL_PUBS", context.auth.uid);
    const fb = admin.database().ref("/Users/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
                var type = ds.child("type").val();
                if (type === "Publisher") {
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
    const thread_id = data.thread_id;
    console.log("L:/", "CALLED_SEND_MESSAGE", rid);
    const msg_path = "/Messages/" + thread_id;
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
    const view_key = data.viewer_key;
    const fb = admin.database().ref("/Users/" + key);
    return fb.once('value').then(dataSnapshot => {

        var status = "NO_REQUEST";
        var req_snap = dataSnapshot.child("RequestsReceived");
        if (req_snap.hasChild(view_key)) {
            status = "CHECK_REQUEST";
            var ref = req_snap.child(view_key).val();
            const req_db = admin.database().ref("/Requests/" + ref + "/status/");
            req_db.on('value', function(statShot) {
                status = statShot.val();
            });
        } else {
            var msg_snap = dataSnapshot.child("Messages");
             if (msg_snap.hasChild(view_key)) {
                    status = "STATUS_ACCEPTED";

            }
        }

        return {profile: dataSnapshot.val(), status: status};


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
        const user_update = admin.database().ref("/Users/" + user_key + "/RequestsSent/").update(
        user_deliverable);
        const recv_update = admin.database().ref("/Users/" + recv_key + "/RequestsReceived/").update(
        recv_deliverable);
        return Promise.all([recv_update]);
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
    var keys = [];
    console.log("L:/", "CALLED_SEARCH_FOR_IDEAS", query);
    const fb = admin.database().ref("/Ideas/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
            ds.forEach(ds1 =>{
                console.log(query);
                var idea = ds1.child("IdeaName").val()
                var writer = ds1.child("WriterName").val();
                var mkey = ds1.key;
                console.log(idea);
                if (idea.includes(query))  {
                    ideas.push(idea);
                    writers.push(writer);
                    keys.push(mkey);
                }
        });



        });
                return {Ideas: ideas,
                        Writers: writers,
                        Keys: keys};
      });
    });

   /* console.log("L:/", "CALLED_CREATE_REQUEST", context.auth.uid);
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
    });*/

exports.grabUserFriends = functions.https.onCall((data, context) => {

    const user_id = data.user_id;
    var names = [];
    var thread_ids = [];
    var keys = [];
    var friend_ids = [];
    console.log("L:/", "CALLED_GRAB_USER_FRIENDS", context.auth.uid);
    const fb = admin.database().ref("/Users/" + user_id + "/Messages/");
    var sender_data = fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
              var friend_id = ds.key;
              var thread_id = ds.val();
              var friend_name = "";
              var id = "";
              friend_ids.push(friend_id);
              thread_ids.push(thread_id);


        });
            return {friend_ids: friend_ids,
                                     threads: thread_ids};
        });
    var receiver_data =  sender_data.then(function(result) {
        result["friend_ids"].forEach(friend => {
              admin.database().ref("/Users/" + friend).on('value', function(value) {
                              id = value.key;
                               friend_name = value.child("name").val();
                               keys.push(id)
                               names.push(friend_name);


              }, function(errorObject) {
                 console.log("Failure: " + errorObject.code);
              });
          });
                return {names: names,
                keys: keys};
          });
    return Promise.all([sender_data, receiver_data]).then(function([result_send, result_recv]) {

            return {friend_names: result_recv["names"],
                    keys: result_recv["keys"],
                    friend_ids: result_send["friend_ids"],
                    threads: result_send["threads"]};
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
//handle a request
exports.handleRequest = functions.https.onCall((data, context) => {

    const response = data.result;
    const req_id = data.request_id;
    const requester_id = data.requester_id;
    const user_id = data.user_id;
    console.log("L:/", "CALLED_HANDLE_REQUEST", context.auth.uid);
    if (response === "ACCEPT_REQ") {
        //add user to each others profile
        var requester_ref = admin.database().ref("/Users/" + requester_id + "/Permissions/");
        //writer to editor
        var user_ref = admin.database().ref("/Users/" + user_id + "/Permissions/");
        //using true for placeholder value. just need the ids in the folder to know that user has permission
        requester_ref.child(user_id).set(true);
        user_ref.child(requester_id).set(true);

        const message_id = requester_id.substring(0, 5) + user_id.substring(0, 5);

        //create messaging services

        var message_ref_user = admin.database().ref("Users/" + user_id +"/Messages/");
        var message_ref_requester = admin.database().ref("/Users/" + requester_id + "/Messages/");
        message_ref_user.child(requester_id).set(message_id);
        message_ref_requester.child(user_id).set(message_id);

        //remove refs to request
        var requester_req_ref = admin.database().ref("/Users/" + requester_id + "/RequestsSent/" + user_id)
        .remove();
        var user_req_ref = admin.database().ref("/Users/" + user_id + "/RequestsReceived/" + requester_id)
                                          .remove();

        //update status
        var request_ref = admin.database().ref("/Requests/" + req_id)
        request_ref.update({status : "STATUS_ACCEPTED"});

    } else if (response === "REJECT_REQ") {
           var update_req_ref = admin.database().ref("/Requests/" + req_id);
           update_req_ref.update({status : "STATUS_DENIED"});

    } else {
        console.log("there was error", "error", "error!");
    }

});


exports.grabUserRequests = functions.https.onCall((data, context) => {

    const user_id = data.user_id;
    var names = [];
    var user_ids = [];
    var request_ids = [];
    console.log("L:/", "CALLED_GRAB_USER_REQUESTS", context.auth.uid);
    const fb = admin.database().ref("/Users/" + user_id + "/RequestsReceived/");
    var request_data = fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
              var user_id = ds.key;
              var request_id = ds.val();
              var other_name = "";
              user_ids.push(user_id);
              request_ids.push(request_id);


        });
            return {user_ids: user_ids,
                                     requests: request_ids};
        });
    var receiver_data =  request_data.then(function(result) {
        result["user_ids"].forEach(user => {
              admin.database().ref("/Users/" + user).on('value', function(value) {

                               name = value.child("name").val();

                               names.push(name);


              }, function(errorObject) {
                 console.log("Failure: " + errorObject.code);
              });
          });
                return {names: names,
                };
          });
    return Promise.all([request_data, receiver_data]).then(function([result_request, result_recv]) {

            return {names: result_recv["names"],

                    user_ids: result_request["user_ids"],
                    request_ids: result_request["requests"]};
    });



    });


exports.checkRequestStatus = functions.https.onCall((data, context) => {

    const request_id = data.request_id;
    console.log("L:/", "CALLED_CHECK_REQUEST", context.auth.uid);
    const fb = admin.database().ref("/Requests/" + request_id + "/status/");
    var request_data = fb.once('value').then(dataSnapshot => {
        return {status: dataSnapshot.val()};
    });

});

exports.getRequestId = functions.https.onCall((data, context) => {

    const user_id = data.user_id;
    const requestee_id = data.requestee_id;
    console.log("L:/", "CALLED_GET_REQUEST_ID", context.auth.uid);
    const fb = admin.database().ref("/Users/" + user_id + "/RequestsReceived/" + requestee_id);
    var request_data = fb.once('value').then(dataSnapshot => {
        return {request_id: dataSnapshot.val()};
    });

});
exports.pushSuggestion = functions.https.onCall((data, context) => {
    const pub_id = data.pub_id;
    const comments = data.comments;
    const author_name = data.author;
    const idea = data.idea;
    console.log("L:/", "CALLED_PUSH_SUGG", context.auth.uid);
    const fb = admin.database().ref("/Users/" + pub_id + "/Suggestions/");
    const suggestion = {name : author_name,
                    idea : idea,
                    comments : comments};
    fb.push(suggestion);

});

exports.getPublisherIdeas = functions.https.onCall((data, context) => {
    const query = data.query;
    var ideas = [];
    var writers = [];
    var keys = [];
    var userID = [];
    console.log("L:/", "CALLED_GET_PUBLISHER_IDEAS", query);
    const fb = admin.database().ref("/Ideas/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
            ds.forEach(ds1 =>{
                var idea = ds1.child("IdeaName").val();
                var writer = ds1.child("WriterName").val();
                var mkey = ds1.key;
                var userID = ds1.child("Publisher").val();
                if (query === userID) {
                    ideas.push(idea);
                    writers.push(writer);
                    keys.push(mkey);
                }
            });
        });
         return {Ideas: ideas,
                 Writers: writers,
                 Keys: keys};
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

exports.getIdeaByID = functions.https.onCall((data, context) => {


    console.log("L:/", "CALLED_GET_IDEA_BY_ID", context.auth.uid);
    const ideaKey = data.ideaKey;
    let idea;
    const fb = admin.database().ref("/Ideas/");
    return fb.once('value').then(dataSnapshot => {
        dataSnapshot.forEach(ds => {
            ds.forEach(ds1 => {
                if (ds1.key === ideaKey) {
                    idea = ds1.val();
                }
            });
        });
        console.log(idea);
        return idea;
    });
});

exports.buyIdea = functions.https.onCall((data, context) => {

    let updates = {};
    console.log(data.newPublisherID + "\n" +
                data.writerID + "\n" +
                data.writerID);

    updates["Publisher"] = data.newPublisherID;

    const fb = admin.database().ref("/Ideas/" + data.writerID + "/" + data.boughtIdeaID);
    fb.update(updates);
//    fb.once('once').then(datasnapshot => {
//        datasnapshot.val('Publisher') = data.newPublisherID;
//    });
    return 0;
});