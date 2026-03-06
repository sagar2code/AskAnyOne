import pool from "../db/database.js";

export async function getAllQuestions(req, res) {
  try {
    const result = await pool.query(
      `SELECT 
          q.id,
          q.title,
          q.body,
          q.image_url,
          q.user_id,
          u.username
        FROM questions q
        JOIN users u ON q.user_id = u.id
        ORDER BY q.id DESC;`
    );
    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch questions" });
  }
}

export async function createQuestion(req, res) {
  try {
    const { title, body } = req.body;

    if (!title || !body) {
      return res.status(400).json({ error: "Title and body required" });
    }
    

    let imageUrl= null ;
    if (req.file){
      imageUrl= req.file.path
    }
    
    const result = await pool.query(
      `INSERT INTO questions (title, body, image_url, user_id)
       VALUES ($1, $2, $3, $4)
       RETURNING *`,
      [title, body, imageUrl, req.user.userId]
    );

    res.status(201).json(result.rows[0]);

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to create question" });
  }
}

export async function getQuestionById(req, res) {
  try {
    const { id } = req.params;

    const result = await pool.query(
      `SELECT 
          q.id,
          q.title,
          q.body,
          q.image_url,
          q.user_id,
          u.username
       FROM questions q
       JOIN users u ON q.user_id = u.id
       WHERE q.id = $1`,
      [id]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: "Question not found" });
    }

    res.json(result.rows[0]);

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch question" });
  }
}

export async function deleteQuestion(req, res) {
  try {
    const { id } = req.params; // question id from URL
    const userId = req.user.userId; // logged in user id from JWT

    // 1) Check question exists + get owner
    const questionResult = await pool.query(
      "SELECT user_id FROM questions WHERE id = $1",
      [id]
    );

    if (questionResult.rows.length === 0) {
      return res.status(404).json({ error: "Question not found" });
    }

    const ownerId = questionResult.rows[0].user_id;

    if (ownerId !== userId) {
      return res.status(401).json({ error: "You can delete only your own question" });
    }

    await pool.query("DELETE FROM answers WHERE question_id = $1", [id]);

    await pool.query("DELETE FROM questions WHERE id = $1", [id]);

    res.json({ message: "Question deleted successfully" });

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to delete question" });
  }
}

export async function getMyQuestions(req, res) {
  try {
    const userId = req.user.userId;

    const result = await pool.query(
      "SELECT id, title, body, image_url, user_id FROM questions WHERE user_id = $1 ORDER BY id DESC",
      [userId]
    );

    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch my questions" });
  }
}



