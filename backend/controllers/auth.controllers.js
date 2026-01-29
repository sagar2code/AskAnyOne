import pool from "../db/database.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

export async function registerController(req , res){
   try{
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ error: "Email and password are required" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const result = await pool.query(
    `INSERT INTO users (email, password_hash)
    VALUES ($1, $2)
    RETURNING id`,//returns the id of the user
    [email, hashedPassword]
    );

    const user = result.rows[0];

    const token = jwt.sign(
    { userId: user.id },
    process.env.JWT_SECRET,
    { expiresIn: "2h" }
    );

    res.status(201).json({ token });

   } catch (error){
    if (error.code==23505){ //postgres error code for duplicates
        return res.status(409).json({error : "Email already exists"})
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
      "SELECT id, email, password_hash FROM users WHERE email = $1",
      [email]
    );

    if (result.rows.length === 0) {
      return res.status(401).json({ error: "Invalid credentials" });
    }

    const user = result.rows[0];

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
