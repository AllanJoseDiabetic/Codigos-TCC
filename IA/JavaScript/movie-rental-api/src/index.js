const config = require('config');
const mongoose = require('mongoose');
const Fawn = require('fawn');

const app = require('./app');
const logger = require('./logger');

mongoose.Promise = global.Promise;

process.on('unhandledRejection', function (reason) {
  const message = reason && reason.message
    ? reason.message
    : String(reason);

  logger.error(
    'Promessa rejeitada sem tratamento.',
    {
      message: message
    }
  );
});

function readConfiguration() {
  const port = Number(
    config.get('app.port')
  );

  const mongoUri = config.get(
    'mongo.uri'
  );

  const jwtSecret = config.get(
    'security.jwtSecret'
  );

  if (!Number.isInteger(port) ||
      port < 1 ||
      port > 65535) {
    throw new Error(
      'A porta HTTP configurada é inválida.'
    );
  }

  if (typeof mongoUri !== 'string' ||
      mongoUri.trim().length === 0) {
    throw new Error(
      'Configure MONGODB_URI com um endereço válido.'
    );
  }

  if (typeof jwtSecret !== 'string' ||
      jwtSecret.trim().length === 0) {
    throw new Error(
      'Configure JWT_SECRET antes de iniciar a API.'
    );
  }

  return {
    port: port,
    mongoUri: mongoUri
  };
}

async function start() {
  const settings = readConfiguration();

  await mongoose.connect(
    settings.mongoUri
  );

  logger.info(
    'Conexão com MongoDB estabelecida.'
  );

  Fawn.init(mongoose);

  app.listen(settings.port, function () {
    logger.info(
      'API de locadora iniciada na porta ' +
      settings.port + '.'
    );
  });
}

start().catch(function (error) {
  logger.error(
    'Falha na inicialização da aplicação.',
    {
      message: error.message,
      stack: error.stack
    }
  );

  process.exitCode = 1;
});