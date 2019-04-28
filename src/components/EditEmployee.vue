<template>
	<v-container class="title font-weight-medium"
		>employee id: {{ employeeData.employeeID }}</v-container
	>
</template>

<script>
export default {
	name: "EditEmployee",
	data() {
		const employeeID = this.$route.params.employeeID;
		return {
			employeeData: {
				employeeID
			}
		};
	}
};
</script>
