module.exports = {
	lintOnSave: false,
	transpileDependencies: [/node_modules[/\\\\]vuetify[/\\\\]/],
	pluginOptions: {
		electronBuilder: {
			builderOptions: {
				appId: "com.electron.CMSC447",
				productName: "Nurse Roster Builder",
				directories: {
					buildResources: "build",
					output: "dist_electron"
				}
			}
		}
	}
};
