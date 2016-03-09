(function () {

    "use strict";

    var endsWith = function (s1, s2) {
        return s1.length >= s2.length && s1.substr(s1.length - s2.length) == s2;
    };

    var fs = require("fs"),
        jsx = require('react-tools');

    function throwIfErr(e) {
        if (e) throw e;
    }

    JSON.parse(process.argv[2]).forEach(function (inputFile) {
        var result, outputFile;
        var fileContents = fs.readFileSync(inputFile, "utf8");

        var jsx = require('react-tools');

        var compileResult = jsx.transform(fileContents, {harmony: true});
        result = compileResult.code;
        if (result === undefined) {
            result = compileResult;
        }
        outputFile = inputFile.replace(".jsx", ".js");

        fs.writeFile(outputFile, result, "utf8", function (e) {
            throwIfErr(e);
        });

    });
})();

