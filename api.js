const express = require('express');
const app = express();
const bodyParser = require('body-parser');
app.use(bodyParser.json());
require('dotenv').config();

const admin = require("firebase-admin");
const serviceAccount = require("./firebase.json");

admin.initializeApp({
    credential: admin.credential.cert({
        project_id: process.env.FIREBASE_PROJECT_ID,
        private_key: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, '\n'),
        client_email: process.env.FIREBASE_CLIENT_EMAIL,
    }),
    databaseURL: "https://fraudguard-a1654-default-rtdb.asia-southeast1.firebasedatabase.app/",
});
app.post('/transactions/:id', (req, res) => {
    const transaction = req.body;
    const token = req.headers['token'];
    console.log("post", transaction, token);
    sendFCMMessage(transaction, token);
    res.status(201).send();
});

// Route to handle sending FCM data
app.post('/result/:id', async (req, res) => {
    const resultData = req.headers['result'];
    const userId = req.params.id;

    try {
        const token = await getToken(userId);
        console.log(token);
        const message = {
            token: "eNbADIdrT4-HAZIMIvNUDG:APA91bGCJTGzuGD2_FBgj1xZ36gFVIwHKM8Sh7o5FiaBkWJP9bXc4LQEu6dHID4jItKbcZjG_0cMeu4et2WVfeImeSV1IvZtR5kakmMiJyrKdfHPxHRXiKc1tR5M-O9NadgSModisIWd",
            data: {
                result: resultData,
            },
        };

        admin.messaging().send(message)
            .then((response) => {
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
    } catch (error) {
        console.error('Error:', error);
        res.status(500).send('Error sending FCM data');
    }
});



function sendFCMMessage(data, token) {  // Fix: Changed 'toke' to 'token'
    const message = {
        data: {
            transactionData: JSON.stringify(data)
        },
        token: token, // Fix: Changed 'toke' to 'token'
    };

    admin.messaging().send(message)
        .then((response) => {
            console.log('Successfully sent message:', response);
        })
        .catch((error) => {
            console.log('Error sending message:', error);
        });
}
// Function to retrieve the token
async function getToken(id) {
    try {
        // Get a reference to the database
        const db = admin.database();

        const tokenRef = db.ref(`CustomerAccount/${id}/bankToken`);

        // Retrieve the token value
        const snapshot = await tokenRef.once('value');
        const token = snapshot.val();
        return token; // Return the retrieved token
    } catch (error) {
        console.error('Error retrieving token:', error);
        throw error; // Rethrow the error for handling in the calling code
    }
}

app.listen(8080, () => console.log('Server is running on port 8080'));
