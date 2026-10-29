const express = require('express');
const bcrypt = require('bcrypt');
const lodash = require('lodash');

const User = require('../models').User;

const validation = require('../validation');

const router = express.Router();

router.post(
  '/',
  validation.validateBody(validation.schemas.auth),
  async function (req, res) {
    const body = req.validatedBody;

    const email = lodash.toLower(
      lodash.trim(body.email)
    );

    const user = await User.findOne({
      email: email
    });

    if (!user) {
      return res.status(401).json({
        error: 'E-mail ou senha inválidos.'
      });
    }

    const validPassword = await bcrypt.compare(
      body.password,
      user.password
    );

    if (!validPassword) {
      return res.status(401).json({
        error: 'E-mail ou senha inválidos.'
      });
    }

    const token = user.generateAuthToken();

    res.header('x-auth-token', token);

    res.json({
      token: token,
      tokenType: 'Bearer',
      expiresIn: 604800
    });
  }
);

module.exports = router;