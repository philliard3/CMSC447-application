<script>
import { VBtn } from "vuetify/lib";

export default {
	name: "jsx",
	data() {
		return {
			text: "go home"
		};
	},
	/*
    'h' is an alias for the Vue.js createElement function.
    Because h is not called explicitly in the JSX code, an eslint ignore comment is used to avoid warnings on linting/fixing
  */
	// eslint-disable-next-line no-unused-vars
	render(h) {
		return (
			<VBtn color="primary" to="/">
				{this.text}
			</VBtn>
		);
	}
};
</script>
