const mongoose = require('mongoose');
const jwt = require('jsonwebtoken');
const config = require('config');

const genreSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    trim: true,
    minlength: 2,
    maxlength: 80
  }
});

const customerSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    trim: true,
    minlength: 2,
    maxlength: 100
  },

  phone: {
    type: String,
    required: true,
    trim: true,
    minlength: 5,
    maxlength: 30
  },

  isGold: {
    type: Boolean,
    default: false
  }
});

const movieSchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
    trim: true,
    minlength: 1,
    maxlength: 200
  },

  genre: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Genre',
    required: true
  },

  numberInStock: {
    type: Number,
    required: true,
    min: 0
  },

  dailyRentalRate: {
    type: Number,
    required: true,
    min: 0
  }
});

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    trim: true,
    minlength: 2,
    maxlength: 100
  },

  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
    trim: true,
    maxlength: 255
  },

  password: {
    type: String,
    required: true
  },

  isAdmin: {
    type: Boolean,
    default: false
  }
});

userSchema.methods.generateAuthToken = function () {
  return jwt.sign(
    {
      _id: this._id.toString(),
      isAdmin: this.isAdmin
    },
    config.get('security.jwtSecret'),
    {
      expiresIn: '7d'
    }
  );
};

const rentalSchema = new mongoose.Schema({
  customer: {
    _id: {
      type: mongoose.Schema.Types.ObjectId,
      required: true
    },

    name: {
      type: String,
      required: true
    },

    phone: {
      type: String,
      required: true
    },

    isGold: {
      type: Boolean,
      required: true
    }
  },

  movie: {
    _id: {
      type: mongoose.Schema.Types.ObjectId,
      required: true
    },

    title: {
      type: String,
      required: true
    },

    dailyRentalRate: {
      type: Number,
      required: true,
      min: 0
    }
  },

  dateOut: {
    type: Date,
    required: true,
    default: Date.now
  },

  dateReturned: {
    type: Date,
    default: null
  },

  rentalFee: {
    type: Number,
    default: null,
    min: 0
  }
});

rentalSchema.index({
  'customer._id': 1,
  'movie._id': 1,
  dateReturned: 1
});

const Genre = mongoose.model('Genre', genreSchema);
const Customer = mongoose.model('Customer', customerSchema);
const Movie = mongoose.model('Movie', movieSchema);
const User = mongoose.model('User', userSchema);
const Rental = mongoose.model('Rental', rentalSchema);

module.exports = {
  Genre: Genre,
  Customer: Customer,
  Movie: Movie,
  User: User,
  Rental: Rental
};