var exec = require("cordova/exec");

exports.init = function (arg0, success, error) {
  exec(success, error, "zebraRfid", "init", [arg0]);
};

exports.checkConnect = function (arg0, success, error) {
  exec(success, error, "zebraRfid", "check_connect", [arg0]);
};

exports.connect = function (arg0, arg1, success, error) {
  exec(success, error, "zebraRfid", "connect", [arg0, arg1]);
};

exports.disconnect = function (arg0, success, error) {
  exec(success, error, "zebraRfid", "disconnect", [arg0]);
};

/*exports.change_mode = function (arg0, success, error) {
    exec(success, error, 'zebraRfid', 'change_mode', [arg0]);
};*/

exports.change_power = function (arg0, success, error) {
  exec(success, error, "zebraRfid", "change_power", [arg0]);
};

exports.write_tag = function (arg0, arg1, arg2, arg3, success, error) {
  exec(success, error, "zebraRfid", "write_tag", [arg0, arg1, arg2, arg3]);
};

exports.lock_tag = function (arg0, success, error) {
  exec(success, error, "zebraRfid", "lock_tag", [arg0]);
};

// exports.start_read = function (success, error) {
//     exec(success, error, 'zebraRfid', 'start_read', []);
// };

// exports.stop_read = function (success, error) {
//     exec(success, error, 'zebraRfid', 'stop_read', []);
// };
