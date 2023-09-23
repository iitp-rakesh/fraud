const http = require('http');
const mysql = require('mysql2');
const querystring = require('querystring');

// Replace these with your own database credentials
const dbConfig = {
    host: 'sql.freedb.tech',
    user: 'freedb_iitp_rakesh',
    password: 'AmW#wA!Rv6m26W8',
    database: 'freedb_FraudGuard',
};

const server = http.createServer((req, res) => {
    if (req.method === 'POST' && req.url === '/register') {
        let body = '';

        req.on('data', (chunk) => {
            body += chunk;
        });

        req.on('end', () => {
            const registrationData = querystring.parse(body);
            console.log(registrationData.account_number, registrationData.cvv)
            // Create a MySQL connection
            const connection = mysql.createConnection(dbConfig);

            connection.connect((err) => {
                if (err) {
                    console.error('Error connecting to the database:', err);
                    res.statusCode = 500;
                    res.end('Internal server error');
                    return;
                }

                // Check if the registration data exists in the database
                const query = `SELECT * FROM debit_card WHERE 
                       account_number = ? AND
                       debit_card_number = ? AND
                       expiry_date = ? AND
                       cvv = ? AND
                       pin = ?`;

                connection.query(
                    query,
                    [
                        registrationData.account_number,
                        registrationData.debit_card_number,
                        registrationData.expiry_date,
                        registrationData.cvv,
                        registrationData.pin,
                    ],
                    (queryError, results) => {
                        if (queryError) {
                            console.error('Error querying the database:', queryError);
                            res.statusCode = 500;
                            res.end('Internal server error');
                            connection.end();
                            return;
                        }

                        if (results.length > 0) {
                            // Data exists in the database
                            res.statusCode = 200;
                            res.setHeader('Content-Type', 'application/json');
                            res.end(JSON.stringify({ success: true, message: 'Registration successful' }));
                        } else {
                            // Data does not exist in the database
                            res.statusCode = 400;
                            res.setHeader('Content-Type', 'application/json');
                            res.end(JSON.stringify({ success: false, message: 'Incorrect Credentials' }));
                        }

                        connection.end();
                    }
                );
            });
        });
    }
    else if (req.method === 'GET' && req.url === '/') {
        // Respond to GET /check
        res.statusCode = 200;
        res.setHeader('Content-Type', 'text/plain');
        res.end('Server is working.');
    }
    else if (req.method === 'POST' && req.url === '/createAccount') {
        let body = '';

        req.on('data', (chunk) => {
            body += chunk;
        });

        req.on('end', () => {
            const userData = querystring.parse(body);
            console.log(userData.account_number, userData.password);

            // Create a MySQL connection
            const connection = mysql.createConnection(dbConfig);

            connection.connect((err) => {
                if (err) {
                    console.error('Error connecting to the database:', err);
                    res.statusCode = 500;
                    res.end('Internal server error');
                    return;
                }

                // Insert the user data into the database
                const query = 'INSERT INTO users (account_number, password) VALUES (?, ?)';

                connection.query(
                    query,
                    [userData.account_number, userData.password],
                    (queryError, results) => {
                        if (queryError) {
                            console.error('Error querying the database:', queryError);
                            res.statusCode = 500;
                            res.end('Internal server error');
                            connection.end();
                            return;
                        }

                        // User was successfully created
                        res.statusCode = 201;
                        res.setHeader('Content-Type', 'application/json');
                        res.end(JSON.stringify({ success: true, message: 'Account created successfully' }));

                        connection.end();
                    }
                );
            });
        });
    }
    else if (req.method === 'POST' && req.url === '/login') {
        let body = '';
    
        req.on('data', (chunk) => {
            body += chunk;
        });
    
        req.on('end', () => {
            const loginData = querystring.parse(body);
            console.log(loginData.account_number, loginData.password);
    
            // Create a MySQL connection
            const connection = mysql.createConnection(dbConfig);
    
            connection.connect((err) => {
                if (err) {
                    console.error('Error connecting to the database:', err);
                    res.statusCode = 500;
                    res.end('Internal server error');
                    return;
                }
    
                // Check login credentials in the database
                const query = 'SELECT * FROM users WHERE account_number = ? AND password = ?';
    
                connection.query(
                    query,
                    [loginData.account_number, loginData.password],
                    (queryError, results) => {
                        if (queryError) {
                            console.error('Error querying the database:', queryError);
                            res.statusCode = 500;
                            res.end('Internal server error');
                            connection.end();
                            return;
                        }
    
                        if (results.length > 0) {
                            // Login successful
                            res.statusCode = 200;
                            res.setHeader('Content-Type', 'application/json');
                            res.end(JSON.stringify({ success: true, message: 'Login successful' }));
                        } else {
                            // Incorrect login credentials
                            res.statusCode = 401;
                            res.setHeader('Content-Type', 'application/json');
                            res.end(JSON.stringify({ success: false, message: 'Incorrect credentials' }));
                        }
    
                        connection.end();
                    }
                );
            });
        });
    }
    
    else {
        res.statusCode = 404;
        res.end('Not Found');
    }
});

const port = 3000; // Change this to the desired port number
server.listen(port, () => {
    console.log(`Server is listening on port ${port}`);
});
