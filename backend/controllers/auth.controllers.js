import pool from "../db/database.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import { OAuth2Client } from "google-auth-library";
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID); //  Google’s google-auth-library

//It is an official Node.js library for verifying Google ID tokens.

export async function registerController(req , res){
   try{
    const { email, password , username } = req.body;

    if (!email || !password || !username) {
      return res.status(400).json({ error: "Email and password are required" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const result = await pool.query(
    `INSERT INTO users (email, password_hash , username)
    VALUES ($1, $2 , $3)
    RETURNING id , username`,//returns the id of the user
    [email, hashedPassword , username]
    );

    const user = result.rows[0];

    const token = jwt.sign(
   { userId: user.id, username: user.username },
    process.env.JWT_SECRET,
    { expiresIn: "2h" }
    );

    res.status(201).json({ token });

   } catch (error){
    if (error.code === "23505") {
  if (error.constraint === "users_email_key") {
    return res.status(409).json({ error: "Email already exists" });
  }

  if (error.constraint === "users_username_key") {
    return res.status(409).json({ error: "Username already taken" });
  }
}

    console.error(error);
    res.status(500).json({ error: "Registration failed" });
   }

}   

export async function loginController(req , res){

    try {


    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ error: "Email and password are required" });
    }

    const result = await pool.query(
      "SELECT id, email, username, password_hash, provider FROM users WHERE email = $1",
      [email]
    );

    if (result.rows.length === 0) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const user = result.rows[0];

          if (user.provider !== "local") {
         return res.status(400).json({error: "This account uses Google Sign-In"});
       }

    const isMatch = await bcrypt.compare(password, user.password_hash);

    if (!isMatch) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const token = jwt.sign(
      { userId: user.id },
      process.env.JWT_SECRET,
      { expiresIn: "1h" }
    );

    res.json({ token });

  } catch (error) {
    console.error(error);
    res.status(500).json({ error: "Login failed" });
  }
}

export async function googleLogin(req, res) {
  try {
    const { idToken } = req.body;

    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });// downloads Google public keys from a site (This is called JWKS endpoint) ,Decodes JWT header and finds matching public key and verifies things

    const payload = ticket.getPayload();
    const email = payload.email;
     //ticket is an instance of: LoginTicket class from google-auth-library
/*
LoginTicket {
   envelope: { ... },
   payload: { ... },
   clientId: "...",
}
{
a basic example of what payload may look like depending on account etc etc
  "iss": "https://accounts.google.com",
  "azp": "5249267....apps.googleusercontent.com",
  "aud": "5249267....apps.googleusercontent.com",
  "sub": "109876543210987654321",
  "email": "sagar@gmail.com",
  "email_verified": true,
  "name": "Sagar Saji",
  "picture": "https://lh3.googleusercontent.com/a/AAcHTte...",
  "given_name": "Sagar",
  "family_name": "Saji",
  "locale": "en",
  "iat": 1700000000,
  "exp": 1700003600
}*/

    let userResult = await pool.query(
      "SELECT * FROM users WHERE email = $1",
      [email]
    );

    if (userResult.rows.length > 0) {
      const user = userResult.rows[0];
       

  if (user.provider !== "google") {
    return res.status(400).json({
      error: "Account exists with email/password. Use normal login." });
    }


      const token = jwt.sign(
        { userId: user.id },
        process.env.JWT_SECRET,
        { expiresIn: "2h" }
      );

      return res.json({ token });
    }

    // User does not exist → need username
    return res.json({
      needsUsername: true,
      email
    });

  } catch (err) {
    console.error(err);
    res.status(401).json({ error: "Invalid Google token" });
  }
}

export async function completeGoogleRegistration(req, res) {
  try {
    const { idToken, username } = req.body;

    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });

    const payload = ticket.getPayload();
    const email = payload.email;

    const result = await pool.query(
      `INSERT INTO users (email, username, provider)
       VALUES ($1, $2, 'google')
       RETURNING id`,
      [email, username]
    );

    const token = jwt.sign(
      { userId: result.rows[0].id },
      process.env.JWT_SECRET,
      { expiresIn: "2h" }
    );

    res.json({ token });

  } catch (err) {
    console.error(err);
    if (err.code === "23505") {
  if (err.constraint === "users_email_key") {
    return res.status(409).json({ error: "Email already exists" });
  }

  if (err.constraint === "users_username_key") {
    return res.status(409).json({ error: "Username already taken" });
  }
}
    res.status(500).json({ error: "Registration failed" });
  }
}


