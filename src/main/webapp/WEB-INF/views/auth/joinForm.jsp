<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../layout/header.jsp"%>


<div class="container">
	<!-- username=값&password=값&email=값&address=값 ==> x-www-urlencoded타입 (mime 타입) ==> body에 데이터 담김-->
	<form action="/auth/join" method="POST">
		<div class="form-group">
			<label for="username">Username:</label> 
			<input type="text" class="form-control" placeholder="Enter username" name="username" />
		</div>
		<div class="form-group">
			<label for="password">Password:</label> 
			<input type="password" class="form-control" placeholder="Enter password" name="password" />
		</div>
		<div class="form-group">
			<label for="email">Email address:</label> 
			<input type="email" class="form-control" placeholder="Enter email" name="email" />
		</div>
		<input class="btn btn-info" type="button" onClick="goPopup();" value="주소 찾기" />
		<div class="form-group">
			<label for="address">Address:</label> 
			<input type="text" class="form-control" placeholder="Enter address" name="address" id ="address" readonly="readonly" />
		</div>
		<button type="submit" class="btn btn-primary">회원가입</button>

	</form>
</div>

<script>
	function goPopup() {
		var pop = window.open("/juso", "pop",
				"width=570,height=420, scrollbars=yes, resizable=yes");
	}

	function jusoCallBack(roadFullAddr) {
		let addressEL = document.querySelector("#address");
		addressEL.value=roadFullAddr;
		console.log(addressEL);
	}
</script>

<%@include file="../layout/footer.jsp"%>
