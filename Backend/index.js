require("dotenv").config();

// Initialize Express
const app = require("express")();
const bodyParser = require("body-parser");
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Import the database connection
require("./database/mongodb");

// Import routes
const debugRouter = require("./routes/debug");
const userRouter = require("./routes/user");
app.use("/debug", debugRouter);
app.use("/user", userRouter);

// Listen on PORT
const server = app.listen(process.env.PORT, function () { // TODO Read process.env.PORT
    const host = server.address().address;
    const port = server.address().port;
    console.log("Listening at http://%s:%s", host, port);
});