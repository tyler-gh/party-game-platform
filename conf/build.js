(function () {

    "use strict";

    var fs = require("fs"),
        jsx = require('react-tools');

    function throwIfErr(e) {
        if (e) throw e;
    }

    var inputFiles = JSON.parse(process.argv[2]);
    var outputFiles = JSON.parse(process.argv[3]);
    if (inputFiles.length !== outputFiles.length) {
        throwIfErr("Input files length must match output files length")
    }
    for (var i = 0; i < inputFiles.length; i++) {
        var inputFile = inputFiles[i];

        var fileContents = fs.readFileSync(inputFile, "utf8");

        var compileResult = jsx.transform(fileContents, {harmony: true});
        var result = compileResult.code;
        if (result === undefined) {
            result = compileResult;
        }

        fs.writeFile(outputFiles[i], result, "utf8", function (e) {
            throwIfErr(e);
        });

    }
})();

