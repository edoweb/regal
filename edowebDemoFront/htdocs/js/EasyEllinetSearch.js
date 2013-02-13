function EasyEllinetSearch(step) {
	this.host = "localhost";
	this.step = step;
	this.from = 0;
	this.to = this.step;
}

EasyEllinetSearch.prototype.alert = function(parameter) {
	alert("Hello " + parameter);
}

EasyEllinetSearch.prototype.search = function() {
	this.from = 0;
	this.to = this.step;
	var myQuery = $("#queryForm").val();
	var searchterm = $("#searchterm").val();
	if(searchterm.match(/^[*]+$/)) {
		myQuery = "{\"from\" : \"" + this.from + "\", \"size\" : \"" + this.step + "\",\"query\" :{\"match_all\" :{}}}";
	}
	else {
		searchterm = searchterm.replace(/^[*]+/,"");
		searchterm = searchterm.replace(/[*]+$/,"");
		if(searchterm.length>1) {
			myQuery = "{\"from\" : \"" + this.from + "\", \"size\" : \"" + this.step + "\",\"query\" :{\"query_string\" :{\"query\" :\"*" + searchterm.toLowerCase() + "*\",\"analyze_wildcard\":\"true\"}}}";
		}
	}
	$("#queryForm").empty().append(myQuery);
	$("#queryDiv").empty().append("<h3>Query</h3>", myQuery);
	if(myQuery) {
		this.request(myQuery,searchterm);
	}

}

EasyEllinetSearch.prototype.searchNext = function(from, to) {
	var myQuery = $("#queryForm").val();
	var searchterm = $("#searchterm").val();

	if(searchterm.match(/^[*]+$/)) {
		myQuery = "{\"from\" : \"" + from + "\", \"size\" : \"" + this.step + "\",\"query\" :{\"match_all\" :{}}}";
	}
	else {
		searchterm = searchterm.replace(/^[*]+/,"");
		searchterm = searchterm.replace(/[*]+$/,"");
		if(searchterm.length>1) {
			myQuery = "{\"from\" : \"" + from + "\", \"size\" : \"" + this.step + "\",\"query\" :{\"query_string\" :{\"query\" :\"*" + searchterm.toLowerCase() + "*\",\"analyze_wildcard\":\"true\"}}}";
		}
	}
	$("#queryForm").empty().append(myQuery);
	$("#queryDiv").empty().append("<h3>Query</h3>", myQuery);
	if(myQuery) {
		this.request(myQuery,searchterm);
	}

}

EasyEllinetSearch.prototype.htmlEncode = function(value) {
	return $('<div/>').text(value).html();
}

EasyEllinetSearch.prototype.htmlDecode = function(value) {
	return $('<div/>').html(value).text();
}

EasyEllinetSearch.prototype.request = function(myQuery,searchterm) {

	var request = {
		type : "POST",
		url : "http://localhost/search/_search",
		data : myQuery,
		processData : false,
		dataType : "json",
		context : this,
		success : function(data, statusText, xhr) {

			var obj = jQuery.parseJSON(JSON.stringify(data, null, '  '));
			var totalHits = obj.hits.total;
			$("#hitsDiv").empty().append("<b>Treffer: </b>", totalHits);
			$("#hitsDiv").append("<div>" + elli.from + " - " + elli.to + "</div>");

			if(totalHits == 0) {
			    //$("#debugDiv").empty().append(JSON.stringify(data, null, '  '));
			    $("#outputDiv").empty();
				return;
			}

			//			$("#debugDiv").empty().append("<h3>Debug</h3>", obj.hits.hits[0]._source.pid);
			//$("#debugDiv").append(JSON.stringify(data, null, '  '));
			$("#outputDiv").empty();//.append("<h3>Output</h3>");
			$("#outputDiv").append("<ol id=\"resultList\" start=\"" + elli.from + "\"> ");
			var hit;
			var index=0;
			for(hit in obj.hits.hits) {
				index++;
				var source = obj.hits.hits[hit]._source.view;
				var id = source.pid;
				var title = source.title;
				var creator = source.creator;
				var year = source.year;
				var objectUrl = source.uri;
				var thumbnailUrl = source.thumbnail;
				var lobidUrl = source.lobidUrl;
				var alephId = source.alephid;
				var type = source.type;
				var doi = source.doi;
				var ddc = source.ddc;
				var hasPart = source.hasPart;

				if(title) {
					$("#resultList").append("<li id=\"" + id + "\"><a href=\"" + objectUrl + "\">" + this.htmlEncode(title) + "</a><p class=\"metadata\" id=\""+index+"\"></p></li>");

				} else {
					$("#resultList").append("<li id=\"" + id + "\"><a href=\"" + objectUrl + "\">" + id + "</a><p class=\"metadata\" id=\""+index+"\"></p></li>");
				}
				$("#resultList").highlight(searchterm);
				
				//$("#"+id).append("<ul id=\"ul"+id+"\"></ul>")
				
				if(creator)
				{
					$("#"+index).append(this.htmlEncode(creator)+" : "); 
				}
				else
				{
					$("#"+index).append("unknown : "); 
				}
				
				if(year)
				{
					$("#"+index).append(this.htmlEncode(year)+" : ");
				}
				
				if(type)
				{
					$("#"+index).append(this.htmlEncode(type)+" : ");
				}
				if(ddc)
				{
					$("#"+index).append(this.htmlEncode(ddc)+":");
				}
				//$("#"+index).append("<p/>");
				if(id)
				{
					$("#"+index).append(this.htmlEncode(id)+" : ");
				}
				if(alephId)
				{
					$("#"+index).append(this.htmlEncode(alephId)+" : ");
				}
				if(doi)
				{
					$("#"+index).append(this.htmlEncode(doi));
					}
				/*				if(hasPart)
				{
					$("#"+index).append(this.htmlEncode(hasPart));
					}*/
				$("#"+index).highlight(searchterm);
				
			}

			$("#outputDiv").append("</ol>");

			/*
			 * WTF: elli.XXXX heißt, dass das Ding im Kontext der aufrufenden Instanz läuft..Nicht schön: reiner Zufall, dass ich den Namen
			 * der Variable elli kenn.
			 *
			 *
			 */

			if(this.from > 0) {
				$("#outputDiv").append("<input value=\"prev\" class=\"prev\" type=\"button\">");
				$("#hitsDiv").append("<input value=\"prev\" class=\"prev\" type=\"button\">");
				$(".prev").click(function() {
					elli.from = elli.from - elli.step;
					elli.to = elli.to - elli.step;
					elli.searchNext(elli.from, elli.step);

				});
			}
			s = 0;
			if(s + this.step < totalHits) {
				$("#hitsDiv").append("<select class=\"allSteps\" name=\"allStepsN\" size=\"1\">");

				for(; s + this.step < totalHits; s += this.step) {

					$('.allSteps').append("<option>" + s + "</option>");

				}
				$('.allSteps').append("<option>" + s + "</option>");
				$(".allSteps").val(this.from);
				$(".allSteps").change(function() {
					// * 1 makes it a number
					val = $(".allSteps").val() * 1;
					elli.from = val;
					elli.to = val + elli.step;
					elli.searchNext(elli.from, elli.step);

				});
			}
			if(totalHits > this.step && (this.from + this.step) < totalHits) {
				$("#outputDiv").append("<input value=\"next\" class=\"next\" type=\"button\">");
				$("#hitsDiv").append("<input value=\"next\" class=\"next\" type=\"button\">");
				$(".next").click(function() {

					elli.from = elli.from + elli.step;
					elli.to = elli.to + elli.step;
					elli.searchNext(elli.from, elli.step);

				});
			}

		},
		error : function(xhr, message, error) {
			callback(message, xhr, error);
		}
	};
	jQuery.ajax(request);
}

EasyEllinetSearch.prototype.callback = function(data) {
}
