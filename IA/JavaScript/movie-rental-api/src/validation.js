const Joi = require('joi');

Joi.objectId = require('joi-objectid')(Joi);

const schemas = {
  user: Joi.object({
    name: Joi.string()
      .trim()
      .min(2)
      .max(100)
      .required(),

    email: Joi.string()
      .trim()
      .email()
      .max(255)
      .required(),

    password: Joi.string()
      .min(5)
      .max(1024)
      .required()
  }),

  auth: Joi.object({
    email: Joi.string()
      .trim()
      .email()
      .required(),

    password: Joi.string()
      .required()
  }),

  genre: Joi.object({
    name: Joi.string()
      .trim()
      .min(2)
      .max(80)
      .required()
  }),

  customer: Joi.object({
    name: Joi.string()
      .trim()
      .min(2)
      .max(100)
      .required(),

    phone: Joi.string()
      .trim()
      .min(5)
      .max(30)
      .required(),

    isGold: Joi.boolean()
      .required()
  }),

  movie: Joi.object({
    title: Joi.string()
      .trim()
      .min(1)
      .max(200)
      .required(),

    genreId: Joi.objectId()
      .required(),

    numberInStock: Joi.number()
      .integer()
      .min(0)
      .required(),

    dailyRentalRate: Joi.number()
      .min(0)
      .required()
  }),

  rental: Joi.object({
    customerId: Joi.objectId()
      .required(),

    movieId: Joi.objectId()
      .required()
  }),

  return: Joi.object({
    customerId: Joi.objectId()
      .required(),

    movieId: Joi.objectId()
      .required()
  })
};

function validateBody(schema) {
  return function (req, res, next) {
    const result = Joi.validate(
      req.body,
      schema,
      {
        abortEarly: false,
        allowUnknown: false
      }
    );

    if (result.error) {
      return res.status(400).json({
        error: 'Dados inválidos.',
        details: result.error.details.map(function (item) {
          return item.message;
        })
      });
    }

    req.validatedBody = result.value;

    next();
  };
}

module.exports = {
  schemas: schemas,
  validateBody: validateBody
};