var config = {
    id: //YOUR_FOURSQUARE_ID,
    secret: //YOUR_FOURSQUARE_SECRET,
    apiUrl: 'https://api.foursquare.com/'
};
var map;
var markers;
var lat;
var lng;
var flagMirino = false;
var flagInfo = false;
var flagFavorites = false;

function initialize(){
  $( "#map_canvas" ).height($(window).height() - 50);
  markers = new L.FeatureGroup();
  map = new L.Map('map_canvas').addLayer(markers);
  var mapboxUrl = 'https://a.tiles.mapbox.com/v3/thundernote.map-s9wezoke.jsonp';
  wax.tilejson(mapboxUrl, function(tilejson) {
	  tilejson.tiles[0] = "https://a.tiles.mapbox.com/v3/thundernote.map-s9wezoke/{z}/{x}/{y}.png" //fixing mixed content
      map.addLayer(new wax.leaf.connector(tilejson));
  });
    
  localizza();
  
  var input = document.getElementById('searchTextField');
  var autocomplete = new google.maps.places.Autocomplete(input); 
  google.maps.event.addListener(autocomplete, 'place_changed', function() {
        var place = autocomplete.getPlace();
        lat = place.geometry.location.lat();
        lng = place.geometry.location.lng();
        if(flagInfo){
        	$( "#info" ).hide('blind');
        	flagInfo = false;
        }
        if(flagFavorites){
        	$( "#savedPlaces" ).hide('blind');
        	flagFavorites = false;
        }
        
        explore(null);
        if(!flagMirino){
            $( "#mirino" ).removeAttr( "style" ).show('scale');
            flagMirino = true;
         }
  });  
}

function explore(section){
	markers.clearLayers();
    /* Query foursquare API for venue recommendations near the current location. */
    $.getJSON(config.apiUrl + 'v2/venues/explore?ll=' + lat + ',' + lng 
              + '&client_id=' + config.id + '&client_secret=' + config.secret 
              + '&section=' + section + '&v=20150426' + '&callback=?', {}, function(data) {
      venues = data['response']['groups'][0]['items'];
      /* Place marker for each venue. */
      for (var i = 0; i < venues.length; i++) {
    	var venue = venues[i];
  
        /* Get marker's location */
        var latLng = new L.LatLng(
          venue['venue']['location']['lat'],
          venue['venue']['location']['lng']
        );
        /* Build icon for each icon */
        var leafletIcon = L.Icon.extend({
        	options: {
              iconUrl: venue['venue']['categories'][0]['icon']['prefix'] + 'bg_44' + venue['venue']['categories'][0]['icon']['suffix'],
              shadowUrl: null,
              iconSize: new L.Point(32,32),
              iconAnchor: new L.Point(16, 41),
              popupAnchor: new L.Point(0, -51)
            }
        });
        var icon = new leafletIcon();
        var marker = new L.Marker(latLng, {icon: icon})
          .bindPopup(venue['venue']['name'], { closeButton: false })
          .on('mouseover', function(e) { this.openPopup(); })
          .on('mouseout', function(e) { this.closePopup(); })
          .on('click', function(e){ displayInfo(this); },venue);
        markers.addLayer(marker);
      }
      //compute the optimal map's zoom level depending on current markers
      map.fitBounds(markers.getBounds());
    });
}

function localizza(){
	/* HTML 5 geolocation. */
	if(navigator.geolocation){
		navigator.geolocation.getCurrentPosition(function(data) {
			lat = data['coords']['latitude'];
			lng = data['coords']['longitude'];

			explore(null);    
		});
	}else{
		alert('Il tuo browser non supporta la geolocalizzazione!');
		lat = 41.893;
		lng = 12.491;
		
		explore(null);
	}
}

function displayInfo(venue){
	flagInfo = true;
	var location = venue['venue']['location'];
	var address = "" + location['address'] + ", " + location['city'] + ", "  + location['cc'];
	var url = venue['venue']['url'];
	var photos, photoUrl;

	$.getJSON(config.apiUrl + 'v2/venues/'+ venue['venue']['id'] + '/photos?group=venue' + '&v=20150426'
			+ '&client_id=' + config.id + '&client_secret=' + config.secret + '&callback=?', {}, function(data) {

				if(data['response']['photos']['count'] != 0){
					
				   photos = data['response']['photos']['items'];
				   photoUrl = photos[0]['prefix'] + "100x100" + photos[0]['suffix'];
				   
				}else{
					photoUrl = "Not Found";
				}
				
				$( "#info" ).empty();
				$( "<img src='images/x.png'/>" )
				.addClass("closeGif").click(function() { $( "#info" ).hide('blind'); flagInfo = false; }).appendTo("#info");
				$( "#info" ).append("<table><tr><td><h1>" + venue['venue']['name'] + "</h1></td></tr></table>")
				.append("<div id='images'></div>");
				if(photoUrl != "Not Found"){
					buildGallery(photos);
					$( "#images" ).append("<img src='" + photoUrl + "' alt='No Photo'/>").append("<a id='start' href='#'> View all photos</a>");
					$('#gallery').ppGallery({
						   showHiddenGalleryButton: '#start', //hide the gallery and assign a button
					});
				}
				$("<img src='images/savePlace.png'/>").addClass("saveButton")
				.click(function() { addPlace(venue['venue']['name'], address, venue['venue']['id']); }).appendTo("#images");
				$( "#info" ).append("<h3><font color='blue'>Address: </font>" + location['address'] + ", " + location['city'] + ", "  + location['cc'] + "</h3>")
				.append("<h3><font color='blue'>Phone: </font>" + venue['venue']['contact']['formattedPhone'] + "</h3>")
				.append("<h3><font color='blue'>Type: </font>" + venue['venue']['categories'][0]['name'] + "</h3>")
				.append("<h3><font color='blue'>Checkins: </font>" + venue['venue']['stats']['checkinsCount'] + "</h3>")
				.addClass('info')
				.show('blind')
				.draggable();

				if(url != undefined){
					$( "#info" ).append("<h3><font color='blue'>Web Site: </font><a href='" + url + "' target='_blank'>" + url + "</a>");
				}
			});
}

function buildGallery(photos){
	$( "#gallery" ).empty();
	$( "#container" ).remove();
	//$( "#savedPlaces" ).empty();
	//$( "#savedPlaces" ).append(document.createTextNode($('body').html())).addClass("savedPlaces").show("blind").draggable();
	for (var i = 0; i < photos.length; i++) {
		//$( "#gallery" ).append("<li><a href='"+ photos[i]['prefix'] + photos[i]['width'] + "x" + photos[i]['height'] + photos[i]['suffix'] + "'><img src='" + photos[i]['prefix'] + "110x61" + photos[i]['suffix'] + "'></a></li>");
		$( "#gallery" ).append("<li><a href='"+ photos[i]['prefix'] + "750x500" + photos[i]['suffix'] + "'><img src='" + photos[i]['prefix'] + "110x61" + photos[i]['suffix'] + "'></a></li>");
	}
}

function addPlace(name, address, venue){
	$.post("/placeServlet", {placeName:name, placeAddress:address, placeVenueId:venue}, function(data) {
		$( "#dialog-message" ).empty().append("<p>" + data + "</p>").dialog({
			modal: true,
			buttons: {
				Ok: function() {
					$( this ).dialog( "close" );
				}
			}
		});
		getPlaces();
	});
}

function getPlaces(){
	var r = new Date().getTime();
	$.get("/getPlacesServlet" + "?r=" + r, function(data) {
		if(data['state'] == 'ok'){
			flagFavorites = true;
		    var names = data['names'];
		    var addresses = data['addresses'];
		    var venuesId = data['venuesId'];
		    $( "#savedPlaces" ).empty();
		    $( "<img src='images/x.png'/>" )
		    .addClass("closeGif").click(function() { $( "#savedPlaces" ).hide('blind'); flagFavorites = false; }).appendTo("#savedPlaces");
		    $( "#savedPlaces" ).append("<h1> Your Favorite Places: </h1>")
		    .append("<table>");
		    for(var i = 0; i < names.length; i++){
			    $( "#savedPlaces" ).append("<tr><td id='name" + i + "'></td><td id='delete" + i + "'>&nbsp&nbsp</td></tr>");
			    $( "<h3 style='cursor:pointer;'>" + names[i] + " (" + addresses[i] + ")</h3>" )
			    .click({param1: venuesId[i]}, displayPlace)
			    .hover(function() {
		            $(this).css('background-color', 'LightGrey');
		        },
		        function() {
		            $(this).css('background-color', 'white');
		        }).appendTo("#name" + i);
			    $("&nbsp&nbsp<img style='cursor:pointer;' src='images/redX.gif' title='Delete Place'/>")
			    .click({param1: venuesId[i]}, deletePlace).appendTo("#delete" + i);
		    }
		    $("img[title]").tooltip({ 
			    track: true,
			    delay: 0,
			    showURL: false,
			    showBody: " - "
		    });
		    $( "#savedPlaces" ).append("</table>").addClass("savedPlaces").show("blind").draggable();
		}else{
			if(flagFavorites)
				$( "#savedPlaces" ).hide("blind");
			flagFavorites = false;
			$( "#dialog-message" ).empty().append("<p> No saved place found. Choose your best places and save them! </p>").dialog({
				modal: true,
				buttons: {
					Ok: function() {
						$( this ).dialog( "close" );
					}
				}
			});
		}
	});
}

function displayPlace(event){	
	var venueId = event.data.param1;
	$( "#savedPlaces" ).hide("blind");
	flagFavorites = false;
	$.getJSON(config.apiUrl + 'v2/venues/'+ venueId 
			+ '?v=20150426&client_id=' + config.id + '&client_secret=' + config.secret + '&callback=?', {}, function(data) {
				var venue = data['response'];

				var latLng = new L.LatLng(
						venue['venue']['location']['lat'],
						venue['venue']['location']['lng']
				);

				markers.clearLayers();
				map.setView(latLng, 15);
				displayInfo(venue);

				var leafletIcon = L.Icon.extend({
					options: {
					   iconUrl: venue['venue']['categories'][0]['icon']['prefix'] + 'bg_44' + venue['venue']['categories'][0]['icon']['suffix'],
					   shadowUrl: null,
					   iconSize: new L.Point(32,32),
					   iconAnchor: new L.Point(16, 41),
					   popupAnchor: new L.Point(0, -51)
					}
				});
				var icon = new leafletIcon();
				var marker = new L.Marker(latLng, {icon: icon})
				.bindPopup(venue['venue']['name'], { closeButton: false })
				.on('mouseover', function(e) { this.openPopup(); })
				.on('mouseout', function(e) { this.closePopup(); });
				markers.addLayer(marker);
				
				if(!flagMirino){
		            $( "#mirino" ).removeAttr( "style" ).show('scale');
		            flagMirino = true;
		         }
			         
			});
}

function deletePlace(event){
	var venueId = event.data.param1;
	$.post("/deletePlaceServlet", {placeVenueId:venueId}, function(data) {
		$( "#dialog-message" ).empty().append("<p>" + data + "</p>").dialog({
			modal: true,
			buttons: {
				Ok: function() {
					$( this ).dialog( "close" );
				}
			}
		});
		getPlaces();
	});
}

function mirinoOFF(){
	$( "#mirino" ).hide('explode', 1000);
	$( "#searchTextField" ).val('');
	flagMirino = false;
	if(flagInfo){
		$( "#info" ).hide('blind');
		flagInfo = false;
	}
	if(flagFavorites){
		$( "#savedPlaces" ).hide('blind');
		flagFavorites = false;
	}
}

google.maps.event.addDomListener(window, 'load', initialize);

// JQuery
$(document).ready(function() {
	$("img[title]").tooltip({ 
		track: true,
		delay: 0,
		showURL: false,
		showBody: " - "
	});

	$("#jMenu").jMenu({
		ulWidth : 'auto',
		effects : {
			effectSpeedOpen : 300,
			effectTypeClose : 'slide'
		},
		animatedText : false
	});
});