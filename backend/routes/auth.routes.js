import express from "express"
import { loginController, registerController } from "../controllers/auth.controllers.js";
import { googleLogin, completeGoogleRegistration } from "../controllers/auth.controllers.js";


const router = express.Router()



router.post( "/login", loginController)
router.post( "/register", registerController)

router.post("/google", googleLogin);
router.post("/google/complete", completeGoogleRegistration);

export default router 