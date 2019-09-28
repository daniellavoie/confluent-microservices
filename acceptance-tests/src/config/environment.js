module.exports = {
  getProperty: function(propertyName, defaultValue) {
    var value = process.env[propertyName];
    if(value == undefined) {
      return defaultValue;
    }

    return value;
  }
}