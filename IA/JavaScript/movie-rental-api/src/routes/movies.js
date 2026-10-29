const express = require('express');

const models = require('../models');

const Movie = models.Movie;
const Genre = models.Genre;
const Rental = models.Rental;

const auth = require('../middleware/auth');
const admin = require('../middleware/admin');
const validateId = require('../middleware/validate-id');

const validation = require('../validation');

const router = express.Router();

router.get('/', async function (req, res) {
  const movies = await Movie.find()
    .populate('genre', 'name')
    .sort('title');

  res.json(movies);
});

router.get(
  '/:id',
  validateId,
  async function (req, res) {
    const movie = await Movie.findById(
      req.params.id
    ).populate('genre', 'name');

    if (!movie) {
      return res.status(404).json({
        error: 'Filme não encontrado.'
      });
    }

    res.json(movie);
  }
);

router.post(
  '/',
  auth,
  validation.validateBody(validation.schemas.movie),
  async function (req, res) {
    const body = req.validatedBody;

    const genre = await Genre.findById(
      body.genreId
    );

    if (!genre) {
      return res.status(400).json({
        error: 'O gênero informado não existe.'
      });
    }

    const movie = new Movie({
      title: body.title,
      genre: genre._id,
      numberInStock: body.numberInStock,
      dailyRentalRate: body.dailyRentalRate
    });

    await movie.save();

    const savedMovie = await Movie.findById(
      movie._id
    ).populate('genre', 'name');

    res.status(201).json(savedMovie);
  }
);

router.put(
  '/:id',
  auth,
  validateId,
  validation.validateBody(validation.schemas.movie),
  async function (req, res) {
    const body = req.validatedBody;

    const genre = await Genre.findById(
      body.genreId
    );

    if (!genre) {
      return res.status(400).json({
        error: 'O gênero informado não existe.'
      });
    }

    const movie = await Movie.findByIdAndUpdate(
      req.params.id,
      {
        title: body.title,
        genre: genre._id,
        numberInStock: body.numberInStock,
        dailyRentalRate: body.dailyRentalRate
      },
      {
        new: true,
        runValidators: true
      }
    );

    if (!movie) {
      return res.status(404).json({
        error: 'Filme não encontrado.'
      });
    }

    const updatedMovie = await Movie.findById(
      movie._id
    ).populate('genre', 'name');

    res.json(updatedMovie);
  }
);

router.delete(
  '/:id',
  auth,
  admin,
  validateId,
  async function (req, res) {
    const movie = await Movie.findById(
      req.params.id
    );

    if (!movie) {
      return res.status(404).json({
        error: 'Filme não encontrado.'
      });
    }

    const pendingRental = await Rental.findOne({
      'movie._id': movie._id,
      dateReturned: null
    });

    if (pendingRental) {
      return res.status(400).json({
        error: 'Não é possível excluir um filme com locações pendentes.'
      });
    }

    await movie.remove();

    res.json(movie);
  }
);

module.exports = router;