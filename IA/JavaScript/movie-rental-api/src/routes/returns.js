const express = require('express');
const Fawn = require('fawn');
const moment = require('moment');

const models = require('../models');

const Rental = models.Rental;
const Movie = models.Movie;

const auth = require('../middleware/auth');

const validation = require('../validation');

const router = express.Router();

router.use(auth);

router.post(
  '/',
  validation.validateBody(validation.schemas.return),
  async function (req, res) {
    const body = req.validatedBody;

    const criteria = {
      'customer._id': body.customerId,
      'movie._id': body.movieId
    };

    const rental = await Rental.findOne({
      'customer._id': body.customerId,
      'movie._id': body.movieId,
      dateReturned: null
    }).sort({
      dateOut: -1
    });

    if (!rental) {
      const previousRental = await Rental.findOne(
        criteria
      );

      if (previousRental) {
        return res.status(400).json({
          error: 'Não existe locação aberta para este cliente e filme.'
        });
      }

      return res.status(404).json({
        error: 'Locação não encontrada.'
      });
    }

    const movie = await Movie.findById(
      rental.movie._id
    );

    if (!movie) {
      return res.status(404).json({
        error: 'O filme associado à locação não existe.'
      });
    }

    const returnedAt = new Date();

    const elapsedDays = moment(returnedAt).diff(
      moment(rental.dateOut),
      'days',
      true
    );

    const chargedDays = Math.max(
      1,
      Math.ceil(elapsedDays)
    );

    const rentalFee = Math.round(
      chargedDays *
      rental.movie.dailyRentalRate *
      100
    ) / 100;

    const task = new Fawn.Task();

    await task
      .update(
        'rentals',
        {
          _id: rental._id,
          dateReturned: null
        },
        {
          $set: {
            dateReturned: returnedAt,
            rentalFee: rentalFee
          }
        }
      )
      .update(
        'movies',
        {
          _id: movie._id
        },
        {
          $inc: {
            numberInStock: 1
          }
        }
      )
      .run();

    const updatedRental = await Rental.findById(
      rental._id
    );

    res.json(updatedRental);
  }
);

module.exports = router;