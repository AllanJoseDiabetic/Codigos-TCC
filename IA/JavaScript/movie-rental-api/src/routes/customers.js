const express = require('express');

const Customer = require('../models').Customer;

const auth = require('../middleware/auth');
const validateId = require('../middleware/validate-id');

const validation = require('../validation');

const router = express.Router();

router.use(auth);

router.get('/', async function (req, res) {
  const customers = await Customer.find()
    .sort('name');

  res.json(customers);
});

router.get(
  '/:id',
  validateId,
  async function (req, res) {
    const customer = await Customer.findById(
      req.params.id
    );

    if (!customer) {
      return res.status(404).json({
        error: 'Cliente não encontrado.'
      });
    }

    res.json(customer);
  }
);

router.post(
  '/',
  validation.validateBody(validation.schemas.customer),
  async function (req, res) {
    const body = req.validatedBody;

    const customer = new Customer({
      name: body.name,
      phone: body.phone,
      isGold: body.isGold
    });

    await customer.save();

    res.status(201).json(customer);
  }
);

router.put(
  '/:id',
  validateId,
  validation.validateBody(validation.schemas.customer),
  async function (req, res) {
    const body = req.validatedBody;

    const customer = await Customer.findByIdAndUpdate(
      req.params.id,
      {
        name: body.name,
        phone: body.phone,
        isGold: body.isGold
      },
      {
        new: true,
        runValidators: true
      }
    );

    if (!customer) {
      return res.status(404).json({
        error: 'Cliente não encontrado.'
      });
    }

    res.json(customer);
  }
);

router.delete(
  '/:id',
  validateId,
  async function (req, res) {
    const customer = await Customer.findByIdAndRemove(
      req.params.id
    );

    if (!customer) {
      return res.status(404).json({
        error: 'Cliente não encontrado.'
      });
    }

    res.json(customer);
  }
);

module.exports = router;