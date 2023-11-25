const express = require('express');
const app = express();
const bodyParser = require('body-parser');
app.use(bodyParser.json());

const admin = require("firebase-admin");
const serviceAccount = require("./firebase.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://fraudguard-a1654-default-rtdb.asia-southeast1.firebasedatabase.app/"
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

    const token = await getToken(userId);
    console.log(token);
    const message = {
        token: token,
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
