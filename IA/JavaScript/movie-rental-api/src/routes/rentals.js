const express = require('express');
const Fawn = require('fawn');

const models = require('../models');

const Customer = models.Customer;
const Movie = models.Movie;
const Rental = models.Rental;

const auth = require('../middleware/auth');
const validateId = require('../middleware/validate-id');

const validation = require('../validation');

const router = express.Router();

router.use(auth);

router.get('/', async function (req, res) {
  const rentals = await Rental.find()
    .sort({
      dateOut: -1
    });

  res.json(rentals);
});

router.get(
  '/:id',
  validateId,
  async function (req, res) {
    const rental = await Rental.findById(
      req.params.id
    );

    if (!rental) {
      return res.status(404).json({
        error: 'Locação não encontrada.'
      });
    }

    res.json(rental);
  }
);

router.post(
  '/',
  validation.validateBody(validation.schemas.rental),
  async function (req, res) {
    const body = req.validatedBody;

    const customer = await Customer.findById(
      body.customerId
    );

    if (!customer) {
      return res.status(404).json({
        error: 'Cliente não encontrado.'
      });
    }

    const movie = await Movie.findById(
      body.movieId
    );

    if (!movie) {
      return res.status(404).json({
        error: 'Filme não encontrado.'
      });
    }

    if (movie.numberInStock <= 0) {
      return res.status(400).json({
        error: 'Não há unidades disponíveis deste filme.'
      });
    }

    const rental = new Rental({
      customer: {
        _id: customer._id,
        name: customer.name,
        phone: customer.phone,
        isGold: customer.isGold
      },

      movie: {
        _id: movie._id,
        title: movie.title,
        dailyRentalRate: movie.dailyRentalRate
      },

      dateOut: new Date(),
      dateReturned: null,
      rentalFee: null
    });

    const task = new Fawn.Task();

    await task
      .update(
        'movies',
        {
          _id: movie._id,
          numberInStock: {
            $gt: 0
          }
        },
        {
          $inc: {
            numberInStock: -1
          }
        }
      )
      .save(
        'rentals',
        rental.toObject()
      )
      .run();

    const savedRental = await Rental.findById(
      rental._id
    );

    res.status(201).json(savedRental);
  }
);

module.exports = router;