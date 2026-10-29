const express = require('express');
const bcrypt = require('bcrypt');

const User = require('../models').User;

const auth = require('../middleware/auth');

const validation = require('../validation');

const router = express.Router();

router.post(
  '/',
  validation.validateBody(validation.schemas.user),
  async function (req, res) {
    const body = req.validatedBody;

    const email = body.email.trim().toLowerCase();

    const existingUser = await User.findOne({
      email: email
    });

    if (existingUser) {
      return res.status(400).json({
        error: 'Este e-mail já está cadastrado.'
      });
    }

    const salt = await bcrypt.genSalt(10);

    const passwordHash = await bcrypt.hash(
      body.password,
      salt
    );

    const user = new User({
      name: body.name,
      email: email,
      password: passwordHash,
      isAdmin: false
    });

    try {
      await user.save();
    } catch (error) {
      if (error.code === 11000) {
        return res.status(400).json({
          error: 'Este e-mail já está cadastrado.'
        });
      }

      throw error;
    }

    const token = user.generateAuthToken();

    res.header('x-auth-token', token);

    return res.status(201).json({
      _id: user._id,
      name: user.name,
      email: user.email,
      isAdmin: user.isAdmin
    });
  }
);

router.get(
  '/me',
  auth,
  async function (req, res) {
    const user = await User.findById(req.user._id)
      .select('_id name email isAdmin');

    if (!user) {
      return res.status(404).json({
        error: 'Usuário não encontrado.'
      });
    }

    res.json(user);
  }
);

module.exports = router;