require('express-async-errors');

const express = require('express');
const cors = require('cors');

const logger = require('./logger');
const errorHandler = require('./middleware/error');

const users = require('./routes/users');
const auth = require('./routes/auth');
const genres = require('./routes/genres');
const customers = require('./routes/customers');
const movies = require('./routes/movies');
const rentals = require('./routes/rentals');
const returns = require('./routes/returns');

const app = express();

app.disable('x-powered-by');

app.use(cors());

app.use(express.json({
  limit: '64kb'
}));

app.use(function (req, res, next) {
  logger.info(
    req.method + ' ' + req.originalUrl
  );

  next();
});

app.use('/api/users', users);
app.use('/api/auth', auth);
app.use('/api/genres', genres);
app.use('/api/customers', customers);
app.use('/api/movies', movies);
app.use('/api/rentals', rentals);
app.use('/api/returns', returns);

app.use('/api', function (req, res) {
  res.status(404).json({
    error: 'Endpoint não encontrado.'
  });
});

app.use(errorHandler);

module.exports = app;