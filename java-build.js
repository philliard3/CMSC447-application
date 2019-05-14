const exec = require("child_process").exec,
	os = require("os");
// eslint-disable-next-line
function puts(error, stdout, stderr) {
	// eslint-disable-next-line
	console.log(stdout);
}

if (os.type() === "Linux" || os.type() === "Darwin") {
	// build for Unix
	exec("sh java-build.sh", puts);
} else if (os.type() === "Windows_NT") {
	// build for Windows
	exec("java-build.bat", puts);
} else {
	throw new Error("Unsupported OS found: " + os.type());
}
