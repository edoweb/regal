function Search(step) {
	this.host = "localhost";
	this.step = step;
	this.from = 0;
	this.to = this.step;
	this.buildIndexSelection();
	this.indices;
}
Search.prototype.buildIndexSelection = function(parameter) {

	var request = {
		type : "GET",
		url : "http://localhost/search/_status",

		processData : false,
		dataType : "json",
		context : this,
		success : function(data, statusText, xhr) {

			var obj = jQuery.parseJSON(JSON.stringify(data, null, '  '));
			this.indices = obj.indices;
			var i = 0;
			for (index in obj.indices) {

				if (i % 3 == 0) {
					$("#searchform").append("<br/>");
				}
				i++;
				$("#searchform").append(
						"<input	type=\"checkbox\" id=\"" + index
								+ "\" value=\"" + index + "\">" + index
								+ "</input>");
			}
		}
	};
	jQuery.ajax(request);
}
Search.prototype.alert = function(parameter) {
	alert("Hello " + parameter);
}

Search.prototype.search = function() {
	this.from = 0;
	this.to = this.step;
	var myQuery = $("#queryForm").val();
	var searchterm = $("#searchterm").val();
	if (searchterm.match(/^[*]+$/)) {
		myQuery = "{\"from\" : \"" + this.from + "\", \"size\" : \""
				+ this.step + "\",\"query\" :{\"match_all\" :{}}}";
	} else {
		searchterm = searchterm.replace(/^[*]+/, "");
		searchterm = searchterm.replace(/[*]+$/, "");
		if (searchterm.length > 1) {
			myQuery = "{\"from\" : \"" + this.from + "\", \"size\" : \""
					+ this.step
					+ "\",\"query\" :{\"query_string\" :{\"query\" :\"*"
					+ searchterm.toLowerCase()
					+ "*\",\"analyze_wildcard\":\"true\"}}}";
		}
	}
	$("#queryForm").empty().append(myQuery);
	$("#queryDiv").empty().append("<h3>Query</h3>", myQuery);
	if (myQuery) {
		this.request(myQuery, searchterm);
	}

}

Search.prototype.searchNext = function(from, to) {
	var myQuery = $("#queryForm").val();
	var searchterm = $("#searchterm").val();

	if (searchterm.match(/^[*]+$/)) {
		myQuery = "{\"from\" : \"" + from + "\", \"size\" : \"" + this.step
				+ "\",\"query\" :{\"match_all\" :{}}}";
	} else {
		searchterm = searchterm.replace(/^[*]+/, "");
		searchterm = searchterm.replace(/[*]+$/, "");
		if (searchterm.length > 1) {
			myQuery = "{\"from\" : \"" + from + "\", \"size\" : \"" + this.step
					+ "\",\"query\" :{\"query_string\" :{\"query\" :\"*"
					+ searchterm.toLowerCase()
					+ "*\",\"analyze_wildcard\":\"true\"}}}";
		}
	}
	$("#queryForm").empty().append(myQuery);
	$("#queryDiv").empty().append("<h3>Query</h3>", myQuery);
	if (myQuery) {
		this.request(myQuery, searchterm);
	}

}

Search.prototype.htmlEncode = function(value) {
	return $('<div/>').text(value).html();
}

Search.prototype.htmlDecode = function(value) {
	return $('<div/>').html(value).text();
}

Search.prototype.request = function(myQuery, searchterm) {

	var indexStr = "";

	for (index in this.indices) {
		if ($("#" + index).attr("checked")) {
			indexStr = indexStr + "," + index
		}
	}

	indexStr = indexStr + "/_search"
	indexStr = indexStr.substring(1);

	var request = {
		type : "POST",
		url : "http://localhost/search/" + indexStr,
		data : myQuery,
		processData : false,
		dataType : "json",
		context : this,
		success : function(data, statusText, xhr) {

			var obj = jQuery.parseJSON(JSON.stringify(data, null, '  '));
			var totalHits = obj.hits.total;
			$("#hitsDiv").empty().append("<b>Treffer: </b>", totalHits);
			$("#hitsDiv").append(
					"<div>" + search.from + " - " + search.to + "</div>");

			if (totalHits == 0) {
				// $("#debugDiv").empty().append(JSON.stringify(data, null, '
				// '));
				$("#outputDiv").empty();
				return;
			}

			// $("#debugDiv").empty().append("<h3>Debug</h3>",
			// obj.hits.hits[0]._source.pid);
			// $("#debugDiv").append(JSON.stringify(data, null, ' '));
			$("#outputDiv").empty();// .append("<h3>Output</h3>");
			$("#outputDiv").append(
					"<ol id=\"resultList\" start=\"" + search.from + "\"> ");
			var hit;
			var index = 0;
			for (hit in obj.hits.hits) {
				index++;
				var source = obj.hits.hits[hit]._source;
				var id = source.pid;
				var title = source.title;
				var creator = source.creator;
				var year = source.year;
				var objectUrl = source.apiUrl;
				if (!objectUrl) {
					objectUrl = source.uri;
				}
				var thumbnailUrl = source.thumbnail;
				var lobidUrl = source.lobidUrl;
				var alephId = source.alephid;
				var type = source.type;
				var doi = source.doi;
				var ddc = source.ddc;
				var hasPart = source.hasPart;

				if (title) {
					$("#resultList").append(
							"<li id=\"" + id + "\"><a href=\"" + objectUrl
									+ "\">" + this.htmlEncode(title)
									+ "</a><p class=\"metadata\" id=\"" + index
									+ "\"></p></li>");

				} else {
					$("#resultList").append(
							"<li id=\"" + id + "\"><a href=\"" + objectUrl
									+ "\">" + id
									+ "</a><p class=\"metadata\" id=\"" + index
									+ "\"></p></li>");
				}
				$("#resultList").highlight(searchterm);

				// $("#"+id).append("<ul id=\"ul"+id+"\"></ul>")

				if (creator) {
					$("#" + index).append(this.htmlEncode(creator) + " : ");
				} else {
					$("#" + index).append("unknown : ");
				}

				if (year) {
					$("#" + index).append(this.htmlEncode(year) + " : ");
				}

				if (type) {
					$("#" + index).append(this.htmlEncode(type) + " : ");
				}
				if (ddc) {
					$("#" + index).append(this.htmlEncode(ddc) + ":");
				}
				// $("#"+index).append("<p/>");
				if (id) {
					$("#" + index).append(this.htmlEncode(id) + " : ");
				}
				if (alephId) {
					$("#" + index).append(this.htmlEncode(alephId) + " : ");
				}
				if (doi) {
					$("#" + index).append(this.htmlEncode(doi));
				}
				/*
				 * if(hasPart) { $("#"+index).append(this.htmlEncode(hasPart)); }
				 */
				$("#" + index).highlight(searchterm);

			}

			$("#outputDiv").append("</ol>");

			/*
			 * WTF: search.XXXX heißt, dass das Ding im Kontext der aufrufenden
			 * Instanz läuft..Nicht schön: reiner Zufall, dass ich den Namen der
			 * Variable search kenn.
			 * 
			 * 
			 */

			if (this.from > 0) {
				$("#outputDiv")
						.append(
								"<input value=\"prev\" class=\"prev\" type=\"button\">");
				$("#hitsDiv")
						.append(
								"<input value=\"prev\" class=\"prev\" type=\"button\">");
				$(".prev").click(function() {
					search.from = search.from - search.step;
					search.to = search.to - search.step;
					search.searchNext(search.from, search.step);

				});
			}
			s = 0;
			if (s + this.step < totalHits) {
				$("#hitsDiv")
						.append(
								"<select class=\"allSteps\" name=\"allStepsN\" size=\"1\">");

				for (; s + this.step < totalHits; s += this.step) {

					$('.allSteps').append("<option>" + s + "</option>");

				}
				$('.allSteps').append("<option>" + s + "</option>");
				$(".allSteps").val(this.from);
				$(".allSteps").change(function() {
					// * 1 makes it a number
					val = $(".allSteps").val() * 1;
					search.from = val;
					search.to = val + search.step;
					search.searchNext(search.from, search.step);

				});
			}
			if (totalHits > this.step && (this.from + this.step) < totalHits) {
				$("#outputDiv")
						.append(
								"<input value=\"next\" class=\"next\" type=\"button\">");
				$("#hitsDiv")
						.append(
								"<input value=\"next\" class=\"next\" type=\"button\">");
				$(".next").click(function() {

					search.from = search.from + search.step;
					search.to = search.to + search.step;
					search.searchNext(search.from, search.step);

				});
			}

		},
		error : function(xhr, message, error) {
			console.log(message);
			console.log(error);
		}
	};
	jQuery.ajax(request);
}

Search.prototype.callback = function(data) {
}
