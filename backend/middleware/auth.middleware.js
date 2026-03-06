
import jwt from  "jsonwebtoken"

export function authMiddleware(req, res, next) {

  const authHeader = req.headers.authorization;

  if (!authHeader) {
    return res.status(401).json({ error: "No token provided" });
  }

  const token = authHeader.split(" ")[1];

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    
    req.user = decoded;

    /*
    req = {
        headers: {...},
        body: {...},
        user: { userId: 5 , username : sagar, iat: ..., exp: ... },
        ...
          }

    */
   // res.send and res.json is same , just .json is used for json API

    next();
  } catch (err) {
    return res.status(403).json({ error: "Session expired ....Logging Out Soon" });
  }
}   