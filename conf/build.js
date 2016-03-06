

(function () {

    "use strict";

    var fs = require("fs"),
        jsx =  require('react-tools');

    function throwIfErr(e) {
        if (e) throw e;
    }

    JSON.parse(process.argv[2]).forEach(function (inputFile) {
        fs.readFile(inputFile, "utf8", function (e, contents) {
            throwIfErr(e);

            var compileResult = jsx.transform(contents, {harmony:true});

            var js = compileResult.code;
            if (js === undefined) {
                js = compileResult;
            }

            fs.writeFile(inputFile.replace(".jsx", ".js"), js, "utf8", function (e) {
                throwIfErr(e);
            });

        });
    });
})();

