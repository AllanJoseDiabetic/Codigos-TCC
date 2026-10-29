const logger = require('../logger');

module.exports = function (err, req, res, next) {
  if (err.type === 'entity.parse.failed') {
    return res.status(400).json({
      error: 'O corpo da requisição não contém JSON válido.'
    });
  }

  if (err.code === 11000) {
    return res.status(400).json({
      error: 'Já existe um registro com um valor exclusivo informado.'
    });
  }

  logger.error('Erro interno durante a requisição.', {
    method: req.method,
    path: req.originalUrl,
    message: err.message,
    stack: err.stack
  });

  res.status(500).json({
    error: 'Não foi possível concluir a operação.'
  });
};