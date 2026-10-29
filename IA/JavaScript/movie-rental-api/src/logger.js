const winston = require('winston');
const config = require('config');

const logger = new winston.Logger({
  transports: [
    new winston.transports.Console({
      level: 'info',
      colorize: false,
      timestamp: true
    })
  ]
});

if (process.env.LOG_TO_MONGODB === 'true') {
  require('winston-mongodb');

  logger.add(winston.transports.MongoDB, {
    db: config.get('mongo.uri'),
    collection: 'api_diagnostics',
    level: 'error'
  });
}

module.exports = logger;