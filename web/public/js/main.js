$(document).ready(function () {
  // setting active nav item
  $('.active').removeClass('active');
  var url = window.location;
  $('ul.nav a').filter(function () {
	return this.href == url;
   }).parent().addClass('active').parent().parent().addClass('active');

   $('.action').click(function() {
     a = this 
     $.ajax({
      type: 'POST',
  	data: $.param({'id':this.id, 'action':this.text}),
 	success: function(data){$(a).text(data['action']); },
      dataType: 'json',
      url: '/action',
      cache:false
    });
  });
});


