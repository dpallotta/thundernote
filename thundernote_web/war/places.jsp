<%@page import="it.psd.thundernote.server.LoginHelper"%>
<%@page import="it.psd.thundernote.server.domain.UserAccount"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>Thundernote - Places</title>
    
  <link rel="stylesheet" href="./leaflet/leaflet-0.4.4.css" />
  <!--[if lte IE 8]>
      <link rel="stylesheet" href="./leaflet/leaflet-0.4.4.ie.css" />
  <![endif]-->
  <link href="./jMenu.jquery.css" type="text/css" media="screen" rel="stylesheet"/>
  <link href="./jquery.tooltip.css" type="text/css" rel="stylesheet" />
  <link href="./jquery-ui-1.8.23.custom.css" type="text/css" rel="stylesheet" />
  <link href="ppgallery/css/ppgallery.css" rel="stylesheet" type="text/css" />
  
  <script src="./jquery-1.8.0.min.js" type="text/javascript"></script>
  <script src="./jquery-ui-1.8.23.min.js" type="text/javascript"></script>
  <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_GOOGLE_API_KEY&libraries=places"></script>
  <script src="./leaflet/leaflet-0.4.4.js"></script>
  <script src="./wax.leaf.min.js" type="text/javascript"></script>
  <script src="./jquery.tooltip.js" type="text/javascript"></script>
  <script src="./jMenu.jquery.js" type="text/javascript"></script>
  <script type="text/javascript" src="ppgallery/js/ppgallery.js"></script>
  <script src="./places.js" type="text/javascript"></script>
  <script type="text/javascript">
  
     <% UserAccount currentUser = LoginHelper.getLoggedInUser(session, null);
        String userName = "Login...";
        if(LoginHelper.isLoggedIn(request)){
        	userName = currentUser.getName();
        }
        else{ %>
        	
            window.location = "https://thunder-note.appspot.com/";
        
     <% } %>
  
  </script>
  
  <style type="text/css">
        html { height: 100%; }
        body { height: 100%; margin: 0; padding: 0; }
        /* Give our markers a background image */
        .leaflet-marker-icon {
          background: url(images/pin-blue-transparent.png);
          padding: 6px;
          padding-bottom: 17px;
          top: -6px;
          left: -6px;
          }
        .button {
                -moz-box-shadow:inset 0px 1px 0px 0px #bbdaf7;
                -webkit-box-shadow:inset 0px 1px 0px 0px #bbdaf7;
                box-shadow:inset 0px 1px 0px 0px #bbdaf7;
                background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #79bbff), color-stop(1, #378de5) );
                background:-moz-linear-gradient( center top, #79bbff 5%, #378de5 100% );
                filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#79bbff', endColorstr='#378de5');
                background-color:#79bbff;
                -moz-border-radius:6px;
                -webkit-border-radius:6px;
                border-radius:6px;
                border:1px solid #84bbf3;
                display:inline-block;
                color:#ffffff;
                font-family:arial;
                font-size:12px;
                font-weight:bold;
                padding:6px 18px;
                text-decoration:none;
                text-shadow:1px 1px 0px #528ecc;
        }.button:hover {
                background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #378de5), color-stop(1, #79bbff) );
                background:-moz-linear-gradient( center top, #378de5 5%, #79bbff 100% );
                filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#378de5', endColorstr='#79bbff');
                background-color:#378de5;
        }.button:active {
                position:relative;
                top:1px;
        }
        .header{
            height: 45px;
            border:1px solid black;
            filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='LightGrey', endColorstr='Silver'); /* per Internet Explorer */
            background: -webkit-gradient(linear, left top, left bottom, from(LightGrey), to(Grey)); /* per browser webkit come Safari */
            background: -moz-linear-gradient(top,  LightGrey,  Grey); /* per firefox 3.6+ */
        }
        .info{
          left: 1%;
          border: 3px solid #00A550;
          border-radius: 10px;
          position: absolute;
          top: 38%;
          z-index: 99;
          background-color: white;
          padding-left: 6px;
          padding-right: 6px;
          cursor: move;
        }
        .savedPlaces{
          left: 55%;
          border: 3px solid #00A550;
          border-radius: 10px;
          position: absolute;
          top: 20%;
          z-index: 99;
          background-color: white;
          padding-left: 6px;
          padding-right: 6px;
          cursor: move;
        }
        .closeGif{
          float: right;
          cursor: pointer;
        }
        .saveButton{
          float: right;
          cursor: pointer;
        }
</style>
</head>

<body>
  <div class='header'>
    <table style='float: left;'>
      <tr>
      <td><img src="images/logo.png" alt="Thundernote" style='background-color:white;'/></td>
      <td><input id="searchTextField" type="text" size="50" onclick='javascript: this.select();'></td>
      </tr>
    </table>
    <table style='float: right;' cellspacing='7px'>
      <tr>
      <td id='mirino' style='visibility: hidden;'><a href='javascript:localizza(); mirinoOFF();'>
              <img src="images/mirino.gif" alt="Localizzati" style='background-color:white; border-radius: 4px' title='Around you'/>
          </a>
      </td>
      <td><img onclick='getPlaces();' style='cursor:pointer;' src='images/favoritePlaces.png' title='Your Saved Places'/></td>
      <td><label><%= userName %></label></td>
      <td><a href="logout.jsp" class="button">Logout</a></td>
      </tr>
    </table>
  </div>
  <div style='width: 100%' id="map_canvas"></div>
  <div style="position: absolute; top: 60px; right: 7px; z-index: 99;">
        <ul id="jMenu">
          <li><a class="fNiv">Filter By</a>
            <ul>
              <li class="arrow"></li>
              <li><a href='javascript:explore(null)'>No Filters</a></li>
              <li><a href='javascript:explore("food")'>Food</a></li>
              <li><a href='javascript:explore("drinks")'>Drinks</a></li>
              <li><a href='javascript:explore("coffee")'>Coffee</a></li>
              <li><a href='javascript:explore("shops")'>Shops</a></li>
              <li><a href='javascript:explore("arts")'>Arts</a></li>
              <li><a href='javascript:explore("outdoors")'>Outdoors</a></li>
            </ul>
          </li>
        </ul>
   </div>
   <div id="info"></div>
   <div id="savedPlaces"></div>
   <div id="dialog-message" title="Server Response"></div>
   <ul id="gallery"></ul>
</body>
</html>