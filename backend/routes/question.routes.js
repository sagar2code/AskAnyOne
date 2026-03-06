import { authMiddleware } from "../middleware/auth.middleware.js";
import {
  getAllQuestions,
  createQuestion,
  getQuestionById,
  deleteQuestion,
  getMyQuestions
} from "../controllers/question.controller.js";
import express from "express"
import { createAnswer, deleteAnswer, getAnswersByQuestion  } from "../controllers/answer.controller.js";
import { upload } from "../middleware/upload.js";

/*When any client (Android/Postman/browser) hits your server, Node’s HTTP engine receives it, and Express wraps it into a nicer object called:

 req (Request)
 res (Response)*/
const router = express.Router()

router.get("/", getAllQuestions);
router.post("/", authMiddleware, upload.single("image"), createQuestion); // single is a fucntion which returns a middleware itself

router.get("/my", authMiddleware, getMyQuestions);


router.get("/:id", getQuestionById);

router.get("/:id/answers", getAnswersByQuestion);
router.post("/:id/answers", authMiddleware, createAnswer);

router.delete("/:id", authMiddleware, deleteQuestion)
router.delete("/:id/answers", authMiddleware, deleteAnswer);


export default router;
