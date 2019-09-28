const puppeteer = require( "puppeteer" );
const puppeteerConfig = require( "./puppeteer-config" );

module.exports = {
  launch : function() {
    return puppeteer.launch(puppeteerConfig.launchConfig);
  }
}