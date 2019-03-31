const functions = require('firebase-functions');

const admin=require('firebase-admin');
admin.initializeApp();


exports.notifyNewLocation=functions.firestore
    .document('Location/{id}')
    .onCreate((docSnapshot,context)=>{
        const location=docSnapshot.data();
        const nom=location['nom'];
        var topic = 'news'
        const notif={
            notification:{
                title:" Miniprojet",
                body:" New location called "+nom +" is added",

            },
            topic: topic
        };
       admin.messaging().send(notif)
         .then((response) => {

          return console.log('Successfully sent message:', response);
         })
         .catch((error) => {
          return  console.log('Error sending message:', error);
         });
    });

