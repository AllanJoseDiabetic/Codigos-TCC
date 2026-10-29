const express = require('express');

const models = require('../models');

const Genre = models.Genre;
const Movie = models.Movie;

const auth = require('../middleware/auth');
const admin = require('../middleware/admin');
const validateId = require('../middleware/validate-id');

const validation = require('../validation');

const router = express.Router();

router.get('/', async function (req, res) {
  const genres = await Genre.find()
    .sort('name');

  res.json(genres);
});

router.get(
  '/:id',
  validateId,
  async function (req, res) {
    const genre = await Genre.findById(req.params.id);

    if (!genre) {
      return res.status(404).json({
        error: 'Gênero não encontrado.'
      });
    }

    res.json(genre);
  }
);

router.post(
  '/',
  auth,
  validation.validateBody(validation.schemas.genre),
  async function (req, res) {
    const genre = new Genre({
      name: req.validatedBody.name
    });

    await genre.save();

    res.status(201).json(genre);
  }
);

router.put(
  '/:id',
  auth,
  validateId,
  validation.validateBody(validation.schemas.genre),
  async function (req, res) {
    const genre = await Genre.findByIdAndUpdate(
      req.params.id,
      {
        name: req.validatedBody.name
      },
      {
        new: true,
        runValidators: true
      }
    );

    if (!genre) {
      return res.status(404).json({
        error: 'Gênero não encontrado.'
      });
    }

    res.json(genre);
  }
);

router.delete(
  '/:id',
  auth,
  admin,
  validateId,
  async function (req, res) {
    const genre = await Genre.findById(req.params.id);

    if (!genre) {
      return res.status(404).json({
        error: 'Gênero não encontrado.'
      });
    }

    const referencedMovie = await Movie.findOne({
      genre: genre._id
    });

    if (referencedMovie) {
      return res.status(400).json({
        error: 'Não é possível excluir um gênero utilizado por filmes.'
      });
    }

    await genre.remove();

    res.json(genre);
  }
);

module.exports = router;