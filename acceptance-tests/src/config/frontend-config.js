const environment = require( "./environment" );

module.exports = {
  url: environment.getProperty("FRONTEND_URL", "http://localhost:4200")
};