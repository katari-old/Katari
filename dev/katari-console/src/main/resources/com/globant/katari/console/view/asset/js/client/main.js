$(document).ready(function() {
	$(function() {
		$("#tabs").tabs();
		$("#textarea-container").resizable({ handles: 's', alsoResize: 'iframe' });

        $("#dialog").dialog({
			bgiframe: true,
			autoOpen: false,
			height: 400,
			width: 330,
			modal: true,
			buttons: {
				Submit: function() {
                    $("#captchaQuestion").val($("#dialogCaptchaQuestion").val());
                    $("#captchaAnswer").val($("#dialogCaptchaAnswer").val());
                    $("#title").val($("#dialogTitle").val());
                    $("#title").val($("#dialogTitle").val());
                    $("#author").val($("#dialogAuthor").val());
                    $("#tags").val($("#dialogTags").val());
                    $("#script").val(editor.getCode());
                    $("#publishform").submit();
				}
			}
		});
	});

    $("#publishButton").click(function(event) {
        var code = editor.getCode();
        // better trim() function than JQuery's
        if (code.replace(/^\s+|\s+$/g, '').length > 0) {
            $('#dialog').dialog('open');
            event.preventDefault();
        } else {
            alert("Please enter a script before publishing.");
            event.preventDefault();
        }
    });

    $("#executeButton").click(function(event) {
		$.ajax({
		   	type: "POST",
		    url: "execute.do",
		    data: { script: editor.getCode() },
			dataType: "json",
			
		    success: function(data) {
				$('#output').text("");
				$('#error').text("");
				
				if (data.output.length > 0) {
					$("#tabs").tabs('select', 0);
					$('#output').text(data.output).fadeIn();
				} else {
					$('#output').fadeOut();
				}

				if (data.error.length > 0) {
					if (data.error != "null") {
						$("#tabs").tabs('select', 1);
					}
					$('#error').text(data.error).fadeIn();
				} else {
					$('#error').fadeOut();
				}
		    },

			error: function (XMLHttpRequest, textStatus, errorThrown) {
				alert("Error interacting with the Groovy web console server: " + errorThrown);
			}

		});
    });

	$('#loadingDiv')
	    .hide()
	    .ajaxStart(function() {
	        $(this).show();
	    })
	    .ajaxStop(function() {
	        $(this).hide();
	    });
});
