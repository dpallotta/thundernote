<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Thundernote - Logout</title>
</head>
<body>

<p>Please wait...</p>
<% session.invalidate(); %>

<script type="text/javascript">

  window.location = "https://thunder-note.appspot.com/";

</script>

</body>
</html>