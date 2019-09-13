$(function() {

	var stripeAPI = "pk_test_Ztdn7ROKq9iHWxQH8UxxoArH";
	Stripe.setPublishableKey(stripeAPI);

	$("#btnConfirm").click(function() {
		var url = '';
		var url1 = $("#targetURL1").val();
		var url2 = $("#targetURL2").val();
		if(url1.length > 0){
			url = url + url1;
		}
		if(url2.length > 0){
			url = url + ",";
			url = url + url2;
		}
		// var url = url1 + "," + url2;

		var keyword = '';
		var keyword1 = $("#keyword1").val();
		var keyword2 = $("#keyword2").val();
		
		if(keyword1.length > 0){
			keyword = keyword + keyword1;
		}
		if(keyword2.length > 0){
			keyword = keyword + ",";
			keyword = keyword + keyword2;
		}
		
		if(url.length > 0 && keyword.length > 0){
			var data = {
				urls: url,
				keys:  keyword
			}
			$.post('/reserve', data, function(res, status){
				
				$("#targetURLConfirm").val(url);
				$("#keywordConfirm").val(keyword);
				$('#content').css('display','');

				// console.log(res);
				console.log(status);
			});
		}else{
			alert("Url field and keyword feild is empty!!!!");
		}
	});

	$(".btn-info").click(function(){
		var myValue = $(this).val();
		// alert(myValue);
		if(myValue == "O"){
			$(this).val("X");
			$(this).css({'background-color' : '#d6d1d1'});
		}else{
			$(this).val("O");
			$(this).css({'background-color' : '#17a2b8'});
		}
		
	});

	$("#btnReserve").click(function(){
		// console.log('test');
		var url = '';
		var url1 = $("#targetURL1").val();
		var url2 = $("#targetURL2").val();
		if(url1.length > 0){
			url = url1;
		}
		if(url2.length > 0){
			url = url + ",";
			url = url + url2;
		}

		var keyword1 = $("#keyword1").val();
		var keyword2 = $("#keyword2").val();
		var keyword = '';
		if(keyword1.length > 0){
			keyword = keyword1;
		}
		if(keyword2.length > 0){
			keyword = keyword + ",";
			keyword = keyword2;
		}
		if(url.length > 0 && keyword.length > 0){
			var data = {
				urls: url,
				keys:  keyword
			}
			$.post('/information', data, function(res, status){
				console.log(status);
				console.log(res);
				window.location.href = res;
			});
		}else{
			alert("Url field and keyword feild is empty!!!!");
		}
		
	});

	$('#chk_transfer').click(function(event) {
		/* Act on the event */
		$('#dv_transfer').show();
	});
	
	$('#chk_invoice').click(function(event) {
		/* Act on the event */
		$('#dv_transfer').hide();
	});

	$('#chk_stripe').click(function(event) {
		/* Act on the event */
		$('#dv_transfer').hide();
	});

	$("#btn-back-reserve").click(function(){
		$.post("/backtop", "", function(res, status){
			console.log(status);
			window.location.href = res;
		});
	});

	$("#btn-next-confirm").click(function(){

		// console.log('test');
		var str_comp = $("#firstName").val();
		var str_email = $("#email").val();
		var str_username = $("#username").val();
		var str_phone = $("#address2").val();
		var str_address = $("#address").val();

		var obj = $('#inforcrawl').val();
		var temp = obj.split("#");
		var str_urls = temp[0];
		var str_keys = temp[1];

		var type_pay = $("input:radio[name='paymentMethod']:checked").val();

		var data_comp = {
			companyname: str_comp,
			email:  str_email,
			username: str_username,
			phone: str_phone,
			address: str_address,
			urls: str_urls,
			keys: str_keys,
			pay: type_pay
		}

		console.log(type_pay);
		$.post('/confirm', data_comp, function(res, status){
			console.log(status);
			// console.log(res);
			window.location.href = res;
		});
	});

	$("#back-infor").click(function(){
		$.post('/information', "", function(res, status){
			console.log(status);
			window.location.href = res;
		});
	});

	$("#btn-confirm-finish").click(function(){
		var type_pay = $('#typepay').val();
		var money = $("#id-money").text();
		money = money.replace('$', '');
		$("#id-amount").val(money);
		$("#id-amount-h").val(money);
		if(type_pay === 'Stripe'){
			$('#stripe').modal();

			console.log(type_pay);
			$.post('/finish', "", function(res, status){
				console.log(status);
				window.location.href = res;
			});
		}else{
			$.post('/finished', "", function(res, status){
				console.log(status);
				window.location.href = res;
			});
		}
		
	});

	$("#btn-back-top").click(function(){
		$.post("/backtop", "", function(res, status){
			console.log(status);
			window.location.href = res;
		});
	});

	//callback to handle the response from stripe
	function stripeResponseHandler(status, response) {
		var onlyUpdateCC = $('input[name=update-payment-flg').val();

		if (response.error) {
			//enable the submit button
	        $('#payBtn').removeAttr("disabled");
	        //display the errors on the form
	        $(".payment-errors").html(response.error.message);
	        console.log(response.error);
		}else{
			var form$ = $("#paymentFrm");
	        //get token id
	        var token = response['id'];
	        console.log(token);

	        //insert the token into the form
	        form$.append("<input type='hidden' name='stripeToken' value='" + token + "' />");
	        $.ajax({
	        	type:'POST',
				url: '/finish',
				data:$('#paymentFrm').serialize()+'&card_submit=1',
				beforeSend: function(){
					$('#payBtn').prop('disabled', true);
					$('#paymentFrm').css('opacity', '.5');
				},
				success:function(res){
					console.log(res);

					$('.name').val('');
					$('.email').val('');
					$('.card-number').val('');
					$('.card-expiry-month').val('');
					$('.card-expiry-year').val('');
					$('.card-cvc').val('');
					// $(".payment-status").html('<span style="color:green;">Your payment was successful. TXN ID: '+msg+'</span>');
					
					$('#payBtn').prop('disabled', false);
					$('#paymentFrm').css('opacity', '');

					window.location.href = res;
				},
				error: function (jqXHR, exception) {
					console.log(jqXHR);
				}
	        });
		}
	}

	//on form submit
    $("#paymentFrm").submit(function(event) {

        //disable the submit button to prevent repeated clicks
        $('#payBtn').attr("disabled", "disabled");
        
        //create single-use token to charge the user
        Stripe.createToken({
            number: $('.card-number').val(),
            cvc: $('.card-cvc').val(),
            exp_month: $('.card-expiry-month').val(),
            exp_year: $('.card-expiry-year').val()
        }, stripeResponseHandler);
        
        //submit from callback
        return false;
    });

});