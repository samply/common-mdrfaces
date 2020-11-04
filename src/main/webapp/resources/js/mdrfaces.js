$(function() {
	/* the time picker */
	$('.clockpicker').clockpicker({
		placement : 'top',
		align : 'left',
		donetext : 'OK'
	});

	/* boolean indeterminate */
	$("input[data-indeterminate='true']").prop("indeterminate", true);

	/* for tooltip support */
	$('.hasTooltip').tooltip();

	/* date picker: activate also on icon click */
	$('.datepicker').find('input').datetimepicker({
		format: "yyyy-MM-DD"
	});

	$('.datepicker').find('.input-group-addon').click(function() {
		$(this).closest('.datepicker').find('input').focus();
	});

	/* date time picker */
	$('.datetimepicker').find('input').datetimepicker();

	$('.datetimepicker').find('.input-group-addon').click(function() {
		$(this).closest('.datetimepicker').find('input').focus();
	});

	/* catalogue filter */
	$(".select2Basic").select2();

});
