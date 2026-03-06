import pool from "../db/database.js";

export async function getAnswersByQuestion(req, res) {
  try {
    const { id } = req.params;

    const result = await pool.query(
      `SELECT 
          a.id,
          a.body,
          a.user_id,
          a.question_id,
          u.username
       FROM answers a
       JOIN users u ON a.user_id = u.id
       WHERE a.question_id = $1
       ORDER BY a.id DESC`,
      [id]
    );

    res.json(result.rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to fetch answers" });
  }
}

export async function createAnswer(req, res) {

  try {
      

    const { id } = req.params; // question id
    const { body } = req.body;

    if (!body) {
      return res.status(400).json({ error: "Answer body required" });
    }

    const result = await pool.query(
      `INSERT INTO answers (body, question_id, user_id)
       VALUES ($1, $2, $3)
       RETURNING *`,
      [body, id, req.user.userId]
    );

    res.status(201).json(result.rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to create answer" });
  }
}

export async function deleteAnswer(req, res) {
  try {
    const { id } = req.params; // answer id
    const userId = req.user.userId;

    const answerResult = await pool.query(
      "SELECT user_id FROM answers WHERE id = $1",
      [id]
    );

    if (answerResult.rows.length === 0) {
      return res.status(404).json({ error: "Answer not found" });
    }

    const ownerId = answerResult.rows[0].user_id;

    if (ownerId !== userId) {
      return res.status(401).json({ error: "You can delete only your own answer" });
    }

    await pool.query("DELETE FROM answers WHERE id = $1", [id]);

    res.json({ message: "Answer deleted successfully" });

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Failed to delete answer" });
  }
}

