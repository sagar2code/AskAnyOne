import dotenv from "dotenv";
dotenv.config();
import express from "express"
import cors from "cors"
import authroutes from "./routes/auth.routes.js"
import questionRoutes from "./routes/question.routes.js"


const app =express()
app.use(cors())
app.use(express.json())


app.use("/auth",authroutes )
app.use("/questions", questionRoutes)


const PORT = process.env.PORT || 5000;

app.listen(PORT, "0.0.0.0", () => {
  console.log(`Server running on port ${PORT}`);
});


import pool from "./db/database.js";

pool.query("SELECT NOW()", (err, res) => {
  if (err) {
    console.error("DB connection failed", err);
  } else {
    console.log("DB connected at:", res.rows[0].now);
  }
});
