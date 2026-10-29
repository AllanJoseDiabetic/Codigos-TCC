const jwt = require('jsonwebtoken');
const config = require('config');

const User = require('../models').User;

module.exports = async function (req, res, next) {
  const token = req.header('x-auth-token');

  if (!token) {
    return res.status(401).json({
      error: 'Token de autenticação não informado.'
    });
  }

  let decoded;

  try {
    decoded = jwt.verify(
      token,
      config.get('security.jwtSecret')
    );
  } catch (error) {
    return res.status(401).json({
      error: 'Token inválido ou expirado.'
    });
  }

  if (!decoded._id) {
    return res.status(401).json({
      error: 'Token de autenticação inválido.'
    });
  }

  const user = await User.findById(decoded._id)
    .select('_id isAdmin');

  if (!user) {
    return res.status(401).json({
      error: 'Usuário associado ao token não existe.'
    });
  }

  req.user = {
    _id: user._id,
    isAdmin: user.isAdmin
  };

  next();
};