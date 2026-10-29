module.exports = function (req, res, next) {
  if (!req.user) {
    return res.status(401).json({
      error: 'Autenticação obrigatória.'
    });
  }

  if (!req.user.isAdmin) {
    return res.status(403).json({
      error: 'Esta operação exige privilégios de administrador.'
    });
  }

  next();
};