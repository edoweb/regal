function EasyEllinetEditor() {

}

EasyEllinetEditor.prototype.alert = function(parameter) {
	alert("Hello " + parameter);
}
EasyEllinetEditor.prototype.catchEvents = function() {
	$("#go").click(function() {
		elli.read($("#pid").val());
	});
}
insertProperty = function(parent, id, key, field) {
	keyId = id + "key";
	valId = id + "val";
	var butIdAdd = id + "butAdd";
	butIdDel = id + "butDel";

	$("#" + id).append('<input class="keyInput" id="' + keyId + '" type="text" name="' + key + '" />');
	$("#" + id).append('<input class="valInput" id="' + valId + '" type="text" name="' + field + '" />');
	$("#" + keyId).val(key);
	$("#" + valId).val(field);
	if(key.match("^info:hbz")) 
	{
		$("#" + keyId).attr("readonly", "readonly");
		$("#" + valId).attr("readonly", "readonly");
		$("#" + keyId).css("display","none");
		$("#" + valId).css("display","none");
		return;
	}
	$("#" + id).append('<input class="addField" id="' + butIdAdd + '" type="button" />  ');
	$("#" + id).append('<input class="delField" id="' + butIdDel + '" type="button" />');
	
	$("#" + butIdAdd).val("+");
	$("#" + butIdDel).val("-");

	$("#" + butIdAdd).click(function() {
		panelid = id + "_";

		$("#" + parent).append('<p id="' + panelid + '"></p>');
		insertProperty(parent, panelid, key, field);

		$(this).remove();
		$(this).prev().remove();
	});

	$("#" + butIdDel).click(function() {
		removePropertyPanel(id);
	});
}
insertPropertyPanel = function(id, key, field) {
	panelid = id + "_";

	$("#" + id).append('<p id="' + panelid + '"></p>');
	insertProperty(id, panelid, key, field);

}
removePropertyPanel = function(id) {
	$("#" + id).last().remove();

}
save = function() {
	var URL = "https://orthos.hbz-nrw.de:8443/EdowebEditor/" + pid;

	var data = {};
	data.About = pid;

	var keys = [];
	$(".keyInput").each(function(i) {
		keys.push($(this).val())
	});
	var vals = [];
	$(".valInput").each(function(i) {
		vals.push($(this).val())
	});
	for(var i = 0; i < keys.length; i++) {
		var key = keys[i];
		if( key in data == false) {
			data[key] = [];
			data
		}

		data[key].push(vals[i]);

	}
	console.log(JSON.stringify(data));

	/*$(".formContainer").empty();
	$(".buttonsContainer").empty();*/
	var request = {
		url : URL,
		type : "POST",
		data : JSON.stringify(data),
		processData : false,
		context : this,
		success : function(data,statusText, xhr)  {
			alert("Data succesfully saved!");
		},
		error : function(xhr, message, error) {
			alert(message + " " + error + " " + xhr);
		}
	};

	$.ajax(request);

}

EasyEllinetEditor.prototype.read = function(parameter) {

	var URL = "https://orthos.hbz-nrw.de:8443/EdowebEditor/" + parameter;

	$(".formContainer").empty();
	$(".buttonsContainer").empty();
	var request = {
		url : URL,
		success : function(data) {
			id = 0;
			jQuery.each(data, function(key, val) {
				if(key == "About") {

				} else {

					jQuery.each(val, function() {
						$(".formContainer").append('<p id="' + id + '"></p>');
						insertPropertyPanel(id, key, this);
						id++;
					});
				}
			});

			$(".buttonsContainer").append('<input id="cancelButton" type="button" value="cancel" /> ');
			$(".buttonsContainer").append('<input id="submitButton" type="button" value="save" /><p/>');
			$("#submitButton").click(function() {
				save();
			});
			$("#cancelButton").click(function() {
				elli.read($("#pid").val());
			});
		},
		dataType : "json",
		error : function(xhr, message, error) {
			alert(message + " " + error + " " + xhr);
		}
	};

	$.ajax(request);

}